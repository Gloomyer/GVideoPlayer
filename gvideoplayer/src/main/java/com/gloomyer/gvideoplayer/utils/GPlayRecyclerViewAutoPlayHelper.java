package com.gloomyer.gvideoplayer.utils;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gloomyer.gvideoplayer.GVideoManager;
import com.gloomyer.gvideoplayer.view.GVideoView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 自动播放辅助类
 */
public class GPlayRecyclerViewAutoPlayHelper extends RecyclerView.OnScrollListener {


    public static GPlayRecyclerViewAutoPlayHelper get(String tag) {
        return new GPlayRecyclerViewAutoPlayHelper(tag);
    }

    public static GPlayRecyclerViewAutoPlayHelper get() {
        return get(GVideoManager.get().getDefaultTAG());
    }

    private HashMap<String, RecyclerView> mRecyclerViews;
    private HashMap<String, LinearLayoutManager> mLayoutManagers;
    private HashMap<String, Integer> videoViewIds;
    private HashMap<String, Boolean> isBands;
    private String tag;

    private GPlayRecyclerViewAutoPlayHelper(String tag) {
        mRecyclerViews = new HashMap<>();
        mLayoutManagers = new HashMap<>();
        videoViewIds = new HashMap<>();
        isBands = new HashMap<>();
        this.tag = tag;
    }

    /**
     * 是否绑定了
     *
     * @return
     */
    public boolean isBand(String tag) {
        Boolean isBand = isBands.get(tag);
        return isBand != null && isBand;
    }

    /**
     * 是否绑定了
     *
     * @return
     */
    public boolean isBand() {
        return isBand(tag);
    }


    /**
     * 绑定
     *
     * @param mRecyclerView 要绑定的RecycelrView
     * @param videoViewId   GPlayView在holder中的id
     */
    public void bind(String tag, final RecyclerView mRecyclerView, int videoViewId) {
        mRecyclerViews.put(tag, mRecyclerView);
        videoViewIds.put(tag, videoViewId);
        try {
            LinearLayoutManager mLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            mLayoutManagers.put(tag, mLayoutManager);
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
        isBands.put(tag, true);
    }

    /**
     * 绑定
     *
     * @param mRecyclerView 要绑定的RecycelrView
     * @param videoViewId   GPlayView在holder中的id
     */
    public void bind(RecyclerView mRecyclerView, int videoViewId) {
        bind(tag, mRecyclerView, videoViewId);
    }


    /**
     * 解除所有绑定
     */
    public void unBind(String tag) {
        RecyclerView view = mRecyclerViews.get(tag);
        if (view != null)
            view.removeOnScrollListener(this);
        mRecyclerViews.remove(tag);
        mLayoutManagers.remove(tag);
        videoViewIds.remove(tag);
        isBands.remove(tag);
    }

    /**
     * 解除所有绑定
     */
    public void unBind() {
        unBind(tag);
    }


    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE
                && GPlayUtils.getNetType(recyclerView.getContext()) == 1) { //wifi == 1
            LinearLayoutManager mLayoutManager = mLayoutManagers.get(tag);
            Integer videoViewId = videoViewIds.get(tag);
            if (mLayoutManager == null || videoViewId == null) return;
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
                if (GVideoManager.get().getLastPlayerView() != null) {
                    GVideoManager.get().getLastPlayerView().pause();
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

        } else {
            if (GVideoManager.get().getLastPlayerView() != null
                    && GVideoManager.get().isPlaying()) {
                GVideoManager.get().onPause(tag);
            }
        }
    }

    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
    }


}
