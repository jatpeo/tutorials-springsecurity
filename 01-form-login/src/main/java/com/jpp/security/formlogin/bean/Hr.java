package com.jpp.security.formlogin.bean;

import java.io.Serializable;

public class Hr {

    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Hr(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public Hr() {

    }
}
