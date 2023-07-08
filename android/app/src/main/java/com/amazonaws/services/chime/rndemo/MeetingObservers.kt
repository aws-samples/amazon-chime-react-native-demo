/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */

package com.amazonaws.services.chime.rndemo

import com.amazonaws.services.chime.rndemo.RNEventEmitter.Companion.RN_EVENT_ATTENDEES_JOIN
import com.amazonaws.services.chime.rndemo.RNEventEmitter.Companion.RN_EVENT_ATTENDEES_LEAVE
import com.amazonaws.services.chime.rndemo.RNEventEmitter.Companion.RN_EVENT_ATTENDEES_MUTE
import com.amazonaws.services.chime.rndemo.RNEventEmitter.Companion.RN_EVENT_ATTENDEES_UNMUTE
import com.amazonaws.services.chime.rndemo.RNEventEmitter.Companion.RN_EVENT_VIDEO_TILE_ADD
import com.amazonaws.services.chime.rndemo.RNEventEmitter.Companion.RN_EVENT_VIDEO_TILE_REMOVE
import com.amazonaws.services.chime.sdk.meetings.audiovideo.AttendeeInfo
import com.amazonaws.services.chime.sdk.meetings.audiovideo.AudioVideoObserver
import com.amazonaws.services.chime.sdk.meetings.audiovideo.SignalUpdate
import com.amazonaws.services.chime.sdk.meetings.audiovideo.VolumeUpdate
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.RemoteVideoSource
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.VideoTileObserver
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.VideoTileState
import com.amazonaws.services.chime.sdk.meetings.realtime.RealtimeObserver
import com.amazonaws.services.chime.sdk.meetings.realtime.datamessage.DataMessage
import com.amazonaws.services.chime.sdk.meetings.realtime.datamessage.DataMessageObserver
import com.amazonaws.services.chime.sdk.meetings.session.MeetingSessionStatus
import com.amazonaws.services.chime.sdk.meetings.utils.logger.ConsoleLogger
import com.amazonaws.services.chime.sdk.meetings.utils.logger.LogLevel

class MeetingObservers(private val eventEmitter: RNEventEmitter) : RealtimeObserver, VideoTileObserver, AudioVideoObserver, DataMessageObserver {
    private val logger = ConsoleLogger(LogLevel.DEBUG)
    private var isAudioSessionStopped = true
    private var isVideoSessionStopped = true
    companion object {
        private const val TAG = "MeetingObservers"
    }

    override fun onAttendeesJoined(attendeeInfo: Array<AttendeeInfo>) {
        attendeeInfo.forEach {
            logger.info(TAG, "Received attendee join event for attendee: ${it.attendeeId} with externalUserId ${it.externalUserId}")
            eventEmitter.sendAttendeeUpdateEvent(RN_EVENT_ATTENDEES_JOIN, it)
        }
    }

    override fun onAttendeesLeft(attendeeInfo: Array<AttendeeInfo>) {
        attendeeInfo.forEach {
            logger.info(TAG, "Received attendee leave event for attendee ${it.attendeeId} with externalUserId ${it.externalUserId}")
            eventEmitter.sendAttendeeUpdateEvent(RN_EVENT_ATTENDEES_LEAVE, it)
        }
    }

    override fun onAttendeesDropped(attendeeInfo: Array<AttendeeInfo>) {
        attendeeInfo.forEach {
            logger.info(TAG, "Received attendee drop event for attendee ${it.attendeeId} with externalUserId ${it.externalUserId}")
            eventEmitter.sendAttendeeUpdateEvent(RN_EVENT_ATTENDEES_LEAVE, it)
        }
    }

    override fun onAttendeesMuted(attendeeInfo: Array<AttendeeInfo>) {
        attendeeInfo.forEach {
            logger.info(TAG, "Received attendee mute event for attendee ${it.attendeeId} with externalUserId ${it.externalUserId}")
            eventEmitter.sendReactNativeEvent(RN_EVENT_ATTENDEES_MUTE, it.attendeeId)
        }
    }

    override fun onAttendeesUnmuted(attendeeInfo: Array<AttendeeInfo>) {
        attendeeInfo.forEach {
            logger.info(TAG, "Received attendee unmute event for attendee ${it.attendeeId} with externalUserId ${it.externalUserId}")
            eventEmitter.sendReactNativeEvent(RN_EVENT_ATTENDEES_UNMUTE, it.attendeeId)
        }
    }

    override fun onSignalStrengthChanged(signalUpdates: Array<SignalUpdate>) {
        // Not implemented for demo purposes
    }

    override fun onVolumeChanged(volumeUpdates: Array<VolumeUpdate>) {
        // Not implemented for demo purposes
    }

    override fun onVideoTileAdded(tileState: VideoTileState) {
        logger.info(TAG, "Received video tile add event for attendee: ${tileState.attendeeId}")
        eventEmitter.sendVideoTileEvent(RN_EVENT_VIDEO_TILE_ADD, tileState)
    }

    override fun onVideoTilePaused(tileState: VideoTileState) {
        // Not implemented for demo purposes
    }

    override fun onVideoTileRemoved(tileState: VideoTileState) {
        logger.info(TAG, "Received video tile remove event for attendee: ${tileState.attendeeId}")
        eventEmitter.sendVideoTileEvent(RN_EVENT_VIDEO_TILE_REMOVE, tileState)
    }

    override fun onVideoTileResumed(tileState: VideoTileState) {
        // Not implemented for demo purposes
    }

    override fun onVideoTileSizeChanged(tileState: VideoTileState) {
        // Not implemented for demo purposes
    }

    override fun onAudioSessionCancelledReconnect() {
        // Not implemented for demo purposes
    }

    override fun onAudioSessionDropped() {
        // Not implemented for demo purposes
    }

    override fun onAudioSessionStarted(reconnecting: Boolean) {
        logger.info(TAG, "Received event for audio session started. Reconnecting: $reconnecting")

        if (!reconnecting) {
            isAudioSessionStopped = false
            isVideoSessionStopped = false
            eventEmitter.sendReactNativeEvent(RNEventEmitter.RN_EVENT_MEETING_START, null)
        }
    }

    override fun onAudioSessionStartedConnecting(reconnecting: Boolean) {
        // Not implemented for demo purposes
    }

    override fun onAudioSessionStopped(sessionStatus: MeetingSessionStatus) {
        // Not implemented for demo purposes
        isAudioSessionStopped = true
        emitRnMeetingEndEventIfNeeded()
    }

    override fun onCameraSendAvailabilityUpdated(available: Boolean) {
        // Not implemented for demo purposes
    }

    override fun onConnectionBecamePoor() {
        // Not implemented for demo purposes
    }

    override fun onConnectionRecovered() {
        // Not implemented for demo purposes
    }

    override fun onVideoSessionStarted(sessionStatus: MeetingSessionStatus) {
        // Not implemented for demo purposes
    }

    override fun onVideoSessionStartedConnecting() {
        // Not implemented for demo purposes
    }

    override fun onVideoSessionStopped(sessionStatus: MeetingSessionStatus) {
        // Not implemented for demo purposes
        isVideoSessionStopped = true
        emitRnMeetingEndEventIfNeeded()
    }

    override fun onDataMessageReceived(dataMessage: DataMessage) {
        eventEmitter.sendDataMessageEvent(RNEventEmitter.RN_EVENT_DATA_MESSAGE_RECEIVE, dataMessage)
    }

    override fun onRemoteVideoSourceAvailable(sources: List<RemoteVideoSource>) {
        // Not implemented for demo purposes
    }

    override fun onRemoteVideoSourceUnavailable(sources: List<RemoteVideoSource>) {
        // Not implemented for demo purposes
    }

    private fun emitRnMeetingEndEventIfNeeded() {
        if(isAudioSessionStopped && isVideoSessionStopped) {
            eventEmitter.sendReactNativeEvent(RNEventEmitter.RN_EVENT_MEETING_END, null)
        }
    }
}
