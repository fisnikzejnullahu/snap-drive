package com.fisnikz.snapdrive.api.users.entity;

import javax.json.bind.annotation.JsonbTransient;

/**
 * @author Fisnik Zejnullahu
 */
public class User {

    private String id;
    private String email;
    private String googleId;
    private int derivativeIterations;
    private String derivativeSalt;
    private String privateKey;
    private String publicKey;
    private String nonce;
    private String registerAt;

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    @JsonbTransient
    public int getDerivativeIterations() {
        return derivativeIterations;
    }

    public void setDerivativeIterations(int derivativeIterations) {
        this.derivativeIterations = derivativeIterations;
    }

    @JsonbTransient
    public String getDerivativeSalt() {
        return derivativeSalt;
    }

    public void setDerivativeSalt(String derivativeSalt) {
        this.derivativeSalt = derivativeSalt;
    }

    @JsonbTransient
    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    @JsonbTransient
    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    @JsonbTransient
    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

//    public int getCounter() {
//        return counter;
//    }
//
//    public void setCounter(int counter) {
//        this.counter = counter;
//    }

    public String getRegisterAt() {
        return registerAt;
    }

    public void setRegisterAt(String registerAt) {
        this.registerAt = registerAt;
    }
}
