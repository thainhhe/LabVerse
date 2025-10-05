package com.example.labverse.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "reading_lists",
        foreignKeys = @ForeignKey(entity = UserEntity.class,
                parentColumns = "user_id",
                childColumns = "user_id",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("user_id")})
public class ReadingListEntity {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "reading_list_id")
    private String readingListId;

    @NonNull
    @ColumnInfo(name = "user_id")
    private String userId;

    @NonNull
    private String name;

    private String description;

    @ColumnInfo(name = "is_shared")
    private boolean isShared;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    @ColumnInfo(name = "updated_at")
    private long updatedAt;

    @ColumnInfo(name = "sync_status")
    private String syncStatus;

    public ReadingListEntity(@NonNull String readingListId, @NonNull String userId, @NonNull String name) {
        this.readingListId = readingListId;
        this.userId = userId;
        this.name = name;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
        this.isShared = false;
        this.syncStatus = "pending";
    }

    // Getters and Setters
    @NonNull
    public String getReadingListId() { return readingListId; }
    public void setReadingListId(@NonNull String readingListId) { this.readingListId = readingListId; }

    @NonNull
    public String getUserId() { return userId; }
    public void setUserId(@NonNull String userId) { this.userId = userId; }

    @NonNull
    public String getName() { return name; }
    public void setName(@NonNull String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isShared() { return isShared; }
    public void setShared(boolean shared) { isShared = shared; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    public String getSyncStatus() { return syncStatus; }
    public void setSyncStatus(String syncStatus) { this.syncStatus = syncStatus; }
}
