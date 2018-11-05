package com.gloomyer.gvideoplayer.view;

import android.content.Context;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gloomyer.gvideoplayer.R;
import com.gloomyer.gvideoplayer.constants.GPlayState;
import com.gloomyer.gvideoplayer.constants.GPlayViewUIState;
import com.gloomyer.gvideoplayer.utils.GPlayUtils;

/**
 * 视频控制展示View
 */
public class GVideoControllerView extends FrameLayout implements GestureDetector.OnGestureListener {

    private SeekBar sbVideo;
    private ProgressBar pbVideo;
    private ImageView ivStart;
    private ImageView ivPause;
    private ImageView ivImage;
    private LinearLayout llLoading;
    private LinearLayout llTop;
    private ImageView ivClose;
    private ImageView ivBack;
    private ImageView ivFull;
    private TextView tvTotalTime;
    private TextView tvProgressTime;
    private TextView tvTitle;
    private long videoProgress;
    private long duration;
    private GVideoView videoView;
    private GPlayViewUIState uiState;
    private GestureDetector mGestureDetector;
    private RectF sbRect;

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
        LayoutInflater.from(context).inflate(R.layout.gvideo_controller_view_layout, this, true);
        sbVideo = findViewById(R.id.sb_video);
        pbVideo = findViewById(R.id.pb_video);
        ivStart = findViewById(R.id.iv_start);
        ivPause = findViewById(R.id.iv_pause);
        ivImage = findViewById(R.id.iv_image);
        llLoading = findViewById(R.id.ll_loading);
        llTop = findViewById(R.id.ll_top);
        ivClose = findViewById(R.id.iv_close);
        ivBack = findViewById(R.id.iv_back);
        tvTotalTime = findViewById(R.id.tv_total_time);
        tvProgressTime = findViewById(R.id.tv_progress_time);
        tvTitle = findViewById(R.id.tv_title);
        ivFull = findViewById(R.id.iv_full);

        ivClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //退出全屏模式
                videoView.exitFullScreen();
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
                    uiState = GPlayViewUIState.FULL_HORIZONTAL;
                    ivFull.setImageResource(R.drawable.gvideo_shrink);
                    videoView.entryFullHorzontal();
                } else {
                    uiState = GPlayViewUIState.FULL_SCREEN;
                    ivFull.setImageResource(R.drawable.gvideo_enlarge);
                    videoView.exitFullHorzontal();

                }
            }
        });
        uiState = GPlayViewUIState.LIST_ITEM;
        mGestureDetector = new GestureDetector(context, this);
        normal();
    }

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
        return true;
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
        pbVideo.setVisibility(GONE);
        sbVideo.setVisibility(GONE);
        ivImage.setVisibility(GONE);
        llLoading.setVisibility(GONE);
        llTop.setVisibility(GONE);
        tvTotalTime.setVisibility(GONE);
        tvProgressTime.setVisibility(GONE);
        ivPause.setVisibility(GONE);
        ivFull.setVisibility(GONE);

        ivStart.setVisibility(VISIBLE);
    }

    /**
     * 迷你模式 只包含底部进度条
     */
    public void mini() {
        pbVideo.setVisibility(VISIBLE);

        sbVideo.setVisibility(GONE);
        ivStart.setVisibility(GONE);
        ivImage.setVisibility(GONE);
        ivFull.setVisibility(GONE);
        llLoading.setVisibility(GONE);
        llTop.setVisibility(GONE);
        tvTotalTime.setVisibility(GONE);
        tvProgressTime.setVisibility(GONE);
        ivPause.setVisibility(GONE);
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
        ivImage.setVisibility(GONE);
        llTop.setVisibility(GONE);
        tvTotalTime.setVisibility(GONE);
        tvProgressTime.setVisibility(GONE);
        ivPause.setVisibility(GONE);
    }

    /**
     * 包含顶部标题 退出全屏模式 不含其他UI组件
     * 只显示5s 然后进入mini 模式
     */
    public void topBottomPause() {
        pbVideo.setVisibility(GONE);
        ivStart.setVisibility(GONE);
        ivImage.setVisibility(GONE);
        llLoading.setVisibility(GONE);
        ivBack.setVisibility(GONE);

        ivFull.setVisibility(VISIBLE);
        ivPause.setVisibility(VISIBLE);
        llTop.setVisibility(VISIBLE);
        ivClose.setVisibility(VISIBLE);
        tvTotalTime.setVisibility(VISIBLE);
        tvProgressTime.setVisibility(VISIBLE);
        sbVideo.setVisibility(VISIBLE);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                mini();
            }
        }, 3000);
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
        //点击播放
        if (videoView.getPlayState() == GPlayState.Idle) {
            videoView.start();
        }

        if (uiState == GPlayViewUIState.LIST_ITEM) {
            videoView.entryFullScreen();
        } else {
            topBottomPause();

        }
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
