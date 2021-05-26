package com.gostar.driverversion;


import io.realm.annotations.Required;

public class AcceptPackage {
    @Required

    /* renamed from: id */
    private String f275id;
    @Required
    private String orderId;
    @Required
    private String pass;
    private String status;
    private String statusMessage;
    @Required
    private String user;

    public String getId() {
        return this.f275id;
    }

    public void setId(String id) {
        this.f275id = id;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user2) {
        this.user = user2;
    }

    public String getPass() {
        return this.pass;
    }

    public void setPass(String pass2) {
        this.pass = pass2;
    }

    public String getOrderId() {
        return this.orderId;
    }

    public void setOrderId(String orderId2) {
        this.orderId = orderId2;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status2) {
        this.status = status2;
    }

    public String getStatusMessage() {
        return this.statusMessage;
    }

    public void setStatusMessage(String statusMessage2) {
        this.statusMessage = statusMessage2;
    }

    AcceptPackage(String id, String user2, String pass2, String orderId2) {
        this.f275id = id;
        this.user = user2;
        this.pass = pass2;
        this.orderId = orderId2;
    }
}
