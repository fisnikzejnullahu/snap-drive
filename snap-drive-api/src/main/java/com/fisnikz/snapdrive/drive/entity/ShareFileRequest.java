package com.fisnikz.snapdrive.drive.entity;

/**
 * @author Fisnik Zejnullahu
 */
public class ShareFileRequest {

    private String fileId;
    private String recipientUsername;

    public ShareFileRequest() {
    }

    public ShareFileRequest(String fileId, String recipientUsername) {
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
}
