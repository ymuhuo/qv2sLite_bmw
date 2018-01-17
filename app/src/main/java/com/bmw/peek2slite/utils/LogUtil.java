package com.bmw.peek2slite.utils;

import android.util.Log;

import com.bmw.peek2slite.view.ui.BaseActivity;


/**
 * Created by yMuhuo on 2017/2/7.
 */
public class LogUtil {

    private static final String TAG = BaseActivity.TAG;
    private static StringBuilder builder;

    public static void log(String key, String msg) {
        if (builder == null)
            builder = new StringBuilder();

        Log.i(TAG, builder.append(key).append(msg).toString());
        deleteData();
    }

    public static void log(String key, int msg) {
        if (builder == null)
            builder = new StringBuilder();

        Log.i(TAG, builder.append(key).append(msg).toString());
        deleteData();
    }

    public static void log(String msg) {
        Log.i(TAG, msg);
    }

    public static void error(String key, String msg) {
        if (builder == null)
            builder = new StringBuilder();
        Log.e(TAG, builder.append(key).append(msg).toString());
        deleteData();
    }

    public static void error(String key, int msg) {
        if (builder == null)
            builder = new StringBuilder();
        Log.e(TAG, builder.append(key).append(msg).toString());
        deleteData();
    }

    public static void error(String key, Exception e) {
        if (builder == null)
            builder = new StringBuilder();
        Log.e(TAG, builder.append(key).append(e.toString()).toString());
        deleteData();
    }

    public static void error(String msg) {
        Log.e(TAG, msg);
    }

    private synchronized static void deleteData() {
        if (builder.toString().length() >= 0)
            builder.delete(0, builder.toString().length());
    }
}
