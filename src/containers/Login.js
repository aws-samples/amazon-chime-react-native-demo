/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */

import React from 'react';
import { View, Text, TextInput, Button, Alert } from 'react-native';
import styles from '../Style';

export class Login extends React.Component {
  constructor() {
    super();
    this.meetingName = "";
    this.userName = "";
  }

  startMeeting = () => {
    if (!this.meetingName || !this.userName) {
      Alert.alert("Meeting name and user name can not be empty");
    } else {
      this.props.onSubmit(this.meetingName, this.userName);
    }
  }

  renderForm() {
    return (
      <React.Fragment>
        <TextInput style={styles.inputBox} placeholder="Meeting ID" onChangeText={(val) => this.meetingName = val.trim()} />
        <TextInput style={styles.inputBox} placeholder="Your Name" onChangeText={(val) => this.userName = val.trim()} />
        <Button title="Start" onPress={this.startMeeting} />
      </React.Fragment>
    );
  }

  renderLoading() {
    return <Text>Loading, please wait...</Text>
  }

  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.title}>Mobile SDK Demo</Text>
        <Text style={styles.subtitle}>powered by React Native</Text>
        { !!this.props.isLoading ? this.renderLoading() : this.renderForm() }
      </View>
    );
  }
}
