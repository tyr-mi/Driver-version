package com.gostar.driverversion;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.exifinterface.media.ExifInterface;
import com.carto.styles.MarkerStyle;
import com.carto.styles.MarkerStyleBuilder;
import com.gostar.driverversion.databinding.ActivityMainRefactorBinding;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.neshan.common.model.LatLng;
import org.neshan.mapsdk.internal.utils.BitmapUtils;
import org.neshan.mapsdk.model.Marker;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivityRefactor extends AppCompatActivity implements View.OnClickListener {
    public static boolean hasActiveOrder;
    public static MainActivityRefactor mainActivityRefactor;
    public static String password;
    public static String username;
    final int REQUEST_CODE = 123;
    String TAG = ".MainActivityRefactor ;;;;;;";
    private Map<String, Object> acceptedOrder;
    /* access modifiers changed from: private */
    public ActivityMainRefactorBinding binding;
    private Call<AcceptPackage> call;
    /* access modifiers changed from: private */
    public List<Map<String, Object>> currentOrder;
    private Call<CurrentTaskRequest> currentTaskRequestCall;
    /* access modifiers changed from: private */
    public OrdersDialog dialog;
    private Call<EndShiftRequest> endShiftRequestCall;
    private Call<FinishOrder> finishCall;
    private String[] lats;
    private Disposable locationDisposable;
    private String[] longs;
    private ArrayList<Marker> markerArray = new ArrayList<>();
    private String name;
    /* access modifiers changed from: private */
    public String orderId;
    private Call<OrderRetroClass> orderListRequestCall;
    private Disposable ordersDisposable;
    private Marker originMarker;
    private Realm realm;
    private RealmResults<UserDbClass> result;
    /* access modifiers changed from: private */
    public GetDataService service;
    /* access modifiers changed from: private */
    public Call<StartShiftRequest> startShiftRequestCall;
    /* access modifiers changed from: private */
    public int status;
    private Marker userMarker;

    public Disposable getOrdersDisposable() {
        return this.ordersDisposable;
    }

    public void setOrdersDisposable(Disposable ordersDisposable2) {
        this.ordersDisposable = ordersDisposable2;
    }

    public Disposable getLocationDisposable() {
        return this.locationDisposable;
    }

    public void setLocationDisposable(Disposable locationDisposable2) {
        this.locationDisposable = locationDisposable2;
    }

    public static boolean isHasActiveOrder() {
        return hasActiveOrder;
    }

    public static void setHasActiveOrder(boolean hasActiveOrder2) {
        hasActiveOrder = hasActiveOrder2;
    }

    public String getOrderId() {
        return this.orderId;
    }

    public void setOrderId(String orderId2) {
        this.orderId = orderId2;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        ActivityMainRefactorBinding inflate = ActivityMainRefactorBinding.inflate(getLayoutInflater());
        this.binding = inflate;
        setContentView(inflate.getRoot());
        mainActivityRefactor = this;
        getDatabase();
        prepareNetworkReq();
        clickListeners();
    }

    private void clickListeners() {
        this.binding.packageActionButton.setOnClickListener(this);
    }

    private void getDatabase() {
        realm = Realm.getDefaultInstance();
        result = realm.where(UserDbClass.class).findAll();
        UserDbClass user = result.get(0);
        username = user.getUsername();
        this.name = user.getName();
        password = user.getPassword();
    }

    private void prepareNetworkReq() {
        this.service = (GetDataService) RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        initLayoutReferences();
        startReceivingLocationUpdates();
        startReceivingOrders();
    }

    private void startReceivingOrders() {
        setOrdersDisposable(FirebaseService.orderPublishSubject.subscribeWith(new DisposableObserver<Map<String, String>>() {
            public void onNext(Map<String, String> map) {
            }

            public void onError(Throwable e) {
            }

            public void onComplete() {
            }
        }));
        setOrdersDisposable(FirebaseService.orderPublishSubject
            .subscribeWith(new DisposableObserver<Map<String, String>() {
                @Override
                public void onNext(@NonNull Map<String, String> stringObjectMap) {

                }

                @Override
                public void onError(@NonNull Throwable e) {

                }

                @Override
                public void onComplete() {

                }
            }));
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            if (resultCode == -1) {
                Log.e(this.TAG, "User agreed to make required location settings changes.");
            } else if (resultCode == 0) {
                Log.e(this.TAG, "User chose not to make required location settings changes.");
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
    }

    private void initLayoutReferences() {
        initViews();
        initMap();
    }

    private void initViews() {
        getCurrentOrder();
        getUserLocation();
    }

    private void initMap() {
        this.binding.neshanMap.moveCamera(new LatLng(36.68140547d, 48.49236757d), 0.0f);
        this.binding.neshanMap.setZoom(12.0f, 0.0f);
    }

    public void startReceivingLocationUpdates() {
        Dexter.withActivity(this).withPermission("android.permission.ACCESS_FINE_LOCATION").withListener(new PermissionListener() {
            public void onPermissionGranted(PermissionGrantedResponse response) {
            }

            public void onPermissionDenied(PermissionDeniedResponse response) {
                if (response.isPermanentlyDenied()) {
                    MainActivityRefactor.this.openSettings();
                }
            }

            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

    private void getCurrentOrder() {
        Call<OrderRetroClass> orderList = this.service.orderList(new OrderListRequest(26, username, password));
        this.orderListRequestCall = orderList;
        orderList.enqueue(new Callback<OrderRetroClass>() {
            public void onResponse(Call<OrderRetroClass> call, Response<OrderRetroClass> response) {
                OrderRetroClass orderRetroClass = response.body();
                if (orderRetroClass != null) {
                    orderRetroClass.convert();
                }
                List unused = MainActivityRefactor.this.currentOrder = orderRetroClass.mapList;
                if (MainActivityRefactor.this.currentOrder.size() > 0) {
                    MainActivityRefactor mainActivityRefactor = MainActivityRefactor.this;
                    mainActivityRefactor.orderId = String.valueOf(((Map) mainActivityRefactor.currentOrder.get(0)).get("id"));
                    MainActivityRefactor mainActivityRefactor2 = MainActivityRefactor.this;
                    status = Integer.parseInt(String.valueOf(((Map) mainActivityRefactor2.currentOrder.get(0)).get(NotificationCompat.CATEGORY_STATUS)));
                    MainActivityRefactor.this.binding.orderDetailLayout.setVisibility(View.VISIBLE);
                    MainActivityRefactor.setHasActiveOrder(true);
                    MainActivityRefactor mainActivityRefactor3 = MainActivityRefactor.this;
                    mainActivityRefactor3.setAcceptedOrder((Map) mainActivityRefactor3.currentOrder.get(0));
                    MainActivityRefactor.this.satisfyStatus();
                    LocationService.calculate = true;
                    return;
                }
                MainActivityRefactor.this.binding.orderDetailLayout.setVisibility(View.INVISIBLE);
                MainActivityRefactor.setHasActiveOrder(false);
                LocationService.calculate = false;
                LocationService.resetTime();
                LocationService.resetTotalTime();
                MainActivityRefactor.this.getOrders();
            }

            public void onFailure(Call<OrderRetroClass> call, Throwable t) {
            }
        });
    }

    public void getOrders() {
        setOrdersDisposable(LocationService.orderDetail.subscribeWith(new DisposableObserver<List<Map<String, Object>>>() {
            public void onNext(List<Map<String, Object>> orders) {
                MainActivityRefactor.this.getOrdersDisposable().dispose();
                if (MainActivityRefactor.this.dialog == null || !MainActivityRefactor.this.dialog.isShowing()) {
                    MainActivityRefactor mainActivityRefactor = MainActivityRefactor.this;
                    MainActivityRefactor mainActivityRefactor2 = MainActivityRefactor.this;
                    OrdersDialog unused = mainActivityRefactor.dialog = new OrdersDialog(mainActivityRefactor2, orders, mainActivityRefactor2);
                    MainActivityRefactor.this.dialog.show();
                }
                MainActivityRefactor.this.getOrders();
            }

            public void onError(Throwable e) {
            }

            public void onComplete() {
            }
        }));
    }

    /* access modifiers changed from: private */
    public void openSettings() {
        Intent intent = new Intent();
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", BuildConfig.APPLICATION_ID, (String) null));
        startActivity(intent);
    }

    /* access modifiers changed from: private */
    public void getUserLocation() {
        setLocationDisposable((Disposable) LocationService.locationPublish.subscribeWith(new DisposableObserver<String>() {
            public void onNext(String coordinates) {
                MainActivityRefactor.this.getLocationDisposable().dispose();
                String[] latLng = coordinates.split("#");
                MainActivityRefactor.this.binding.neshanMap.moveCamera(new LatLng(Double.parseDouble(latLng[0]), Double.parseDouble(latLng[1])), 0.0f);
                MainActivityRefactor.this.binding.neshanMap.setZoom(12.0f, 0.0f);
                MainActivityRefactor.this.showUserMarker(latLng[0], latLng[1]);
                MainActivityRefactor.this.getUserLocation();
            }

            public void onError(Throwable e) {
            }

            public void onComplete() {
            }
        }));
    }

    public void setAcceptedOrder(Map<String, Object> order) {
        LocationService.calculate = true;
        setHasActiveOrder(true);
        setOrderId((String) order.get("id"));
        this.binding.orderDetailLayout.setVisibility(View.VISIBLE);
        this.acceptedOrder = order;
        this.lats = ((String) order.get("lat")).split("#");
        this.longs = ((String) order.get("lon")).split("#");
        String lat = String.valueOf(order.get("originLatitude"));
        String lon = String.valueOf(order.get("originLongitude"));
        String cost = convertToFa((String) order.get("cost")) + " تومان ";
        showOriginMarker(lat, lon);
        showDestOnMap();
    }

    /* access modifiers changed from: private */
    public void showUserMarker(String lat, String lon) {
        LatLng origin = new LatLng((double) Float.parseFloat(lat), (double) Float.parseFloat(lon));
        MarkerStyleBuilder markStCr = new MarkerStyleBuilder();
        markStCr.setSize(30.0f);
        markStCr.setBitmap(BitmapUtils.createBitmapFromAndroidBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_motor)));
        MarkerStyle markSt = markStCr.buildStyle();
        if (this.userMarker != null) {
            this.binding.neshanMap.removeMarker(this.userMarker);
        }
        this.userMarker = new Marker(origin, markSt);
        this.binding.neshanMap.addMarker(this.userMarker);
    }

    private void showOriginMarker(String lat, String lon) {
        LatLng origin = new LatLng((double) Float.parseFloat(lat), (double) Float.parseFloat(lon));
        MarkerStyleBuilder markStCr = new MarkerStyleBuilder();
        markStCr.setSize(30.0f);
        markStCr.setBitmap(BitmapUtils.createBitmapFromAndroidBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_restaurant_png)));
        this.originMarker = new Marker(origin, markStCr.buildStyle());
        this.binding.neshanMap.addMarker(this.originMarker);
        this.markerArray.add(this.originMarker);
    }

    private void showDestOnMap() {
        int index = 0;
        MarkerStyleBuilder markStCr = new MarkerStyleBuilder();
        markStCr.setSize(30.0f);
        markStCr.setBitmap(BitmapUtils.createBitmapFromAndroidBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_home_png)));
        MarkerStyle markSt = markStCr.buildStyle();
        String[] strArr = this.lats;
        int length = strArr.length;
        int i = 0;
        while (i < length) {
            String lat = strArr[i];
            if (!lat.equals("NULL")) {
                Marker marker = new Marker(new LatLng((double) Float.parseFloat(lat), (double) Float.parseFloat(this.longs[index])), markSt);
                this.markerArray.add(marker);
                this.binding.neshanMap.addMarker(marker);
                index++;
                i++;
            } else {
                return;
            }
        }
    }

    private String convertToFa(String faNumbers) {
        for (String[] num : new String[][]{new String[]{"0", "۰"}, new String[]{"1", "۱"}, new String[]{ExifInterface.GPS_MEASUREMENT_2D, "۲"}, new String[]{ExifInterface.GPS_MEASUREMENT_3D, "۳"}, new String[]{"4", "۴"}, new String[]{"5", "۵"}, new String[]{"6", "۶"}, new String[]{"7", "۷"}, new String[]{"8", "۸"}, new String[]{"9", "۹"}}) {
            faNumbers = faNumbers.replace(num[0], num[1]);
        }
        return faNumbers;
    }

    private void finishOrder() {
        Call<FinishOrder> finishOrder = this.service.finishOrder(new FinishOrder(15, username, password, Integer.parseInt(this.orderId), LocationService.getDistance(), 0, 0));
        this.finishCall = finishOrder;
        finishOrder.enqueue(new Callback<FinishOrder>() {
            public void onResponse(Call<FinishOrder> call, Response<FinishOrder> response) {
                FinishOrder order = response.body();
                if (order == null) {
                    return;
                }
                if (order.getStatus() == 1) {
                    StartShiftRequest startShiftRequest = new StartShiftRequest(9, MainActivityRefactor.username, MainActivityRefactor.password);
                    MainActivityRefactor mainActivityRefactor = MainActivityRefactor.this;
                    Call unused = mainActivityRefactor.startShiftRequestCall = mainActivityRefactor.service.startShift(startShiftRequest);
                    MainActivityRefactor.this.startShiftRequestCall.enqueue(new Callback<StartShiftRequest>() {
                        public void onResponse(Call<StartShiftRequest> call, Response<StartShiftRequest> response) {
                            StartShiftRequest start = response.body();
                            if (start == null || start.getStatus() != 1) {
                                Toast.makeText(MainActivityRefactor.this.getApplicationContext(), "در بروزرسانی موقعیت شما مشکلی پیش آمده است", Toast.LENGTH_LONG).show();
                                return;
                            }
                            Toast.makeText(MainActivityRefactor.this.getApplicationContext(), "وضعیت شما با موفقیت بروزرسانی شد", Toast.LENGTH_LONG).show();
                            MainActivityRefactor.this.getOrders();
                        }

                        public void onFailure(Call<StartShiftRequest> call, Throwable t) {
                            Toast.makeText(MainActivityRefactor.this.getApplicationContext(), "در بروزرسانی موقعیت شما مشکلی پیش آمده است", Toast.LENGTH_LONG).show();
                        }
                    });
                    return;
                }
                Toast.makeText(MainActivityRefactor.mainActivityRefactor, "در اتمام سفر مشکلی پیش آمده است لطفا دوباره تلاش کنید", Toast.LENGTH_LONG).show();
            }

            public void onFailure(Call<FinishOrder> call, Throwable t) {
            }
        });
    }

    private void deliver() {
        Call<CurrentTaskRequest> currentTask = this.service.getCurrentTask(new CurrentTaskRequest(54, username, password, Integer.parseInt(this.orderId)));
        this.currentTaskRequestCall = currentTask;
        currentTask.enqueue(new Callback<CurrentTaskRequest>() {
            public void onResponse(Call<CurrentTaskRequest> call, Response<CurrentTaskRequest> response) {
                CurrentTaskRequest currentTask = response.body();
                if (currentTask != null) {
                    int unused = MainActivityRefactor.this.status = currentTask.getStatus();
                    MainActivityRefactor.this.satisfyStatus();
                }
            }

            public void onFailure(Call<CurrentTaskRequest> call, Throwable t) {
            }
        });
    }

    /* access modifiers changed from: private */
    public void satisfyStatus() {
        switch (this.status) {
            case 3:
                clearMap();
                this.binding.orderDetailLayout.setVisibility(View.INVISIBLE);
                setHasActiveOrder(false);
                this.markerArray = new ArrayList<>();
                this.orderId = "0";
                this.userMarker = null;
                finishOrder();
                return;
            case 5:
                this.binding.packageDetailText.setText("بسته را تحویل بگیرید");
                return;
            case 6:
                this.binding.packageDetailText.setText("مقصد اول");
                clearMap();
                showOneMarker(1);
                return;
            case 7:
                this.binding.packageDetailText.setText("مقصد دوم");
                clearMap();
                showOneMarker(2);
                return;
            case 8:
                this.binding.packageDetailText.setText("مقصد سوم");
                clearMap();
                showOneMarker(3);
                return;
            case 9:
                this.binding.packageDetailText.setText("مقصد چهارم");
                clearMap();
                showOneMarker(4);
                return;
            default:
                return;
        }
    }

    private int getCurrentStatus() {
        if (this.currentOrder.size() > 0) {
            return ((Integer) this.currentOrder.get(0).get(NotificationCompat.CATEGORY_STATUS)).intValue();
        }
        return 0;
    }

    private void showOneMarker(int index) {
        this.binding.neshanMap.addMarker(this.markerArray.get(index));
    }

    private void clearMap() {
        Iterator<Marker> it = this.markerArray.iterator();
        while (it.hasNext()) {
            this.binding.neshanMap.removeMarker(it.next());
        }
    }

    public void onClick(View view) {
        if (view.getId() == R.id.package_action_button) {
            deliver();
        }
    }
}
