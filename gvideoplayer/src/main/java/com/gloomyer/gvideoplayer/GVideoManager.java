package com.gloomyer.gvideoplayer;

import android.app.Activity;
import android.content.Context;

import com.gloomyer.gvideoplayer.constants.GEventMsg;
import com.gloomyer.gvideoplayer.constants.GPlayViewUIState;
import com.gloomyer.gvideoplayer.interfaces.GCreateVideoPlayerListener;
import com.gloomyer.gvideoplayer.interfaces.IMeidiaPlayer;
import com.gloomyer.gvideoplayer.playerimpl.AndroidMeidiaPlayerImpl;
import com.gloomyer.gvideoplayer.utils.GListenerManager;
import com.gloomyer.gvideoplayer.utils.GPlayRecyclerViewAutoPlayHelper;
import com.gloomyer.gvideoplayer.view.GVideoView;

import java.util.HashMap;
import java.util.Map;

/**
 * 核心管理器
 */
public class GVideoManager {

    private static final String DEFAULT_TAG = "defaultTag";

    private enum Instance {
        I;
        GVideoManager manager;

        Instance() {
            manager = new GVideoManager();
        }
    }

    public static GVideoManager get() {
        return Instance.I.manager;
    }

    private Map<String, GVideoView> cacheViews;
    private Map<String, Boolean> cacheMyPauses;
    private GCreateVideoPlayerListener mCreateVideoPlayerListener;

    private GVideoManager() {
        cacheViews = new HashMap<>();
        cacheMyPauses = new HashMap<>();
    }

    /**
     * 设置播放器
     *
     * @param mListener
     */
    public void setCreateVideoPlayerListener(GCreateVideoPlayerListener mListener) {
        this.mCreateVideoPlayerListener = mListener;
    }

    /**
     * 被暂停
     */
    public void onPause(String tag) {
        GVideoView view = cacheViews.get(tag);
        if (view != null
                && view.isPlaying()) {
            view.pause();
            cacheMyPauses.put(tag, true);
        }
    }

    /**
     * 被暂停
     */
    public void onPause() {
        onPause(DEFAULT_TAG);
    }

    /**
     * @param tag
     */
    public void onResume(String tag) {
        GVideoView view = cacheViews.get(tag);
        Boolean myPause = cacheMyPauses.get(tag);
        if (view != null
                && myPause != null
                && myPause) {
            cacheMyPauses.put(tag, false);
            view.start();
        }
    }

    /**
     * 被恢复
     */
    public void onResume() {
        onResume(DEFAULT_TAG);
    }

    /**
     * 释放资源
     */
    public void onDestory() {
        mCreateVideoPlayerListener = null;
        GEventMsg msg = new GEventMsg();
        msg.what = GEventMsg.WHAT_DESTORY;
        GListenerManager.get().sendEvent(msg);
        GPlayRecyclerViewAutoPlayHelper.get().unBind();
        cacheMyPauses.clear();
        cacheViews.clear();
        cacheMyPauses = null;
        cacheViews = null;
    }

    /**
     * 设置当前播放的view
     *
     * @param playerView
     */
    public void setLastPlayer(String tag, GVideoView playerView) {
        cacheViews.put(tag, playerView);
    }

    /**
     * 设置当前播放的view
     *
     * @param playerView
     */
    public void setLastPlayer(GVideoView playerView) {
        setLastPlayer(DEFAULT_TAG, playerView);
    }

    /**
     * 获取上一次播放的View
     *
     * @return
     */
    public GVideoView getLastPlayerView(String tag) {
        return cacheViews.get(tag);
    }

    /**
     * 获取上一次播放的View
     *
     * @return
     */
    public GVideoView getLastPlayerView() {
        return getLastPlayerView(DEFAULT_TAG);
    }

    /**
     * 是否正在播放视频
     *
     * @return
     */
    public boolean isPlaying() {
        return isPlaying(DEFAULT_TAG);
    }

    /**
     * 是否正在播放视频
     *
     * @return
     */
    public boolean isPlaying(String tag) {
        GVideoView view = cacheViews.get(tag);
        return view != null && view.isPlaying();
    }

    /**
     * 创建MeidiaPlayer
     *
     * @param context
     * @return
     */
    public IMeidiaPlayer creatMediaPlayer(Context context) {
        if (mCreateVideoPlayerListener != null)
            return mCreateVideoPlayerListener.create(context);
        else
            return new AndroidMeidiaPlayerImpl(context);
    }

    /**
     * 返回键
     *
     * @param activity
     */
    public void onBackPressed(String tag, Activity activity) {
        GVideoView view = cacheViews.get(tag);
        if (isPlaying()) {
            if (view.getPlayUIState() == GPlayViewUIState.LIST_ITEM) {
                activity.onBackPressed();
            } else if (view.getPlayUIState() == GPlayViewUIState.FULL_SCREEN) {
                view.exitFullScreen();
            } else if (view.getPlayUIState() == GPlayViewUIState.FULL_HORIZONTAL) {
                view.exitFullHorzontal();
            }
        } else {
            activity.onBackPressed();
        }
    }

    /**
     * 返回键
     *
     * @param activity
     */
    public void onBackPressed(Activity activity) {
        onBackPressed(DEFAULT_TAG, activity);
    }

    /**
     * 获取默认TAG
     *
     * @return
     */
    public String getDefaultTAG() {
        return DEFAULT_TAG;
    }
}
