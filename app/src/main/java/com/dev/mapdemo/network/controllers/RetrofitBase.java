package com.dev.mapdemo.network.controllers;

import android.os.Bundle;
import android.support.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class RetrofitBase<RequestModel> implements Callback<RequestModel> {
    private final Call<RequestModel> call;
    private Bundle bundle;
    private int totalRetries;
    private int retryCount = 0;

    protected RetrofitBase(Call<RequestModel> call, int totalRetries, Bundle bundle) {
        this.call = call;
        this.totalRetries = totalRetries;
        this.bundle = bundle;
    }

    @Override
    public void onResponse(@NonNull Call<RequestModel> call, @NonNull Response<RequestModel> response) {
        try {
            if (!isCallSuccess(response)) {
                if (retryCount++ < totalRetries) {
                    retry();
                } else {
                    onNetworkSuccess(call, response, bundle);
                }
            } else {
                onNetworkSuccess(call, response, bundle);
            }
        } catch (Exception e) {
            onNetworkError(call, e, bundle);
        }
    }

    @Override
    public void onFailure(@NonNull Call<RequestModel> call, @NonNull Throwable RequestModel) {
        try {
            if (retryCount++ < totalRetries) {
                retry();
            } else {
                onNetworkFailure(call, RequestModel, bundle);
            }
        } catch (Exception e) {
            onNetworkError(call, e, bundle);
        }
    }

    protected abstract void onNetworkSuccess(Call<RequestModel> call, Response<RequestModel> response, Bundle bundle) throws Exception;

    protected abstract void onNetworkFailure(Call<RequestModel> call, Throwable RequestModel, Bundle bundle) throws Exception;

    protected abstract void onNetworkError(Call<RequestModel> call, Throwable error, Bundle bundle);

    private void retry() {
        call.clone().enqueue(this);
    }

    private boolean isCallSuccess(Response response) {
        int code = response.code();
        return (code >= 200 && code < 400);
    }

}
