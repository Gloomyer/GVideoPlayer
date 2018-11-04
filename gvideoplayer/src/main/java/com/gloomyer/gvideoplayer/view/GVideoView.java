package com.gloomyer.gvideoplayer.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.gloomyer.gvideoplayer.constants.GPlayState;
import com.gloomyer.gvideoplayer.constants.GPlayViewUIState;
import com.gloomyer.gvideoplayer.interfaces.GOnPlayStateChangeListener;
import com.gloomyer.gvideoplayer.interfaces.GOnPreparedListener;
import com.gloomyer.gvideoplayer.interfaces.GPlayStateChangeListener;
import com.gloomyer.gvideoplayer.interfaces.IMeidiaPlayer;
import com.gloomyer.gvideoplayer.playerimpl.AndroidMeidiaPlayerImpl;
import com.gloomyer.gvideoplayer.utils.GPlayUtils;

import java.io.IOException;

/**
 * 主要视频展示View
 * 默认认为在list中
 */
public class GVideoView extends FrameLayout implements TextureView.SurfaceTextureListener {

    /**
     * 主要容器
     */
    private FrameLayout mContainer;
    private GOnPlayStateChangeListener mOnPlayStateChangeListener;
    private IMeidiaPlayer mMeidiaPlayer; //播放器
    private String mCover;//预览图地址,暂不支持非网络图片
    private int playErrorImg;
    private TextureView mTextureView; //视频渲染View
    private GPlayViewUIState mPlayUIState; //当前播放器的UI状态
    private GVideoControllerView mControllerView;
    private String videoUrl;

    public GVideoView(Context context) {
        this(context, null);
    }

    public GVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContainer = new FrameLayout(context);
        mContainer.setBackgroundColor(Color.BLACK);
        addView(mContainer, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        mTextureView = new TextureView(context);
        mControllerView = new GVideoControllerView(context);
        setUIState(GPlayViewUIState.LIST_ITEM);

        mTextureView.setSurfaceTextureListener(this);
    }

    /**
     * 设置播放器，如果为空将使用默认的AndroidMediaPlayerImpl
     *
     * @param mMeidiaPlayer 可以为空
     */
    public void createPlayer(IMeidiaPlayer mMeidiaPlayer) {
        if (mMeidiaPlayer == null) {
            mMeidiaPlayer = new AndroidMeidiaPlayerImpl();
        }
        this.mMeidiaPlayer = mMeidiaPlayer;
    }

    /**
     * 设置播放器状态回调
     *
     * @param mListener
     */
    public void setOnPlayStateChangeListener(GOnPlayStateChangeListener mListener) {
        this.mOnPlayStateChangeListener = mListener;
    }

    /**
     * 设置预览图
     */
    public void setCover(String cover) {
        this.mCover = cover;
    }

    /**
     * 设置播放失败图片，暂时只支持资源文件
     *
     * @param resId
     */
    public void setPlayErrorImg(int resId) {
        this.playErrorImg = resId;
    }


    /**
     * 设置视频播放地址
     *
     * @param videoUrl
     */
    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    /**
     * 开始播放
     */
    public void start() {
        if (getPlayState() == GPlayState.Idle) {
            initMiediaPlayer();
        } else if (isPlaying()) {
            entryFullScreen();
        }
    }

    /**
     * 进入垂直全屏模式
     */
    private void entryFullScreen() {
        removeView(mContainer);
        GPlayUtils.hideActionBar(getContext());
        ViewGroup contentView = GPlayUtils.scanForActivity(getContext())
                .findViewById(android.R.id.content);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        FrameLayout fl = new FrameLayout(getContext());
        fl.setBackgroundColor(Color.BLACK);
        fl.addView(mContainer);
        contentView.addView(fl, params);
        mPlayUIState = GPlayViewUIState.FULL_SCREEN;
        mControllerView.setUIState(getPlayState(), mPlayUIState);
    }

    /**
     * 初始化创建播放器
     */
    private void initMiediaPlayer() {
        if (mMeidiaPlayer == null) {
            createPlayer(null);
        }
        try {
            mMeidiaPlayer.setPlayStateChangeListener(new GPlayStateChangeListener() {
                @Override
                public void onPlayStateChange(GPlayState state) {
                    mControllerView.setUIState(state, mPlayUIState);
                }
            });
            mMeidiaPlayer.setOnPreparedListener(new GOnPreparedListener() {
                @Override
                public void onPreparedFinish() {
                    mMeidiaPlayer.start();
                }
            });
            mMeidiaPlayer.setDataSource(videoUrl);
            mMeidiaPlayer.setSurface(new Surface(mTextureView.getSurfaceTexture()));
            mMeidiaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置播放UI
     *
     * @param state
     */
    private void setUIState(GPlayViewUIState state) {
        this.mPlayUIState = state;
        mContainer.removeAllViews();
        if (state == GPlayViewUIState.LIST_ITEM) {
            mContainer.addView(mTextureView, new LayoutParams(LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            mContainer.addView(mControllerView, new LayoutParams(LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }


    /**
     * 当前是否正处于播放中的状态
     *
     * @return
     */
    public boolean isPlaying() {
        return mMeidiaPlayer != null && mMeidiaPlayer.getPlayState() == GPlayState.Playing;
    }

    /**
     * 获取当前播放器的播放状态
     *
     * @return
     */
    public GPlayState getPlayState() {
        return mMeidiaPlayer == null ? GPlayState.Idle : mMeidiaPlayer.getPlayState();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (isPlaying()) {
            mMeidiaPlayer.setSurface(new Surface(surface));
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
