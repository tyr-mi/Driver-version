package com.gostar.driverversion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.realm.Realm;
import io.realm.RealmResults;
import lombok.Getter;
import lombok.Setter;
import retrofit2.Call;

public class MainActivity extends AppCompatActivity {

    private String username;
    private String name;
    private String currentTime;

    private MaterialTextView timeTextView;
    private MaterialTextView usernameTv;
    private MaterialTextView shiftTv;
    private MaterialTextView locationTv;
    private MaterialTextView destinationTv;

    private MaterialCardView shiftCv;
    private MaterialCardView packageDeliverCv;


    private GetDataService service;
    private Call<Retro> call;

    private Realm realm;
    private RealmResults<UserDbClass> result;

    public static boolean isWorking;
    public static int status = 3;

    private boolean receivedOrder = false;

    @Getter
    @Setter
    private Disposable disposableSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {

        super.onResume();
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        getDatabase();


        prepareNetworkReq();

        shiftTv = findViewById(R.id.shift_change_text_view);
        shiftCv = findViewById(R.id.shift_change_button);

        packageDeliverCv = findViewById(R.id.package_deliver);

        usernameTv = findViewById(R.id.main_activity_username);
        usernameTv.setText("خوش آمدید " + name);

        locationTv = findViewById(R.id.package_detail_address_tv);
        destinationTv = findViewById(R.id.package_detail_destination_address_tv);


        setWorkSharedPreference(false);
        setStatusSharedPreference(3);

        boolean isWorking = getIsWorkingSharedPreference();
        status = getStatusSharedPreference();



        if (isWorking) {

            changeUI();
            int time = getTimeSharedPreference();
            if (status == 2) {
                packageDeliverCv.setVisibility(View.VISIBLE);
                RealmResults<OrderDbClass> result = realm.where(OrderDbClass.class).findAll();
                if (result.size() > 0) {
                    String address = result.get(0).getAddress();
                    String destination = result.get(0).getDestination();
                    locationTv.setText(address);
                    destinationTv.setText(destination);
                }
            } else {
                packageDeliverCv.setVisibility(View.INVISIBLE);
            }
        } else {
            packageDeliverCv.setVisibility(View.INVISIBLE);
        }

        clickListener();

        getOrderData();
    }

    private void getDatabase() {
        realm = Realm.getDefaultInstance();
        result = realm.where(UserDbClass.class).findAll();
        UserDbClass user = result.get(0);
        username = user.getUsername();
        name = user.getName();
    }

    private void prepareNetworkReq() {
        service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
    }

    private void changeUI() {
        shiftCv.setCardBackgroundColor(getColor(R.color.red));
        shiftTv.setText("پایان");
    }

    private void clickListener() {

        shiftCv.setOnClickListener(v -> {

            if (!isWorking) {
                setWorkSharedPreference(true);
                shiftCv.setCardBackgroundColor(getColor(R.color.red));
                shiftTv.setText("پایان");
                status = 1;
                isWorking = true;
            } else {
                setWorkSharedPreference(false);
                shiftCv.setCardBackgroundColor(getColor(R.color.green));
                shiftTv.setText("شروع");
                status = 3;
                isWorking = false;
            }
            setStatusSharedPreference(status);
        });

        packageDeliverCv.setOnClickListener(v -> {
            status = 1;
            setStatusSharedPreference(1);
            packageDeliverCv.setVisibility(View.INVISIBLE);
            receivedOrder = false;
            locationTv.setText("");
            destinationTv.setText("");
            RealmResults<OrderDbClass> result = realm.where(OrderDbClass.class).findAll();
            if (result.size() > 0) {
                realm.beginTransaction();
                realm.delete(OrderDbClass.class);
                realm.commitTransaction();
            }
        });

    }

    private void getOrderData() {
        setDisposableSuccess(LocationService.orderDetail
            .subscribeWith(new DisposableObserver<List<Map<String, Object>>>() {
                @Override
                public void onNext(@NonNull List<Map<String, Object>> maps) {
                    if (!receivedOrder) {
                        RealmResults<OrderDbClass> result = realm.where(OrderDbClass.class).findAll();
                        if (result.size() > 0) {
                            realm.beginTransaction();
                            realm.delete(OrderDbClass.class);
                            realm.commitTransaction();
                        }
                        setStatusSharedPreference(2);
                        status = 2;
                        locationTv.setText(String.valueOf(maps.get(0).get("address")));
                        destinationTv.setText(String.valueOf(maps.get(0).get("destination")));
                        packageDeliverCv.setVisibility(View.VISIBLE);
                        receivedOrder = true;
                        realm.beginTransaction();
                        OrderDbClass order = realm.createObject(OrderDbClass.class);
                        order.setAddress(String.valueOf(maps.get(0).get("address")));
                        order.setDestination(String.valueOf(maps.get(0).get("destination")));
                        order.setName(String.valueOf(maps.get(0).get("name")));
                        realm.commitTransaction();
                    }

                }

                @Override
                public void onError(@NonNull Throwable e) {

                }

                @Override
                public void onComplete() {

                }
            }));
    }

    private void getCurrentTime() {
        currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        currentTime = currentTime.substring(0,1);
    }

    private void setSharedPreference() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.start_time_prefrence), Integer.parseInt(currentTime));
        editor.apply();
    }

    private void setStatusSharedPreference(int status) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("status", status);
        editor.apply();
    }

    private void setWorkSharedPreference(boolean state) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("isWorking", state);
        editor.apply();
    }

    private int getTimeSharedPreference() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getInt("startTime",0);
    }

    private boolean getIsWorkingSharedPreference() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getBoolean("isWorking",false);
    }

    private int getStatusSharedPreference() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getInt("status",3);
    }



}