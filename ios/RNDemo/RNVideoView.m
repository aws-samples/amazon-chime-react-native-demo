//
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
//

#import "RNVideoView.h"

#import <VideoToolbox/VideoToolbox.h>
#import <CoreVideo/CoreVideo.h>
#import "RNVideoViewManager.h"
@interface RNVideoView()

@property (nonatomic, weak) RNVideoViewManager *manager;

@end

@implementation RNVideoView

// Implement VideoRenderView protocol so Mobile SDK will be able to
// render video for developer
- (void)renderFrameWithFrame:(id _Nullable)frame
{ 
  if (frame == nil)
  {
    return;
  }
  if (CFGetTypeID((CFTypeRef)frame) == CVPixelBufferGetTypeID())
  {
    struct CGImage *imageBuffer = nil;
    VTCreateCGImageFromCVPixelBuffer((__bridge CVPixelBufferRef)frame, nil, &imageBuffer);
    self.image = [[UIImage alloc] initWithCGImage: imageBuffer];
    CGImageRelease(imageBuffer);
  }
}
@end
