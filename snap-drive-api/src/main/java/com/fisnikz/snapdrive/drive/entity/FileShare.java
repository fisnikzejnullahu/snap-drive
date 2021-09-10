package com.fisnikz.snapdrive.drive.entity;

import com.fisnikz.snapdrive.users.entity.User;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author Fisnik Zejnullahu
 */
@Entity
public class FileShare extends PanacheEntity {

    @ManyToOne
    public DriveFile file;

    @ManyToOne
    @JsonbTransient
    public User recipientUser;

    public LocalDateTime sharedAt;

    public FileShare() {
    }

    public FileShare(DriveFile file, User recipientUser) {
        this.file = file;
        this.recipientUser = recipientUser;
        this.sharedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "FileShares{" +
                "id=" + id +
//                ", file=" + file +
//                ", recipientUser=" + recipientUser +
                ", sharedAt=" + sharedAt +
                '}';
    }
}
