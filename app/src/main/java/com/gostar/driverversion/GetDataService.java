package com.gostar.driverversion;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GetDataService
{

    @POST("api.php")
    Call<OrderRetroClass> sendLocation(@Body SendLocation sendloc);

    @POST("api.php")
    Call<Retro> checkLogin(@Body CheckLogin chlogin);

    @POST("api.php")
    Call<AcceptPackage> acceptPackage(@Body AcceptPackage accPackage);

    @POST("api.php")
    Call<FinishOrder> finishOrder(@Body FinishOrder finishOrder);

    @POST("api.php")
    Call<EndShiftRequest> endShift(@Body EndShiftRequest endShiftRequest);

    @POST("api.php")
    Call<StartShiftRequest> startShift(@Body StartShiftRequest startShiftRequest);

}
