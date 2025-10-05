package com.example.labverse.database.entities;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;
@Entity(tableName = "comments",
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
public class CommentEntity {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "comment_id")
    private String commentId;

    @ColumnInfo(name = "paper_id")
    private String paperId;

    @ColumnInfo(name = "collection_id")
    private String collectionId;

    @NonNull
    @ColumnInfo(name = "user_id")
    private String userId;

    @NonNull
    private String content;

    @ColumnInfo(name = "parent_comment_id")
    private String parentCommentId;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    @ColumnInfo(name = "updated_at")
    private long updatedAt;

    @ColumnInfo(name = "sync_status")
    private String syncStatus;

    public CommentEntity(@NonNull String commentId, @NonNull String userId, @NonNull String content) {
        this.commentId = commentId;
        this.userId = userId;
        this.content = content;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
        this.syncStatus = "pending";
    }

    // Getters and Setters
    @NonNull
    public String getCommentId() { return commentId; }
    public void setCommentId(@NonNull String commentId) { this.commentId = commentId; }

    public String getPaperId() { return paperId; }
    public void setPaperId(String paperId) { this.paperId = paperId; }

    public String getCollectionId() { return collectionId; }
    public void setCollectionId(String collectionId) { this.collectionId = collectionId; }

    @NonNull
    public String getUserId() { return userId; }
    public void setUserId(@NonNull String userId) { this.userId = userId; }

    @NonNull
    public String getContent() { return content; }
    public void setContent(@NonNull String content) { this.content = content; }

    public String getParentCommentId() { return parentCommentId; }
    public void setParentCommentId(String parentCommentId) { this.parentCommentId = parentCommentId; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    public String getSyncStatus() { return syncStatus; }
    public void setSyncStatus(String syncStatus) { this.syncStatus = syncStatus; }
}
