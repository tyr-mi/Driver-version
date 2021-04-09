package com.gostar.driverversion;

public class OrderListRequest {

    private int id;

    private String user;

    private String pass;

    public OrderListRequest(int id, String user, String pass) {
        this.id = id;
        this.user = user;
        this.pass = pass;
    }
}
