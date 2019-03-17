package com.dev.mapdemo;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class MapDemoApp extends Application {
    private static final String TAG = "MapDemoApp";
    private static MapDemoApp applicationInstance = null;
    public int deviceWidth;
    public int deviceHeight;
    public Typeface droidTypeFace;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationInstance = this;
    }

    public static MapDemoApp getInstance() {
        return applicationInstance;
    }


    public static Context getAppContext() {
        return applicationInstance.getApplicationContext();
    }

    public void getDisplayMetrix() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        deviceWidth = metrics.widthPixels;
        deviceHeight = metrics.heightPixels;
    }

    public static int dip2px(float dpValue) {
        final float scale = getAppContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
