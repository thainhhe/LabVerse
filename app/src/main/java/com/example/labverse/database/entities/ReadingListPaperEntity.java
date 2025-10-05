package com.example.labverse.database.entities;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;
@Entity(tableName = "reading_list_papers",
        foreignKeys = {
                @ForeignKey(entity = ReadingListEntity.class,
                        parentColumns = "reading_list_id",
                        childColumns = "reading_list_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = PaperEntity.class,
                        parentColumns = "paper_id",
                        childColumns = "paper_id",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index("reading_list_id"), @Index("paper_id")})
public class ReadingListPaperEntity {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "reading_list_paper_id")
    private String readingListPaperId;

    @NonNull
    @ColumnInfo(name = "reading_list_id")
    private String readingListId;

    @NonNull
    @ColumnInfo(name = "paper_id")
    private String paperId;

    @ColumnInfo(name = "order_index")
    private int orderIndex;

    @ColumnInfo(name = "added_at")
    private long addedAt;

    public ReadingListPaperEntity(@NonNull String readingListPaperId, @NonNull String readingListId,
                                  @NonNull String paperId) {
        this.readingListPaperId = readingListPaperId;
        this.readingListId = readingListId;
        this.paperId = paperId;
        this.addedAt = System.currentTimeMillis();
    }

    // Getters and Setters
    @NonNull
    public String getReadingListPaperId() { return readingListPaperId; }
    public void setReadingListPaperId(@NonNull String readingListPaperId) { this.readingListPaperId = readingListPaperId; }

    @NonNull
    public String getReadingListId() { return readingListId; }
    public void setReadingListId(@NonNull String readingListId) { this.readingListId = readingListId; }

    @NonNull
    public String getPaperId() { return paperId; }
    public void setPaperId(@NonNull String paperId) { this.paperId = paperId; }

    public int getOrderIndex() { return orderIndex; }
    public void setOrderIndex(int orderIndex) { this.orderIndex = orderIndex; }

    public long getAddedAt() { return addedAt; }
    public void setAddedAt(long addedAt) { this.addedAt = addedAt; }
}
