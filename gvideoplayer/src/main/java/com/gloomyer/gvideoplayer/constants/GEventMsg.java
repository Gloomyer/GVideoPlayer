package com.gloomyer.gvideoplayer.constants;


/**
 * 监听者模式消息
 */
public class GEventMsg {
    public static final int WHAT_STOP_PLAY = 0x001;
    public int what;
    public String content;
    public long arg1;
    public long arg2;
    public Object obj;
}
