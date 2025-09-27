package com.example.labverse.models;

import java.io.Serializable;

public class Paper implements Serializable {
    private String id;
    private String title;
    private String authors;
    private String journal;
    private String year;
    private String status; // "unread", "reading", "finished"
    private String priority; // "high", "medium", "low"
    private String pdfPath;
    private String doi;
    private String abstractText;
    private boolean isFavorite;
    private long dateAdded;
    private long lastRead;

    // Constructors
    public Paper() {}

    public Paper(String id, String title, String authors, String journal, String year, String status) {
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.journal = journal;
        this.year = year;
        this.status = status;
        this.dateAdded = System.currentTimeMillis();
        this.lastRead = 0;
        this.isFavorite = false;
        this.priority = "medium";
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getJournal() {
        return journal;
    }

    public void setJournal(String journal) {
        this.journal = journal;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getPdfPath() {
        return pdfPath;
    }

    public void setPdfPath(String pdfPath) {
        this.pdfPath = pdfPath;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getAbstractText() {
        return abstractText;
    }

    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }

    public long getLastRead() {
        return lastRead;
    }

    public void setLastRead(long lastRead) {
        this.lastRead = lastRead;
    }

    @Override
    public String toString() {
        return "Paper{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", authors='" + authors + '\'' +
                ", journal='" + journal + '\'' +
                ", year='" + year + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
