package com.fisnikz.snapdrive.crypto.entity;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * @author Fisnik Zejnullahu
 */
public abstract class BasicFileEncryptionInfo {

    private SecretKey secretKey;
    private IvParameterSpec initializationVectorSpec;
    private byte[] encryptedInput;

    public BasicFileEncryptionInfo(SecretKey secretKey, IvParameterSpec initializationVectorSpec, byte[] encryptedInput) {
        this.secretKey = secretKey;
        this.initializationVectorSpec = initializationVectorSpec;
        this.encryptedInput = encryptedInput;
    }

    public BasicFileEncryptionInfo(String secretKeyBase64UrlEncoded, IvParameterSpec ivBase64UrlEncoded, byte[] encryptedInput) {
        byte[] fileEncryptedKeyDecoded = Base64.getUrlDecoder().decode(secretKeyBase64UrlEncoded);
        SecretKey originalKey = new SecretKeySpec(fileEncryptedKeyDecoded, "AES");



    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    public IvParameterSpec getInitializationVectorSpec() {
        return initializationVectorSpec;
    }

    public void setInitializationVectorSpec(IvParameterSpec initializationVectorSpec) {
        this.initializationVectorSpec = initializationVectorSpec;
    }

    public byte[] getEncryptedInput() {
        return encryptedInput;
    }

    public void setEncryptedInput(byte[] encryptedInput) {
        this.encryptedInput = encryptedInput;
    }
}
