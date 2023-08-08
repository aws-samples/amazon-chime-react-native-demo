/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */

package com.amazonaws.services.chime.rndemo

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.amazonaws.services.chime.rndemo.RNEventEmitter.Companion.RN_EVENT_ERROR
import com.amazonaws.services.chime.rndemo.RNEventEmitter.Companion.RN_EVENT_MEETING_END
import com.amazonaws.services.chime.sdk.meetings.session.DefaultMeetingSession
import com.amazonaws.services.chime.sdk.meetings.session.MeetingSession
import com.amazonaws.services.chime.sdk.meetings.session.MeetingSessionConfiguration
import com.amazonaws.services.chime.sdk.meetings.session.MeetingSessionCredentials
import com.amazonaws.services.chime.sdk.meetings.session.MeetingSessionURLs
import com.amazonaws.services.chime.sdk.meetings.session.defaultUrlRewriter
import com.amazonaws.services.chime.sdk.meetings.utils.logger.ConsoleLogger
import com.amazonaws.services.chime.sdk.meetings.utils.logger.LogLevel
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.modules.core.PermissionAwareActivity
import com.facebook.react.modules.core.PermissionListener

class NativeMobileSDKBridge(
        reactContext: ReactApplicationContext,
        private val eventEmitter: RNEventEmitter,
        private val meetingObservers: MeetingObservers) : ReactContextBaseJavaModule(reactContext), PermissionListener {

    companion object {
        private const val WEBRTC_PERMISSION_REQUEST_CODE = 1
        private const val TAG = "ChimeReactNativeSDKDemoManager"
        private const val KEY_MEETING_ID = "MeetingId"
        private const val KEY_ATTENDEE_ID = "AttendeeId"
        private const val KEY_JOIN_TOKEN = "JoinToken"
        private const val KEY_EXTERNAL_ID = "ExternalUserId"
        private const val KEY_MEDIA_PLACEMENT = "MediaPlacement"
        private const val KEY_AUDIO_FALLBACK_URL = "AudioFallbackUrl"
        private const val KEY_AUDIO_HOST_URL = "AudioHostUrl"
        private const val KEY_TURN_CONTROL_URL = "TurnControlUrl"
        private const val KEY_SIGNALING_URL = "SignalingUrl"
        private const val TOPIC_CHAT = "chat"

        var meetingSession: MeetingSession? = null
    }

    private val logger = ConsoleLogger(LogLevel.DEBUG)

    private val webRtcPermissionPermission = arrayOf(
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    )

    override fun getName(): String {
        return "NativeMobileSDKBridge"
    }

    @ReactMethod
    fun startMeeting(meetingInfo: ReadableMap, attendeeInfo: ReadableMap) {
        logger.info(TAG, "Called startMeeting")

        currentActivity?.let { activity ->
            if (meetingSession != null) {
                meetingSession?.audioVideo?.stop()
                meetingSession = null
            }

            try {
                val sessionConfig = createSessionConfiguration(meetingInfo, attendeeInfo)
                val meetingSession = sessionConfig?.let {
                    DefaultMeetingSession(
                            it,
                            logger,
                            activity.applicationContext
                    )
                }

                if (meetingSession != null) {
                    NativeMobileSDKBridge.meetingSession = meetingSession

                    if (!hasPermissionsAlready()) {
                        val permissionAwareActivity = activity as PermissionAwareActivity
                        permissionAwareActivity.requestPermissions(webRtcPermissionPermission, WEBRTC_PERMISSION_REQUEST_CODE, this)
                        return
                    }

                    startAudioVideo()
                } else {
                    logger.error(TAG, "Failed to create meeting session")
                    eventEmitter.sendReactNativeEvent(RN_EVENT_ERROR, "Failed to create meeting session")
                }
            } catch (exception: Exception) {
                logger.error(TAG, "Error starting the meeting session: ${exception.localizedMessage}")
                eventEmitter.sendReactNativeEvent(RN_EVENT_ERROR, "Error starting the meeting session: ${exception.localizedMessage}")
                return
            }
        }
    }

    private fun hasPermissionsAlready(): Boolean {
        return currentActivity?.let { activity ->
            webRtcPermissionPermission.all {
                ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
            }
        } ?: false
    }

    private fun startAudioVideo() {
        meetingSession?.let {
            it.audioVideo.addRealtimeObserver(meetingObservers)
            it.audioVideo.addVideoTileObserver(meetingObservers)
            it.audioVideo.addAudioVideoObserver(meetingObservers)
            it.audioVideo.addRealtimeDataMessageObserver(TOPIC_CHAT, meetingObservers)
            it.audioVideo.start()
            it.audioVideo.startRemoteVideo()
        }
    }

    private fun createSessionConfiguration(meetingInfo: ReadableMap, attendeeInfo: ReadableMap): MeetingSessionConfiguration? {
        return try {
            val meetingId = meetingInfo.getString(KEY_MEETING_ID) ?: ""
            val attendeeId = attendeeInfo.getString(KEY_ATTENDEE_ID) ?: ""
            val joinToken = attendeeInfo.getString(KEY_JOIN_TOKEN) ?: ""
            val externalUserId = attendeeInfo.getString(KEY_EXTERNAL_ID) ?: ""
            var audioFallbackUrl = ""
            var audioHostUrl = ""
            var turnControlUrl = ""
            var signalingUrl = ""

            meetingInfo.getMap(KEY_MEDIA_PLACEMENT)?.let {
                logger.info(TAG, it.toString())
                audioFallbackUrl = it.getString(KEY_AUDIO_FALLBACK_URL) ?: ""
                audioHostUrl = it.getString(KEY_AUDIO_HOST_URL) ?: ""
                turnControlUrl = it.getString(KEY_TURN_CONTROL_URL) ?: ""
                signalingUrl = it.getString(KEY_SIGNALING_URL) ?: ""
            }

            MeetingSessionConfiguration(meetingId,
                    MeetingSessionCredentials(attendeeId, externalUserId, joinToken),
                    MeetingSessionURLs(audioFallbackUrl, audioHostUrl, turnControlUrl, signalingUrl, ::defaultUrlRewriter))
        } catch (exception: Exception) {
            logger.error(TAG, "Error creating session configuration: ${exception.localizedMessage}")
            eventEmitter.sendReactNativeEvent(RN_EVENT_ERROR, "Error creating session configuration: ${exception.localizedMessage}")
            null
        }
    }

    @ReactMethod
    fun stopMeeting() {
        logger.info(TAG, "Called stopMeeting")

        meetingSession?.audioVideo?.stop()
    }

    @ReactMethod
    fun setMute(isMute: Boolean) {
        logger.info(TAG, "Called setMute: $isMute")

        if (isMute) {
            meetingSession?.audioVideo?.realtimeLocalMute()
        } else {
            meetingSession?.audioVideo?.realtimeLocalUnmute()
        }
    }

    @ReactMethod
    fun setCameraOn(enabled: Boolean) {
        logger.info(TAG, "Called setCameraOn: $enabled")

        if (enabled) {
            meetingSession?.audioVideo?.startLocalVideo()
        } else {
            meetingSession?.audioVideo?.stopLocalVideo()
        }
    }

    @ReactMethod
    fun bindVideoView(viewIdentifier: Double, tileId: Int) {
        logger.info(TAG, "Called bindVideoView for tileId: $tileId with identifier: $viewIdentifier")
    }

    @ReactMethod
    fun unbindVideoView(tileId: Int) {
        logger.info(TAG, "Called unbindVideoView for tileId: $tileId")

        meetingSession?.run {
            audioVideo.unbindVideoView(tileId)
        }
    }

    @ReactMethod
    fun sendDataMessage(topic: String, message: String, lifetimeMs: Int) {
        meetingSession?.audioVideo?.realtimeSendDataMessage(topic, message, lifetimeMs)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, grantResults: IntArray?): Boolean {
        return when (requestCode) {
            WEBRTC_PERMISSION_REQUEST_CODE -> {
                val isMissingPermission: Boolean =
                        grantResults?.isEmpty() ?: false || grantResults?.any { PackageManager.PERMISSION_GRANTED != it } ?: false

                if (isMissingPermission) {
                    eventEmitter.sendReactNativeEvent(RN_EVENT_ERROR, "Unable to start meeting as permissions are not granted")
                    false
                } else {
                    startAudioVideo()
                    true
                }
            }
            else -> false
        }
    }

    // Required for rn built in EventEmitter Calls.
    @ReactMethod
    fun addListener(eventName: String) {

    }

    @ReactMethod
    fun removeListeners(count: Int) {

    }
}
