package com.fisnikz.snapdrive.users.entity;

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

    public DriveFile(String id, String link, long size, long createdAt, ArrayList<String> fileSharesRelationIds) {
        System.out.println("CONSTRUCTOR CALLED!!!!!!!!!!!!!!!!!!!!!!!!");
        this.id = id;
        this.link = link;
        this.size = size;
        this.createdAt = createdAt;
        this.fileSharesRelationIds = fileSharesRelationIds;
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
}
