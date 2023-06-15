/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */

import PropTypes from 'prop-types';
import React from 'react';
import { requireNativeComponent, findNodeHandle } from 'react-native';
import { NativeFunction } from '../utils/Bridge';

export class RNVideoRenderView extends React.Component {

  componentDidMount() {
    // we need to delay the bind video 
    // Because "componentDidMount" will be called "immediately after the initial rendering occurs"
    // This is *before* RCTUIManager add this view to register (so that viewForReactTag() can return a view)
    // So we need to dispatch bindVideoView after this function complete
    // We need to clearTimeout https://github.com/aws/amazon-chime-sdk-android/issues/563 to avoid crashes when setTimeout occurs
    // After view is dismounted.
    this.timeoutId = setTimeout(() => {
      NativeFunction.bindVideoView(findNodeHandle(this), this.props.tileId);
    });
  }

  componentWillUnmount() {
    clearTimeout(this.timeoutId);
    NativeFunction.unbindVideoView(this.props.tileId);
  }

  render() {
    return <RNVideoRenderViewNative {...this.props} />;
  }
}

RNVideoRenderView.propTypes = {
  /**
   * A int value to identifier the Video view, will be used to bind video stream later
   */
  tileId: PropTypes.number,
};

var RNVideoRenderViewNative = requireNativeComponent('RNVideoView', RNVideoRenderView);
