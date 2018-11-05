package com.gloomyer.gvideoplayer.interfaces;

import com.gloomyer.gvideoplayer.constants.GEventMsg;

/**
 * 监听者模式
 */
public interface GListener {
    void onEvent(GEventMsg msg);
}
