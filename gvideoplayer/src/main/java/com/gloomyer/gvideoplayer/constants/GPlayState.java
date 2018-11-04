package com.gloomyer.gvideoplayer.constants;

/**
 * 播放器的状态
 */
public enum GPlayState {
    Idle,
    Prepareing, //准备中
    PrepareFinish, //准备完成
    Playing, //播放中
    Pause, //暂停中
    Stop, //主动调用Stop 或者 播完完成都是这个状态
}
