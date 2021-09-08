package com.fisnikz.snapdrive.crypto.entity;

/**
 * @author Fisnik Zejnullahu
 */
public class FileEncryptionFinalResultOLD {

    private String base64EncryptedFileKey;
    private String base64FileInitializationVector;

    public FileEncryptionFinalResultOLD() {
    }

    public FileEncryptionFinalResultOLD(String base64EncryptedFileKey, String base64FileInitializationVector) {
        this.base64EncryptedFileKey = base64EncryptedFileKey;
        this.base64FileInitializationVector = base64FileInitializationVector;
    }

    public String getBase64EncryptedFileKey() {
        return base64EncryptedFileKey;
    }

    public void setBase64EncryptedFileKey(String base64EncryptedFileKey) {
        this.base64EncryptedFileKey = base64EncryptedFileKey;
    }

    public String getBase64FileInitializationVector() {
        return base64FileInitializationVector;
    }

    public void setBase64FileInitializationVector(String base64FileInitializationVector) {
        this.base64FileInitializationVector = base64FileInitializationVector;
    }
}
