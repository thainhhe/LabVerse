package com.example.labverse.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "citations",
        foreignKeys = @ForeignKey(entity = PaperEntity.class,
                parentColumns = "paper_id",
                childColumns = "paper_id",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("paper_id")})
public class CitationEntity {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "citation_id")
    private String citationId;

    @NonNull
    @ColumnInfo(name = "paper_id")
    private String paperId;

    private String title;
    private String authors;
    private String journal;
    private String year;
    private String doi;

    @ColumnInfo(name = "bibtex")
    private String bibtex;

    @ColumnInfo(name = "citation_order")
    private int citationOrder;

    public CitationEntity(@NonNull String citationId, @NonNull String paperId) {
        this.citationId = citationId;
        this.paperId = paperId;
    }

    // Getters and Setters
    @NonNull
    public String getCitationId() { return citationId; }
    public void setCitationId(@NonNull String citationId) { this.citationId = citationId; }

    @NonNull
    public String getPaperId() { return paperId; }
    public void setPaperId(@NonNull String paperId) { this.paperId = paperId; }

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

    public String getBibtex() { return bibtex; }
    public void setBibtex(String bibtex) { this.bibtex = bibtex; }

    public int getCitationOrder() { return citationOrder; }
    public void setCitationOrder(int citationOrder) { this.citationOrder = citationOrder; }
}