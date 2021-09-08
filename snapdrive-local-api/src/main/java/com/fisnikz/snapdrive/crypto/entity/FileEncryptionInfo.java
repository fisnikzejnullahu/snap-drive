package com.fisnikz.snapdrive.crypto.entity;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 * @author Fisnik Zejnullahu
 */
public class FileEncryptionInfo extends BasicFileEncryptionInfo {

    private String encryptedFileName;

    public FileEncryptionInfo(SecretKey secretKey, IvParameterSpec initializationVectorSpec, byte[] encryptedInput, String encryptedFileName) {
        super(secretKey, initializationVectorSpec, encryptedInput);
        this.encryptedFileName = encryptedFileName;
    }

    public String getEncryptedFileName() {
        return encryptedFileName;
    }

    public void setEncryptedFileName(String encryptedFileName) {
        this.encryptedFileName = encryptedFileName;
    }
}
