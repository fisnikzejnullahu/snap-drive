package com.fisnikz.snapdrive.api.drive.entity;

/**
 * @author Fisnik Zejnullahu
 */
public class DownloadedFileMetadata {

    private String fileName;
    private String mimeType;
    private byte[] bytes;

    public DownloadedFileMetadata(String fileName, String mimeType, byte[] bytes) {
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.bytes = bytes;
    }

    public String getFileName() {
        return fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public byte[] getBytes() {
        return bytes;
    }
}
