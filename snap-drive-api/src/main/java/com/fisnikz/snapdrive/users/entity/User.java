package com.fisnikz.snapdrive.users.entity;

import com.fisnikz.snapdrive.drive.entity.DriveFile;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @author Fisnik Zejnullahu
 */
@Entity
public class User extends PanacheEntityBase {

    @Id
    public String id;

    public String username;

    public String hashedPassword;
    public String passwordSalt;
    public int derivativeIterations;
    public String derivativeSalt;

    @Column(columnDefinition = "TEXT")
    public String privateKey;

    @Column(columnDefinition = "TEXT")
    public String publicKey;
    public String nonce;
    //    private int counter;
    public LocalDateTime registerAt;

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
        this.registerAt = LocalDateTime.now();
//        this.counter = counter;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
