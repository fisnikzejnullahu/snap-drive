package com.fisnikz.snapdrive.users.entity;

/**
 * @author Fisnik Zejnullahu
 */
public class UserLoginResponse {

    private String username;
    private String privateKey;

    public UserLoginResponse() {
    }

    public UserLoginResponse(String username, String privateKey) {
        this.username = username;
        this.privateKey = privateKey;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
}
