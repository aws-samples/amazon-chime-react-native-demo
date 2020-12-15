//
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
//

#import "RNVideoViewManager.h"

@implementation RNVideoViewManager
static NSMutableDictionary<NSString*, NSValue*> *videoMap;

RCT_EXPORT_MODULE(RNVideoView);

- (UIView *)view
{
  DefaultVideoRenderView *innerView = [[DefaultVideoRenderView alloc] init];
  innerView.contentMode = UIViewContentModeScaleAspectFit;
  return innerView;
}

@end

