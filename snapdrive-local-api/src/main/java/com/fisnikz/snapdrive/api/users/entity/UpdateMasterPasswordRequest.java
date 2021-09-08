package com.fisnikz.snapdrive.api.users.entity;

/**
 * @author Fisnik Zejnullahu
 */
public class UpdateMasterPasswordRequest {

    private String oldMasterPassword;
    private String newMasterPassword;

    public UpdateMasterPasswordRequest() {
    }

    public String getOldMasterPassword() {
        return oldMasterPassword;
    }

    public void setOldMasterPassword(String oldMasterPassword) {
        this.oldMasterPassword = oldMasterPassword;
    }

    public String getNewMasterPassword() {
        return newMasterPassword;
    }

    public void setNewMasterPassword(String newMasterPassword) {
        this.newMasterPassword = newMasterPassword;
    }
}
