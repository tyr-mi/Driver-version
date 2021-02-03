package com.gostar.driverversion;

import io.realm.annotations.Required;
import lombok.Getter;
import lombok.Setter;

public class CheckLogin
{
    @Required
    @Getter
    @Setter
    private int id;
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
    private String title;

    CheckLogin(int id, String username, String password)
        {
            this.id = id;
            this.user = username;
            this.pass = password;
            this.title = "user";
        }


}
