package com.dev.mapdemo.utils;

import android.util.Log;

import com.dev.mapdemo.interfaces.Logger;

public class AppLogs extends AppLogger implements Logger {
    private static AppLogs logger;

    private AppLogs() {

    }

    public static AppLogs getInstance() {
        if (logger == null) {
            synchronized (AppLogs.class) {
                logger = new AppLogs();
            }
        }
        return logger;
    }


    @Override
    protected String setDirNameForLog() {
        return "DIR_NAME";
    }

    @Override
    protected String setFileNameForLog() {
        return "FILE_NAME";
    }

    @Override
    protected AppLogger getSingletonObject() {
        return logger;
    }

    @Override
    public void i(String TAG, String str) {
        Log.i(TAG, str);
    }

    @Override
    public void d(String TAG, String str) {
        Log.d(TAG, str);
    }

    @Override
    public void e(String TAG, String str) {
        Log.e(TAG, str);
    }

    @Override
    public void exception(String TAG, Throwable t) {
        if (t != null) {
            e(TAG, t.getMessage() != null ? t.getMessage() : "Empty message");
            t.printStackTrace();
        }
    }

}
