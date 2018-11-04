package com.gloomyer.gvideoplayer.interfaces;

/**
 * 缓冲进度回调
 */
public interface GOnBufferingUpdateListener {

    /**
     * 进度回调
     *
     * @param process 取值0-100
     */
    void onBufferUpdate(int process);
}
