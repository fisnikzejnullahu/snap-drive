package com.fisnikz.snapdrive.crypto.entity;

/**
 * @author Fisnik Zejnullahu
 */
public class SharedUsers {

    private String userId;
    private String base64FileKey;

    public SharedUsers() {
    }

    public SharedUsers(String userId, String base64FileKey) {
        this.userId = userId;
        this.base64FileKey = base64FileKey;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBase64FileKey() {
        return base64FileKey;
    }

    public void setBase64FileKey(String base64FileKey) {
        this.base64FileKey = base64FileKey;
    }

    @Override
    public String toString() {
        return "SharedUsers{" +
                "userId='" + userId + '\'' +
                ", base64FileKey='" + base64FileKey + '\'' +
                '}';
    }
}
