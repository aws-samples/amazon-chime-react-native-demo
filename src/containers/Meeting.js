/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */

import React from 'react';
import { View, Text, FlatList, Alert } from 'react-native';
import styles from '../Style';
import { NativeFunction, getSDKEventEmitter, MobileSDKEvent, MeetingError } from '../utils/Bridge';
import { RNVideoRenderView } from '../components/RNVideoRenderView';
import { MuteButton } from '../components/MuteButton';
import { CameraButton } from '../components/CameraButton';
import { HangOffButton } from '../components/HangOffButton';
import { AttendeeItem } from '../components/AttendeeItem';
import { VideoTiles } from '../components/VideoTiles';

// Maps attendee Id to attendee Name
const attendeeNameMap = {};

export class Meeting extends React.Component {
  constructor() {
    super();
    this.state = {
      attendees: [],
      videoTiles: [],
      mutedAttendee: [],
      localTileId: null,
      meetingTitle: '',
      screenShareTile: null,
    };
  }

  remoteTileIdSet = new Set();

  componentDidMount() {
    /**
     * Attendee Join and Leave handler
     */
    this.onAttendeesJoinSubscription = getSDKEventEmitter().addListener(
      MobileSDKEvent.OnAttendeesJoin,
      ({ attendeeId, externalUserId }) => {
        if (!(attendeeId in attendeeNameMap)) {
          // The externalUserId will be a format such as c19587e7#Alice
          attendeeNameMap[attendeeId] = externalUserId.split('#')[1];
        }
        this.setState((oldState) => ({
          ...oldState,
          attendees: oldState.attendees.concat([attendeeId]),
        }));
      }
    );

    this.onAttendeesLeaveSubscription = getSDKEventEmitter().addListener(
      MobileSDKEvent.OnAttendeesLeave,
      ({ attendeeId }) => {
        this.setState((oldState) => ({
          ...oldState,
          attendees: oldState.attendees.filter((attendeeToCompare) => attendeeId != attendeeToCompare),
        }));
      }
    );

    /**
     * Attendee Mute & Unmute handler
     */
    this.onAttendeesMuteSubscription = getSDKEventEmitter().addListener(
      MobileSDKEvent.OnAttendeesMute,
      (attendeeId) => {
        this.setState((oldState) => ({
          ...oldState,
          mutedAttendee: oldState.mutedAttendee.concat([attendeeId]),
        }));
      }
    );

    this.onAttendeesUnmuteSubscription = getSDKEventEmitter().addListener(
      MobileSDKEvent.OnAttendeesUnmute,
      (attendeeId) => {
        this.setState((oldState) => ({
          ...oldState,
          mutedAttendee: oldState.mutedAttendee.filter((attendeeToCompare) => attendeeId != attendeeToCompare),
        }));
      }
    );

    /**
     * Video tile Add & Remove Handler
     */
    this.onAddVideoTileSubscription = getSDKEventEmitter().addListener(MobileSDKEvent.OnAddVideoTile, (tileState) => {
      if (tileState.isScreenShare) {
        this.setState((oldState) => ({
          ...oldState,
          screenShareTile: tileState.tileId,
        }));
      } else if (tileState.isLocal) {
        this.setState((oldState) => ({
          ...oldState,
          localTileId: tileState.tileId,
        }));
      } else {
        // Calling `NativeFunction.unbindVideoView` with the streaming tile ID triggers the `OnAddVideoTile` event.
        // Make sure that the demo does not append the streaming tile ID to the list.
        if (this.remoteTileIdSet.has(tileState.tileId)) {
          return;
        } else {
          this.remoteTileIdSet.add(tileState.tileId);
        }

        this.setState((oldState) => ({
          ...oldState,
          videoTiles: [...oldState.videoTiles, tileState.tileId],
        }));
      }
    });

    this.onRemoveVideoTileSubscription = getSDKEventEmitter().addListener(
      MobileSDKEvent.OnRemoveVideoTile,
      (tileState) => {
        if (tileState.isScreenShare) {
          this.setState((oldState) => ({
            ...oldState,
            screenShareTile: null,
          }));
        } else if (tileState.isLocal) {
          this.setState((oldState) => ({
            ...oldState,
            localTileId: null,
          }));
        } else {
          this.remoteTileIdSet.delete(tileState.tileId);

          this.setState((oldState) => ({
            ...oldState,
            videoTiles: oldState.videoTiles.filter((tileIdToCompare) => tileIdToCompare != tileState.tileId),
          }));
        }
      }
    );

    /**
     * Data message handler
     */
    this.onDataMessageReceivedSubscription = getSDKEventEmitter().addListener(
      MobileSDKEvent.OnDataMessageReceive,
      (dataMessage) => {
        const str = `Received Data message (topic: ${dataMessage.topic}) ${dataMessage.data} from ${dataMessage.senderAttendeeId}:${dataMessage.senderExternalUserId} at ${dataMessage.timestampMs} throttled: ${dataMessage.throttled}`;
        console.log(str);
        NativeFunction.sendDataMessage(dataMessage.topic, str, 1000);
      }
    );

    /**
     * General Error handler
     */
    this.onErrorSubscription = getSDKEventEmitter().addListener(MobileSDKEvent.OnError, (errorType) => {
      switch (errorType) {
        case MeetingError.OnMaximumConcurrentVideoReached:
          Alert.alert('Failed to enable video', 'maximum number of concurrent videos reached!');
          break;
        default:
          Alert.alert('Error', errorType);
          break;
      }
    });
  }

  componentWillUnmount() {
    if (this.onAttendeesJoinSubscription) {
      this.onAttendeesJoinSubscription.remove();
    }
    if (this.onAttendeesLeaveSubscription) {
      this.onAttendeesLeaveSubscription.remove();
    }
    if (this.onAttendeesMuteSubscription) {
      this.onAttendeesMuteSubscription.remove();
    }
    if (this.onAttendeesUnmuteSubscription) {
      this.onAttendeesUnmuteSubscription.remove();
    }
    if (this.onAddVideoTileSubscription) {
      this.onAddVideoTileSubscription.remove();
    }
    if (this.onRemoveVideoTileSubscription) {
      this.onRemoveVideoTileSubscription.remove();
    }
    if (this.onDataMessageReceivedSubscription) {
      this.onDataMessageReceivedSubscription.remove();
    }
    if (this.onErrorSubscription) {
      this.onErrorSubscription.remove();
    }
  }

  render() {
    const currentMuted = this.state.mutedAttendee.includes(this.props.selfAttendeeId);
    return (
      <View style={[styles.container, { justifyContent: 'flex-start' }]}>
        <Text style={styles.title}>{this.props.meetingTitle}</Text>
        <View style={styles.buttonContainer}>
          <MuteButton muted={currentMuted} onPress={() => NativeFunction.setMute(!currentMuted)} />
          <CameraButton
            disabled={this.state.localTileId !== null}
            onPress={() => NativeFunction.setCameraOn(this.state.localTileId === null)}
          />
          <HangOffButton onPress={() => NativeFunction.stopMeeting()} />
        </View>
        <Text style={styles.title}>Remote video grid</Text>
        <VideoTiles remoteVideoTileIds={this.state.videoTiles} />
        {this.state.localTileId !== null && (
          <React.Fragment>
            <Text style={styles.title}>Local video</Text>
            <View style={styles.videoContainer}>
              <RNVideoRenderView
                style={styles.localVideo}
                key={this.state.localTileId}
                tileId={this.state.localTileId}
              />
            </View>
          </React.Fragment>
        )}
        {!!this.state.screenShareTile && (
          <React.Fragment>
            <Text style={styles.title}>Screen Share</Text>
            <View style={styles.videoContainer}>
              <RNVideoRenderView
                style={styles.screenShare}
                key={this.state.screenShareTile}
                tileId={this.state.screenShareTile}
              />
            </View>
          </React.Fragment>
        )}
        <Text style={styles.title}>Attendee</Text>
        <FlatList
          style={styles.attendeeList}
          data={this.state.attendees}
          renderItem={({ item }) => (
            <AttendeeItem
              attendeeName={attendeeNameMap[item] ? attendeeNameMap[item] : item}
              muted={this.state.mutedAttendee.includes(item)}
            />
          )}
          keyExtractor={(item) => item}
        />
      </View>
    );
  }
}
