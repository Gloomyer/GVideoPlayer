package com.gloomyer.gvideoplayer.playerimpl;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.gloomyer.gvideoplayer.constants.GPlayState;
import com.gloomyer.gvideoplayer.interfaces.GOnBufferingUpdateListener;
import com.gloomyer.gvideoplayer.interfaces.GOnErrorListener;
import com.gloomyer.gvideoplayer.interfaces.GOnPreparedListener;
import com.gloomyer.gvideoplayer.interfaces.GPlayCompletionListener;
import com.gloomyer.gvideoplayer.interfaces.GPlayStateChangeListener;
import com.gloomyer.gvideoplayer.interfaces.GVideoProgressListener;
import com.gloomyer.gvideoplayer.interfaces.IMeidiaPlayer;

import java.io.IOException;

/**
 * android 系统自带播放器实现类
 */
public class AndroidMeidiaPlayerImpl implements IMeidiaPlayer,
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        Runnable,
        MediaPlayer.OnErrorListener {

    private static float mVolume = IMeidiaPlayer.DEFAULT_VOLUME;//音量大小 默认100
    private MediaPlayer mMediaPlayer;
    private GOnBufferingUpdateListener mOnBufferingUpdateListener;
    private GOnPreparedListener mOnPreparedListener;
    private GPlayStateChangeListener mPlayStateChangeListener;
    private GPlayCompletionListener mPlayCompletionListener;
    private GPlayState mPlayState;
    private GVideoProgressListener mVideoProgressListener;
    private boolean progressThreadIsRun; //视频播放进度线程开启状态
    private long lastProgress = 0; //记录上次视频播放的值，用于合理时间退出线程
    private Handler mHandler;
    private boolean mute;
    private AudioManager mAudioManager;
    private GOnErrorListener mOnErrorListener;

    public AndroidMeidiaPlayerImpl(Context context) {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.setOnErrorListener(this);
        mPlayState = GPlayState.Idle;
        initAudioManager(context);
    }

    private void initAudioManager(Context context) {
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            mAudioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
        }
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
        mMediaPlayer.setDisplay(null);
        mMediaPlayer.stop();
        mOnBufferingUpdateListener = null;
        mOnErrorListener = null;
        mOnPreparedListener = null;
        mMediaPlayer = null;
        mAudioManager = null;
        mPlayCompletionListener = null;
        mPlayStateChangeListener = null;
        mVideoProgressListener = null;
        progressThreadIsRun = false;
        mHandler = null;
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
    public void setLoop(boolean isLoop) {
        mMediaPlayer.setLooping(isLoop);
    }

    @Override
    public boolean isLoop() {
        return mMediaPlayer.isLooping();
    }

    @Override
    public long getDuration() {
        return mMediaPlayer.getDuration();
    }

    @Override
    public long getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    @Override
    public void setVideoProgressListener(GVideoProgressListener mListener) {
        this.mVideoProgressListener = mListener;
        //开启视频进度监听回掉线程
        if (!progressThreadIsRun) {
            if (mHandler == null)
                mHandler = new Handler();
            progressThreadIsRun = true;
            new Thread(this).start();
        }
    }

    @Override
    public void setProgress(long progress) {
        try {
            mMediaPlayer.seekTo((int) progress);
        } catch (Exception e) {
            mMediaPlayer.seekTo(Integer.MAX_VALUE);
        }
    }

    @Override
    public void setMute(boolean mute) {
        if (mute) {
            mMediaPlayer.setVolume(0, 0);
        } else {
            mMediaPlayer.setVolume(getVolume(), getVolume());
        }
        this.mute = mute;
    }

    @Override
    public float getVolume() {
        return mVolume;
    }

    @Override
    public void setVolume(float volume) {
        mVolume = volume;
        mMediaPlayer.setVolume(volume, volume);
    }

    @Override
    public void setOnErrorListener(GOnErrorListener mListener) {
        this.mOnErrorListener = mListener;
    }

    @Override
    public boolean isMute() {
        return mute;
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

    @Override
    public void run() {
        while (progressThreadIsRun) {
            if (mMediaPlayer.getCurrentPosition() != lastProgress
                    && getPlayState() != GPlayState.Idle)
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mVideoProgressListener != null)
                            mVideoProgressListener.onProgress(
                                    mMediaPlayer.getCurrentPosition(),
                                    mMediaPlayer.getDuration());
                    }
                });
            lastProgress = mMediaPlayer.getCurrentPosition();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        progressThreadIsRun = false;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        if (mOnErrorListener != null) {
            mOnErrorListener.onError(what, extra, 0);
        }
        setPlayState(GPlayState.Error);
        return false;
    }
}
