package com.gostar.driverversion;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.LinkedTransferQueue;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.realm.Realm;
import io.realm.RealmResults;
import lombok.Getter;
import lombok.Setter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {


    public static String username;
    public static String name;
    public static String password;
    private String currentTime;
    private String orderId;
    private String[] lon , lat;
    private int currentOrderIndex = 0;
    private double selfLat;
    private double selfLon;

    private double sourceLat;
    private double sourceLon;

    private MarkerOptions selfMarkerOption;
    private Marker selfMarker;
    private LatLng selfLatLng;

    private MarkerOptions sourceMarkerOption;
    private Marker sourceMarker;
    private LatLng sourceLatLng;

    private MarkerOptions markerOptions;
    private GpsTracker gpsTracker;

    private MaterialTextView timeTextView;
    private MaterialTextView usernameTv;
    private MaterialTextView shiftTv;
    private MaterialTextView packagereceiverName;
    private MaterialTextView locationTv;
    private MaterialTextView destinationTv;
    private MaterialTextView phoneNumberTv;
    private MaterialTextView restaurantNameTv;
    private MaterialTextView costTv;
    private Button accept_Bt;
    private Button declinePkg;

    private MaterialCardView shiftCv;
    private MaterialCardView packageDeliverCv;


    private GetDataService service;
    private Call<AcceptPackage> call;
    private Call<FinishOrder> finishCall;
    private Call<EndShiftRequest> endShiftRequestCall;
    private Call<StartShiftRequest> startShiftRequestCall;
    private Call<OrderRetroClass> orderListRequestCall;

    private Realm realm;
    private RealmResults<UserDbClass> result;

    public static boolean isWorking;
    public static int status = 3;

    private boolean receivedOrder = false;

    List<Map<String, Object>> orderData;


    private GoogleMap googleMap;

    @Getter
    @Setter
    private Disposable disposableSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);
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

        restaurantNameTv = findViewById(R.id.package_restaurant_detail_tv);
        phoneNumberTv = findViewById(R.id.package_phoneNumber_detail_tv);
        costTv = findViewById(R.id.package_detail_cost_tv);
        accept_Bt = findViewById(R.id.accept_package_bt);
        declinePkg = findViewById(R.id.decline_package_bt);
        packagereceiverName = findViewById(R.id.package_receiver_name_detail_tv);


        //status = getStatusSharedPreference();

        OrderListRequest orderList = new OrderListRequest(26,username,password);
        orderListRequestCall = service.orderList(orderList);
        orderListRequestCall.enqueue(new Callback<OrderRetroClass>() {
            @Override
            public void onResponse(Call<OrderRetroClass> call, Response<OrderRetroClass> response) {
                OrderRetroClass orderRetroClass = response.body();
                if (orderRetroClass != null) {
                    orderRetroClass.convert();
                }
                orderData = orderRetroClass.mapList;
                if (orderRetroClass.mapList.size() > 0) {
                    status = 2;
                    //setPackageInfo(orderData.get(0).get("address",));
                    destinationTv.setText((String) orderData.get(0).get("address"));
                    orderId = (String) orderData.get(0).get("id");
                    restaurantNameTv.setText((String) orderData.get(0).get("restaurantName"));
                    costTv.setText((String) orderData.get(0).get("cost"));
                    String tempLon = (String) orderData.get(currentOrderIndex).get("lon");
                    String tempLat = (String) orderData.get(currentOrderIndex).get("lat");
                    sourceLat = Double.parseDouble((String) orderData.get(0).get("originLatitude"));
                    sourceLon = Double.parseDouble((String) orderData.get(0).get("originLongitude"));
                    sourceLatLng = new LatLng(sourceLat,sourceLon);
                    phoneNumberTv.setText((String) orderData.get(0).get("telephone"));
                    lon = tempLon.split("#");
                    lat = tempLat.split("#");
                    LatLng myLocation = null;
                    packageDeliverCv.setVisibility(View.VISIBLE);
                    accept_Bt.setVisibility(View.INVISIBLE);
                    declinePkg.setVisibility(View.INVISIBLE);
                    sourceMarkerOption = new MarkerOptions().position(sourceLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.destination));
                    sourceMarker = googleMap.addMarker(sourceMarkerOption);
                    for (int i = 0; i < lon.length; i++) {
                        if (lat[i].equals("NULL") || lon[i].equals("NULL")) {
                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sourceLatLng));
                            return;
                        }
                        myLocation = new LatLng(Double.parseDouble(lat[i]), Double.parseDouble(lon[i]));
                        markerOptions = new MarkerOptions().position(myLocation);
                        googleMap.addMarker(markerOptions);
                    }

                } else {

                }
            }

            @Override
            public void onFailure(Call<OrderRetroClass> call, Throwable t) {

            }
        });


        if (status == 2 || status == 1) {

            changeUI();
            int time = getTimeSharedPreference();
            if (status == 2) {
//                packageDeliverCv.setVisibility(View.VISIBLE);
//                RealmResults<OrderDbClass> result = realm.where(OrderDbClass.class).findAll();
//                if (result.size() > 0) {
//                    OrderDbClass order = result.get(0);
//                    locationTv.setText(order.getRestaurantAddress());
//                    destinationTv.setText(order.getDestinationsAddress());
//                    costTv.setText(order.getCost());
//                    packagereceiverName.setText(order.getReceiverName());
//                    String tempLon = order.getLon();
//                    String tempLat = order.getLat();
//                    lon = tempLon.split("#");
//                    lat = tempLat.split("#");
//                    googleMap.clear();
//                    for (int i = 0; i < lon.length; i++) {
//                        if (lat[i].equals("NULL") || lon[i].equals("NULL"))
//                            return;
//                        LatLng myLocation = new LatLng(Double.parseDouble(lat[i]), Double.parseDouble(lon[i]));
//                        markerOptions = new MarkerOptions().position(myLocation);
//                        googleMap.addMarker(markerOptions);
//                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
//                    }
//                }
//
//                orderId = (String) orderData.get(currentOrderIndex).get("id");

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
        password = user.getPassword();
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

            if (status == 3) {
                StartShiftRequest startShiftRequest = new StartShiftRequest(9,username,password);
                startShiftRequestCall = service.startShift(startShiftRequest);
                startShiftRequestCall.enqueue(new Callback<StartShiftRequest>() {
                    @Override
                    public void onResponse(Call<StartShiftRequest> call, Response<StartShiftRequest> response) {
                        setWorkSharedPreference(true);
                        shiftCv.setCardBackgroundColor(getColor(R.color.red));
                        shiftTv.setText("پایان");
                        status = 1;
                        isWorking = true;
                        setStatusSharedPreference(1);
                    }

                    @Override
                    public void onFailure(Call<StartShiftRequest> call, Throwable t) {

                    }
                });

            } else {

                EndShiftRequest endShiftRequest = new EndShiftRequest(10,username,password);
                endShiftRequestCall = service.endShift(endShiftRequest);
                endShiftRequestCall.enqueue(new Callback<EndShiftRequest>() {
                    @Override
                    public void onResponse(Call<EndShiftRequest> call, Response<EndShiftRequest> response) {
                        setWorkSharedPreference(false);
                        shiftCv.setCardBackgroundColor(getColor(R.color.green));
                        shiftTv.setText("شروع");
                        status = 3;
                        isWorking = false;
                        setStatusSharedPreference(3);
                    }

                    @Override
                    public void onFailure(Call<EndShiftRequest> call, Throwable t) {

                    }
                });
            }
        });
        accept_Bt.setOnClickListener(v -> {

            AcceptPackage acceptPackage = new AcceptPackage("8",username,password,orderId);
            call = service.acceptPackage(acceptPackage);
            call.enqueue(new Callback<AcceptPackage>() {
                @Override
                public void onResponse(Call<AcceptPackage> call, Response<AcceptPackage> response) {
                    if (orderData.size() < 1) {
                        return;
                    }
                    packageDeliverCv.setVisibility(View.VISIBLE);
                    accept_Bt.setVisibility(View.INVISIBLE);
                    declinePkg.setVisibility(View.INVISIBLE);
                    RealmResults<OrderDbClass> result = realm.where(OrderDbClass.class).findAll();
                    if (result.size() > 0) {
                        realm.beginTransaction();
                        realm.delete(OrderDbClass.class);
                        realm.commitTransaction();
                    }
                    //setStatusSharedPreference(2);
                    status = 2;
                    Map<String,Object> currentOrder = orderData.get(currentOrderIndex);
                    orderId = (String) currentOrder.get("id");
                    realm.beginTransaction();
                    OrderDbClass order = realm.createObject(OrderDbClass.class);
                    String tempReceiversName = String.valueOf(currentOrder.get("receiverName"));
                    tempReceiversName = tempReceiversName.replace("#","-");
                    int index = tempReceiversName.indexOf('N');
//                    tempReceiversName = tempReceiversName.substring(0,index - 1);
                    order.setRestaurantAddress(String.valueOf(currentOrder.get("address")));
                    order.setDestinationsAddress(String.valueOf(currentOrder.get("destination")));
                    order.setReceiverName(tempReceiversName);
                    order.setCost(String.valueOf(currentOrder.get("cost")));
                    order.setRestaurantName(String.valueOf(currentOrder.get("restaurantName")));
                    order.setLat(String.valueOf(currentOrder.get("lat")));
                    order.setLon(String.valueOf(currentOrder.get("lon")));
                    order.setOrderId(orderId);
                    realm.commitTransaction();
                }

                @Override
                public void onFailure(Call<AcceptPackage> call, Throwable t) {

                }
            });
        });

        packageDeliverCv.setOnClickListener(v -> {


            packageDeliverCv.setVisibility(View.INVISIBLE);
            receivedOrder = false;
            locationTv.setText("");
            destinationTv.setText("");

            FinishOrder finishOrder = new FinishOrder(15,username,password, Integer.parseInt(orderId));
            finishCall = service.finishOrder(finishOrder);
            finishCall.enqueue(new Callback<FinishOrder>() {
                @Override
                public void onResponse(Call<FinishOrder> call, Response<FinishOrder> response) {
                    RealmResults<OrderDbClass> result = realm.where(OrderDbClass.class).findAll();
                    if (result.size() > 0) {
                        realm.beginTransaction();
                        realm.delete(OrderDbClass.class);
                        realm.commitTransaction();
                    }
                    accept_Bt.setVisibility(View.VISIBLE);
                    declinePkg.setVisibility(View.VISIBLE);
                    googleMap.clear();
                    StartShiftRequest startShiftRequest = new StartShiftRequest(9,username,password);
                    startShiftRequestCall = service.startShift(startShiftRequest);
                    startShiftRequestCall.enqueue(new Callback<StartShiftRequest>() {
                        @Override
                        public void onResponse(Call<StartShiftRequest> call, Response<StartShiftRequest> response) {
                            setStatusSharedPreference(1);
                            status = 1;
                        }

                        @Override
                        public void onFailure(Call<StartShiftRequest> call, Throwable t) {

                        }
                    });
                }

                @Override
                public void onFailure(Call<FinishOrder> call, Throwable t) {

                }
            });
        });

        declinePkg.setOnClickListener(v -> {
            if (currentOrderIndex == orderData.size()) {
                Toast.makeText(this,"سفارش دیگری ندارید", Toast.LENGTH_LONG).show();
            } else {
                if (orderData.size() < 1) {
                    return;
                }
                currentOrderIndex++;
                //locationTv.setText(String.valueOf(orderData.get(currentOrderIndex).get("address")));
                //destinationTv.setText(String.valueOf(orderData.get(currentOrderIndex).get("destination")));
                setPackageInfo(String.valueOf(orderData.get(currentOrderIndex).get("address")),String.valueOf(orderData.get(currentOrderIndex).get("destination")),String.valueOf(orderData.get(currentOrderIndex).get("cost")),
                        String.valueOf(orderData.get(currentOrderIndex).get("restaurantName")),String.valueOf(orderData.get(currentOrderIndex).get("receiverName")));
                String tempLon = (String) orderData.get(currentOrderIndex).get("lon");
                String tempLat = (String) orderData.get(currentOrderIndex).get("lat");
                lon = tempLon.split("#");
                lat = tempLat.split("#");
                googleMap.clear();
                googleMap.addMarker(selfMarkerOption);
                sourceLat = Double.parseDouble((String) orderData.get(0).get("originLatitude"));
                sourceLon = Double.parseDouble((String) orderData.get(0).get("originLongitude"));
//                sourceLatLng = new LatLng(sourceLat,sourceLon);
//                sourceMarkerOption = new MarkerOptions().position(sourceLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.destination));
//                sourceMarker = googleMap.addMarker(sourceMarkerOption);
                sourceMarker = addMarkerToMap(sourceLat,sourceLon,"source",false);
                orderId = (String) orderData.get(currentOrderIndex).get("id");
                LatLng myLocation = null;
                for (int i = 0; i < lon.length; i++) {
                    if (lat[i].equals("NULL") || lon[i].equals("NULL")) {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                        currentOrderIndex++;
                        return;
                    }
                    myLocation = new LatLng(Double.parseDouble(lat[i]), Double.parseDouble(lon[i]));
                    markerOptions = new MarkerOptions().position(myLocation);
                    googleMap.addMarker(markerOptions);

                }

                googleMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));

            }

        });



    }

    private void getOrderData()
    {
        setDisposableSuccess(LocationService.orderDetail
            .subscribeWith(new DisposableObserver<List<Map<String, Object>>>() {
                @Override
                public void onNext(@NonNull List<Map<String, Object>> maps) {

                    getDisposableSuccess().dispose();

                    selfLat = gpsTracker.getLatitude();
                    selfLon = gpsTracker.getLongitude();
                    selfLatLng = new LatLng(selfLat,selfLon);
                    selfMarker.setPosition(selfLatLng);

                    if (status == 2) {
                        return;
                    }

                    if (maps.size() == 0) {
                        accept_Bt.setVisibility(View.INVISIBLE);
                        declinePkg.setVisibility(View.INVISIBLE);
                    } else {
                        accept_Bt.setVisibility(View.VISIBLE);
                        declinePkg.setVisibility(View.VISIBLE);
                    }

                    currentOrderIndex = 0;
                    orderData = maps;

                    RealmResults<OrderDbClass> result = realm.where(OrderDbClass.class).findAll();
                    if (result.size() > 0) {
                        realm.beginTransaction();
                        realm.delete(OrderDbClass.class);
                        realm.commitTransaction();
                    }
//                    setStatusSharedPreference(2);
//                    locationTv.setText(String.valueOf(maps.get(0).get("address")));
//                    destinationTv.setText(String.valueOf(maps.get(0).get("destination")));
//                    costTv.setText(String.valueOf(maps.get(0).get("cost")));
//                    restaurantNameTv.setText(String.valueOf(maps.get(0).get("restaurantName")));
//                    String tempReceiversName = String.valueOf(maps.get(0).get("receiverName"));
//                    tempReceiversName = tempReceiversName.replace("#","-");
//                    int index = tempReceiversName.indexOf('N');
//                    tempReceiversName = tempReceiversName.substring(0,index - 1);
//                    packagereceiverName.setText(tempReceiversName);
//                    sourceLat = Double.parseDouble((String) orderData.get(0).get("originLatitude"));
//                    sourceLon = Double.parseDouble((String) orderData.get(0).get("originLongitude"));
                    setPackageInfo(String.valueOf(maps.get(0).get("address")),String.valueOf(maps.get(0).get("destination")),String.valueOf(maps.get(0).get("cost")),
                            String.valueOf(maps.get(0).get("restaurantName")),String.valueOf(maps.get(0).get("receiverName")));
                    googleMap.clear();
                    sourceMarker = addMarkerToMap(sourceLat,sourceLon,"source",false);
                    String tempLon = (String) maps.get(0).get("lon");
                    String tempLat = (String) maps.get(0).get("lat");
                    lon = tempLon.split("#");
                    lat = tempLat.split("#");
                    selfMarker = addMarkerToMap(selfLat,selfLon,"driver",false);
                    LatLng myLocation = null;
                    for (int i = 0; i < lon.length ; i++) {
                        if (lat[i].equals("NULL") || lon[i].equals("NULL")) {
                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                            orderId = (String) maps.get(0).get("id");
                            getOrderData();
                            return;
                        }
                        addMarkerToMap(Double.parseDouble(lat[i]),Double.parseDouble(lon[i]),"none",false);
                        myLocation = new LatLng(Double.parseDouble(lat[i]),Double.parseDouble(lon[i]));
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

    private void setPackageInfo(String address, String destination, String cost, String restaurantName, String receiverName) {
        locationTv.setText(address);
        destinationTv.setText(String.valueOf(destination));
        costTv.setText(String.valueOf(cost));
        restaurantNameTv.setText(restaurantName);
        String tempReceiversName = String.valueOf(receiverName);
        tempReceiversName = tempReceiversName.replace("#","-");
        int index = tempReceiversName.indexOf('N');
//                    tempReceiversName = tempReceiversName.substring(0,index - 1);
        packagereceiverName.setText(tempReceiversName);
    }

    private Marker addMarkerToMap(double lat, double lon, String type, boolean move) {
        LatLng latLng = new LatLng(lat,lon);
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        switch (type) {
            case "driver":
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_motorcycle_png));
                break;
            case "source":
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.destination));
                break;
        }
        Marker marker = googleMap.addMarker(markerOptions);
        if (move) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }
        return marker;
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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.gpsTracker = new GpsTracker(this);
        this.googleMap = googleMap;
        selfLat = gpsTracker.getLatitude();
        selfLon = gpsTracker.getLongitude();
        LatLng currentLatLng = new LatLng(selfLat,selfLon);
        selfMarkerOption = new MarkerOptions().position(currentLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_motorcycle_png));
        selfMarker = googleMap.addMarker(selfMarkerOption);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,12.0f));
    }
}