package com.gostar.driverversion;

import io.realm.annotations.Required;
import lombok.Getter;
import lombok.Setter;

public class SendLocation
{
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
    private Double lat;

    @Required
    @Getter
    @Setter
    private Double lon;


    SendLocation(String id, String user, Double lat, Double lon, String pass)
    {
        this.id=id;
        this.user=user;
        this.lat=lat;
        this.lon=lon;
        this.pass = pass;
    }

}
