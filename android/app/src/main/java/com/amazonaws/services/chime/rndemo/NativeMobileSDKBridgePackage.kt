/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */

package com.amazonaws.services.chime.rndemo

import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager
import java.util.Collections.singletonList

import kotlin.collections.ArrayList

class NativeMobileSDKBridgePackage : ReactPackage {
    override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> {
        val eventEmitter = RNEventEmitter(reactContext)
        val meetingObservers = MeetingObservers(eventEmitter)

        val modules = ArrayList<NativeModule>()
        modules.add(NativeMobileSDKBridge(reactContext, eventEmitter, meetingObservers))
        return modules
    }

    override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
        return singletonList(RNVideoViewManager())
    }
}
