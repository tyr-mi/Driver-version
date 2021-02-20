package com.gostar.driverversion;

import javax.annotation.RegEx;

import io.realm.annotations.Required;

public class FinishOrder {

    @Required
    private int id;

    @Required
    private String user;

    @Required
    private String pass;

    @Required
    private int orderId;

    public FinishOrder(int id, String user, String pass, int orderId) {
        this.id = id;
        this.user = user;
        this.pass = pass;
        this.orderId = orderId;
    }
}
