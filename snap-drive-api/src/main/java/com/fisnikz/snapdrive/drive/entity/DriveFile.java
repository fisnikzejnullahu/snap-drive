package com.fisnikz.snapdrive.drive.entity;

import com.fisnikz.snapdrive.users.entity.User;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.StringReader;
import java.time.LocalDateTime;
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

    public LocalDateTime createdAt;

    @ManyToOne
    @JsonbTransient
    @NotNull
    public User user;

    @Transient
    public String userId;

    @JsonbTransient
    @OneToMany(mappedBy = "file", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    public List<FileShare> fileShares;

    public DriveFile() {
    }

    public DriveFile(String id, String link, long size, LocalDateTime createdAt, User user) {
        this(id, link, size, createdAt);
        this.user = user;
    }

    public DriveFile(String id, String link, long size, LocalDateTime createdAt) {
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
