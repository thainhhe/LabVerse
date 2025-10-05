package com.example.labverse.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "collection_papers",
        foreignKeys = {
                @ForeignKey(entity = CollectionEntity.class,
                        parentColumns = "collection_id",
                        childColumns = "collection_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = PaperEntity.class,
                        parentColumns = "paper_id",
                        childColumns = "paper_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = UserEntity.class,
                        parentColumns = "user_id",
                        childColumns = "added_by",
                        onDelete = ForeignKey.SET_NULL)
        },
        indices = {@Index("collection_id"), @Index("paper_id"), @Index("added_by")})
public class CollectionPaperEntity {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "collection_paper_id")
    private String collectionPaperId;

    @NonNull
    @ColumnInfo(name = "collection_id")
    private String collectionId;

    @NonNull
    @ColumnInfo(name = "paper_id")
    private String paperId;

    @ColumnInfo(name = "added_by")
    private String addedBy;

    @ColumnInfo(name = "added_at")
    private long addedAt;

    private String status;
    private String priority;

    @ColumnInfo(name = "sync_status")
    private String syncStatus;

    public CollectionPaperEntity(@NonNull String collectionPaperId, @NonNull String collectionId,
                                 @NonNull String paperId) {
        this.collectionPaperId = collectionPaperId;
        this.collectionId = collectionId;
        this.paperId = paperId;
        this.addedAt = System.currentTimeMillis();
        this.syncStatus = "pending";
    }

    // Getters and Setters
    @NonNull
    public String getCollectionPaperId() { return collectionPaperId; }
    public void setCollectionPaperId(@NonNull String collectionPaperId) { this.collectionPaperId = collectionPaperId; }

    @NonNull
    public String getCollectionId() { return collectionId; }
    public void setCollectionId(@NonNull String collectionId) { this.collectionId = collectionId; }

    @NonNull
    public String getPaperId() { return paperId; }
    public void setPaperId(@NonNull String paperId) { this.paperId = paperId; }

    public String getAddedBy() { return addedBy; }
    public void setAddedBy(String addedBy) { this.addedBy = addedBy; }

    public long getAddedAt() { return addedAt; }
    public void setAddedAt(long addedAt) { this.addedAt = addedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getSyncStatus() { return syncStatus; }
    public void setSyncStatus(String syncStatus) { this.syncStatus = syncStatus; }
}
