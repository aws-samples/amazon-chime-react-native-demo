//
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
//

#import "NativeMobileSDKBridge.h"
#import <AVFoundation/AVFoundation.h>
#import "RNVideoViewManager.h"
#import <AmazonChimeSDKMedia/AmazonChimeSDKMedia.h>
#import "MeetingObservers.h"
#import <React/RCTUIManager.h>

@implementation NativeMobileSDKBridge

static DefaultMeetingSession *meetingSession;
static ConsoleLogger *logger;

RCT_EXPORT_MODULE();

- (NSArray<NSString *> *)supportedEvents
{
  return
  @[
    kEventOnMeetingStart,
    kEventOnMeetingEnd,
    kEventOnAttendeesJoin,
    kEventOnAttendeesLeave,
    kEventOnAttendeesMute,
    kEventOnAttendeesUnmute,
    kEventOnAddVideoTile,
    kEventOnRemoveVideoTile,
    kEventOnDataMessageReceive,
    kEventOnError
  ];
}

# pragma mark: Native Function
RCT_EXPORT_METHOD(startMeeting:(NSDictionary *)meetingInfoDict attendeeInfo:(NSDictionary *)attendeeInfoDict)
{
  if (meetingSession != nil)
  {
    [meetingSession.audioVideo stop];
    meetingSession = nil;
  }

  logger = [[ConsoleLogger alloc] initWithName:@"NativeMobileSDKBridge" level:LogLevelDEFAULT];
  [logger infoWithMsg: [[NSString alloc] initWithFormat:@"Running Amazon Chime SDK (%@)", Versioning.sdkVersion]];
  // Parse meeting join data from payload
  NSDictionary *mediaPlacementDict = [meetingInfoDict objectForKey:kMediaPlacement];

  // Parse meeting info
  NSString *meetingId = [meetingInfoDict objectForKey:kMeetingId];
  NSString *externalMeetingId = [meetingInfoDict objectForKey:kExternalMeetingId];
  NSString *meetingRegion = [meetingInfoDict objectForKey:kMediaRegion];

  // Parse meeting join info
  NSString *audioFallbackUrl = [mediaPlacementDict objectForKey:kAudioFallbackUrl];
  NSString *audioHostUrl = [mediaPlacementDict objectForKey:kAudioHostUrl];
  NSString *turnControlUrl = [mediaPlacementDict objectForKey:kTurnControlUrl];
  NSString *signalingUrl = [mediaPlacementDict objectForKey:kSignalingUrl];

  // Parse attendee info
  NSString *attendeeId = [attendeeInfoDict objectForKey:kAttendeeId];
  NSString *externalUserId = [attendeeInfoDict objectForKey:kExternalUserId];
  NSString *joinToken = [attendeeInfoDict objectForKey:kJoinToken];

  // Initialize meeting session through AmazonChimeSDK
  MediaPlacement *mediaPlacement = [[MediaPlacement alloc] initWithAudioFallbackUrl:audioFallbackUrl
                                                                       audioHostUrl:audioHostUrl
                                                                       signalingUrl:signalingUrl
                                                                     turnControlUrl:turnControlUrl];

  Meeting *meeting = [[Meeting alloc] initWithExternalMeetingId:externalMeetingId
                                                 mediaPlacement:mediaPlacement
                                                    mediaRegion:meetingRegion
                                                      meetingId:meetingId];
  
  CreateMeetingResponse *createMeetingResponse = [[CreateMeetingResponse alloc] initWithMeeting:meeting];

  Attendee *attendee = [[Attendee alloc] initWithAttendeeId:attendeeId
                                             externalUserId:externalUserId joinToken:joinToken];

  CreateAttendeeResponse *createAttendeeResponse = [[CreateAttendeeResponse alloc] initWithAttendee:attendee];
  MeetingSessionConfiguration *meetingSessionConfiguration = [[MeetingSessionConfiguration alloc] initWithCreateMeetingResponse:createMeetingResponse
                                                                                                         createAttendeeResponse:createAttendeeResponse];

  meetingSession = [[DefaultMeetingSession alloc] initWithConfiguration:meetingSessionConfiguration
                                                                 logger:logger];
  [self startAudioClient];
}

RCT_EXPORT_METHOD(stopMeeting)
{
  [meetingSession.audioVideo stop];
  meetingSession = nil;
  [self sendEventWithName:kEventOnMeetingEnd body: nil];
}

RCT_EXPORT_METHOD(setMute:(BOOL)isMute)
{
  BOOL success = true;
  if (isMute)
  {
      success = [meetingSession.audioVideo realtimeLocalMute];
  }
  else
  {
      success = [meetingSession.audioVideo realtimeLocalUnmute];
  }

  if (!success)
  {
    [self sendEventWithName:kEventOnError body:@"Failed to set mute state"];
  }
}

RCT_EXPORT_METHOD(setCameraOn:(BOOL)isOn)
{
  if (isOn)
  {
    [self startVideo];
  }
  else
  {
    [meetingSession.audioVideo stopLocalVideo];
  }

}

RCT_EXPORT_METHOD(bindVideoView:(NSNumber * _Nonnull)viewIdentifier tileId:(NSNumber * _Nonnull)tileId)
{
  dispatch_async(dispatch_get_main_queue(), ^{
    UIView* view = [self.bridge.uiManager viewForReactTag:viewIdentifier];
    if(view != nil) {
      [meetingSession.audioVideo bindVideoViewWithVideoView:(DefaultVideoRenderView*)view tileId:[tileId integerValue]];
    } else {
      [logger errorWithMsg:@"Failed to bind video view"];
    }
  });
}

RCT_EXPORT_METHOD(unbindVideoView:(NSNumber * _Nonnull)tileId)
{
  dispatch_async(dispatch_get_main_queue(), ^{
    [meetingSession.audioVideo unbindVideoViewWithTileId:[tileId integerValue]];
  });
}

#pragma mark: Media Related Function
-(void)startVideo
{
  AVAuthorizationStatus status = [AVCaptureDevice authorizationStatusForMediaType:AVMediaTypeVideo];
  switch (status)
  {
    case AVAuthorizationStatusNotDetermined:
    {
      [AVCaptureDevice requestAccessForMediaType:AVMediaTypeVideo completionHandler:^(BOOL granted)
      {
          if (granted)
          { // Access has been granted ..retry starting video
            [self startVideo];
          } else { // Access denied
            [self sendEventWithName:kEventOnError body:@"User denied camera permission"];
          }
      }];
      break;
    }
    case AVAuthorizationStatusAuthorized:
    {
      NSError* error;
      [meetingSession.audioVideo startLocalVideoAndReturnError:&error];
      if(error != nil)
      {
        [self sendEventWithName:kEventOnError body:@"Fail to start local video"];
      }
      break;
    }
    case AVAuthorizationStatusDenied:
    {
      [[UIApplication sharedApplication] openURL:[NSURL URLWithString:UIApplicationOpenSettingsURLString]];
      break;
    }
    default:
      break;
  }
}

-(void)startAudioClient
{
  if (meetingSession == nil)
  {
    [logger errorWithMsg:@"meetingSession is not initialized"];
    return;
  }
  MeetingObservers* observer = [[MeetingObservers alloc] initWithBridge:self logger:logger];
  [meetingSession.audioVideo addRealtimeObserverWithObserver:observer];
  [meetingSession.audioVideo addVideoTileObserverWithObserver:observer];
  [meetingSession.audioVideo addAudioVideoObserverWithObserver:observer];
  [meetingSession.audioVideo addRealtimeDataMessageObserverWithTopic:@"chat" observer:observer];
  [self startAudioVideo];
}

-(void)startAudioVideo
{
   NSError* error = nil;
   BOOL started = [meetingSession.audioVideo startAndReturnError:&error];
   if (started && error == nil)
   {
     [logger infoWithMsg:@"RN meeting session was started successfully"];

     [meetingSession.audioVideo startRemoteVideo];
   }
   else
   {
     NSString *errorMsg = [NSString stringWithFormat:@"Failed to start meeting, error: %@", error.description];
     [logger errorWithMsg:errorMsg];
     
     // Handle missing permission error
     if ([error.domain isEqual:@"AmazonChimeSDK.PermissionError"])
     {
       AVAudioSessionRecordPermission permissionStatus = [[AVAudioSession sharedInstance] recordPermission];
       if (permissionStatus == AVAudioSessionRecordPermissionUndetermined)
       {
         [[AVAudioSession sharedInstance] requestRecordPermission:^(BOOL granted)
         {
           if (granted)
           {
             [logger infoWithMsg:@"Audio permission granted"];
             // Retry after permission is granted
             [self startAudioVideo];
           }
           else
           {
             [logger infoWithMsg:@"Audio permission not granted"];
             [self sendEventWithName:kEventOnMeetingEnd body:nil];
           }
         }];
       }
       else if (permissionStatus == AVAudioSessionRecordPermissionDenied)
       {
         [logger errorWithMsg:@"User did not grant permission, should redirect to Settings"];
         [self sendEventWithName:kEventOnMeetingEnd body:nil];
       }
     }
     else
     {
       // Uncaught error
       [self sendEventWithName:kEventOnError body: errorMsg];
       [self sendEventWithName:kEventOnMeetingEnd body:nil];
     }
   }
}

RCT_EXPORT_METHOD(sendDataMessage:(NSString* _Nonnull)topic data:(NSString* _Nonnull)data lifetimeMs:(int)lifetimeMs)
{
  if (meetingSession == nil) {
    return;
  }
  
  [meetingSession.audioVideo realtimeSendDataMessageWithTopic:topic data:data lifetimeMs:lifetimeMs error:nil];
}

@end
