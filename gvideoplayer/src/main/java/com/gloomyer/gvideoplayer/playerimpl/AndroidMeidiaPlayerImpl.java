package com.gloomyer.gvideoplayer.playerimpl;

import android.media.MediaPlayer;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.gloomyer.gvideoplayer.constants.GPlayState;
import com.gloomyer.gvideoplayer.interfaces.GOnBufferingUpdateListener;
import com.gloomyer.gvideoplayer.interfaces.GOnPreparedListener;
import com.gloomyer.gvideoplayer.interfaces.GPlayCompletionListener;
import com.gloomyer.gvideoplayer.interfaces.GPlayStateChangeListener;
import com.gloomyer.gvideoplayer.interfaces.IMeidiaPlayer;

import java.io.IOException;

/**
 * android 系统自带播放器实现类
 */
public class AndroidMeidiaPlayerImpl implements IMeidiaPlayer, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private MediaPlayer mMediaPlayer;
    private GOnBufferingUpdateListener mOnBufferingUpdateListener;
    private GOnPreparedListener mOnPreparedListener;
    private GPlayStateChangeListener mPlayStateChangeListener;
    private GPlayCompletionListener mPlayCompletionListener;
    private GPlayState mPlayState;

    public AndroidMeidiaPlayerImpl() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mPlayState = GPlayState.Idle;
    }

    @Override
    public void prepare() {
        setPlayState(GPlayState.Prepareing);
        mMediaPlayer.prepareAsync();
    }

    @Override
    public void start() {
        mMediaPlayer.start();
        setPlayState(GPlayState.Playing);
    }

    @Override
    public void pause() {
        mMediaPlayer.pause();
        setPlayState(GPlayState.Pause);
    }

    @Override
    public void stop() {
        mMediaPlayer.stop();
        setPlayState(GPlayState.Stop);
    }

    @Override
    public void setDataSource(String url) throws IOException {
        mMediaPlayer.setDataSource(url);
    }

    @Override
    public void setOnBufferingUpdateListener(GOnBufferingUpdateListener mListener) {
        this.mOnBufferingUpdateListener = mListener;

    }

    @Override
    public void setOnPreparedListener(GOnPreparedListener mListener) {
        this.mOnPreparedListener = mListener;
    }

    @Override
    public void setDisplay(SurfaceHolder sh) {
        mMediaPlayer.setDisplay(sh);
    }

    @Override
    public void setSurface(Surface sf) {
        mMediaPlayer.setSurface(sf);
    }

    @Override
    public void setPlayStateChangeListener(GPlayStateChangeListener mListener) {
        this.mPlayStateChangeListener = mListener;
    }

    @Override
    public void setPlayCompletionListener(GPlayCompletionListener mListener) {
        this.mPlayCompletionListener = mListener;
    }

    @Override
    public GPlayState getPlayState() {
        return mPlayState;
    }

    @Override
    public int getVideoWidth() {
        return mMediaPlayer.getVideoWidth();
    }

    @Override
    public int getVideoHeight() {
        return mMediaPlayer.getVideoHeight();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
        if (mOnBufferingUpdateListener != null)
            mOnBufferingUpdateListener.onBufferUpdate(i);
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        setPlayState(GPlayState.PrepareFinish);
        if (mOnPreparedListener != null)
            mOnPreparedListener.onPreparedFinish();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        setPlayState(GPlayState.Stop);
        if (mPlayCompletionListener != null) {
            mPlayCompletionListener.completion();
        }
    }

    /**
     * 设置播放状态
     *
     * @param state
     */
    private void setPlayState(GPlayState state) {
        this.mPlayState = state;
        if (mPlayStateChangeListener != null)
            mPlayStateChangeListener.onPlayStateChange(state);
    }
}
