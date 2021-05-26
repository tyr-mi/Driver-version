package com.gostar.driverversion;


import io.realm.RealmObject;
import io.realm.annotations.Required;

public class OrderDbClass extends RealmObject {
    @Required
    public String cost;
    @Required
    public String destinationsAddress;
    @Required
    public String lat;
    @Required
    public String lon;
    public String name;
    @Required
    public String orderId;
    public String phoneNumber;
    @Required
    public String receiverName;
    @Required
    public String restaurantAddress;
    public String restaurantLoc;
    @Required
    public String restaurantName;



    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId=orderId;
    }

    public String getRestaurantLoc() {
        return restaurantLoc;
    }

    public void setRestaurantLoc(String restaurantLoc) {
        this.;
    }

    public String getRestaurantAddress() {
        return realmGet$restaurantAddress();
    }

    public void setRestaurantAddress(String restaurantAddress2) {
        realmSet$restaurantAddress(restaurantAddress2);
    }

    public String getReceiverName() {
        return realmGet$receiverName();
    }

    public void setReceiverName(String receiverName2) {
        realmSet$receiverName(receiverName2);
    }

    public String getDestinationsAddress() {
        return realmGet$destinationsAddress();
    }

    public void setDestinationsAddress(String destinationsAddress2) {
        realmSet$destinationsAddress(destinationsAddress2);
    }

    public String getName() {
        return realmGet$name();
    }

    public void setName(String name2) {
        realmSet$name(name2);
    }

    public String getCost() {
        return realmGet$cost();
    }

    public void setCost(String cost2) {
        realmSet$cost(cost2);
    }

    public String getPhoneNumber() {
        return realmGet$phoneNumber();
    }

    public void setPhoneNumber(String phoneNumber2) {
        realmSet$phoneNumber(phoneNumber2);
    }

    public String getRestaurantName() {
        return realmGet$restaurantName();
    }

    public void setRestaurantName(String restaurantName2) {
        realmSet$restaurantName(restaurantName2);
    }

    public String getLon() {
        return realmGet$lon();
    }

    public void setLon(String lon2) {
        realmSet$lon(lon2);
    }

    public String getLat() {
        return realmGet$lat();
    }

    public void setLat(String lat2) {
        realmSet$lat(lat2);
    }
}
