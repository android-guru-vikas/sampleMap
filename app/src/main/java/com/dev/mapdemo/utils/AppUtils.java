package com.dev.mapdemo.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public final class AppUtils {
    private static AppUtils instance;

    private AppUtils() {

    }

    public static AppUtils getInstance() {
        if (instance == null) {
            synchronized (AppUtils.class) {
                if (instance == null) {
                    instance = new AppUtils();
                }
            }
        }
        return instance;
    }

    public static boolean isNetworkEnabled(Context pContext) {
        if (pContext != null) {
            try {
                ConnectivityManager conMngr = (ConnectivityManager) pContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = conMngr != null ? conMngr.getActiveNetworkInfo() : null;
                return activeNetwork != null && activeNetwork.isConnected();
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public Object getValueFromData(Object data) {
        return (data == null || data.equals("null")) ? "" : data;
    }
}