package com.example.labverse.models;

import java.io.Serializable;

public class Collection implements Serializable {
    private String id;
    private String name;
    private String description;
    private String createdBy;
    private int paperCount;
    private boolean isPublic;
    private long dateCreated;
    private String accessLevel; // "read", "write", "admin"

    public Collection() {}

    public Collection(String id, String name, String description, String createdBy, int paperCount, boolean isPublic) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdBy = createdBy;
        this.paperCount = paperCount;
        this.isPublic = isPublic;
        this.dateCreated = System.currentTimeMillis();
        this.accessLevel = "read";
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public int getPaperCount() { return paperCount; }
    public void setPaperCount(int paperCount) { this.paperCount = paperCount; }

    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean isPublic) { this.isPublic = isPublic; }

    public long getDateCreated() { return dateCreated; }
    public void setDateCreated(long dateCreated) { this.dateCreated = dateCreated; }

    public String getAccessLevel() { return accessLevel; }
    public void setAccessLevel(String accessLevel) { this.accessLevel = accessLevel; }
}
