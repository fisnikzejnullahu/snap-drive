package com.fisnikz.snapdrive.crypto.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Fisnik Zejnullahu
 */
public class FileEncryptionFinalResult {

    private String ownerId;
    private String signedPublicKey;
    private String base64FileKey;
    private String base64FileInitializationVector;
    private List<SharedUsers> sharedUsers;

    public FileEncryptionFinalResult() {
    }

    public FileEncryptionFinalResult(String base64FileKey, String base64FileInitializationVector) {
        this.base64FileKey = base64FileKey;
        this.base64FileInitializationVector = base64FileInitializationVector;
        this.sharedUsers = new ArrayList<>();
    }

    public FileEncryptionFinalResult(String ownerId, String signedPublicKey, String base64FileKey, String base64FileInitializationVector) {
        this.ownerId = ownerId;
        this.signedPublicKey = signedPublicKey;
        this.base64FileKey = base64FileKey;
        this.base64FileInitializationVector = base64FileInitializationVector;
        this.sharedUsers = new ArrayList<>();
    }

    public void shareFile(String userId, String base64FileKey) {
        this.sharedUsers.add(new SharedUsers(userId, base64FileKey));
    }

    public void removeUserShare(String userId) {
        this.sharedUsers.removeIf(sharedUsers -> sharedUsers.getUserId().equals(userId));
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getSignedPublicKey() {
        return signedPublicKey;
    }

    public void setSignedPublicKey(String signedPublicKey) {
        this.signedPublicKey = signedPublicKey;
    }

    public String getBase64FileKey() {
        return base64FileKey;
    }

    public void setBase64FileKey(String base64FileKey) {
        this.base64FileKey = base64FileKey;
    }

    public String getBase64FileInitializationVector() {
        return base64FileInitializationVector;
    }

    public void setBase64FileInitializationVector(String base64FileInitializationVector) {
        this.base64FileInitializationVector = base64FileInitializationVector;
    }

    public List<SharedUsers> getSharedUsers() {
        return sharedUsers;
    }

    public void setSharedUsers(List<SharedUsers> sharedUsers) {
        this.sharedUsers = sharedUsers;
    }

    public SharedUsers getSharedInfoPerUser(String userId) {
        return this.getSharedUsers()
                .stream()
                .filter(sharedUsers -> sharedUsers.getUserId().equals(userId))
                .findFirst().get();
    }
}
