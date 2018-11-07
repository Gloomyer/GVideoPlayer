package com.gloomyer.gvideoplayer;

import android.content.Context;

import com.gloomyer.gvideoplayer.constants.GEventMsg;
import com.gloomyer.gvideoplayer.interfaces.GCreateVideoPlayerListener;
import com.gloomyer.gvideoplayer.interfaces.IMeidiaPlayer;
import com.gloomyer.gvideoplayer.playerimpl.AndroidMeidiaPlayerImpl;
import com.gloomyer.gvideoplayer.utils.GListenerManager;
import com.gloomyer.gvideoplayer.utils.GPlayRecyclerViewAutoPlayHelper;
import com.gloomyer.gvideoplayer.view.GVideoView;

/**
 * 核心管理器
 */
public class GVideoManager {


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

    private GVideoView lastPlayerView;
    private boolean isMyPause;
    private GCreateVideoPlayerListener mCreateVideoPlayerListener;

    private GVideoManager() {
        isMyPause = false;
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
    public void onPause() {
        if (lastPlayerView != null
                && lastPlayerView.isPlaying()) {
            isMyPause = true;
            lastPlayerView.pause();
        }
    }

    /**
     * 被恢复
     */
    public void onResume() {
        if (lastPlayerView != null
                && isMyPause) {
            isMyPause = false;
            lastPlayerView.start();
        }
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
    }

    /**
     * 设置当前播放的view
     *
     * @param playerView
     */
    public void setLastPlayer(GVideoView playerView) {
        this.lastPlayerView = playerView;
    }

    /**
     * 获取上一次播放的View
     *
     * @return
     */
    public GVideoView getLastPlayerView() {
        return lastPlayerView;
    }

    /**
     * 是否正在播放视频
     *
     * @return
     */
    public boolean isPlaying() {
        return lastPlayerView != null && lastPlayerView.isPlaying();
    }

    /**
     * 暂停当前播放的
     */
    public void pause() {
        if (lastPlayerView != null
                && lastPlayerView.isPlaying()) {
            lastPlayerView.pause();
        }
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

}
