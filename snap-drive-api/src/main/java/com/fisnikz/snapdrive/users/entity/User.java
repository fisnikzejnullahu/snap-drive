package com.fisnikz.snapdrive.users.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Fisnik Zejnullahu
 */
@Entity
public class User extends PanacheEntityBase {

    @Id
    public String id;

    @Column(unique = true)
    public String email;

    public String googleId;

    @Column(columnDefinition = "TEXT")
    public String privateKey;

    @Column(columnDefinition = "TEXT")
    public String publicKey;

    public String derivativeSalt;

    public int derivativeIterations;

    public String nonce;
    public LocalDateTime registerAt;

    public User() {
    }

    public User(String googleId, String email) {
        this.id = UUID.randomUUID().toString();
        this.registerAt = LocalDateTime.now();
        this.googleId = googleId;
        this.email = email;
    }
}
