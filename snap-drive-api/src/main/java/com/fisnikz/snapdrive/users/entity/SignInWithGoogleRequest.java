package com.fisnikz.snapdrive.users.entity;

/**
 * @author Fisnik Zejnullahu
 */
public class SignInWithGoogleRequest {

    private String email;
    private String googleId;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }
}
