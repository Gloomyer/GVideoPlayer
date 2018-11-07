package com.gloomyer.gvideoplayer.view;

import android.content.Context;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gloomyer.gvideoplayer.R;
import com.gloomyer.gvideoplayer.constants.GPlayViewUIState;
import com.gloomyer.gvideoplayer.utils.GPlayUtils;

/**
 * 视频控制展示View
 */
public class GVideoControllerView extends FrameLayout implements GestureDetector.OnGestureListener {

    private SeekBar sbVideo;
    private ProgressBar pbVideo;
    private ProgressBar pbolume;
    private ImageView ivStart;
    private ImageView ivPause;
    private LinearLayout llLoading;
    private LinearLayout llTop;
    private LinearLayout llVolume;
    private RelativeLayout rlVideoBrightness;
    private RelativeLayout rlVideoPosition;
    private ImageView ivClose;
    private ImageView ivBack;
    private ImageView ivFull;
    private TextView tvTotalTime;
    private TextView tvVideoBrightness;
    private TextView tvProgressTime;
    private TextView tvTitle;
    private TextView tvDuration;
    private TextView tvCurrent;
    private ImageView ivCover;
    private long videoProgress;
    private long duration;
    private GVideoView videoView;
    private GPlayViewUIState uiState;
    private GestureDetector mGestureDetector;
    private RectF sbRect;
    private String cover;
    private Handler mHandler;
    private boolean isScroll;
    private float scrollX;
    private float scrollY;
    private RectF leftScreenRect;
    private long setVideoCurrent;

    public GVideoControllerView(Context context) {
        this(context, null);
    }

    public GVideoControllerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GVideoControllerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mHandler = new Handler(context.getMainLooper());
        LayoutInflater.from(context).inflate(R.layout.gvideo_controller_view_layout, this, true);
        ivCover = findViewById(R.id.iv_cover);
        sbVideo = findViewById(R.id.sb_video);
        pbVideo = findViewById(R.id.pb_video);
        ivStart = findViewById(R.id.iv_start);
        ivPause = findViewById(R.id.iv_pause);
        llLoading = findViewById(R.id.ll_loading);
        llTop = findViewById(R.id.ll_top);
        ivClose = findViewById(R.id.iv_close);
        ivBack = findViewById(R.id.iv_back);
        tvTotalTime = findViewById(R.id.tv_total_time);
        tvProgressTime = findViewById(R.id.tv_progress_time);
        tvTitle = findViewById(R.id.tv_title);
        ivFull = findViewById(R.id.iv_full);
        llVolume = findViewById(R.id.ll_volume);
        pbolume = findViewById(R.id.pb_volume);
        rlVideoBrightness = findViewById(R.id.rl_video_brightness);
        tvVideoBrightness = findViewById(R.id.tv_video_brightness);
        rlVideoPosition = findViewById(R.id.rl_video_position);
        tvCurrent = findViewById(R.id.tv_current);
        tvDuration = findViewById(R.id.tv_duration);

        llVolume.setVisibility(GONE);
        rlVideoBrightness.setVisibility(GONE);
        rlVideoPosition.setVisibility(GONE);
        ivClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //退出全屏模式
                videoView.exitFullScreen();
            }
        });
        ivBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.exitFullHorzontal();
                operation();
            }
        });
        sbVideo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                videoView.setProgress(progress);
            }
        });
        ivFull.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uiState == GPlayViewUIState.FULL_SCREEN) {
                    videoView.entryFullHorzontal();
                } else {
                    videoView.exitFullHorzontal();
                }
                operation();
            }
        });
        ivPause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.pause();
                operation();
            }
        });
        uiState = GPlayViewUIState.LIST_ITEM;
        mGestureDetector = new GestureDetector(context, this);
        normal();
    }


    /**
     * 设置UI状态
     *
     * @param state
     */
    public void setUIState(GPlayViewUIState state) {
        uiState = state;
    }


    /**
     * 设置视频总时长
     *
     * @param duration
     */
    public void setVideDuration(long duration) {
        this.duration = duration;
        tvTotalTime.setText(GPlayUtils.videoTime2Value(duration));
        try {
            pbVideo.setMax((int) duration);
            sbVideo.setMax((int) duration);
        } catch (Exception e) {
            pbVideo.setMax(Integer.MAX_VALUE);
            sbVideo.setMax(Integer.MAX_VALUE);
        }
    }

    /**
     * 设置视频播放View
     *
     * @param playView
     */
    public void setPlayView(GVideoView playView) {
        this.videoView = playView;
    }

    /**
     * 设置视频播放的进度
     *
     * @param progress
     */
    public void setVideoProgress(long progress) {
        this.videoProgress = progress;
        tvProgressTime.setText(GPlayUtils.videoTime2Value(progress));

        try {
            pbVideo.setProgress((int) progress);
            sbVideo.setProgress((int) progress);
        } catch (Exception e) {
            pbVideo.setProgress(Integer.MAX_VALUE);
            sbVideo.setProgress(Integer.MAX_VALUE);
        }
        pbVideo.invalidate();
        sbVideo.invalidate();
    }

    /**
     * 设置视频标题
     *
     * @param text
     */
    public void setTitle(CharSequence text) {
        tvTitle.setText(text);
    }


    /**
     * 正常模式，只展示播放按钮
     */
    public void normal() {
        pbVideo.setProgress(0);
        sbVideo.setProgress(0);

        pbVideo.setVisibility(GONE);
        sbVideo.setVisibility(GONE);
        llLoading.setVisibility(GONE);
        llTop.setVisibility(GONE);
        tvTotalTime.setVisibility(GONE);
        tvProgressTime.setVisibility(GONE);
        ivPause.setVisibility(GONE);
        ivFull.setVisibility(GONE);

        ivStart.setVisibility(VISIBLE);
        ivCover.setVisibility(VISIBLE);
    }

    /**
     * 暂停模式
     */
    public void pause() {
        sbVideo.setVisibility(GONE);
        llLoading.setVisibility(GONE);
        llTop.setVisibility(GONE);
        tvTotalTime.setVisibility(GONE);
        tvProgressTime.setVisibility(GONE);
        ivPause.setVisibility(GONE);
        ivFull.setVisibility(GONE);
        ivCover.setVisibility(GONE);
        ivPause.setVisibility(GONE);

        pbVideo.setVisibility(VISIBLE);
        ivStart.setVisibility(VISIBLE);
    }

    /**
     * 迷你模式 只包含底部进度条
     */
    public void mini() {
        pbVideo.setVisibility(VISIBLE);

        ivStart.setVisibility(videoView.isPlaying() ? GONE : VISIBLE);

        sbVideo.setVisibility(GONE);
        ivFull.setVisibility(GONE);
        llLoading.setVisibility(GONE);
        llTop.setVisibility(GONE);
        tvTotalTime.setVisibility(GONE);
        tvProgressTime.setVisibility(GONE);
        ivPause.setVisibility(GONE);
        ivCover.setVisibility(GONE);
    }

    /**
     * 准备模式 只展示准备中
     */
    public void prepare() {
        llLoading.setVisibility(VISIBLE);

        ivFull.setVisibility(GONE);
        pbVideo.setVisibility(GONE);
        sbVideo.setVisibility(GONE);
        ivStart.setVisibility(GONE);
        llTop.setVisibility(GONE);
        tvTotalTime.setVisibility(GONE);
        tvProgressTime.setVisibility(GONE);
        ivPause.setVisibility(GONE);
        ivCover.setVisibility(GONE);
    }

    /**
     * 包含顶部标题 退出全屏模式 不含其他UI组件
     * 只显示5s 然后进入mini 模式
     */
    public void operation() {
        mHandler.removeCallbacks(entryMini);

        pbVideo.setVisibility(GONE);
        ivStart.setVisibility(GONE);
        llLoading.setVisibility(GONE);
        ivCover.setVisibility(GONE);
        ivStart.setVisibility(videoView.isPlaying() ? GONE : VISIBLE);


        if (uiState == GPlayViewUIState.FULL_SCREEN) {
            ivClose.setVisibility(VISIBLE);
            ivBack.setVisibility(GONE);
            ivFull.setImageResource(R.drawable.gvideo_enlarge);
        } else {
            ivBack.setVisibility(VISIBLE);
            ivClose.setVisibility(GONE);
            ivFull.setImageResource(R.drawable.gvideo_shrink);
        }

        ivFull.setVisibility(VISIBLE);
        ivPause.setVisibility(VISIBLE);
        llTop.setVisibility(VISIBLE);
        tvTotalTime.setVisibility(VISIBLE);
        tvProgressTime.setVisibility(VISIBLE);
        sbVideo.setVisibility(VISIBLE);
        mHandler.postDelayed(entryMini, 3000);
    }

    /**
     * 进入mini模式 定时器使用
     */
    private Runnable entryMini = new Runnable() {
        @Override
        public void run() {
            mini();
        }
    };


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (sbVideo.getVisibility() == VISIBLE) {
            //用户触动seekbar
            if (sbRect == null) {
                int[] location = GPlayUtils.getViewLocationByScreen(sbVideo);
                sbRect = new RectF(location[0], location[1], sbVideo.getWidth() + location[0], sbVideo.getHeight() + location[1]);
            }

            if (sbRect.contains(event.getX(), event.getY())) {
                return false;
            }
        }
        mGestureDetector.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_UP) {
            isScroll = false;
            leftScreenRect = null;
            if (llVolume.getVisibility() == VISIBLE)
                llVolume.setVisibility(GONE);
            if (rlVideoBrightness.getVisibility() == VISIBLE)
                rlVideoBrightness.setVisibility(GONE);
            if (rlVideoPosition.getVisibility() == VISIBLE) {
                //设定时间
                videoView.setProgress(setVideoCurrent);
                rlVideoPosition.setVisibility(GONE);
            }
            isScrollVolumeing = false;
            isScrollBrightness = false;
            isScrollPosition = false;
        }
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (uiState == GPlayViewUIState.LIST_ITEM
                || !videoView.isPlaying()) {
            videoView.start();
        } else {
            operation();
        }
        return false;
    }

    private float startX;
    private float startY;
    private boolean isScrollVolume; //是否是调节音量
    private boolean isScrollVolumeing; ///是否正在调节声音
    private boolean isScrollBrightness; //是否正在调节亮度
    private boolean isScrollPosition; //是否正在调节进度
    private int startVolumeValue;
    private float startBrightness;
    private float startCurrent;

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (uiState == GPlayViewUIState.LIST_ITEM) return false;
        if (!isScroll) {
            //清零上次的
            scrollX = 0f;
            scrollY = 0f;
            //记录起点坐标
            startX = e1.getX();
            startY = e1.getY();
            //初始化rectf
            leftScreenRect = new RectF(getWidth() / 2, 0, getWidth(), getHeight());
            isScrollVolume = leftScreenRect.contains(startX, startY);
        }
        isScroll = true;
        scrollX += distanceX;
        scrollY += distanceY;

        if ((Math.abs(scrollX) < Math.abs(scrollY)
                || isScrollVolumeing
                || isScrollBrightness)
                && !isScrollPosition) {
            //上下滚动
            //阀值 高度的一半==100  view高度/2/100 等于1个点的需要移动的像素值
            float size = getHeight() * 1.0f / 4 / 50;
            if (isScrollVolume
                    || isScrollVolumeing) {
                isScrollVolumeing = true;
                if (llVolume.getVisibility() != VISIBLE) {
                    llVolume.setVisibility(VISIBLE);
                    pbolume.setMax(100);
                    startVolumeValue = (int) (videoView.getVolume() * 100);
                    pbolume.setProgress(startVolumeValue);
                }
                //调节音量
                float value = scrollY / size; //要调节的值
                int volume = (int) (startVolumeValue + value);

                if (volume < 0) volume = 0;
                else if (volume > 100) volume = 100;
                pbolume.setProgress(volume);
                videoView.setVolume(volume * 1.0f / 100);//调节真实的音量
            } else {
                //调节亮度
                isScrollBrightness = true;
                if (rlVideoBrightness.getVisibility() != VISIBLE) {
                    rlVideoBrightness.setVisibility(VISIBLE);
                    startBrightness = videoView.getCurrentBrightness() * 100;
                    tvVideoBrightness.setText(startBrightness + "%");
                }
                float value = scrollY / size; //要调节的值
                int brightness = (int) (startBrightness + value);

                if (brightness < 0) brightness = 0;
                else if (brightness > 100) brightness = 100;

                tvVideoBrightness.setText(String.valueOf(brightness) + "%");
                videoView.setBrightness(brightness * 1.0f / 100);
            }
        } else if (Math.abs(scrollX) > Math.abs(scrollY)
                || isScrollPosition) {
            //左右移动
            float size = getWidth() * 1.0f / duration;
            isScrollPosition = true;
            if (rlVideoPosition.getVisibility() != VISIBLE) {
                rlVideoPosition.setVisibility(VISIBLE);
                tvCurrent.setText(GPlayUtils.videoTime2Value(videoProgress));
                tvDuration.setText("/" + GPlayUtils.videoTime2Value(duration));
                startCurrent = videoProgress;
            }
            float value = (-scrollX) / size; //要调节的值
            long current = (long) (startCurrent + value);
            if (current < 0) current = 0;
            else if (current > duration) current = duration;
            tvCurrent.setText(GPlayUtils.videoTime2Value(current));
            setVideoCurrent = current;
        }

        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    /**
     * 设置Cover
     *
     * @param cover
     */
    public void setCover(String cover) {
        this.cover = cover;
    }

    public ImageView getCoverIv() {
        return ivCover;
    }
}
