package com.fisnikz.snapdrive.crypto.entity;

import javax.crypto.SecretKey;

/**
 * @author Fisnik Zejnullahu
 */
public class DerivativePasswordKeyInfo {

    private SecretKey secretKey;
    private byte[] salt;
    private int iterations;

    public DerivativePasswordKeyInfo(SecretKey secretKey, byte[] salt, int iterations) {
        this.secretKey = secretKey;
        this.salt = salt;
        this.iterations = iterations;
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }
}
