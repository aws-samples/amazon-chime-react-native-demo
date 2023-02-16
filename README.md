# Amazon Chime SDK React Native Demo
The Amazon Chime SDK is a set of real-time communications components that developers can use to quickly add audio calling, video calling, and screen sharing capabilities to their own applications. Developers can leverage the same communication infrastructure and services that power Amazon Chime, an online meetings service from AWS, to deliver engaging experiences in their applications. For instance, they can add video calling to a healthcare application so patients can consult remotely with doctors on health issues, or add audio calling to a company website so customers can quickly connect with sales. By using the Amazon Chime SDK, developers can eliminate the cost, complexity, and friction of creating and maintaining their own real-time communication infrastructure and services.

This demo shows how to integrate the [Amazon Chime SDK](https://aws.amazon.com/blogs/business-productivity/amazon-chime-sdks-ios-android/) into your React Native (or RN) application.

For more details about the SDK APIs, please refer to the **Getting Started** guide of the following SDK repositories:
* [amazon-chime-sdk-android](https://github.com/aws/amazon-chime-sdk-android/blob/master/guides/01_Getting_Started.md)
* [amazon-chime-sdk-ios](https://github.com/aws/amazon-chime-sdk-ios/blob/master/guides/01_Getting_Started.md)

> *Note: Deploying the Amazon Chime SDK demo applications contained in this repository will cause your AWS Account to be billed for services, including the Amazon Chime SDK, used by the application.*

# How to Run the React Native Demo Application
## 1. Clone the repository
* Run `git clone` to download the source code
* Run `npm install` inside the folder that was just created

## 2. Add SDK binaries to the project
### Android
The Mobile SDKs for Android could be downloaded from the Maven Central repository, by integrated into your Android project's Gradle files, or you can be directly embedded via .aar files.

#### From Maven
We recommend obtaining the dependency from Maven. To obtain the dependencies from Maven, add the dependencies to `android/app/build.gradle`.

```
dependencies {
    implementation 'software.aws.chimesdk:amazon-chime-sdk-media:$MEDIA_VERSION'
    implementation 'software.aws.chimesdk:amazon-chime-sdk:$SDK_VERSION'
}
```
The version numbers could be obtained from the [release](https://github.com/aws/amazon-chime-sdk-android/releases).

#### From S3
You can also download the Mobile SDKs from S3 following these steps:

* Create a folder named `libs` under `android/app`
* Download the Amazon Chime SDK binaries
    - [amazon-chime-sdk-0.17.10.tar.gz](https://amazon-chime-sdk-android.s3.amazonaws.com/sdk/0.17.10/amazon-chime-sdk-0.17.10.tar.gz)
    - [amazon-chime-sdk-media-0.17.11.tar.gz](https://amazon-chime-sdk-android.s3.amazonaws.com/media/0.17.11/amazon-chime-sdk-media-0.17.11.tar.gz)
* Unzip, copy both `amazon-chime-sdk.aar` and `amazon-chime-sdk-media.aar` into the `android/app/libs` folder

### iOS
#### From CocoaPods
With `pod 'AmazonChimeSDK-Bitcode', '0.22.7'` declared in the `ios/Podfile`, the specified version of `AmazonChimeSDK-Bitcode` pod will be downloaded and integrated when you run `pod install` in the step 5 below.

`AmazonChimeSDK-No-Bitcode` is available in [Cocoapods](https://cocoapods.org/) as well.

The current version of Demo app works with AmazonChimeSDK up to `0.22.7`.

## 3. Deploy the serverless demo
Follow the instructions in [amazon-chime-sdk-js](https://github.com/aws/amazon-chime-sdk-js/tree/master/demos/serverless) to deploy the serverless demo.

## 4. Update the server URLs
Update `SERVER_URL` and `SERVER_REGION` in `src/utils/Api.js` with the server URL and region of the serverless demo you created.

## 5. Build and run
Run `npm start` to start the React Native development server.

### Android
* Connect a physical Android testing device (*we currently do not support x86 architecture/simulators*) to your computer
* Run `npx react-native run-android` which builds and installs the demo onto the testing device

* In order to run on single device run `npx react-native run-android --deviceId=<device-id>`. Device id can be obtained from `adb devices`

### iOS
* Run `pod install` in `ios` folder
* Open `ios/RNDemo.xcworkspace` in Xcode
* Select iOS simulator or a testing device, then build and run the demo application


# How to Integrate the Amazon Chime SDK into Your Existing React Native Application

## 1. Configure your project
Follow the steps below to add the Amazon Chime SDK into your React Native projects for both iOS and Android.

* [amazon-chime-sdk-android](https://github.com/aws/amazon-chime-sdk-android/blob/master/README.md#setup)
* [amazon-chime-sdk-ios](https://github.com/aws/amazon-chime-sdk-ios/blob/master/README.md#setup)

## 2. Request permission
The following camera and microphone permissions need to be granted to enable audio and video functions.

### Android
* `android.permission.CAMERA`
* `android.permission.MODIFY_AUDIO_SETTINGS`
* `android.permission.RECORD_AUDIO`

### iOS
* `NSCameraUsageDescription`
* `NSMicrophoneUsageDescription`


More details on how to request permissions can be found in [Android](https://developer.android.com/training/permissions/requesting) and [iOS](https://developer.apple.com/documentation/avfoundation/cameras_and_media_capture/requesting_authorization_for_media_capture_on_ios?language=objc) official documents.

## 3. Add libraries for SDK

### Android
You'll need following libraries to be able to build the Android application.
From `build.gradle`
```
buildscript {
    ext {
        kotlin_version = '1.3.71'
        minSdkVersion = 21
        compileSdkVersion = 29
        targetSdkVersion = 29
    }
    dependencies {
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
    }
allprojects {
    repositories {
        flatDir {
            dirs 'libs'
        }
    }
}
```

From `app/build.gradle`
```
apply plugin: 'kotlin-android'
apply plugin: 'kotlin-android-extensions'

dependencies {
    implementation(name: 'amazon-chime-sdk', ext: 'aar')
    implementation(name: 'amazon-chime-sdk-media', ext: 'aar')
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.3'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.3'
    implementation 'com.google.code.gson:gson:2.8.6'
}
```

## 4. Connect React Native code to the Amazon Chime SDK

You need [Native Module](https://reactnative.dev/docs/native-modules-ios) to proxy calls to the Amazon Chime SDK. A Native Module exposes native methods as JavaScript functions to be used in the React Native code. And Amazon Chime SDK callbacks are converted to React Native events that are handled by JavaScript listeners.

[Android: Send Events to JavaScript](https://reactnative.dev/docs/native-modules-android#sending-events-to-javascript) and [iOS: Send Events to JavaScript](https://reactnative.dev/docs/native-modules-ios#sending-events-to-javascript) provide good examples on how to pass events from native to React Native. You can also look at the following files in our demo application for reference.
* React Native: [Bridge.js](src/utils/Bridge.js)
* iOS: [NativeMobileSDKBridge.h](ios/RNDemo/NativeMobileSDKBridge.h) and [NativeMobileSDKBridge.m](ios/RNDemo/NativeMobileSDKBridge.m)
* Android: [NativeMobileSDKBridge.kt](android/app/src/main/java/com/amazonaws/services/chime/rndemo/NativeMobileSDKBridge.kt) and  [RNEventEmitter.kt](android/app/src/main/java/com/amazonaws/services/chime/rndemo/RNEventEmitter.kt)

To directly reuse code from this demo, here are the files you likely need and main functionality of each file.

### React Native
* `src/utils/Bridge.js`: Native Module API calls and the event handler singleton.
* `src/components/RNVideoRenderView.js`: Wrapper for the video tile UI component.

### iOS
Add these files to you iOS project through **Xcode**
* `ios/RNDemo/MeetingObserver.h` and `ios/RNDemo/MeetingObserver.m`: Event handlers to pass Amazon Chime SDK events into React Native.
* `ios/RNDemo/NativeMobileSDKBridge.h` and `ios/RNDemo/NativeMobileSDKBridge.m`: Functions that will be available in React Native through Native Module.
* `ios/RNDemo/RNVideoViewManager.h` and `ios/RNDemo/RNVideoViewManager.m`: UI component manager.

### Android
The following files are all under `android/app/src/main/java/com/amazonaws/services/chime/rndemo`.
If you are copy pasting the following files, make sure to adjust the package path and import path accordingly at the top of these files.
* `NativeMobileSDKBridge.kt`:Functions that will be available in React Native through Native Module.
* `RNEventEmitter.kt`: Utility class that provides helper to send events to React Native.
* `MeetingObservers.kt`: Event handlers to pass Amazon Chime SDK events into React Native.
* `RNVideoViewManager.kt`: UI component manager.
* `NativeMobileSDKBridgePackage.kt`: Package definition to register the bridge in the Android application.
* `MainApplication.java`: Because **React Native framework already generates this file for you**, you only need to add the following line in function `protected List<ReactPackage> getPackages()` to register the Native Module.
~~~kotlin
packages.add(new NativeMobileSDKBridgePackage());
~~~

# More Examples in the Demo Application
## How to start a meeting session

1. When the login button is pressed, `startMeeting()` in  `Login.js` is called.
2. After `App.js` completes HTTP request, the meeting response object is passed into `NativeFuntion.startMeeting()`
3. As defined in `Bridge.js`, the function will call the corresponding native platform code, ochastrated by React Bridge.
    - For iOS
    ~~~objective-c
    // NativeMobileSDKBridge.m
    RCT_EXPORT_METHOD(startMeeting:(NSDictionary *)meetingInfoDict attendeeInfo:(NSDictionary *)attendeeInfoDict)
    ~~~
    - For Android
    ~~~kotlin
    // ChimeReactnativeSDKDemoManager.kt
    @ReactMethod
    fun startMeeting(meetingInfo: ReadableMap, attendeeInfo: ReadableMap)
    ~~~
4. In the native code, `startMeeting()` function will do the following.
    1. Construct `MeetingSession`
    2. Bind `AudioVideoObserver` which will listen to the Amazon Chime SDK events
    3. Call `MeetingSession.AudioVideo.start()`
    4. An `onAudioSessionStarted` event will be triggered and handled when the meeting is started
        - For iOS
        ~~~objective-c
        // MeetingObservers.m
        - (void)audioSessionDidStartWithReconnecting:(BOOL)reconnecting
        {
          if (!reconnecting)
          {
            [_logger infoWithMsg:@"Meeting Started!"];
            [_bridge sendEventWithName:kEventOnMeetingStart body:nil];
          }
        }
        ~~~
        - For Android
        ~~~kotlin
        // MeetingObservers.kt
        override fun onAudioSessionStarted(reconnecting: Boolean) {
          logger.info(TAG, "Received event for audio session started. Reconnecting: $reconnecting")

          if (!reconnecting) {
            eventEmitter.sendReactNativeEvent(RNEventEmitter.RN_EVENT_MEETING_START, null)
          }
        }
        ~~~
5. A [ReactNativeEvent](https://reactnative.dev/docs/native-components-ios#events) called `OnMeetingStart` will be dispatched to the React Native side.
6. In `App.js`, there is a handler registered for this event.
~~~javascript
// App.js
this.onMeetingStartSubscription = getSDKEventEmitter().addListener(MobileSDKEvent.OnMeetingStart, () => {
  this.setState({ isInMeeting: true });
});
~~~
7. After state `isInMeeting` is set to be `true`, user will start to see the meeting view.

## About video tile
A customized [Android Native UI Component](https://reactnative.dev/docs/native-components-android) and [iOS Native UI Component](https://reactnative.dev/docs/native-components-ios) is created to render the video tile from the React Native side.

### How to bind/unbind the video tile
We need to find the reference of the native view to bind/unbind the video tile.

#### iOS
This can be done by `[uiManager viewForReactTag:];`. React Native provides a way to find this "tag" by calling `findNodeHandle`. We will pass the tagId as an integer from React Native to Native side.
~~~javascript
// RNVideoRenderView.js
componentDidMount() {
  setTimeout(() => {
    NativeFunction.bindVideoView(findNodeHandle(this), this.props.tileId);
  });
}
~~~
~~~objective-c
RCT_EXPORT_METHOD(bindVideoView:(NSNumber * _Nonnull)viewIdentifier tileId:(NSNumber * _Nonnull)tileId)
{
  dispatch_async(dispatch_get_main_queue(), ^{
    UIView* view = [self.bridge.uiManager viewForReactTag:viewIdentifier];
    [meetingSession.audioVideo bindVideoViewWithVideoView:(DefaultVideoRenderView*)view tileId:[tileId integerValue]];
  });
}
~~~

#### Android
There is no `viewForReactTag` function available in Android. Therefore, we use [View Prop Setter](https://reactnative.dev/docs/native-components-android#3-expose-view-property-setters-using-reactprop-or-reactpropgroup-annotation) to handle the binding.
~~~kotlin
// RNVideoViewManager.kt
@ReactProp(name = "tileId")
fun setTileId(renderView: DefaultVideoRenderView, tileId: Int) {
  logger.info(TAG, "Setting tileId: $tileId")

  ChimeReactNativeSDKDemoManager.meetingSession?.let {
    it.audioVideo.bindVideoView(renderView, tileId)
  }
}
~~~

### Potential race condition
If you want to create a video tile and then bind the video immediately, you may run into a race condition. The `bind` function is called before the native UI component is fully initialized.

#### iOS
To mitigate this race condition, we need to make sure `bind` function is called after UI component is initialized.
~~~javascript
setTimeout(() => {
  NativeFunction.bindVideoView(findNodeHandle(this), this.props.tileId);
});
~~~

#### Android
In Android, we use `setTileId()` to bind the video tile. This will solve the problem because `setTileId()` is called after the UI component is initialized. You can read [Direct Manipulation](https://reactnative.dev/docs/direct-manipulation) for more details.


# Cleanup

If you no longer want to keep the demo active in your AWS account and want to avoid incurring AWS charges, the demo resources can be removed. Delete the two AWS CloudFormation (https://aws.amazon.com/cloudformation/) stacks created in the prerequisites that can be found in the AWS CloudFormation console (https://console.aws.amazon.com/cloudformation/home).


**Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.**
