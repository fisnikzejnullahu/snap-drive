package com.fisnikz.snapdrive.api.drive.entity;

/**
 * @author Fisnik Zejnullahu
 */
public class FilePermission {

    private String emailAddress;
    private String role;

    public FilePermission(String emailAddress, String role) {
        this.emailAddress = emailAddress;
        this.role = role;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
