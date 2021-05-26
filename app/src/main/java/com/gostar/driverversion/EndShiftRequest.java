package com.gostar.driverversion;


import io.realm.annotations.Required;

public class EndShiftRequest {
    @Required

    /* renamed from: id */
    private int id;
    @Required
    private String pass;
    @Required
    private String user;

    public EndShiftRequest(int id, String user2, String pass2) {
        this.id = id;
        this.user = user2;
        this.pass = pass2;
    }
}
