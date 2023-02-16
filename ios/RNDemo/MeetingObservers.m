//
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
//

#import <Foundation/Foundation.h>
#import "MeetingObservers.h"
#import "NativeMobileSDKBridge.h"

@implementation MeetingObservers
{
  NativeMobileSDKBridge* _bridge;
  ConsoleLogger * _logger;
}

- (id)initWithBridge: (NativeMobileSDKBridge *) bridge logger:(ConsoleLogger * )logger
{
  _bridge = bridge;
  _logger = logger;
  return self;
}

- (void)attendeesDidJoinWithAttendeeInfo:(NSArray<AttendeeInfo *> * _Nonnull)attendeeInfo
{
  for (id currentAttendeeInfo in attendeeInfo) 
  {
    [_bridge sendEventWithName:kEventOnAttendeesJoin body:@{@"attendeeId":[currentAttendeeInfo attendeeId], @"externalUserId":[currentAttendeeInfo externalUserId]}];
    [_logger infoWithMsg:[NSString stringWithFormat:@"Attendee %@ join", [currentAttendeeInfo externalUserId]]];
  }
}

- (void)attendeesDidLeaveWithAttendeeInfo:(NSArray<AttendeeInfo *> * _Nonnull)attendeeInfo
{
  for (id currentAttendeeInfo in attendeeInfo) 
  {
    [_bridge sendEventWithName:kEventOnAttendeesLeave body:@{@"attendeeId":[currentAttendeeInfo attendeeId], @"externalUserId":[currentAttendeeInfo externalUserId]}];
    [_logger infoWithMsg:[NSString stringWithFormat:@"AttendeeQuit(leave) : %@ ", [currentAttendeeInfo externalUserId]]];
  }
}

- (void)attendeesDidMuteWithAttendeeInfo:(NSArray<AttendeeInfo *> * _Nonnull)attendeeInfo
{
  for (id currentAttendeeInfo in attendeeInfo) 
  {
    [_bridge sendEventWithName:kEventOnAttendeesMute body:[currentAttendeeInfo attendeeId]];
    [_logger infoWithMsg:[NSString stringWithFormat:@"Attendee %@ mute", [currentAttendeeInfo externalUserId]]];
  }
}

- (void)attendeesDidUnmuteWithAttendeeInfo:(NSArray<AttendeeInfo *> * _Nonnull)attendeeInfo
{
  for (id currentAttendeeInfo in attendeeInfo)
  {
    [_bridge sendEventWithName:kEventOnAttendeesUnmute body:[currentAttendeeInfo attendeeId]];
    [_logger infoWithMsg:[NSString stringWithFormat:@"Attendee %@ unmute", [currentAttendeeInfo externalUserId]]];
  }
}

- (void)signalStrengthDidChangeWithSignalUpdates:(NSArray<SignalUpdate *> * _Nonnull)signalUpdates
{
  for (id currentSignalUpdate in signalUpdates)
  {
    [_logger infoWithMsg:[NSString stringWithFormat:@"Attendee %@ signalStrength changed to %lu", [[currentSignalUpdate attendeeInfo] attendeeId], [currentSignalUpdate signalStrength]]];
  }
}

- (void)volumeDidChangeWithVolumeUpdates:(NSArray<VolumeUpdate *> * _Nonnull)volumeUpdates
{
  for (id currentVolumeUpdate in volumeUpdates)
  {
    [_logger infoWithMsg:[NSString stringWithFormat:@"Attendee %@ volumeLevel changed to %ld", [[currentVolumeUpdate attendeeInfo] attendeeId], [currentVolumeUpdate volumeLevel]]];
  }
}

- (void)attendeesDidDropWithAttendeeInfo:(NSArray<AttendeeInfo *> * _Nonnull)attendeeInfo
{
  for (id currentAttendeeInfo in attendeeInfo) 
  {
    [_bridge sendEventWithName:kEventOnAttendeesLeave body:@{@"attendeeId":[currentAttendeeInfo attendeeId], @"externalUserId":[currentAttendeeInfo externalUserId]}];
    [_logger infoWithMsg:[NSString stringWithFormat:@"AttendeeQuit(drop) : %@ ", [currentAttendeeInfo externalUserId]]];
  }
}

- (void)videoTileDidAddWithTileState:(VideoTileState * _Nonnull)tileState
{
  [_bridge sendEventWithName:kEventOnAddVideoTile body:@{@"tileId":[NSNumber numberWithInt: (int)tileState.tileId], @"isLocal":@(tileState.isLocalTile), @"isScreenShare":@(tileState.isContent), @"attendeeId":tileState.attendeeId, @"pauseState":[NSNumber numberWithInt: (int)tileState.pauseState], @"videoStreamContentHeight":[NSNumber numberWithInt: (int)tileState.videoStreamContentHeight], @"videoStreamContentWidth":[NSNumber numberWithInt: (int)tileState.videoStreamContentWidth]}];
}

- (void)videoTileDidPauseWithTileState:(VideoTileState * _Nonnull)tileState
{
  // Not implemented for demo purposes
}

- (void)videoTileDidRemoveWithTileState:(VideoTileState * _Nonnull)tileState
{
   [_bridge sendEventWithName:kEventOnRemoveVideoTile body:@{@"tileId":[NSNumber numberWithInt: (int)tileState.tileId], @"isLocal":@(tileState.isLocalTile), @"isScreenShare":@(tileState.isContent)}];
}

- (void)videoTileDidResumeWithTileState:(VideoTileState * _Nonnull)tileState
{
  // Not implemented for demo purposes
}

- (void)videoTileSizeDidChangeWithTileState:(VideoTileState * _Nonnull)tileState {
  // Not implemented for demo purposes
}


- (void)audioSessionDidCancelReconnect
{
  // Not implemented for demo purposes
}

- (void)audioSessionDidStartConnectingWithReconnecting:(BOOL)reconnecting
{
  // Not implemented for demo purposes
}

- (void)audioSessionDidStartWithReconnecting:(BOOL)reconnecting
{
  if (!reconnecting)
  {
    [_logger infoWithMsg:@"Meeting Started!"];
    [_bridge sendEventWithName:kEventOnMeetingStart body:nil];
  }
}

- (void)audioSessionDidStopWithStatusWithSessionStatus:(MeetingSessionStatus * _Nonnull)sessionStatus
{
  // Not implemented for demo purposes
}

- (void)connectionDidBecomePoor
{
  // Not implemented for demo purposes
}

- (void)connectionDidRecover
{
  // Not implemented for demo purposes
}

- (void)videoSessionDidStartConnecting
{
  // Not implemented for demo purposes
}

- (void)videoSessionDidStartWithStatusWithSessionStatus:(MeetingSessionStatus * _Nonnull)sessionStatus
{
  if (sessionStatus.statusCode == sVideoAtCapacityViewOnly)
  {
    [_bridge sendEventWithName:kEventOnError body:kErrorEventOnMaximumConcurrentVideoReached];
  }
}

- (void)videoSessionDidStopWithStatusWithSessionStatus:(MeetingSessionStatus * _Nonnull)sessionStatus
{
  // Not implemented for demo purposes
}

- (void)audioSessionDidDrop
{
  // Not implemented for demo purposes
}

- (void)remoteVideoSourcesDidBecomeAvailableWithSources:(NSArray<RemoteVideoSource *> * _Nonnull)sources {
  // Not implemented for demo purposes
}

- (void)remoteVideoSourcesDidBecomeUnavailableWithSources:(NSArray<RemoteVideoSource *> * _Nonnull)sources {
  // Not implemented for demo purposes
}

- (void)cameraSendAvailabilityDidChangeWithAvailable:(BOOL)available {
  // Not implemented for demo purposes
}

- (void)dataMessageDidReceivedWithDataMessage:(DataMessage *)dataMessage {
  [_bridge sendEventWithName:kEventOnDataMessageReceive body:@{
    @"data":[dataMessage text],
    @"topic":[dataMessage topic],
    @"senderAttendeeId":[dataMessage senderAttendeeId],
    @"senderExternalUserId":[dataMessage senderExternalUserId],
    @"throttled":@(dataMessage.throttled),
    @"timestampMs":@(dataMessage.timestampMs)
  }];
}


@end

