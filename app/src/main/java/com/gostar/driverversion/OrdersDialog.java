package com.gostar.driverversion;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.exifinterface.media.ExifInterface;
import com.carto.styles.MarkerStyle;
import com.carto.styles.MarkerStyleBuilder;
import com.gostar.driverversion.databinding.DialogOrdersBinding;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.neshan.common.model.LatLng;
import org.neshan.mapsdk.internal.utils.BitmapUtils;
import org.neshan.mapsdk.model.Marker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersDialog extends AppCompatDialog implements View.OnClickListener {
    private DialogOrdersBinding binding;
    private Call<AcceptPackage> callAccept;
    /* access modifiers changed from: private */
    public int index = 0;
    private String[] lats;
    private String[] longs;
    /* access modifiers changed from: private */
    public MainActivityRefactor mainActivityRefactor;
    private ArrayList<Marker> markerArray = new ArrayList<>();
    /* access modifiers changed from: private */
    public List<Map<String, Object>> orders;
    private Marker originMarker;
    private GetDataService service;
    private View view;

    public OrdersDialog(Context context, List<Map<String, Object>> orders2, MainActivityRefactor mainActivityRefactor2) {
        super(context);
        this.mainActivityRefactor = mainActivityRefactor2;
        this.orders = orders2;
        inflateView();
    }

    private void inflateView() {
        DialogOrdersBinding inflate = DialogOrdersBinding.inflate(getLayoutInflater());
        this.binding = inflate;
        ConstraintLayout root = inflate.getRoot();
        this.view = root;
        setContentView((View) root);
        getWindow().setLayout(-1, -1);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        setOrderProperties(0);
        this.binding.ordersMap.moveCamera(new LatLng(36.68140547d, 48.49236757d), 0.0f);
        this.binding.ordersMap.setZoom(12.0f, 0.0f);
        prepareNetworkReq();
        clickListeners();
    }

    private void prepareNetworkReq() {
        this.service = (GetDataService) RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
    }

    private void clickListeners() {
        this.binding.ordersDialogBack.setOnClickListener(this);
        this.binding.ordersDialogNext.setOnClickListener(this);
        this.binding.ordersDialogDecline.setOnClickListener(this);
        this.binding.ordersDialogAccept.setOnClickListener(this);
    }

    private void setOrderProperties(int index2) {
        Map<String, Object> order = this.orders.get(index2);
        this.binding.ordersDetailPrice.setText((String) order.get("cost"));
        this.lats = ((String) order.get("lat")).split("#");
        this.longs = ((String) order.get("lon")).split("#");
        String lat = String.valueOf(order.get("originLatitude"));
        String lon = String.valueOf(order.get("originLongitude"));
        String cost = convertToFa((String) order.get("cost"));
        this.binding.ordersDetailPrice.setText(cost + " تومان ");
        showDestOnMap();
        showOriginMarker(lat, lon);
    }

    private void showOriginMarker(String lat, String lon) {
        LatLng origin = new LatLng((double) Float.parseFloat(lat), (double) Float.parseFloat(lon));
        MarkerStyleBuilder markStCr = new MarkerStyleBuilder();
        markStCr.setSize(30.0f);
        markStCr.setBitmap(BitmapUtils.createBitmapFromAndroidBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_restaurant_png)));
        this.originMarker = new Marker(origin, markStCr.buildStyle());
        this.binding.ordersMap.addMarker(this.originMarker);
        this.markerArray.add(this.originMarker);
    }

    private void showDestOnMap() {
        int index2 = 0;
        MarkerStyleBuilder markStCr = new MarkerStyleBuilder();
        markStCr.setSize(30.0f);
        markStCr.setBitmap(BitmapUtils.createBitmapFromAndroidBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_home_png)));
        MarkerStyle markSt = markStCr.buildStyle();
        String[] strArr = this.lats;
        int length = strArr.length;
        int i = 0;
        while (i < length) {
            String lat = strArr[i];
            if (!lat.equals("NULL")) {
                Marker marker = new Marker(new LatLng((double) Float.parseFloat(lat), (double) Float.parseFloat(this.longs[index2])), markSt);
                this.markerArray.add(marker);
                this.binding.ordersMap.addMarker(marker);
                index2++;
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

    private void clearMap() {
        Iterator<Marker> it = this.markerArray.iterator();
        while (it.hasNext()) {
            this.binding.ordersMap.removeMarker(it.next());
        }
    }

    private void acceptPackage() {
        Call<AcceptPackage> acceptPackage = this.service.acceptPackage(new AcceptPackage("8", MainActivityRefactor.username, MainActivityRefactor.password, (String) this.orders.get(this.index).get("id")));
        this.callAccept = acceptPackage;
        acceptPackage.enqueue(new Callback<AcceptPackage>() {
            public void onResponse(Call<AcceptPackage> call, Response<AcceptPackage> response) {
                AcceptPackage acceptPackage = response.body();
                if (acceptPackage.getStatus().equals("1")) {
                    OrdersDialog.this.mainActivityRefactor.setAcceptedOrder((Map) OrdersDialog.this.orders.get(OrdersDialog.this.index));
                } else if (!acceptPackage.getStatus().equals("0")) {
                    Toast.makeText(OrdersDialog.this.getContext(), "در دریافت سفارش مشکلی پیش آمده", Toast.LENGTH_LONG).show();
                }
                OrdersDialog.this.dismiss();
            }

            public void onFailure(Call<AcceptPackage> call, Throwable t) {
            }
        });
    }

    public void onClick(View view2) {
        switch (view2.getId()) {
            case R.id.orders_dialog_accept /*2131231033*/:
                acceptPackage();
                return;
            case R.id.orders_dialog_back /*2131231034*/:
                if (this.index == 0) {
                    Toast.makeText(getContext(), "سفارش دیگری نیست", Toast.LENGTH_LONG).show();
                    return;
                }
                clearMap();
                int i = this.index - 1;
                this.index = i;
                setOrderProperties(i);
                return;
            case R.id.orders_dialog_decline /*2131231036*/:
                dismiss();
                return;
            case R.id.orders_dialog_next /*2131231039*/:
                if (this.index == this.orders.size() - 1) {
                    Toast.makeText(getContext(), "سفارش دیگری نیست", Toast.LENGTH_LONG).show();
                    return;
                }
                this.index++;
                clearMap();
                setOrderProperties(this.index);
                return;
            default:
                return;
        }
    }
}
