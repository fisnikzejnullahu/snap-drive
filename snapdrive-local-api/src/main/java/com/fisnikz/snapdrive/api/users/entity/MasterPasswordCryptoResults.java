package com.fisnikz.snapdrive.api.users.entity;

/**
 * @author Fisnik Zejnullahu
 */
public class MasterPasswordCryptoResults {

    private String privateKeyBase64;
    private String publicKeyBase64;
    private String nonceBase64;
    private String pbkdf2Salt;
    private int pbkdf2Iterations;

    public MasterPasswordCryptoResults() {
    }

    public MasterPasswordCryptoResults(String privateKeyBase64, String publicKeyBase64, String nonceBase64, String pbkdf2Salt, int pbkdf2Iterations) {
        this.privateKeyBase64 = privateKeyBase64;
        this.publicKeyBase64 = publicKeyBase64;
        this.nonceBase64 = nonceBase64;
        this.pbkdf2Salt = pbkdf2Salt;
        this.pbkdf2Iterations = pbkdf2Iterations;
    }

    public String getPrivateKeyBase64() {
        return privateKeyBase64;
    }

    public void setPrivateKeyBase64(String privateKeyBase64) {
        this.privateKeyBase64 = privateKeyBase64;
    }

    public String getPublicKeyBase64() {
        return publicKeyBase64;
    }

    public void setPublicKeyBase64(String publicKeyBase64) {
        this.publicKeyBase64 = publicKeyBase64;
    }

    public String getNonceBase64() {
        return nonceBase64;
    }

    public void setNonceBase64(String nonceBase64) {
        this.nonceBase64 = nonceBase64;
    }

    public String getPbkdf2Salt() {
        return pbkdf2Salt;
    }

    public void setPbkdf2Salt(String pbkdf2Salt) {
        this.pbkdf2Salt = pbkdf2Salt;
    }

    public int getPbkdf2Iterations() {
        return pbkdf2Iterations;
    }

    public void setPbkdf2Iterations(int pbkdf2Iterations) {
        this.pbkdf2Iterations = pbkdf2Iterations;
    }
}
