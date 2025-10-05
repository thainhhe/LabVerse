package com.example.labverse.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "annotations",
        foreignKeys = {
                @ForeignKey(entity = PaperEntity.class,
                        parentColumns = "paper_id",
                        childColumns = "paper_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = UserEntity.class,
                        parentColumns = "user_id",
                        childColumns = "user_id",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index("paper_id"), @Index("user_id")})
public class AnnotationEntity {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "annotation_id")
    private String annotationId;

    @NonNull
    @ColumnInfo(name = "paper_id")
    private String paperId;

    @NonNull
    @ColumnInfo(name = "user_id")
    private String userId;

    @NonNull
    private String type; // "highlight", "note", "drawing"

    @ColumnInfo(name = "page_number")
    private int pageNumber;

    private String content;
    private String color;

    @ColumnInfo(name = "position_data")
    private String positionData; // JSON string

    @ColumnInfo(name = "created_at")
    private long createdAt;

    @ColumnInfo(name = "updated_at")
    private long updatedAt;

    @ColumnInfo(name = "sync_status")
    private String syncStatus;

    @ColumnInfo(name = "firebase_id")
    private String firebaseId;

    // Constructor
    public AnnotationEntity(@NonNull String annotationId, @NonNull String paperId,
                            @NonNull String userId, @NonNull String type) {
        this.annotationId = annotationId;
        this.paperId = paperId;
        this.userId = userId;
        this.type = type;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
        this.syncStatus = "pending";
    }

    // Getters and Setters
    @NonNull
    public String getAnnotationId() { return annotationId; }
    public void setAnnotationId(@NonNull String annotationId) { this.annotationId = annotationId; }

    @NonNull
    public String getPaperId() { return paperId; }
    public void setPaperId(@NonNull String paperId) { this.paperId = paperId; }

    @NonNull
    public String getUserId() { return userId; }
    public void setUserId(@NonNull String userId) { this.userId = userId; }

    @NonNull
    public String getType() { return type; }
    public void setType(@NonNull String type) { this.type = type; }

    public int getPageNumber() { return pageNumber; }
    public void setPageNumber(int pageNumber) { this.pageNumber = pageNumber; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getPositionData() { return positionData; }
    public void setPositionData(String positionData) { this.positionData = positionData; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    public String getSyncStatus() { return syncStatus; }
    public void setSyncStatus(String syncStatus) { this.syncStatus = syncStatus; }

    public String getFirebaseId() { return firebaseId; }
    public void setFirebaseId(String firebaseId) { this.firebaseId = firebaseId; }
}
