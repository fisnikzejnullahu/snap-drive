package com.fisnikz.snapdrive.api.users.entity;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.JsonbBuilder;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * @author Fisnik Zejnullahu
 */
public class DriveFile {
    private String id;
    private String link;
    private long size;
    private long createdAt;
    private ArrayList<String> fileSharesRelationIds;

    public DriveFile() {
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

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public ArrayList<String> getFileSharesRelationIds() {
        return fileSharesRelationIds;
    }

    public void setFileSharesRelationIds(ArrayList<String> fileSharesRelationIds) {
        this.fileSharesRelationIds = fileSharesRelationIds;
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
        return Json.createReader(new StringReader(JsonbBuilder.create().toJson(this))).readObject();
    }
}
