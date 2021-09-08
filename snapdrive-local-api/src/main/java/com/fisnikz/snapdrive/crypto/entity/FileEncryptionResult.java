package com.fisnikz.snapdrive.crypto.entity;

/**
 * @author Fisnik Zejnullahu
 */
public class FileEncryptionResult {

    private FileKeyEncryptionInfo fileKeyEncryptionInfo;
    private FileEncryptionInfo fileEncryptionInfo;

    public FileEncryptionResult(FileKeyEncryptionInfo fileKeyEncryptionInfo, FileEncryptionInfo fileEncryptionInfo) {
        this.fileKeyEncryptionInfo = fileKeyEncryptionInfo;
        this.fileEncryptionInfo = fileEncryptionInfo;
    }

    public FileKeyEncryptionInfo getFileKeyEncryptionInfo() {
        return fileKeyEncryptionInfo;
    }

    public void setFileKeyEncryptionInfo(FileKeyEncryptionInfo fileKeyEncryptionInfo) {
        this.fileKeyEncryptionInfo = fileKeyEncryptionInfo;
    }

    public FileEncryptionInfo getFileEncryptionInfo() {
        return fileEncryptionInfo;
    }

    public void setFileEncryptionInfo(FileEncryptionInfo fileEncryptionInfo) {
        this.fileEncryptionInfo = fileEncryptionInfo;
    }
}
