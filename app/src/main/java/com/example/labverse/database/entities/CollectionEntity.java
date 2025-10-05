package com.example.labverse.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "collections",
        foreignKeys = @ForeignKey(entity = UserEntity.class,
                parentColumns = "user_id",
                childColumns = "created_by",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("created_by")})
public class CollectionEntity {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "collection_id")
    private String collectionId;

    @NonNull
    @ColumnInfo(name = "created_by")
    private String createdBy;

    @NonNull
    private String name;

    private String description;

    @ColumnInfo(name = "is_public")
    private boolean isPublic;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    @ColumnInfo(name = "updated_at")
    private long updatedAt;

    @ColumnInfo(name = "sync_status")
    private String syncStatus;

    @ColumnInfo(name = "firebase_id")
    private String firebaseId;

    // Constructor
    public CollectionEntity(@NonNull String collectionId, @NonNull String createdBy, @NonNull String name) {
        this.collectionId = collectionId;
        this.createdBy = createdBy;
        this.name = name;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
        this.isPublic = false;
        this.syncStatus = "pending";
    }

    // Getters and Setters
    @NonNull
    public String getCollectionId() { return collectionId; }
    public void setCollectionId(@NonNull String collectionId) { this.collectionId = collectionId; }

    @NonNull
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(@NonNull String createdBy) { this.createdBy = createdBy; }

    @NonNull
    public String getName() { return name; }
    public void setName(@NonNull String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean isPublic) { this.isPublic = isPublic; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    public String getSyncStatus() { return syncStatus; }
    public void setSyncStatus(String syncStatus) { this.syncStatus = syncStatus; }

    public String getFirebaseId() { return firebaseId; }
    public void setFirebaseId(String firebaseId) { this.firebaseId = firebaseId; }
}
