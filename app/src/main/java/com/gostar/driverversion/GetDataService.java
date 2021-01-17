package com.gostar.driverversion;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GetDataService {

    @POST("api.php")
    Call<OrderRetroClass> sendLocation(@Query("id") String id, @Query("peik_user") String user, @Query("x") double lat, @Query("y") double lon, @Query("status") String status);

    @POST("api.php")
    Call<Retro> checkLogin(@Query("id") String id,@Query("peik_user") String username, @Query("pass") String password);
}
