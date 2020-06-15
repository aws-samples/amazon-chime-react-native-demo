/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */

package com.amazonaws.services.chime.rndemo

import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.DefaultVideoRenderView
import com.amazonaws.services.chime.sdk.meetings.utils.logger.ConsoleLogger
import com.amazonaws.services.chime.sdk.meetings.utils.logger.LogLevel
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp

class RNVideoViewManager : SimpleViewManager<DefaultVideoRenderView>() {
    private val logger = ConsoleLogger(LogLevel.DEBUG)

    companion object {
        // The React Native side is expecting a component named RNVideoView
        private const val REACT_CLASS = "RNVideoView"
        private const val TAG = "RNVideoViewManager"
    }

    override fun createViewInstance(reactContext: ThemedReactContext): DefaultVideoRenderView {
        logger.info(TAG, "Creating view instance")
        return DefaultVideoRenderView(reactContext)
    }

    override fun getName(): String {
        return REACT_CLASS
    }

    @ReactProp(name = "tileId")
    fun setTileId(renderView: DefaultVideoRenderView, tileId: Int) {
        logger.info(TAG, "Setting tileId: $tileId")

        NativeMobileSDKBridge.meetingSession?.let {
            it.audioVideo.bindVideoView(renderView, tileId)
        }
    }
}
