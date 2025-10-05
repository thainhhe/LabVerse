package com.example.labverse.database.entities;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
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
    private String paperId;

    @NonNull
    @ColumnInfo(name = "user_id")
    private String userId;

    @NonNull
    private String title;

    private String authors;
    private String journal;
    private String year;
    private String doi;

    @ColumnInfo(name = "abstract_text")
    private String abstractText;

    @ColumnInfo(name = "pdf_path")
    private String pdfPath;

    @ColumnInfo(name = "pdf_url")
    private String pdfUrl; // Firebase Storage URL

    private String status; // "unread", "reading", "finished"
    private String priority; // "high", "medium", "low"

    @ColumnInfo(name = "is_favorite")
    private boolean isFavorite;

    @ColumnInfo(name = "date_added")
    private long dateAdded;

    @ColumnInfo(name = "last_read")
    private long lastRead;

    @ColumnInfo(name = "current_page")
    private int currentPage;

    @ColumnInfo(name = "total_pages")
    private int totalPages;

    @ColumnInfo(name = "sync_status")
    private String syncStatus;

    @ColumnInfo(name = "last_sync")
    private long lastSync;

    @ColumnInfo(name = "firebase_id")
    private String firebaseId;

    // Constructor
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

    // Getters and Setters
    @NonNull
    public String getPaperId() { return paperId; }
    public void setPaperId(@NonNull String paperId) { this.paperId = paperId; }

    @NonNull
    public String getUserId() { return userId; }
    public void setUserId(@NonNull String userId) { this.userId = userId; }

    @NonNull
    public String getTitle() { return title; }
    public void setTitle(@NonNull String title) { this.title = title; }

    public String getAuthors() { return authors; }
    public void setAuthors(String authors) { this.authors = authors; }

    public String getJournal() { return journal; }
    public void setJournal(String journal) { this.journal = journal; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public String getDoi() { return doi; }
    public void setDoi(String doi) { this.doi = doi; }

    public String getAbstractText() { return abstractText; }
    public void setAbstractText(String abstractText) { this.abstractText = abstractText; }

    public String getPdfPath() { return pdfPath; }
    public void setPdfPath(String pdfPath) { this.pdfPath = pdfPath; }

    public String getPdfUrl() { return pdfUrl; }
    public void setPdfUrl(String pdfUrl) { this.pdfUrl = pdfUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }

    public long getDateAdded() { return dateAdded; }
    public void setDateAdded(long dateAdded) { this.dateAdded = dateAdded; }

    public long getLastRead() { return lastRead; }
    public void setLastRead(long lastRead) { this.lastRead = lastRead; }

    public int getCurrentPage() { return currentPage; }
    public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public String getSyncStatus() { return syncStatus; }
    public void setSyncStatus(String syncStatus) { this.syncStatus = syncStatus; }

    public long getLastSync() { return lastSync; }
    public void setLastSync(long lastSync) { this.lastSync = lastSync; }

    public String getFirebaseId() { return firebaseId; }
    public void setFirebaseId(String firebaseId) { this.firebaseId = firebaseId; }
}
