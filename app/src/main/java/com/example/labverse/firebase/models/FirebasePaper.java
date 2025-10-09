package com.example.labverse.firebase.models;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;
import java.util.HashMap;
import java.util.Map;
public class FirebasePaper {
    @DocumentId
    private String paperId;
    private String userId;
    private String title;
    private String authors;
    private String journal;
    private String year;
    private String doi;
    private String abstractText;
    private String pdfUrl;
    private String status;
    private String priority;
    private boolean isFavorite;
    @ServerTimestamp
    private Timestamp dateAdded;
    @ServerTimestamp
    private Timestamp lastRead;
    private int currentPage;
    private int totalPages;

    public FirebasePaper() {}

    public FirebasePaper(String paperId, String userId, String title) {
        this.paperId = paperId;
        this.userId = userId;
        this.title = title;
        this.status = "unread";
        this.priority = "medium";
        this.isFavorite = false;
        this.currentPage = 0;
    }

    // Getters and Setters
    public String getPaperId() { return paperId; }
    public void setPaperId(String paperId) { this.paperId = paperId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

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

    public String getPdfUrl() { return pdfUrl; }
    public void setPdfUrl(String pdfUrl) { this.pdfUrl = pdfUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }

    public Timestamp getDateAdded() { return dateAdded; }
    public void setDateAdded(Timestamp dateAdded) { this.dateAdded = dateAdded; }

    public Timestamp getLastRead() { return lastRead; }
    public void setLastRead(Timestamp lastRead) { this.lastRead = lastRead; }

    public int getCurrentPage() { return currentPage; }
    public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("paperId", paperId);
        map.put("userId", userId);
        map.put("title", title);
        map.put("authors", authors);
        map.put("journal", journal);
        map.put("year", year);
        map.put("doi", doi);
        map.put("abstractText", abstractText);
        map.put("pdfUrl", pdfUrl);
        map.put("status", status);
        map.put("priority", priority);
        map.put("isFavorite", isFavorite);
        map.put("currentPage", currentPage);
        map.put("totalPages", totalPages);
        return map;
    }
}
