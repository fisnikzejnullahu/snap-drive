package com.fisnikz.snapdrive.api.users.entity;

import java.time.LocalDateTime;

/**
 * @author Fisnik Zejnullahu
 */
public class CreateUserRequest {

    private String username;
    private String password;
    private String privateKey;
    private String publicKey;
    private String nonce;
    private int derivativeIterations;
    private String derivativeSalt;

    private String masterPassword;

    public CreateUserRequest() {
    }

    public CreateUserRequest(String username, String password, String privateKey, String publicKey, String nonce, int derivativeIterations, String derivativeSalt) {
        this.username = username;
        this.password = password;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.nonce = nonce;
        this.derivativeIterations = derivativeIterations;
        this.derivativeSalt = derivativeSalt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getMasterPassword() {
        return masterPassword;
    }

    public void setMasterPassword(String masterPassword) {
        this.masterPassword = masterPassword;
    }
}
