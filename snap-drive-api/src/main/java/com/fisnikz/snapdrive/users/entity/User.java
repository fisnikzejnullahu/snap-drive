package com.fisnikz.snapdrive.users.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

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
    private ArrayList<DriveFile> files;

    public User() {
    }

    public User(String username, String hashedPassword, String passwordSalt, int derivativeIterations, String derivativeSalt, String privateKey, String publicKey, String nonce) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.passwordSalt = passwordSalt;
        this.derivativeIterations = derivativeIterations;
        this.derivativeSalt = derivativeSalt;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.nonce = nonce;
        this.registerAt = LocalDateTime.now().toString();
//        this.counter = counter;
    }

    public ArrayList<DriveFile> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<DriveFile> files) {
        this.files = files;
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

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
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
