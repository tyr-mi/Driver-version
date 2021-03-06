package com.gostar.driverversion;


import io.realm.annotations.Required;

public class StartShiftRequest {
    @Required

    /* renamed from: id */
    private int status;
    @Required
    private String pass;
    private String status_message;
    @Required
    private String user;

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status2) {
        this.status = status2;
    }

    public String getStatus_message() {
        return this.status_message;
    }

    public void setStatus_message(String status_message2) {
        this.status_message = status_message2;
    }

    public StartShiftRequest(int id, String user2, String pass2) {
        this.status = id;
        this.user = user2;
        this.pass = pass2;
    }
}
