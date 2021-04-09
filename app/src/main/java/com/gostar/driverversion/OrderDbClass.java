package com.gostar.driverversion;

import io.realm.RealmObject;
import io.realm.annotations.Required;
import lombok.Getter;
import lombok.Setter;

public class OrderDbClass extends RealmObject {

    @Required
    @Getter
    @Setter
    public String orderId;

    @Getter
    @Setter
    public String restaurantLoc;

    @Required
    @Getter
    @Setter
    public String restaurantAddress;

    @Required
    @Getter
    @Setter
    public String receiverName;

    @Required
    @Getter
    @Setter
    public String destinationsAddress;

    @Getter
    @Setter
    public String name;

    @Required
    @Getter
    @Setter
    public String cost;

    @Getter
    @Setter
    public String phoneNumber;

    @Required
    @Getter
    @Setter
    public String restaurantName;

    @Required
    @Getter
    @Setter
    public String lon;

    @Required
    @Getter
    @Setter
    public String lat;

}
