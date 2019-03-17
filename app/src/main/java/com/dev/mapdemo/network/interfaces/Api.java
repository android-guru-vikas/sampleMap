package com.dev.mapdemo.network.interfaces;

import com.dev.mapdemo.network.models.LocationResponseModel;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface Api {
    @GET("explore")
    Observable<LocationResponseModel> getLocationUpdates();
}
