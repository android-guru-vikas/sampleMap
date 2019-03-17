
package com.dev.mapdemo.network.controllers;

import com.dev.mapdemo.MapDemoApp;
import com.dev.mapdemo.utils.AppUtils;
import com.dev.mapdemo.utils.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitService {
    private static RetrofitService instance;
    private OkHttpClient okHttpClient;
    private Retrofit retrofit;
    private HttpLoggingInterceptor interceptor;

    private RetrofitService() {

    }

    public static RetrofitService getInstance() {
        if (instance == null) {
            synchronized (RetrofitService.class) {
                instance = new RetrofitService();
            }
        }
        return instance;
    }

    public Retrofit builder() {
        return getRetrofit(getOkHttpClient());
    }

    private OkHttpClient getOkHttpClient() {
        int cacheSize = 100 * 1024 * 1024; // 100 MB
        Cache cache = new Cache(MapDemoApp.getAppContext().getCacheDir(), cacheSize);

        if (okHttpClient == null) {
            synchronized (RetrofitService.class) {
                long READ_TIME_OUT = 10000;
                long CONNECTION_TIME_OUT = 10000;
                okHttpClient = new OkHttpClient.Builder()
                        .addInterceptor(OFFLINE_CACHE_CONTROL_INTERCEPTOR)
                        .cache(cache)
                        .readTimeout(READ_TIME_OUT, TimeUnit.MILLISECONDS)
                        .connectTimeout(CONNECTION_TIME_OUT, TimeUnit.MILLISECONDS)
                        .addInterceptor(getLoggingInterceptor())
                        .build();
            }
        }
        return okHttpClient;
    }

    private Retrofit getRetrofit(OkHttpClient okHttpClient) {
        if (retrofit == null) {
            synchronized (RetrofitService.class) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(Constants.APP_BASE_URL)
                        .client(okHttpClient)
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
        }
        return retrofit;
    }

    private HttpLoggingInterceptor getLoggingInterceptor() {
        if (interceptor == null) {
            synchronized (RetrofitService.class) {
                interceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
            }
        }
        return interceptor;
    }

    private static final Interceptor OFFLINE_CACHE_CONTROL_INTERCEPTOR = chain -> {
        Request request = chain.request();
        if (AppUtils.isNetworkEnabled(MapDemoApp.getAppContext())) {
            request = request.newBuilder()
                    .header("Cache-Control", "public, max-age=" + 10)
                    .build();
        } else {
            request.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7);
        }
        return chain.proceed(request);
    };
}