package com.fisnikz.snapdrive.crypto.entity;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 * @author Fisnik Zejnullahu
 */
public class FileKeyEncryptionInfo extends BasicFileEncryptionInfo {

    public FileKeyEncryptionInfo(SecretKey secretKey, IvParameterSpec initializationVectorSpec, byte[] encryptedInput) {
        super(secretKey, initializationVectorSpec, encryptedInput);
    }

}
