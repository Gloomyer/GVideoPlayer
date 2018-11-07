package com.gloomyer.gvideoplayer.interfaces;

/**
 * 当视频播放发生错误
 */
public interface GOnErrorListener {
    /**
     * 错误回掉 参数如果为0 说明这个参数不生效
     *
     * @param arg1
     * @param arg2
     * @param arg3
     */
    void onError(int arg1, int arg2, int arg3);
}
