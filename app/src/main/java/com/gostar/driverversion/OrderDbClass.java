package com.gostar.driverversion;

import io.realm.RealmObject;
import io.realm.annotations.Required;
import lombok.Getter;
import lombok.Setter;

public class OrderDbClass extends RealmObject {

    @Required
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
    public String destination2sLoc;

    @Required
    @Getter
    @Setter
    public String destination3sLoc;

    @Required
    @Getter
    @Setter
    public String destination4sLoc;

    @Required
    @Getter
    @Setter
    public String destinationsLoc;

    @Required
    @Getter
    @Setter
    public String destinationsAddress;

    @Required
    @Getter
    @Setter
    public String destinations2Address;

    @Required
    @Getter
    @Setter
    public String destination3sAddress;

    @Required
    @Getter
    @Setter
    public String destination4sAddress;

    @Required
    @Getter
    @Setter
    public String name;

    @Required
    @Getter
    @Setter
    public String cost;

    @Required
    @Getter
    @Setter
    public String time;


    @Getter
    @Setter
    public int num_of_dests;

    @Required
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
