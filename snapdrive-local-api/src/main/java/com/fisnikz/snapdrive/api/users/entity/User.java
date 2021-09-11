package com.fisnikz.snapdrive.api.users.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fisnikz.snapdrive.api.drive.entity.DriveFile;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.annotation.JsonbTransient;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * @author Fisnik Zejnullahu
 */
public class User {

    private String id;
    private String username;
    private String hashedPassword;
    private String passwordSalt;
    private int derivativeIterations;
    private String derivativeSalt;

    private String privateKey;
    private String publicKey;
    private String nonce;
    //    private int counter;
    private String registerAt;

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonbTransient
    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    @JsonbTransient
    public String getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
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
