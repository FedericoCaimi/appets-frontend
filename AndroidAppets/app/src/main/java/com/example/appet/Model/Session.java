package com.example.appet.Model;

import java.util.Date;

public class Session {

    private String token;
    private String userId;
    private Date dateLogged;
    private boolean isDeleted;

    public String getUserId() {
        return userId;
    }

    public Date getDateLogged() {
        return dateLogged;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public String getToken() {
        return token;
    }
}
