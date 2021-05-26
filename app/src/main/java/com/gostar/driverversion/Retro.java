package com.gostar.driverversion;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Retro {
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("status_message")
    @Expose
    private String statusMessage;

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status2) {
        this.status = status2;
    }

    public String getStatusMessage() {
        return this.statusMessage;
    }

    public void setStatusMessage(String statusMessage2) {
        this.statusMessage = statusMessage2;
    }
}
