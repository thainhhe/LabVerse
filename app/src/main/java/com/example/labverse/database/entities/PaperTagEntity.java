package com.example.labverse.database.entities;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;
@Entity(tableName = "paper_tags",
        foreignKeys = {
                @ForeignKey(entity = PaperEntity.class,
                        parentColumns = "paper_id",
                        childColumns = "paper_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = TagEntity.class,
                        parentColumns = "tag_id",
                        childColumns = "tag_id",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index("paper_id"), @Index("tag_id")})
public class PaperTagEntity {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "paper_tag_id")
    private String paperTagId;

    @NonNull
    @ColumnInfo(name = "paper_id")
    private String paperId;

    @NonNull
    @ColumnInfo(name = "tag_id")
    private String tagId;

    @ColumnInfo(name = "tagged_at")
    private long taggedAt;

    public PaperTagEntity(@NonNull String paperTagId, @NonNull String paperId, @NonNull String tagId) {
        this.paperTagId = paperTagId;
        this.paperId = paperId;
        this.tagId = tagId;
        this.taggedAt = System.currentTimeMillis();
    }

    // Getters and Setters
    @NonNull
    public String getPaperTagId() { return paperTagId; }
    public void setPaperTagId(@NonNull String paperTagId) { this.paperTagId = paperTagId; }

    @NonNull
    public String getPaperId() { return paperId; }
    public void setPaperId(@NonNull String paperId) { this.paperId = paperId; }

    @NonNull
    public String getTagId() { return tagId; }
    public void setTagId(@NonNull String tagId) { this.tagId = tagId; }

    public long getTaggedAt() { return taggedAt; }
    public void setTaggedAt(long taggedAt) { this.taggedAt = taggedAt; }
}

