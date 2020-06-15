/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */

import React from 'react';
import {
  SafeAreaView,
  StatusBar,
  Alert,
  Text
} from 'react-native';
import { Login } from './containers/Login';
import { Meeting } from './containers/Meeting';
import { createMeetingRequest } from './utils/Api';
import { getSDKEventEmitter, MobileSDKEvent, NativeFunction } from './utils/Bridge';
import styles from './Style';

class App extends React.Component {
  constructor() {
    super();

    this.state = {
      isInMeeting: false,
      isLoading: false,
      meetingTitle: '',
      selfAttendeeId: ''
    }
  }

  componentDidMount() {
    this.onMeetingStartSubscription = getSDKEventEmitter().addListener(MobileSDKEvent.OnMeetingStart, () => {
      this.setState({ isInMeeting: true, isLoading: false });
    });

    this.onMeetingEndSubscription = getSDKEventEmitter().addListener(MobileSDKEvent.OnMeetingEnd, () => {
      this.setState({ isInMeeting: false, isLoading: false });
    });

    this.onErrorSubscription = getSDKEventEmitter().addListener(MobileSDKEvent.OnError, (message) => {
      Alert.alert("SDK Error", message);
    });
  }

  componentWillUnmount() {
    if (this.onMeetingEndSubscription) {
      this.onMeetingEndSubscription.remove();
    }
    if (this.onMeetingStartSubscription) {
      this.onMeetingStartSubscription.remove();
    }
    if (this.onErrorSubscription) {
      this.onErrorSubscription.remove();
    }
  }

  initializeMeetingSession = (meetingName, userName) => {
    this.setState({
      isLoading: true,
    })

    createMeetingRequest(meetingName, userName).then(meetingResponse => {
      this.setState({
        meetingTitle: meetingName,
        selfAttendeeId: meetingResponse.JoinInfo.Attendee.Attendee.AttendeeId
      })
      NativeFunction.startMeeting(meetingResponse.JoinInfo.Meeting.Meeting, meetingResponse.JoinInfo.Attendee.Attendee);
    }).catch(error => {
      Alert.alert("Unable to find meeting", `There was an issue finding that meeting. The meeting may have already ended, or your authorization may have expired.\n ${error}`);
      this.setState({ isLoading: false });
    });
  }

  renderRoute() {
    if (this.state.isInMeeting) {
      return <Meeting meetingTitle={this.state.meetingTitle} selfAttendeeId={this.state.selfAttendeeId} />;
    } else {
      return <Login isLoading={this.state.isLoading} onSubmit={(meetingName, userName) => this.initializeMeetingSession(meetingName, userName)} />;
    }
  }

  render() {
    return (
      <React.Fragment>
        <StatusBar />
        <SafeAreaView>
          { this.renderRoute() }
        </SafeAreaView>
      </React.Fragment>
    );
  }
}
export default App;
