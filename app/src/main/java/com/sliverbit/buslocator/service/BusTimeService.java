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

    @GET("bustime/api/v1/getstops")
    Call<BustimeResponse> getStops(@Query("key") String key, @Query("rt") String rt);

    @GET("bustime/api/v1/getpatterns")
    Call<BustimeResponse> getPatterns(@Query("key") String key, @Query("rt") String rt);

    @GET("bustime/api/v1/getpredictions")
    Call<BustimeResponse> getPredictions(@Query("key") String key, @Query("stpid") String stpid, @Query("rt") String rt, @Query("vid") String vid);

    @GET("bustime/api/v1/getservicebulletins")
    Call<BustimeResponse> getServiceBulletins(@Query("key") String key, @Query("rt") String rt, @Query("rtdir") String rtdir, @Query("stpid") String stpid);

    @GET("bustime/api/v1/getlocalelist")
    Call<BustimeResponse> getLocaleList(@Query("key") String key, @Query("locale") String locale);

}
