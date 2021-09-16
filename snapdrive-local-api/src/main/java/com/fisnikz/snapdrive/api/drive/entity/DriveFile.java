package com.fisnikz.snapdrive.api.drive.entity;

import java.util.List;

/**
 * @author Fisnik Zejnullahu
 */
public class DriveFile {
    private String googleDriveId;
    private long size;
    private String createdAt;
    private String fileName;
    private String sharedBy;
    private List<FilePermission> permissions;

    public DriveFile(String googleDriveId, long size, String createdAt, List<FilePermission> permissions) {
        this.googleDriveId = googleDriveId;
        this.size = size;
        this.createdAt = createdAt;
        this.permissions = permissions;
    }

    public DriveFile(String googleDriveId, long size, String createdAt, String sharedBy) {
        this.googleDriveId = googleDriveId;
        this.size = size;
        this.createdAt = createdAt;
        this.sharedBy = sharedBy;
    }

    public String getGoogleDriveId() {
        return googleDriveId;
    }

    public void setGoogleDriveId(String googleDriveId) {
        this.googleDriveId = googleDriveId;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSharedBy() {
        return sharedBy;
    }

    public void setSharedBy(String sharedBy) {
        this.sharedBy = sharedBy;
    }

    public List<FilePermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<FilePermission> permissions) {
        this.permissions = permissions;
    }
}
