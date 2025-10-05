package com.example.labverse.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "collection_members",
        foreignKeys = {
                @ForeignKey(entity = CollectionEntity.class,
                        parentColumns = "collection_id",
                        childColumns = "collection_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = UserEntity.class,
                        parentColumns = "user_id",
                        childColumns = "user_id",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index("collection_id"), @Index("user_id")})
public class CollectionMemberEntity {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "member_id")
    private String memberId;

    @NonNull
    @ColumnInfo(name = "collection_id")
    private String collectionId;

    @NonNull
    @ColumnInfo(name = "user_id")
    private String userId;

    @ColumnInfo(name = "access_level")
    private String accessLevel; // "read", "write", "admin"

    @ColumnInfo(name = "joined_at")
    private long joinedAt;

    @ColumnInfo(name = "sync_status")
    private String syncStatus;

    public CollectionMemberEntity(@NonNull String memberId, @NonNull String collectionId,
                                  @NonNull String userId) {
        this.memberId = memberId;
        this.collectionId = collectionId;
        this.userId = userId;
        this.joinedAt = System.currentTimeMillis();
        this.accessLevel = "read";
        this.syncStatus = "pending";
    }

    // Getters and Setters
    @NonNull
    public String getMemberId() { return memberId; }
    public void setMemberId(@NonNull String memberId) { this.memberId = memberId; }

    @NonNull
    public String getCollectionId() { return collectionId; }
    public void setCollectionId(@NonNull String collectionId) { this.collectionId = collectionId; }

    @NonNull
    public String getUserId() { return userId; }
    public void setUserId(@NonNull String userId) { this.userId = userId; }

    public String getAccessLevel() { return accessLevel; }
    public void setAccessLevel(String accessLevel) { this.accessLevel = accessLevel; }

    public long getJoinedAt() { return joinedAt; }
    public void setJoinedAt(long joinedAt) { this.joinedAt = joinedAt; }

    public String getSyncStatus() { return syncStatus; }
    public void setSyncStatus(String syncStatus) { this.syncStatus = syncStatus; }
}
