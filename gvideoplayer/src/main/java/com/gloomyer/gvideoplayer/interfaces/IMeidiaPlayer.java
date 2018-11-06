package com.gloomyer.gvideoplayer.interfaces;

import android.view.Surface;
import android.view.SurfaceHolder;

import com.gloomyer.gvideoplayer.constants.GPlayState;

import java.io.IOException;

/**
 * 播放器接口，用于第三方播放器实现
 */
public interface IMeidiaPlayer {

    float DEFAULT_VOLUME = .5f;

    /**
     * 异步准备
     */
    void prepare();

    /**
     * 开始播放
     */
    void start();

    /**
     * 暂停播放
     */
    void pause();

    /**
     * 停止播放
     */
    void stop();

    /**
     * 设置播放源
     *
     * @param url
     */
    void setDataSource(String url) throws IOException;

    /**
     * 设置缓冲进度回调
     *
     * @param mListener
     */
    void setOnBufferingUpdateListener(GOnBufferingUpdateListener mListener);

    /**
     * 设置准备完成进度回调
     *
     * @param mListener
     */
    void setOnPreparedListener(GOnPreparedListener mListener);

    /**
     * 设置显示View
     *
     * @param sh
     */
    void setDisplay(SurfaceHolder sh);

    /**
     * 设置显示View
     *
     * @param sf
     */
    void setSurface(Surface sf);

    /**
     * 设置状态监听回调
     */
    void setPlayStateChangeListener(GPlayStateChangeListener mListener);


    /**
     * 设置播放完成回调
     *
     * @param mListener
     */
    void setPlayCompletionListener(GPlayCompletionListener mListener);

    /**
     * 获取当前播放状态
     *
     * @return
     */
    GPlayState getPlayState();

    /**
     * 返回视频宽度
     *
     * @return
     */
    int getVideoWidth();

    /**
     * 返回视频高度
     *
     * @return
     */
    int getVideoHeight();

    /**
     * 设置是否循环播放
     *
     * @param isLoop
     */
    void setLoop(boolean isLoop);

    /**
     * 是否循环播放
     *
     * @return
     */
    boolean isLoop();

    /**
     * 获取视频总时长
     *
     * @return
     */
    long getDuration();

    /**
     * 获取视频当前播放位置
     *
     * @return
     */
    long getCurrentPosition();

    /**
     * 设置视频播放回掉监听
     *
     * @param mListener
     */
    void setVideoProgressListener(GVideoProgressListener mListener);

    /**
     * 设置播放进度
     *
     * @param progress
     */
    void setProgress(long progress);

    /**
     * 设置静音
     *
     * @param mute
     */
    void setMute(boolean mute);

    /**
     * 判断是否属于静音状态
     *
     * @return
     */
    boolean isMute();

    /**
     * 获取当前音量
     *
     * @return
     */
    float getVolume();

    /**
     * 设置音量
     *
     * @param volume
     */
    void setVolume(float volume);
}
