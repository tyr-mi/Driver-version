package com.gostar.driverversion;

import io.realm.annotations.Required;
import lombok.Getter;
import lombok.Setter;

public class CheckLogin
{
    @Required
    @Getter
    @Setter
    private String req_id;
    @Required
    @Getter
    @Setter
    private String username;
    @Required
    @Getter
    @Setter
    private String password;
    CheckLogin(String id, String username, String password)
        {
            this.req_id = id;
            this.username = username;
            this.password = password;
        }


}
