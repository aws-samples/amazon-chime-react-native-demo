/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */

import React, { useCallback, useEffect, useRef, useState } from 'react';
import { Text, View, Button } from 'react-native';
import styles from '../Style';
import { RNVideoRenderView } from './RNVideoRenderView';

const remoteVideoTileCountPerPage = 2;

export const VideoTiles = ({ remoteVideoTileIds }) => {
  const currentRemoteVideoPageIndexRef = useRef(0);
  const [remoteVideoTileIdsOnCurrentPage, setRemoteVideoTileIdsOnCurrentPage] = useState([]);

  const refreshVideoUI = useCallback(() => {
    const remoteVideoStartIndex = currentRemoteVideoPageIndexRef.current * remoteVideoTileCountPerPage;
    const remoteVideoEndIndex = Math.min(
      remoteVideoTileIds.length,
      remoteVideoStartIndex + remoteVideoTileCountPerPage
    );

    if (remoteVideoEndIndex < remoteVideoStartIndex) {
      setRemoteVideoTileIdsOnCurrentPage([]);
    } else {
      setRemoteVideoTileIdsOnCurrentPage(remoteVideoTileIds.slice(remoteVideoStartIndex, remoteVideoEndIndex));
    }
  }, [setRemoteVideoTileIdsOnCurrentPage, remoteVideoTileIds, currentRemoteVideoPageIndexRef]);

  useEffect(() => {
    const totalPages = Math.ceil(remoteVideoTileIds.length / remoteVideoTileCountPerPage);
    if (totalPages > 0 && currentRemoteVideoPageIndexRef.current > totalPages - 1) {
      currentRemoteVideoPageIndexRef.current = totalPages - 1;
    }

    refreshVideoUI();
  }, [remoteVideoTileIds, refreshVideoUI, currentRemoteVideoPageIndexRef]);

  const onPressPrev = useCallback(() => {
    if (currentRemoteVideoPageIndexRef.current > 0) {
      currentRemoteVideoPageIndexRef.current -= 1;
      refreshVideoUI();
    }
  }, [refreshVideoUI, currentRemoteVideoPageIndexRef]);

  const onPressNext = useCallback(() => {
    if (
      currentRemoteVideoPageIndexRef.current + 1 <
      Math.ceil(remoteVideoTileIds.length / remoteVideoTileCountPerPage)
    ) {
      currentRemoteVideoPageIndexRef.current += 1;
      refreshVideoUI();
    }
  }, [refreshVideoUI, currentRemoteVideoPageIndexRef, remoteVideoTileIds, remoteVideoTileCountPerPage]);

  return (
    <View style={styles.videoContainer}>
      <View style={styles.videos}>
        {remoteVideoTileIdsOnCurrentPage.map((tileId) => (
          <RNVideoRenderView style={styles.video} key={tileId} tileId={tileId} />
        ))}
      </View>
      <View style={styles.videoButtons}>
        <Text>{`Page: ${(currentRemoteVideoPageIndexRef.current + 1)}`}</Text>
        <Button title="Prev" onPress={onPressPrev} />
        <Button title="Next" onPress={onPressNext} />
      </View>
    </View>
  );
};
