package com.fisnikz.snapdrive.users.entity;

/**
 * @author Fisnik Zejnullahu
 */
public class CreateUserMasterPasswordRequest {

    private String privateKey;
    private String publicKey;
    private String nonce;
    private int derivativeIterations;
    private String derivativeSalt;

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
