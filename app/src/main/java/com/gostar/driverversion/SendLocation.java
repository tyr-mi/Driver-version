package com.gostar.driverversion;


import io.realm.annotations.Required;

public class SendLocation {
    @Required

    /* renamed from: id */
    private String status;
    @Required
    private Double lat;
    @Required
    private Double lon;
    @Required
    private String pass;
    @Required
    private String user;

    public String getId() {
        return this.status;
    }

    public void setId(String id) {
        this.status = id;
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

    public Double getLat() {
        return this.lat;
    }

    public void setLat(Double lat2) {
        this.lat = lat2;
    }

    public Double getLon() {
        return this.lon;
    }

    public void setLon(Double lon2) {
        this.lon = lon2;
    }

    public SendLocation(String id, String user2, Double lat2, Double lon2, String pass2) {
        this.status = id;
        this.user = user2;
        this.lat = lat2;
        this.lon = lon2;
        this.pass = pass2;
    }
}
