package com.gostar.driverversion;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderRetroClass {

    @SerializedName("records")
    @Expose
    private JsonArray records;
    public List<Map<String,Object>> mapList = new ArrayList<>();

    public JsonArray getStatus() {
        return records;
    }

    public void setStatus(JsonArray status) {
        this.records = status;
    }

    public void convert(){
        Type mapType = new TypeToken<List<Map<String, Object>>>() {
        }.getType();
        this.mapList = new Gson().fromJson(this.records, mapType);
    }

}
