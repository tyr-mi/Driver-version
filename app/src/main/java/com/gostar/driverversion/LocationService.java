package com.gostar.driverversion;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.JsonArray;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.subjects.PublishSubject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.core.app.NotificationCompat.PRIORITY_LOW;
import static androidx.core.app.NotificationCompat.PRIORITY_MIN;

public class LocationService extends Service {

    private static final int NOTIF_ID = 1;
    private static final String NOTIF_CHANNEL_ID = "AriyaNotifChannel";

    private GpsTracker gpsTracker;
    private double latitude;
    private double longitude;

    private GetDataService service;
    private  Call<OrderRetroClass> call;

    public static PublishSubject<List<Map<String,Object>>> orderDetail = PublishSubject.create();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        prepareNetworkReq();

        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            Log.d("sendingLoc","work");
                            getLocation();
                        } catch (Exception e) {

                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 10000);

        return super.onStartCommand(intent, flags, startId);
    }


    public void getLocation(){
        gpsTracker = new GpsTracker(this);
        if(gpsTracker.canGetLocation()){
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
            Log.d("location", String.valueOf(latitude + longitude));
            sendLocationToServer();
        }else{
            Toast.makeText(this,"قادر به دریافت موقعیت شما نیستیم" , Toast.LENGTH_LONG).show();
            gpsTracker.showSettingsAlert();
        }
    }
    private void prepareNetworkReq() {
        service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
    }

    private void sendLocationToServer() {
        SendLocation sendLoc = new SendLocation("7",String.valueOf(MainActivity.username),latitude,longitude);
        call = service.sendLocation(sendLoc);
        call.enqueue(new Callback<OrderRetroClass>() {
            @Override
            public void onResponse(@NotNull Call<OrderRetroClass> call, @NotNull Response<OrderRetroClass> response) {
                OrderRetroClass responseStr = response.body();
                if (response.errorBody() != null) {
                    return;
                }
                assert responseStr != null;
                responseStr.convert();
                List<Map<String,Object>> map = responseStr.mapList;
                if (map.get(0) != null && map.get(0).get("address") != null) {
                    orderDetail.onNext(map);
                }

                Log.d("response receiver","response received");
            }

            @Override
            public void onFailure(Call<OrderRetroClass> call, Throwable t) {
                //Toast.makeText(getApplicationContext(),"قادر به به روز رسانی موقعیت شما نیستیم",Toast.LENGTH_LONG).show();
            }
        });

    }

}
