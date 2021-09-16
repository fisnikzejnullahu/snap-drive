package com.fisnikz.snapdrive.api.users.entity;

/**
 * @author Fisnik Zejnullahu
 */
public class CreateUserMasterPasswordRequest {

    private String masterPassword;
    private String privateKey;
    private String publicKey;
    private String nonce;
    private int derivativeIterations;
    private String derivativeSalt;

    public CreateUserMasterPasswordRequest() {
    }

    public CreateUserMasterPasswordRequest(String privateKey, String publicKey, String nonce, int derivativeIterations, String derivativeSalt, String masterPassword) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.nonce = nonce;
        this.derivativeIterations = derivativeIterations;
        this.derivativeSalt = derivativeSalt;
        this.masterPassword = masterPassword;
    }

    public String getMasterPassword() {
        return masterPassword;
    }

    public void setMasterPassword(String masterPassword) {
        this.masterPassword = masterPassword;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public int getDerivativeIterations() {
        return derivativeIterations;
    }

    public void setDerivativeIterations(int derivativeIterations) {
        this.derivativeIterations = derivativeIterations;
    }

    public String getDerivativeSalt() {
        return derivativeSalt;
    }

    public void setDerivativeSalt(String derivativeSalt) {
        this.derivativeSalt = derivativeSalt;
    }


}
