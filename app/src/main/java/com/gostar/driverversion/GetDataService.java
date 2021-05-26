package com.gostar.driverversion;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface GetDataService {
    @POST("api.php")
    Call<AcceptPackage> acceptPackage(@Body AcceptPackage acceptPackage);

    @POST("api.php")
    Call<Retro> checkLogin(@Body CheckLogin checkLogin);

    @POST("api.php")
    Call<EndShiftRequest> endShift(@Body EndShiftRequest endShiftRequest);

    @POST("api.php")
    Call<FinishOrder> finishOrder(@Body FinishOrder finishOrder);

    @POST("api.php")
    Call<CurrentTaskRequest> getCurrentTask(@Body CurrentTaskRequest currentTaskRequest);

    @POST("api.php")
    Call<OrderRetroClass> orderList(@Body OrderListRequest orderListRequest);

    @POST("api.php")
    Call<OrderRetroClass> sendLocation(@Body SendLocation sendLocation);

    @POST("api.php")
    Call<StartShiftRequest> startShift(@Body StartShiftRequest startShiftRequest);
}
