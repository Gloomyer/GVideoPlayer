package com.gloomyer.gvideoplayer.utils;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gloomyer.gvideoplayer.constants.GEventMsg;
import com.gloomyer.gvideoplayer.view.GVideoView;

import java.util.ArrayList;
import java.util.List;

/**
 * 自动播放辅助类
 */
public class GPlayRecyclerViewAutoPlayHelper extends RecyclerView.OnScrollListener {


    private LinearLayoutManager mLayoutManager;


    private enum Instance {
        I;
        GPlayRecyclerViewAutoPlayHelper instance;

        Instance() {
            instance = new GPlayRecyclerViewAutoPlayHelper();
        }
    }


    public static GPlayRecyclerViewAutoPlayHelper get() {
        return Instance.I.instance;
    }

    private RecyclerView mRecyclerView;
    private int videoViewId;
    private GVideoView lastPlayView;
    private boolean isBand;
    private boolean isMyPause;


    private GPlayRecyclerViewAutoPlayHelper() {
        isMyPause = false;
    }

    /**
     * 是否绑定了
     *
     * @return
     */
    public boolean isBand() {
        return isBand;
    }

    /**
     * 外部调用使用，
     *
     * @param videoView
     */
    public void setLastPlayer(GVideoView videoView) {
        lastPlayView = videoView;
    }

    /**
     * 绑定
     *
     * @param mRecyclerView 要绑定的RecycelrView
     * @param videoViewId   GPlayView在holder中的id
     */
    public void bind(final RecyclerView mRecyclerView, int videoViewId) {
        this.mRecyclerView = mRecyclerView;
        this.videoViewId = videoViewId;
        try {
            mLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        } catch (Exception e) {
            throw new RuntimeException("绑定RecyclerView必须是LinearLayoutManager");
        }
        mRecyclerView.addOnScrollListener(this);
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                onScrollStateChanged(mRecyclerView, RecyclerView.SCROLL_STATE_IDLE);
            }
        });
        isBand = true;
    }

    /**
     * 解除所有绑定
     */
    public void unBind() {
        GEventMsg msg = new GEventMsg();
        msg.what = GEventMsg.WHAT_DESTORY;
        GListenerManager.get().sendEvent(msg);
        mRecyclerView.removeOnScrollListener(this);
        mRecyclerView = null;
        mLayoutManager = null;
        lastPlayView = null;
        isBand = false;
    }


    /**
     * 被暂停
     */
    public void onPause() {
        if (lastPlayView != null
                && lastPlayView.isPlaying()) {
            isMyPause = true;
            lastPlayView.pause();
        }
    }

    /**
     * 被恢复
     */
    public void onResume() {
        if (lastPlayView != null
                && isMyPause) {
            isMyPause = false;
            lastPlayView.start();
        }
    }


    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE
                && GPlayUtils.getNetType(recyclerView.getContext()) == 1) { //wifi == 1
            int firstPos = mLayoutManager.findFirstVisibleItemPosition();
            int lastPos = mLayoutManager.findLastVisibleItemPosition();
            List<View> views = new ArrayList<>();
            for (int i = firstPos; i <= lastPos; i++) {
                View view = mLayoutManager.findViewByPosition(i);
                View videoView = view.findViewById(videoViewId);
                if (videoView != null
                        && videoView instanceof GVideoView) {
                    views.add(view);
                }
            }

            if (views.size() == 0) {
                //没有 销毁所有的
                if (lastPlayView != null) {
                    lastPlayView.pause();
                }
                return;
            }


            GVideoView readyPlayView = null;

            for (int i = 0; i < views.size(); i++) {
                View vp = views.get(i);
                if (vp.getTop() >= 0) {
                    //说明是完全可见的 就用这个就好了
                    readyPlayView = vp.findViewById(videoViewId);
                    break;
                } else {
                    if (i == 0 && views.size() == 1) {
                        //如果只有一个 没有看是否超过50% 超过就用 没有就干脆不用
                        if (vp.getTop() >= vp.getHeight()) {
                            readyPlayView = vp.findViewById(videoViewId);
                            break;
                        }
                    }
                    //其他情况就不属于自动播放
                }
            }

            //根据情况播放
            if (readyPlayView != null) {
                readyPlayView.start();
            }

            if (readyPlayView != lastPlayView
                    && lastPlayView != null) {
                //彻底销毁上一次的
                //lastPlayView.stop();
                //lastPlayView = null;
            }

            lastPlayView = readyPlayView;
        } else {
            if (lastPlayView != null
                    && lastPlayView.isPlaying()) {
                lastPlayView.pause();
            }
        }
    }

    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
    }


}
