package com.fisnikz.snapdrive.api.drive.entity;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.JsonbBuilder;
import java.io.StringReader;
import java.util.List;

/**
 * @author Fisnik Zejnullahu
 */
public class DriveFile {
    private String id;
    private String link;
    private long size;

    private String createdAt;
    private String userId;
    private List<FileShare> shares;

    public DriveFile() {
    }

    public DriveFile(String link, long size, String userId) {
        this.link = link;
        this.size = size;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<FileShare> getShares() {
        return shares;
    }

    public void setShares(List<FileShare> shares) {
        this.shares = shares;
    }

    @Override
    public String toString() {
        return "FileMetadata{" +
                "id='" + id + '\'' +
                ", link='" + link + '\'' +
                ", size=" + size +
                ", createdAt=" + createdAt +
                '}';
    }

    public JsonObject toJsonObject() {
        System.out.println("DRIVE FILE TO JSON");
        JsonObject jsonObject = Json.createReader(new StringReader(JsonbBuilder.create().toJson(this))).readObject();
        System.out.println(jsonObject);
        return jsonObject;
    }
}
