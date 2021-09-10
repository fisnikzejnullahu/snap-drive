package com.fisnikz.snapdrive.drive.entity;

import com.fisnikz.snapdrive.users.entity.User;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Fisnik Zejnullahu
 */
@Entity
public class DriveFile extends PanacheEntityBase {

    @Id
    public String id;

    public String link;
    public long size;
    public long createdAt;

    @ManyToOne
    @JsonbTransient
    @NotNull
    public User user;

    @JsonbTransient
    @OneToMany(mappedBy = "file", cascade = CascadeType.REMOVE)
    public List<FileShare> fileShares;

    public DriveFile() {
    }

    public DriveFile(String id, String link, long size, long createdAt, User user) {
        this(id, link, size, createdAt);
        this.user = user;
    }

    public DriveFile(String id, String link, long size, long createdAt) {
        this.id = id;
        this.link = link;
        this.size = size;
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "DriveFile{" +
                "id='" + id + '\'' +
                ", link='" + link + '\'' +
                ", size=" + size +
                ", createdAt=" + createdAt +
                ", user=" + user +
                ", shared=" + fileShares +
                '}';
    }
}
