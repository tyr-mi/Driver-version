package com.gostar.driverversion;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderRetroClass {
    public List<Map<String, Object>> mapList = new ArrayList();
    @SerializedName("records")
    @Expose
    private JsonArray records;

    public JsonArray getStatus() {
        return this.records;
    }

    public void setStatus(JsonArray status) {
        this.records = status;
    }

    public void convert() {
        this.mapList = (List) new Gson().fromJson((JsonElement) this.records, new TypeToken<List<Map<String, Object>>>() {
        }.getType());
    }
}
