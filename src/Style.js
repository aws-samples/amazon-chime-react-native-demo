/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */

import React from 'react';
import { StyleSheet } from 'react-native';

const styles = StyleSheet.create({
  container: {
    justifyContent: 'center',
    alignItems: 'center',
    height: '95%',
    backgroundColor: 'white'
  },
  buttonContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  title: {
    fontSize: 30,
    fontWeight: '700'
  },
  subtitle: {
    marginBottom: 25,
    marginTop: 10,
    color: 'grey' 
  },
  videoContainer: {
    width: '100%',
    // This is an existing React Native issue:
    // When you create a native android component
    // that use GLSurfaceView (We use this internally), the entire screen will
    // black out
    overflow: 'hidden'
  },
  videos: {
    backgroundColor: 'gray',
    flexDirection: 'row',
    flexWrap: 'wrap',
    width: '100%',
    overflow: 'hidden'
  },
  video: {
    flex: 1,
    flexBasis: '50%',
    width: '50%',
    height: 100,
    aspectRatio: 16 / 9,
  },
  videoButtons: {
    flexDirection: 'row',
  },
  localVideo: {
    width: '100%',
    aspectRatio: 16 / 9,
  },
  screenShare: {
    width: '90%',
    margin: '1%',
    aspectRatio: 16 / 9,
  },
  attendeeList: {
    flex: 1,
    width: '80%'
  },
  attendeeContainer: {
    fontSize: 20,
    margin: 5,
    padding: 5,
    height: 30,
    backgroundColor: '#eee',
    justifyContent: 'space-between', 
    flexDirection: 'row',  
  },
  attendeeMuteImage: {
    resizeMode: 'contain',
    width: 20,
    height: 20
  },
  inputBox: {
    height: 40,
    borderColor: 'black',
    borderWidth: 1,
    margin: 5,
    width: '50%',
    padding: 10,
    color: 'black'
  },
  meetingButton: {
    resizeMode: 'contain',
    width: 50,
    height: 50
  }
});

export default styles;
