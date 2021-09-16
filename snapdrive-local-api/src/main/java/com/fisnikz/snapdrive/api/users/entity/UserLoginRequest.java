package com.fisnikz.snapdrive.api.users.entity;

/**
 * @author Fisnik Zejnullahu
 */
public class UserLoginRequest {

    private String email;

    public UserLoginRequest() {
    }

    public UserLoginRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
