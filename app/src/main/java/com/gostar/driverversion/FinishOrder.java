package com.gostar.driverversion;


import io.realm.annotations.Required;

public class FinishOrder {
    @Required
    private int deliverTime;
    @Required
    private int distance;
    @Required

    /* renamed from: id */
    private int id;
    @Required
    private int orderId;
    @Required
    private String pass;
    private int status;
    private String status_message;
    @Required
    private int totalTime;
    @Required
    private String user;

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatus_message() {
        return this.status_message;
    }

    public void setStatus_message(String status_message2) {
        this.status_message = status_message2;
    }

    public FinishOrder(int id, String user2, String pass2, int orderId2, int distance2, int deliverTime2, int totalTime2) {
        this.id = id;
        this.user = user2;
        this.pass = pass2;
        this.orderId = orderId2;
        this.distance = distance2;
        this.deliverTime = deliverTime2;
        this.totalTime = totalTime2;
    }
}
