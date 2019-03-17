package com.dev.mapdemo.interfaces;

import android.content.Context;

/**
 * Created by devendra on 30/12/17.
 */

public interface Toaster {
    public void show(Context context, String str);
    public void showLong(Context context, String str);
    public void showForTesting(Context context, String str);
}
