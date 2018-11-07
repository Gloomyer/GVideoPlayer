package com.gloomyer.gvideoplayer.utils;

import com.gloomyer.gvideoplayer.constants.GEventMsg;
import com.gloomyer.gvideoplayer.interfaces.GListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 监听者模式管理者
 */
public class GListenerManager {

    private enum Instance {
        I;
        GListenerManager instance;

        Instance() {
            instance = new GListenerManager();
        }
    }

    public static GListenerManager get() {
        return Instance.I.instance;
    }

    private Map<String, List<GListener>> mListeners;

    private GListenerManager() {
        mListeners = new HashMap<>();
    }

    /**
     * 注册
     *
     * @param tag
     * @param listener
     */
    public void register(String tag, GListener listener) {
        List<GListener> mls = mListeners.get(tag);
        if (mls == null) {
            mls = new ArrayList<GListener>();
            mListeners.put(tag, mls);
        }
        mls.add(listener);
    }

    /**
     * 取消注册
     *
     * @param listener
     */
    public void unRegister(String tag, GListener listener) {
        List<GListener> mls = mListeners.get(tag);
        if (mls == null) {
            return;
        }
        mls.remove(listener);
        if (mls.size() == 0)
            mListeners.remove(tag);
    }

    /**
     * 发送消息 唤起监听
     *
     * @param msg
     */
    public void sendEvent(String tag, GEventMsg msg) {
        List<GListener> mls = mListeners.get(tag);
        if (mls == null || mls.size() <= 0) return;
        for (GListener mListener : mls) {
            mListener.onEvent(msg);
        }
    }

    /**
     * 发送消息 唤起监听
     *
     * @param msg
     */
    public void sendEvent(GEventMsg msg) {
        Set<String> keys = mListeners.keySet();
        List<GListener> mls = new ArrayList<>();
        for (String key : keys) {
            List<GListener> sMls = mListeners.get(key);
            if (sMls != null && sMls.size() > 0) {
                for (GListener ml : sMls) {
                    mls.add(ml);
                }
            }
        }
        for (GListener mListener : mls) {
            mListener.onEvent(msg);
        }
    }
}
