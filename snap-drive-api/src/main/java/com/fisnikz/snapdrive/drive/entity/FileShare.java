package com.fisnikz.snapdrive.drive.entity;

import com.fisnikz.snapdrive.users.entity.User;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

/**
 * @author Fisnik Zejnullahu
 */
@Entity
public class FileShare extends PanacheEntityBase {

    @Id
    public String shareId;

    public String driveFileId;

    @ManyToOne
    @JsonbTransient
    public User ownerUser;

    @ManyToOne
    @JsonbTransient
    public User recipientUser;

    public LocalDateTime sharedAt;

    public FileShare() {
    }

    public FileShare(String shareId, String driveFileId, User ownerUser, User recipientUser) {
        this.shareId = shareId;
        this.driveFileId = driveFileId;
        this.ownerUser = ownerUser;
        this.recipientUser = recipientUser;
        this.sharedAt = LocalDateTime.now();
    }

}
