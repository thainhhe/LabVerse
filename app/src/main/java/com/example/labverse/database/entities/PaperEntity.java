package com.example.labverse.database.entities;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;
@Entity(tableName = "papers",
        foreignKeys = @ForeignKey(entity = UserEntity.class,
                parentColumns = "user_id",
                childColumns = "user_id",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("user_id")})
public class PaperEntity {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "paper_id")
    public String paperId;

    @NonNull
    @ColumnInfo(name = "user_id")
    public String userId;

    @NonNull
    public String title;

    public String authors;
    public String journal;
    public String year;
    public String doi;

    @ColumnInfo(name = "abstract_text")
    public String abstractText;

    @ColumnInfo(name = "pdf_path")
    public String pdfPath;

    @ColumnInfo(name = "pdf_url")
    public String pdfUrl; // Firebase Storage URL

    public String status; // "unread", "reading", "finished"
    public String priority; // "high", "medium", "low"

    @ColumnInfo(name = "is_favorite")
    public boolean isFavorite;

    @ColumnInfo(name = "date_added")
    public long dateAdded;

    @ColumnInfo(name = "last_read")
    public long lastRead;

    @ColumnInfo(name = "current_page")
    public int currentPage;

    @ColumnInfo(name = "total_pages")
    public int totalPages;

    @ColumnInfo(name = "sync_status")
    public String syncStatus;

    @ColumnInfo(name = "last_sync")
    public long lastSync;

    @ColumnInfo(name = "firebase_id")
    public String firebaseId;

    // Empty constructor for Room and PaperMapper
    public PaperEntity() {}

    // Constructor
    @Ignore
    public PaperEntity(@NonNull String paperId, @NonNull String userId, @NonNull String title) {
        this.paperId = paperId;
        this.userId = userId;
        this.title = title;
        this.dateAdded = System.currentTimeMillis();
        this.status = "unread";
        this.priority = "medium";
        this.currentPage = 0;
        this.syncStatus = "pending";
    }
}
