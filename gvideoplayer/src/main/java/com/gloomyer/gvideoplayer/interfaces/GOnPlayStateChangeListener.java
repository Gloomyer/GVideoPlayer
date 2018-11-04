package com.gloomyer.gvideoplayer.interfaces;

import com.gloomyer.gvideoplayer.constants.GPlayState;

/**
 * 播放器状态改变回调
 */
public interface GOnPlayStateChangeListener {
    void onChanged(GPlayState state);
}
