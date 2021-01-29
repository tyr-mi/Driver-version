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
    private Double lat;

    @Required
    @Getter
    @Setter
    private Double lon;

    @Required
    @Getter
    @Setter
    private String status;
    SendLocation(String id, String user, Double lat, Double lon, String status)
    {
        this.id=id;
        this.user=user;
        this.lat=lat;
        this.lon=lon;
        this.status=status;
    }

}
