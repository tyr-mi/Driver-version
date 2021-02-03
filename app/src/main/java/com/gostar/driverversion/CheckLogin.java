package com.gostar.driverversion;

import io.realm.annotations.Required;
import lombok.Getter;
import lombok.Setter;

public class CheckLogin
{
    @Required
    @Getter
    @Setter
    private String id;
    @Required
    @Getter
    @Setter
    private String username;
    @Required
    @Getter
    @Setter
    private String password;
    @Required
    @Getter
    @Setter
    private String title;

    CheckLogin(String id, String username, String password)
        {
            this.id = id;
            this.username = username;
            this.password = password;
            this.title = "user";
        }


}
