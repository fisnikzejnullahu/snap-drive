package com.fisnikz.snapdrive.api.drive.entity;

/**
 * @author Fisnik Zejnullahu
 */
public class GoogleStorageQuota {

    private final long storageLimit;
    private final long storageInGoogleDrive;
    private final long storageInGCM;

    public GoogleStorageQuota(long storageLimit, long storageInGoogleDrive, long storageInGCM) {
        this.storageLimit = storageLimit;
        this.storageInGoogleDrive = storageInGoogleDrive;
        this.storageInGCM = storageInGCM;
    }

    public long getStorageLimit() {
        return storageLimit;
    }

    public long getStorageInGoogleDrive() {
        return storageInGoogleDrive;
    }

    public long getStorageInGCM() {
        return storageInGCM;
    }
}
