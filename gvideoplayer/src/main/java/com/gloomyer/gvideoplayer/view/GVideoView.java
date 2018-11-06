package com.gloomyer.gvideoplayer.view;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.gloomyer.gvideoplayer.constants.GEventMsg;
import com.gloomyer.gvideoplayer.constants.GPlayState;
import com.gloomyer.gvideoplayer.constants.GPlayViewUIState;
import com.gloomyer.gvideoplayer.interfaces.GListener;
import com.gloomyer.gvideoplayer.interfaces.GOnPlayStateChangeListener;
import com.gloomyer.gvideoplayer.interfaces.GOnPreparedListener;
import com.gloomyer.gvideoplayer.interfaces.GPlayStateChangeListener;
import com.gloomyer.gvideoplayer.interfaces.GVideoProgressListener;
import com.gloomyer.gvideoplayer.interfaces.GVideoScaleListener;
import com.gloomyer.gvideoplayer.interfaces.IMeidiaPlayer;
import com.gloomyer.gvideoplayer.playerimpl.AndroidMeidiaPlayerImpl;
import com.gloomyer.gvideoplayer.utils.GListenerManager;
import com.gloomyer.gvideoplayer.utils.GPlayRecyclerViewAutoPlayHelper;
import com.gloomyer.gvideoplayer.utils.GPlayUtils;

import java.io.IOException;

/**
 * 主要视频展示View
 * 默认认为在list中
 */
public class GVideoView extends FrameLayout implements TextureView.SurfaceTextureListener, GListener {

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
    private GVideoScaleListener mVideoScaleListener;
    private String videoUrl;
    private boolean waitSetDateSource;

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
        mControllerView.setPlayView(this);
        setUIState(GPlayViewUIState.LIST_ITEM);
        mTextureView.setSurfaceTextureListener(this);
        waitSetDateSource = false;
        GListenerManager.get().register(this);
    }

    /**
     * 设置播放器，如果为空将使用默认的AndroidMediaPlayerImpl
     *
     * @param mMeidiaPlayer 可以为空
     */
    public void createPlayer(IMeidiaPlayer mMeidiaPlayer) {
        if (mMeidiaPlayer == null) {
            mMeidiaPlayer = new AndroidMeidiaPlayerImpl(getContext());
        }
        this.mMeidiaPlayer = mMeidiaPlayer;
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public void setTitle(CharSequence title) {
        mControllerView.setTitle(title);
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
        mControllerView.setCover(cover);
    }

    /**
     * 获取视频封面加载imageview
     *
     * @return
     */
    public ImageView getCoverIv() {
        return mControllerView.getCoverIv();
    }

    /**
     * 设置视频播放地址
     *
     * @param videoUrl
     */
    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
        stop();
        if (waitSetDateSource) {
            initMiediaPlayer();
        }
    }

    /**
     * 开始播放
     */
    public void start() {
        GEventMsg msg = new GEventMsg();
        msg.what = GEventMsg.WHAT_STOP_PLAY;
        msg.obj = this;
        GListenerManager.get().sendEvent(msg);
        if (GPlayRecyclerViewAutoPlayHelper.get().isBand()) {
            GPlayRecyclerViewAutoPlayHelper.get().setLastPlayer(this);
        }
        if (getPlayState() == GPlayState.Idle) {
            if (!TextUtils.isEmpty(videoUrl)) {
                initMiediaPlayer();
            } else {
                waitSetDateSource = true;
            }
        } else if (getPlayState() == GPlayState.Pause) {
            mMeidiaPlayer.start();
        } else if (getPlayState() == GPlayState.Stop) {
            stop();
            if (!TextUtils.isEmpty(videoUrl)) {
                initMiediaPlayer();
            } else {
                waitSetDateSource = true;
            }
        } else if (isPlaying()
                && mPlayUIState == GPlayViewUIState.LIST_ITEM) {
            entryFullScreen();
        } else if (isPlaying()
                && mPlayUIState == GPlayViewUIState.FULL_SCREEN) {
            entryFullHorzontal();
        }
    }


    /**
     * 停止播放
     */
    public void stop() {
        mControllerView.normal();
        if (mMeidiaPlayer != null)
            mMeidiaPlayer.stop();
        mMeidiaPlayer = null;
    }

    /**
     * 暂停播放
     */
    public void pause() {
        mMeidiaPlayer.pause();
        mControllerView.post(new Runnable() {
            @Override
            public void run() {
                mControllerView.pause();
            }
        });
    }

    /**
     * 返回列表模式
     */
    public void exitFullScreen() {
        ViewGroup contentView = GPlayUtils.scanForActivity(getContext())
                .findViewById(android.R.id.content);
        contentView.removeView(mContainer);

        GPlayUtils.showActionBar(getContext());

        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mContainer, params);
        mPlayUIState = GPlayViewUIState.LIST_ITEM;
        mControllerView.mini();
        mControllerView.setUIState(GPlayViewUIState.LIST_ITEM);
        mMeidiaPlayer.setMute(true);
        setBrightness(.5f);
    }

    /**
     * 设置Activity 亮度
     *
     * @param brightness
     */
    public void setBrightness(float brightness) {
        WindowManager.LayoutParams lpa = ((Activity) getContext()).getWindow().getAttributes();
        lpa.screenBrightness = brightness;
        if (lpa.screenBrightness > 1.0f) {
            lpa.screenBrightness = 1.0f;
        } else if (lpa.screenBrightness < 0.01f) {
            lpa.screenBrightness = 0.01f;
        }
        ((Activity) getContext()).getWindow().setAttributes(lpa);
    }

    /**
     * 进入垂直全屏模式
     */
    public void entryFullScreen() {
        removeView(mContainer);
        GPlayUtils.hideActionBar(getContext());
        ViewGroup contentView = GPlayUtils.scanForActivity(getContext())
                .findViewById(android.R.id.content);
        contentView.setBackgroundColor(Color.BLACK);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        contentView.addView(mContainer, params);
        mPlayUIState = GPlayViewUIState.FULL_SCREEN;
        mControllerView.setUIState(GPlayViewUIState.FULL_SCREEN);
        mControllerView.operation();
        mMeidiaPlayer.setMute(false);
    }

    /**
     * 进入横屏全屏模式
     */
    public void entryFullHorzontal() {
        //GPlayUtils.hideActionBar(getContext());
        GPlayUtils.scanForActivity(getContext())
                .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mControllerView.setUIState(GPlayViewUIState.FULL_HORIZONTAL);
        mPlayUIState = GPlayViewUIState.FULL_HORIZONTAL;
        // post 两层 第一层获取的不对
        mContainer.post(new Runnable() {
            @Override
            public void run() {
                mContainer.post(new Runnable() {
                    @Override
                    public void run() {
                        setVideoScale();
                    }
                });
            }
        });
    }

    /**
     * 进入横屏全屏模式
     */
    public void exitFullHorzontal() {
        mPlayUIState = GPlayViewUIState.FULL_SCREEN;
        GPlayUtils.scanForActivity(getContext())
                .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mControllerView.setUIState(GPlayViewUIState.FULL_SCREEN);
        mContainer.post(new Runnable() {
            @Override
            public void run() {
                mContainer.post(new Runnable() {
                    @Override
                    public void run() {
                        setVideoScale();
                    }
                });
            }
        });
    }

    /**
     * 初始化创建播放器
     */
    private void initMiediaPlayer() {
        waitSetDateSource = false;
        if (mMeidiaPlayer == null) {
            createPlayer(null);
        }
        try {
            mMeidiaPlayer.setPlayStateChangeListener(new GPlayStateChangeListener() {
                @Override
                public void onPlayStateChange(GPlayState state) {
                    if (mOnPlayStateChangeListener != null) {
                        mOnPlayStateChangeListener.onChanged(state);
                    }
                    if (state == GPlayState.Prepareing) {
                        mControllerView.prepare();
                    } else {
                        mControllerView.mini();
                    }
                }
            });
            mMeidiaPlayer.setOnPreparedListener(new GOnPreparedListener() {
                @Override
                public void onPreparedFinish() {
                    mMeidiaPlayer.setMute(true);
                    mControllerView.setVideDuration(mMeidiaPlayer.getDuration());
                    mMeidiaPlayer.setVideoProgressListener(new GVideoProgressListener() {
                        @Override
                        public void onProgress(long progress, long duration) {
                            mControllerView.setVideoProgress(progress);
                        }
                    });
                    if (getPlayState() == GPlayState.Pause) return;//停止了不继续播放了
                    mMeidiaPlayer.start();
                    setVideoScale();
                }
            });
            mMeidiaPlayer.setDataSource(videoUrl);
            if (mTextureView.getSurfaceTexture() != null)
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
        if (mMeidiaPlayer != null) {
            mMeidiaPlayer.setSurface(new Surface(surface));
            setVideoScale();
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

    /**
     * 设置视频缩放规则
     *
     * @param mListener
     */
    public void setVideoScaleListener(GVideoScaleListener mListener) {
        this.mVideoScaleListener = mListener;
    }

    /**
     * 设置视频缩放规则
     */
    private void setVideoScale() {
        float mViewHeight = mContainer.getHeight();
        float mViewWidth = mContainer.getWidth();
        float mVideoWidth = mMeidiaPlayer == null ? 0 : mMeidiaPlayer.getVideoWidth();
        float mVideoHeight = mMeidiaPlayer == null ? 0 : mMeidiaPlayer.getVideoHeight();
        Matrix matrix = null;
        if (mVideoScaleListener != null) {
            matrix = mVideoScaleListener.getMatrixRules(mPlayUIState
                    , mViewHeight, mVideoWidth
                    , mVideoHeight, mVideoWidth);
        }

        if (matrix == null) {
            //使用默认规则
            matrix = new Matrix();

            float sx = mViewWidth / mVideoWidth;
            float sy = mViewHeight / mVideoHeight;
            if (mPlayUIState == GPlayViewUIState.LIST_ITEM) {
                float maxScale = Math.max(sx, sy);
                //第1步:把视频区移动到View区,使两者中心点重合.
                matrix.preTranslate((mViewWidth - mVideoWidth) / 2, (mViewHeight - mVideoHeight) / 2);
                //第2步:因为默认视频是fitXY的形式显示的,所以首先要缩放还原回来.
                matrix.preScale(mVideoWidth / mViewWidth, mVideoHeight / mViewHeight);
                //第3步,等比例放大或缩小,直到视频区的一边超过View一边, 另一边与View的另一边相等. 因为超过的部分超出了View的范围,所以是不会显示的,相当于裁剪了.
                matrix.postScale(maxScale, maxScale, mViewWidth / 2, mViewHeight / 2);//后两个参数坐标是以整个View的坐标系以参考的
            } else {
                //第1步:把视频区移动到View区,使两者中心点重合.
                matrix.preTranslate((mViewWidth - mVideoWidth) / 2, (mViewHeight - mVideoHeight) / 2);
                //第2步:因为默认视频是fitXY的形式显示的,所以首先要缩放还原回来.
                matrix.preScale(mVideoWidth / mViewWidth, mVideoHeight / mViewHeight);
                //第3步,等比例放大或缩小,直到视频区的一边和View一边相等.如果另一边和view的一边不相等，则留下空隙
                if (sx >= sy) {
                    matrix.postScale(sy, sy, mViewWidth / 2, mViewHeight / 2);
                } else {
                    matrix.postScale(sx, sx, mViewWidth / 2, mViewHeight / 2);
                }
            }
        }

        mTextureView.setTransform(matrix);
        mTextureView.postInvalidate();
    }

    /**
     * 设置视频播放进度
     *
     * @param progress
     */
    public void setProgress(int progress) {
        mMeidiaPlayer.setProgress(progress);
    }

    @Override
    public void onEvent(GEventMsg msg) {
        if (msg.what == GEventMsg.WHAT_STOP_PLAY) {
            if (msg.obj != this) {
                if (mMeidiaPlayer != null
                        && isPlaying()) {
                    pause();
                }
            }
        }
    }

    /**
     * 获取当前音量
     *
     * @return
     */
    public float getVolume() {
        return mMeidiaPlayer == null ? IMeidiaPlayer.DEFAULT_VOLUME : mMeidiaPlayer.getVolume();
    }

    /**
     * 设置视频播放音量
     *
     * @param volume
     */
    public void setVolume(float volume) {
        if (mMeidiaPlayer != null) {
            mMeidiaPlayer.setVolume(volume);
        }
    }

    /**
     * 获取当前屏幕亮度
     *
     * @return
     */
    public float getCurrentBrightness() {
        float value = ((Activity) getContext()).getWindow().getAttributes().screenBrightness;
        if (value == -1f) value = .5f;
        return value;
    }
}
