package com.gloomyer.gvideoplayer.interfaces;

import com.gloomyer.gvideoplayer.constants.GPlayState;

/**
 * 视频播放状态变化监听器
 */
public interface GPlayStateChangeListener {
    void onPlayStateChange(GPlayState state);
}
