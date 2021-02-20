package com.gostar.driverversion;

import io.realm.annotations.Required;

public class StartShiftRequest {

    @Required
    private int id;

    @Required
    private String user;

    @Required
    private String pass;

    public StartShiftRequest(int id, String user, String pass) {
        this.id = id;
        this.user = user;
        this.pass = pass;
    }
}
