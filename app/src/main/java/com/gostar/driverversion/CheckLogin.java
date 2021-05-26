package com.gostar.driverversion;


import io.realm.annotations.Required;

public class CheckLogin {
    @Required

    /* renamed from: id */
    private int id;
    @Required
    private String pass;
    @Required
    private String title = "user";
    @Required
    private String user;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user2) {
        this.user = user2;
    }

    public String getPass() {
        return this.pass;
    }

    public void setPass(String pass2) {
        this.pass = pass2;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title2) {
        this.title = title2;
    }

    CheckLogin(int id, String username, String password) {
        this.id = id;
        this.user = username;
        this.pass = password;
    }
}
