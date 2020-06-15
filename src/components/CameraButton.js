/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */

import React from 'react'
import {TouchableOpacity, Image} from 'react-native'
import styles from '../Style';

let videoDisabledImg = require('../assets/video-disabled.png');
let videoImg = require('../assets/video.png');

export const CameraButton = ({disabled, onPress}) => {
  return (  
  <TouchableOpacity 
    onPress={() => {
      onPress();
  }}>
    <Image
      style={styles.meetingButton}
      source={disabled ? videoDisabledImg : videoImg}
    />
  </TouchableOpacity>
  ) 
}
