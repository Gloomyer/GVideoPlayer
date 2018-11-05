package com.gloomyer.gvideoplayer.utils;

import com.gloomyer.gvideoplayer.constants.GEventMsg;
import com.gloomyer.gvideoplayer.interfaces.GListener;

import java.util.ArrayList;
import java.util.List;

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

    private List<GListener> mListeners;

    private GListenerManager() {
        mListeners = new ArrayList<>();
    }

    /**
     * 注册
     *
     * @param listener
     */
    public void register(GListener listener) {
        mListeners.add(listener);
    }

    /**
     * 取消注册
     *
     * @param listener
     */
    public void unRegister(GListener listener) {
        mListeners.remove(listener);
    }

    /**
     * 发送消息 唤起监听
     *
     * @param msg
     */
    public void sendEvent(GEventMsg msg) {
        for (GListener mListener : mListeners) {
            mListener.onEvent(msg);
        }
    }
}
