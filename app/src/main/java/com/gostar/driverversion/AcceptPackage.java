package com.gostar.driverversion;

import io.realm.annotations.Required;
import lombok.Getter;
import lombok.Setter;
import retrofit2.http.GET;

public class AcceptPackage {

    @Required
    @Getter
    @Setter
    private String id;

    @Required
    @Getter
    @Setter
    private String user;

    @Required
    @Getter
    @Setter
    private String pass;

    @Required
    @Getter
    @Setter
    private String orderId;

    AcceptPackage(String id, String user , String pass , String orderId) {
        this.id = id;
        this.user = user;
        this.pass = pass;
        this.orderId = orderId;
    }

}
