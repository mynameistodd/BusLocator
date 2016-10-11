package com.sliverbit.buslocator.service;

import com.sliverbit.buslocator.models.BustimeResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by tdeland on 10/1/16.
 */

public interface BusTimeService {

    @GET("bustime/api/v1/gettime")
    Call<BustimeResponse> getTime(@Query("key") String key);

    @GET("bustime/api/v1/getvehicles")
    Call<BustimeResponse> getVehicles(@Query("key") String key, @Query("rt") String rt);

    @GET("bustime/api/v1/getroutes")
    Call<BustimeResponse> getRoutes(@Query("key") String key);

    @GET("bustime/api/v1/getdirections")
    Call<BustimeResponse> getDirections(@Query("key") String key, @Query("rt") String rt);

}
