package com.fisnikz.snapdrive.api.drive.entity;

/**
 * @author Fisnik Zejnullahu
 */
public class FileShare {

    private String fileId;
    private String recipientUsername;
    private String sharedAt;

    public FileShare() {
    }

    public FileShare(String fileId, String recipientUsername) {
        this.fileId = fileId;
        this.recipientUsername = recipientUsername;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getRecipientUsername() {
        return recipientUsername;
    }

    public void setRecipientUsername(String recipientUsername) {
        this.recipientUsername = recipientUsername;
    }

    public String getSharedAt() {
        return sharedAt;
    }

    public void setSharedAt(String sharedAt) {
        this.sharedAt = sharedAt;
    }
}
