package com.gloomyer.gvideoplayer.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.WindowManager;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 工具类
 * 一些辅助方法
 */
public class GPlayUtils {

    /**
     * Get activity from context object
     *
     * @param context something
     * @return object of Activity or null if it is not Activity
     */
    public static Activity scanForActivity(Context context) {
        if (context == null) return null;
        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {
            return scanForActivity(((ContextWrapper) context).getBaseContext());
        }
        return null;
    }

    public static void showActionBar(Context context) {
        ActionBar ab = getAppCompActivity(context).getSupportActionBar();
        if (ab != null) {
            ab.setShowHideAnimationEnabled(false);
            ab.show();
        }
        scanForActivity(context)
                .getWindow()
                .clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    public static void hideActionBar(Context context) {
        ActionBar ab = getAppCompActivity(context).getSupportActionBar();
        if (ab != null) {
            ab.setShowHideAnimationEnabled(false);
            ab.hide();
        }
        scanForActivity(context)
                .getWindow()
                .setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * Get AppCompatActivity from context
     *
     * @param context
     * @return AppCompatActivity if it's not null
     */
    private static AppCompatActivity getAppCompActivity(Context context) {
        if (context == null) return null;
        if (context instanceof AppCompatActivity) {
            return (AppCompatActivity) context;
        } else if (context instanceof ContextThemeWrapper) {
            return getAppCompActivity(((ContextThemeWrapper) context).getBaseContext());
        }
        return null;
    }


    /**
     * 视频时间转换
     *
     * @param time
     * @return
     */
    public static String videoTime2Value(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        try {
            return sdf.format(new Date(time));
        } catch (Exception e) {
            return "00:00";
        }
    }

    /**
     * 获取View在屏幕中的位置
     *
     * @param v
     * @return
     */
    public static int[] getViewLocationByScreen(View v) {
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        return location;
    }
}
