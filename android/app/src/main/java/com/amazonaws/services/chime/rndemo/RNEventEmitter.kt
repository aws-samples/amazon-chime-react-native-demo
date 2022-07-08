/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */

package com.amazonaws.services.chime.rndemo

import com.amazonaws.services.chime.sdk.meetings.audiovideo.AttendeeInfo
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.VideoTileState
import com.amazonaws.services.chime.sdk.meetings.realtime.datamessage.DataMessage
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.WritableMap
import com.facebook.react.bridge.WritableNativeMap
import com.facebook.react.modules.core.DeviceEventManagerModule

class RNEventEmitter(private val reactContext: ReactApplicationContext) {
    companion object {
        // Event types
        const val RN_EVENT_ERROR = "OnError"
        const val RN_EVENT_MEETING_START = "OnMeetingStart"
        const val RN_EVENT_MEETING_END = "OnMeetingEnd"
        const val RN_EVENT_VIDEO_TILE_ADD = "OnAddVideoTile"
        const val RN_EVENT_VIDEO_TILE_REMOVE = "OnRemoveVideoTile"
        const val RN_EVENT_ATTENDEES_JOIN = "OnAttendeesJoin"
        const val RN_EVENT_ATTENDEES_LEAVE = "OnAttendeesLeave"
        const val RN_EVENT_ATTENDEES_MUTE = "OnAttendeesMute"
        const val RN_EVENT_ATTENDEES_UNMUTE = "OnAttendeesUnmute"
        const val RN_EVENT_DATA_MESSAGE_RECEIVE = "OnDataMessageReceive"

        // Additional data for attendee events
        private const val RN_EVENT_KEY_ATTENDEE_ID = "attendeeId"
        private const val RN_EVENT_KEY_EXTERNAL_USER_ID = "externalUserId"

        // Additional data for video tile events
        private const val RN_EVENT_KEY_VIDEO_TILE_ID = "tileId"
        private const val RN_EVENT_KEY_VIDEO_IS_LOCAL = "isLocal"
        private const val RN_EVENT_KEY_VIDEO_IS_SCREEN_SHARE = "isScreenShare"
        private const val RN_EVENT_KEY_VIDEO_ATTENDEE_ID = "attendeeId"
        private const val RN_EVENT_KEY_VIDEO_PAUSE_STATE = "pauseState"
        private const val RN_EVENT_KEY_VIDEO_VIDEO_STREAM_CONTENT_HEIGHT = "videoStreamContentHeight"
        private const val RN_EVENT_KEY_VIDEO_VIDEO_STREAM_CONTENT_WIDTH = "videoStreamContentWidth"

        // Additional data for data message
        private const val RN_EVENT_KEY_DATA_MESSAGE_DATA = "data"
        private const val RN_EVENT_KEY_DATA_MESSAGE_SENDER_ATTENDEE_ID = "senderAttendeeId"
        private const val RN_EVENT_KEY_DATA_MESSAGE_SENDER_EXTERNAL_USER_ID = "senderExternalUserId"
        private const val RN_EVENT_KEY_DATA_MESSAGE_THROTTLED = "throttled"
        private const val RN_EVENT_KEY_DATA_MESSAGE_TIMESTAMP_MS = "timestampMs"
        private const val RN_EVENT_KEY_DATA_MESSAGE_TOPIC= "topic"
    }

    // Used for events such as attendee join and attendee leave
    fun sendAttendeeUpdateEvent(eventName: String, attendeeInfo: AttendeeInfo) {
        val map: WritableMap = WritableNativeMap()
        map.putString(RN_EVENT_KEY_ATTENDEE_ID, attendeeInfo.attendeeId)
        map.putString(RN_EVENT_KEY_EXTERNAL_USER_ID, attendeeInfo.externalUserId)

        sendReactNativeEvent(eventName, map)
    }

    fun sendDataMessageEvent(eventName: String, dataMessage: DataMessage) {
        val map: WritableMap = WritableNativeMap()
        map.putString(RN_EVENT_KEY_DATA_MESSAGE_DATA, dataMessage.text())
        map.putString(RN_EVENT_KEY_DATA_MESSAGE_SENDER_ATTENDEE_ID, dataMessage.senderAttendeeId)
        map.putString(RN_EVENT_KEY_DATA_MESSAGE_SENDER_EXTERNAL_USER_ID, dataMessage.senderExternalUserId)
        map.putBoolean(RN_EVENT_KEY_DATA_MESSAGE_THROTTLED, dataMessage.throttled)
        map.putDouble(RN_EVENT_KEY_DATA_MESSAGE_TIMESTAMP_MS, dataMessage.timestampMs.toDouble())
        map.putString(RN_EVENT_KEY_DATA_MESSAGE_TOPIC, dataMessage.topic)

        sendReactNativeEvent(eventName, map)
    }

    // Used for events such as video tile added and video tile removed
    fun sendVideoTileEvent(eventName: String, tileState: VideoTileState) {
        val map: WritableMap = WritableNativeMap()
        map.putInt(RN_EVENT_KEY_VIDEO_TILE_ID, tileState.tileId)
        map.putBoolean(RN_EVENT_KEY_VIDEO_IS_LOCAL, tileState.isLocalTile)
        map.putBoolean(RN_EVENT_KEY_VIDEO_IS_SCREEN_SHARE, tileState.isContent)
        map.putString(RN_EVENT_KEY_VIDEO_ATTENDEE_ID, tileState.attendeeId)
        map.putInt(RN_EVENT_KEY_VIDEO_PAUSE_STATE, tileState.pauseState.ordinal)
        map.putInt(RN_EVENT_KEY_VIDEO_VIDEO_STREAM_CONTENT_HEIGHT, tileState.videoStreamContentHeight)
        map.putInt(RN_EVENT_KEY_VIDEO_VIDEO_STREAM_CONTENT_WIDTH, tileState.videoStreamContentWidth)
        sendReactNativeEvent(eventName, map)
    }

    // Used for sending events with non String data (Attendee info, video tile state)
    private fun sendReactNativeEvent(eventName: String, data: WritableMap) {
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                .emit(eventName, data)
    }

    // Used for events such as meeting started, meeting ended, and for error messages
    fun sendReactNativeEvent(eventName: String, message: String?) {
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                .emit(eventName, message)
    }
}
