package com.dev.mapdemo.utils;

import android.content.Context;
import android.widget.Toast;

public class AppToast {
    private static AppToast instance;

    private AppToast() {
    }

    public static AppToast getInstance() {
        if (instance == null) {
            synchronized (AppToast.class) {
                if (instance == null) {
                    instance = new AppToast();
                }
            }
        }
        return instance;
    }


    public void showToast(Context context, String message) {
        if (context != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    public void showToastLong(Context context, String message) {
        if (context != null)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public void showToastTesting(Context context, String message) {
        if (context != null)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}
