package com.example.labverse.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "tags",
        indices = {@Index(value = "name", unique = true)})
public class TagEntity {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "tag_id")
    private String tagId;

    @NonNull
    private String name;

    private String color;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    public TagEntity(@NonNull String tagId, @NonNull String name) {
        this.tagId = tagId;
        this.name = name;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters and Setters
    @NonNull
    public String getTagId() { return tagId; }
    public void setTagId(@NonNull String tagId) { this.tagId = tagId; }

    @NonNull
    public String getName() { return name; }
    public void setName(@NonNull String name) { this.name = name; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
