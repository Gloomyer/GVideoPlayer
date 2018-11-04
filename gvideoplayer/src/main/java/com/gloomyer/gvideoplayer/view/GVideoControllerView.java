package com.gloomyer.gvideoplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import com.gloomyer.gvideoplayer.R;
import com.gloomyer.gvideoplayer.constants.GPlayState;
import com.gloomyer.gvideoplayer.constants.GPlayViewUIState;

/**
 * 视频控制展示View
 */
public class GVideoControllerView extends FrameLayout {

    private SeekBar sbVideo;
    private ProgressBar pbVideo;
    private ImageView ivStart;
    private ImageView ivImage;
    private LinearLayout llLoading;
    private LinearLayout llTop;

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
        ivImage = findViewById(R.id.iv_image);
        llLoading = findViewById(R.id.ll_loading);
        llTop = findViewById(R.id.ll_top);

        pbVideo.setMax(100);
        sbVideo.setMax(100);
        setUIState(GPlayState.Idle, GPlayViewUIState.LIST_ITEM);
    }

    /**
     * 设置控制器UI状态
     *
     * @param playState 当前的视频播放器播放状态
     * @param uiState   当前的播放器UI状态
     */
    public void setUIState(GPlayState playState, GPlayViewUIState uiState) {
        if (playState == GPlayState.Idle) {
            sbVideo.setVisibility(GONE);
            pbVideo.setVisibility(GONE);
            llLoading.setVisibility(GONE);

            ivImage.setVisibility(VISIBLE);
            ivStart.setVisibility(VISIBLE);
        } else if (playState == GPlayState.Prepareing
                || playState == GPlayState.PrepareFinish) {
            sbVideo.setVisibility(GONE);
            pbVideo.setVisibility(GONE);
            ivImage.setVisibility(GONE);
            ivStart.setVisibility(GONE);

            llLoading.setVisibility(VISIBLE);
        } else if (playState == GPlayState.Playing) {
            sbVideo.setVisibility(GONE);
            pbVideo.setVisibility(GONE);
            ivImage.setVisibility(GONE);
            ivStart.setVisibility(GONE);
            llLoading.setVisibility(GONE);
        }

        if (uiState == GPlayViewUIState.LIST_ITEM) {
            llTop.setVisibility(GONE);
        } else {
            llTop.setVisibility(VISIBLE);
        }
    }
}
