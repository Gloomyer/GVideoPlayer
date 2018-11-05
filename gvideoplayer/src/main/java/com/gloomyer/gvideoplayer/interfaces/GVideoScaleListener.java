package com.gloomyer.gvideoplayer.interfaces;

import android.graphics.Matrix;

import com.gloomyer.gvideoplayer.constants.GPlayViewUIState;

/**
 * 视频缩放自定义规则
 */
public interface GVideoScaleListener {

    /**
     * 根据UI状态设定缩放规则，如果需要使用默认的返回null即可
     *
     * @param state
     * @param mViewHeight
     * @param mVideoWidth
     * @param mVideoHeight
     * @param videoWidth
     * @return
     */
    Matrix getMatrixRules(GPlayViewUIState state, float mViewHeight, float mVideoWidth, float mVideoHeight, float videoWidth);
}
