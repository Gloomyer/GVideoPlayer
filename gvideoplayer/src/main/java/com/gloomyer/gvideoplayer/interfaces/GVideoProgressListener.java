package com.gloomyer.gvideoplayer.interfaces;

/**
 * 视频播放进度监听
 */
public interface GVideoProgressListener {
    /**
     * @param progress 当前进度
     * @param duration 总时长
     */
    void onProgress(long progress, long duration);
}
