package com.gostar.driverversion;


import io.realm.RealmObject;
import io.realm.annotations.Required;

public class UserDbClass extends RealmObject  {
    @Required
    private String name;
    @Required
    private String password;
    @Required
    private String username;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
