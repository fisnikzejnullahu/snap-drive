package com.fisnikz.snapdrive.drive.entity;

import com.fisnikz.snapdrive.users.entity.DriveFile;

/**
 * @author Fisnik Zejnullahu
 */
public class SharedFileMetadata {

    private String fileId;
    private String ownerId;
    private String ownerUsername;
    private String recipientId;
    private String recipientUsername;

    public SharedFileMetadata() {
    }

    public SharedFileMetadata(String fileId, String ownerId, String ownerUsername, String recipientId, String recipientUsername) {
        this.fileId = fileId;
        this.ownerId = ownerId;
        this.ownerUsername = ownerUsername;
        this.recipientId = recipientId;
        this.recipientUsername = recipientUsername;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getRecipientUsername() {
        return recipientUsername;
    }

    public void setRecipientUsername(String recipientUsername) {
        this.recipientUsername = recipientUsername;
    }
}
