package com.dev.mapdemo.interfaces;

public interface Logger {
    void i(String TAG, String str);

    void d(String TAG, String str);

    void e(String TAG, String str);

    void exception(String TAG, Throwable t);
}
