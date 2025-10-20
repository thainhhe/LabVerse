package com.example.labverse.models;

import java.util.HashSet;
import java.util.Set;

public class SearchFilters {
    private Set<String> authors = new HashSet<>();
    private Set<String> journals = new HashSet<>();
    private Set<String> keywords = new HashSet<>();
    private Integer yearFrom;
    private Integer yearTo;
    private Set<ReadingStatus> readingStatus = new HashSet<>();

    // Constructors
    public SearchFilters() {}

    public SearchFilters(Set<String> authors, Set<String> journals, Set<String> keywords,
                        Integer yearFrom, Integer yearTo, Set<ReadingStatus> readingStatus) {
        this.authors = authors;
        this.journals = journals;
        this.keywords = keywords;
        this.yearFrom = yearFrom;
        this.yearTo = yearTo;
        this.readingStatus = readingStatus;
    }

    // Getters and setters
    public Set<String> getAuthors() { return authors; }
    public void setAuthors(Set<String> authors) { this.authors = authors; }

    public Set<String> getJournals() { return journals; }
    public void setJournals(Set<String> journals) { this.journals = journals; }

    public Set<String> getKeywords() { return keywords; }
    public void setKeywords(Set<String> keywords) { this.keywords = keywords; }

    public Integer getYearFrom() { return yearFrom; }
    public void setYearFrom(Integer yearFrom) { this.yearFrom = yearFrom; }

    public Integer getYearTo() { return yearTo; }
    public void setYearTo(Integer yearTo) { this.yearTo = yearTo; }

    public Set<ReadingStatus> getReadingStatus() { return readingStatus; }
    public void setReadingStatus(Set<ReadingStatus> readingStatus) { this.readingStatus = readingStatus; }

    public boolean isActive() {
        return !authors.isEmpty() || !journals.isEmpty() || !keywords.isEmpty() ||
               yearFrom != null || yearTo != null || !readingStatus.isEmpty();
    }

    public int getActiveFilterCount() {
        int count = authors.size() + journals.size() + keywords.size() + readingStatus.size();
        if (yearFrom != null || yearTo != null) count++;
        return count;
    }
}
