package com.gostar.driverversion;


import io.realm.annotations.Required;

public class CurrentTaskRequest {
    @Required

    /* renamed from: id */
    private int status;
    @Required
    private int orderId;
    @Required
    private String pass;
    private String status_message;
    @Required
    private String user;

    public int getStatus() {
        return this.status;
    }

    public String getStatus_message() {
        return this.status_message;
    }

    CurrentTaskRequest(int id, String user2, String pass2, int orderId2) {
        this.status = id;
        this.user = user2;
        this.pass = pass2;
        this.orderId = orderId2;
    }
}
