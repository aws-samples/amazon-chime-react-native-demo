/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */

import { NativeModules, NativeEventEmitter } from 'react-native';

/**
 * These are the function that will be called from native side
 * i.e. Native -> React Native
 * 
 * NativeEventEmitter.onMeetingStart(meetingName)
 * NativeEventEmitter.onMeetingEnd()
 * NativeEventEmitter.onAttendeesJoin(attendeeInfo)
 * NativeEventEmitter.onAttendeesLeave(attendeeInfo)
 * NativeEventEmitter.onAddVideoTile(tileState)
 * NativeEventEmitter.onRemoveVideoTile(tileState)
 * NativeEventEmitter.onError(errorMessage)
 */
const _eventEmitter = new NativeEventEmitter(NativeModules.NativeMobileSDKBridge);

export const MobileSDKEvent = {
  OnMeetingStart: 'OnMeetingStart',
  OnMeetingEnd: 'OnMeetingEnd',
  OnAttendeesJoin: 'OnAttendeesJoin',
  OnAttendeesLeave: 'OnAttendeesLeave',
  OnAttendeesMute: 'OnAttendeesMute',
  OnAttendeesUnmute: 'OnAttendeesUnmute',
  OnAddVideoTile: 'OnAddVideoTile',
  OnRemoveVideoTile: 'OnRemoveVideoTile',
  OnDataMessageReceive: 'OnDataMessageReceive',
  OnError: 'OnError',
}

export const MeetingError = {
  OnMaximumConcurrentVideoReached: "OnMaximumConcurrentVideoReached"
}

export function getSDKEventEmitter() {
  return _eventEmitter;
}

/**
 * These are functions available for React native to call on native
 * i.e. React Native -> Native
 *
 * NativeModules.NativeMobileSDKBridge.startMeeting(meetingId, userName)
 * NativeModules.NativeMobileSDKBridge.stopMeeting()
 * NativeModules.NativeMobileSDKBridge.setMute(isMute) -> boolean
 * NativeModules.NativeMobileSDKBridge.setCameraOn(isOn) -> boolean
 * NativeModules.NativeMobileSDKBridge.bindVideoView(reactTagId, tileId)
 * NativeModules.NativeMobileSDKBridge.unbindVideoView(reactTagId, tileId)
 */
export const NativeFunction = {
  startMeeting: NativeModules.NativeMobileSDKBridge.startMeeting,
  stopMeeting: NativeModules.NativeMobileSDKBridge.stopMeeting,
  setMute: NativeModules.NativeMobileSDKBridge.setMute,
  setCameraOn: NativeModules.NativeMobileSDKBridge.setCameraOn,
  bindVideoView: NativeModules.NativeMobileSDKBridge.bindVideoView,
  unbindVideoView: NativeModules.NativeMobileSDKBridge.unbindVideoView,
  sendDataMessage: NativeModules.NativeMobileSDKBridge.sendDataMessage,
}
