package com.fisnikz.snapdrive.drive.entity;

import javax.json.bind.annotation.JsonbDateFormat;
import java.time.LocalDateTime;

/**
 * @author Fisnik Zejnullahu
 */
public class ShareFileRequest {

    private String fileId;
    private String recipientUsername;

    private LocalDateTime sharedAt;

    public ShareFileRequest() {
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

    public LocalDateTime getSharedAt() {
        return sharedAt;
    }

    public void setSharedAt(LocalDateTime sharedAt) {
        this.sharedAt = sharedAt;
    }
}
