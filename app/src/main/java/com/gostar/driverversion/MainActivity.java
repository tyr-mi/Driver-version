package com.gostar.driverversion;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.text.SimpleDateFormat;
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


    public static String username;
    public static String name;
    private String currentTime;

    private MaterialTextView timeTextView;
    private MaterialTextView usernameTv;
    private MaterialTextView shiftTv;
    private MaterialTextView locationTv;
    private MaterialTextView destinationTv;
    private MaterialTextView destination2Tv;
    private MaterialTextView destination3Tv;
    private MaterialTextView destination4Tv;
    private MaterialTextView _destination2Tv;
    private MaterialTextView _destination3Tv;
    private MaterialTextView _destination4Tv;
    private MaterialTextView phoneNumberTv;
    private MaterialTextView restaurantNameTv;
    private MaterialTextView costTv;
    private Button accept_Bt;

    private MaterialCardView shiftCv;
    private MaterialCardView packageDeliverCv;


    private GetDataService service;
    private Call<Retro> call;

    private Realm realm;
    private RealmResults<UserDbClass> result;

    public static boolean isWorking;
    public static int status = 3;

    private boolean has_order;
    private boolean receivedOrder = false;
    /** Called when the Driver touches the accept button */
    /**public void accept_clicked(View view)
    {
        if(has_order)
        {


        }
        // Do something in response to button click
    }*/

    @Getter
    @Setter
    private Disposable disposableSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {

        super.onResume();
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //گرفتن user,pass از دیتابیس
        getDatabase();

        //تشکیل کلاس Retro برای درخواست با service
        prepareNetworkReq();

        shiftTv = findViewById(R.id.shift_change_text_view);
        shiftCv = findViewById(R.id.shift_change_button);

        packageDeliverCv = findViewById(R.id.package_deliver);
        packageDeliverCv.setVisibility(View.INVISIBLE);

        usernameTv = findViewById(R.id.main_activity_username);
        usernameTv.setText("خوش آمدید " + name);

        locationTv = findViewById(R.id.package_detail_address_tv);
        destinationTv = findViewById(R.id.package_detail_destination_address_tv);

        //having more than one destination
        destination2Tv = findViewById(R.id.package_detail_destination2_address_tv);
        _destination2Tv = findViewById(R.id.package_detail_destination2_tv);
        destination3Tv = findViewById(R.id.package_detail_destination3_address_tv);
        _destination3Tv = findViewById(R.id.package_detail_destination3_tv);
        destination4Tv = findViewById(R.id.package_detail_destination4_address_tv);
        _destination4Tv = findViewById(R.id.package_detail_destination4_tv);

        restaurantNameTv = findViewById(R.id.package_restaurant_detail_tv);
        phoneNumberTv = findViewById(R.id.package_phoneNumber_detail_tv);
        costTv = findViewById(R.id.package_detail_cost_tv);

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
                    String restaurent_loc = result.get(0).getRestaurantLoc();
                    String restaurant_address = result.get(0).getRestaurantAddress();
                    String destination_loc = result.get(0).getDestinationsLoc();
                    String destination_loc4 = result.get(0).getDestination4sLoc();
                    String destination_loc2 = result.get(0).getDestination2sLoc();
                    String destination_loc3 = result.get(0).getDestination3sLoc();
                    String destination_address = result.get(0).getDestinationsAddress();
                    String destination_address2 = result.get(0).getDestinations2Address();
                    String destination_address3 = result.get(0).getDestination3sAddress();
                    String destination_address4 = result.get(0).getDestination4sAddress();

                    String phone_number = result.get(0).getPhoneNumber();
                    String restaurant_name = result.get(0).getRestaurantName();
                    int num_of_dests = result.get(0).getNum_of_dests();
                    String cost = result.get(0).getCost();
                    locationTv.setText(restaurant_address);
                    restaurantNameTv.setText(restaurant_name);
                    phoneNumberTv.setText(phone_number);
                    costTv.setText(cost);
                    destination2Tv.setVisibility(View.INVISIBLE);
                    destination3Tv.setVisibility(View.INVISIBLE);
                    destination4Tv.setVisibility(View.INVISIBLE);
                    _destination2Tv.setVisibility(View.INVISIBLE);
                    _destination3Tv.setVisibility(View.INVISIBLE);
                    _destination4Tv.setVisibility(View.INVISIBLE);
                    switch (num_of_dests) {
                        case 1:
                            destinationTv.setText(destination_address);
                            break;
                        case 2:
                            destinationTv.setText(destination_address);
                            destination2Tv.setVisibility(View.VISIBLE);
                            _destination2Tv.setVisibility(View.VISIBLE);
                            destination2Tv.setText(destination_address2);
                            break;
                        case 3:
                            destinationTv.setText(destination_address);
                            destination2Tv.setVisibility(View.VISIBLE);
                            destination2Tv.setText(destination_address2);
                            _destination2Tv.setVisibility(View.VISIBLE);
                            destination3Tv.setVisibility(View.VISIBLE);
                            _destination3Tv.setVisibility(View.VISIBLE);
                            destination3Tv.setText(destination_address3);
                            break;
                        case 4:
                            destinationTv.setText(destination_address);
                            destination2Tv.setVisibility(View.VISIBLE);
                            destination2Tv.setText(destination_address2);
                            _destination2Tv.setVisibility(View.VISIBLE);
                            destination3Tv.setVisibility(View.VISIBLE);
                            _destination3Tv.setVisibility(View.VISIBLE);
                            destination3Tv.setText(destination_address3);
                            destination4Tv.setVisibility(View.VISIBLE);
                            _destination4Tv.setVisibility(View.VISIBLE);
                            destination4Tv.setText(destination_address4);
                        default:
                    }
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

    private void getDatabase()
    {
        realm = Realm.getDefaultInstance();
        result = realm.where(UserDbClass.class).findAll();
        UserDbClass user = result.get(0);
        username = user.getUsername();
        name = user.getName();
    }

    private void prepareNetworkReq()
    {
        service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
    }

    private void changeUI()
    {
        shiftCv.setCardBackgroundColor(getColor(R.color.red));
        shiftTv.setText("پایان");
    }

    private void clickListener()
    {

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
        accept_Bt.setOnClickListener(v -> {

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

    private void getOrderData()
    {
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
                        order.setRestaurantAddress(String.valueOf(maps.get(0).get("restaurant_address")));
                        //order.setRestaurantLoc(Double.);
                        //order.setDestinationsAddress(String.valueOf(maps.get(0).get("destination")));
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

   private void getCurrentTime()
    {
        currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        currentTime = currentTime.substring(0,1);
    }

    private void setSharedPreference()
    {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.start_time_prefrence), Integer.parseInt(currentTime));
        editor.apply();
    }

    private void setStatusSharedPreference(int status)
    {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("status", status);
        editor.apply();
    }

    private void setWorkSharedPreference(boolean state)
    {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("isWorking", state);
        editor.apply();
    }

    private int getTimeSharedPreference()
    {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getInt("startTime",0);
    }

    private boolean getIsWorkingSharedPreference()
    {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getBoolean("isWorking",false);
    }

    private int getStatusSharedPreference()
    {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getInt("status",3);
    }



}