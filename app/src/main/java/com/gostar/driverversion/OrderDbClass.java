package com.gostar.driverversion;

import io.realm.RealmObject;
import io.realm.annotations.Required;
import lombok.Getter;
import lombok.Setter;

public class OrderDbClass extends RealmObject {

    @Required
    @Getter
    @Setter
    public String destination;

    @Required
    @Getter
    @Setter
    public String address;

    @Required
    @Getter
    @Setter
    public String name;

}
