package com.dev.mapdemo.network.controllers;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.dev.mapdemo.utils.AppUtils;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class WebServiceExecutor {
    private static WebServiceExecutor instance;
    public final String REQUEST_URL_TAG = "reqUrlTag";
    private int DEFAULT_RETRY_COUNT = 2;
    private Map<String, Call> manageCancelMap;

    private WebServiceExecutor() {
        manageCancelMap = new HashMap<>();
    }

    public static WebServiceExecutor getInstance() {
        if (instance == null) {
            synchronized (WebServiceExecutor.class) {
                if (instance == null) {
                    instance = new WebServiceExecutor();
                }
            }
        }
        return instance;
    }

    public void setRequestUrlTagName(String reqUrlTag, Call call) {
        this.manageCancelMap.put(reqUrlTag, call);
    }


    public String cancel(String reqUrlTag) {
        if (TextUtils.isEmpty(reqUrlTag))
            return "reqUrlTag can not be empty or null";
        if (manageCancelMap != null && manageCancelMap.size() > 0) {
            Call call = manageCancelMap.get(reqUrlTag);
            if (call != null && call.isExecuted()) {
                call.cancel();
                return "Service canceled";
            } else {
                return "Service not Running";
            }
        } else {
            return "Service not Exist";
        }
    }


    public <RequestModel> WebServiceExecutor execute(Context context,
                                                     Call<RequestModel> call, APIResponseListener<RequestModel>
                                                             listener) {
        return execute(context, null, call, DEFAULT_RETRY_COUNT, listener);
    }

    public <RequestModel> WebServiceExecutor execute(Context context, Call<RequestModel> call, int retryCount, final APIResponseListener<RequestModel> listener) {
        return execute(context, null, call, retryCount, listener);
    }

    public <RequestModel> WebServiceExecutor execute(Context context, Bundle bundle, Call<RequestModel> call, APIResponseListener<RequestModel> listener) {
        return execute(context, bundle, call, DEFAULT_RETRY_COUNT, listener);
    }

    public <RequestModel> WebServiceExecutor execute(Context context, Bundle bundle, Call<RequestModel> call, int retryCount,
                                                     final APIResponseListener<RequestModel> listener) {
        if (context != null && AppUtils.getInstance().isNetworkEnabled(context) && call != null) {
            showLoading(true, true, bundle, listener);
            setServiceCallInMap(call, bundle);
            call.enqueue(new RetrofitBase<RequestModel>(call, retryCount, bundle) {
                @Override
                public void onNetworkSuccess(Call<RequestModel> call, Response<RequestModel> response, Bundle bundle) {
                    Log.d("TAG", "Inside onNetworkSuccess : " + response);
                    showLoading(true, false, bundle, listener);
                    if (response != null && response.body() != null && listener != null) {
                        listener.onNetworkSuccess(call, response, bundle);
                    } else {
                        onNetworkFailureAndError(call, response, null, bundle, listener);
                    }
                    resetServiceCallInMap(bundle);
                }

                @Override
                public void onNetworkFailure(Call<RequestModel> call, Throwable t, Bundle bundle) {
                    Log.d("TAG", "Inside onNetworkFailure : " + call.request().url().toString());
                    showLoading(true, false, bundle, listener);
                    onNetworkFailureAndError(call, null, t, bundle, listener);
                    resetServiceCallInMap(bundle);
                }

                @Override
                public void onNetworkError(Call<RequestModel> call, Throwable t, Bundle bundle) {
                    Log.d("TAG", "Inside onNetworkFailure : " + call.request().url().toString());
                    showLoading(true, false, bundle, listener);
                    onNetworkFailureAndError(call, null, t, bundle, listener);
                    resetServiceCallInMap(bundle);
                }
            });
        } else {
            showLoading(false, false, bundle, listener);
        }
        return instance;
    }

    private <RequestModel> void showLoading(boolean isNetworkEnabled, boolean isLoading, Bundle bundle, APIResponseListener<RequestModel> listener) {
        if (listener != null) {
            listener.onLoading(isNetworkEnabled, isLoading, bundle);
        }
    }

    private <RequestModel> void onNetworkFailureAndError(Call<RequestModel> call, Response<RequestModel> response, Throwable t, Bundle bundle, APIResponseListener<RequestModel> listener) {
        if (call != null)
            call.cancel();
        if (listener != null)
            listener.onNetworkFailure(call, response, t, bundle);
        Log.d("TAG", "Inside onNetworkFailureAndError : " + call.request().url().toString());
    }

    private <RequestModel> void setServiceCallInMap(Call<RequestModel> call, Bundle bundle) {
        if (manageCancelMap != null && bundle != null && bundle.containsKey(REQUEST_URL_TAG)) {
            manageCancelMap.put(bundle.getString(REQUEST_URL_TAG), call);
        }
    }

    private void resetServiceCallInMap(Bundle bundle) {
        if (manageCancelMap != null && manageCancelMap.size() > 0 && bundle != null && bundle.containsKey(REQUEST_URL_TAG)) {
            manageCancelMap.remove(bundle.getString(REQUEST_URL_TAG));
        }
    }

    public interface APIResponseListener<RequestModel> {

        void onLoading(boolean isNetworkEnabled, boolean isLoading, Bundle bundle);

        void onNetworkSuccess(Call<RequestModel> call, Response<RequestModel> response, Bundle bundle);

        void onNetworkFailure(Call<RequestModel> call, Response<RequestModel> response, Throwable throwable, Bundle bundle);

    }

}
