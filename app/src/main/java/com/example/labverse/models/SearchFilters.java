package com.example.labverse.models;

import java.util.HashSet;
import java.util.Set;

public class SearchFilters {
    private Set<String> authors = new HashSet<>();
    private Set<String> journals = new HashSet<>();
    private Set<String> keywords = new HashSet<>();
    private Integer year;
    private Set<ReadingStatus> readingStatus = new HashSet<>();

    // Constructors
    public SearchFilters() {}

    public SearchFilters(Set<String> authors, Set<String> journals, Set<String> keywords,
                        Integer year, Set<ReadingStatus> readingStatus) {
        this.authors = authors;
        this.journals = journals;
        this.keywords = keywords;
        this.year = year;
        this.readingStatus = readingStatus;
    }

    // Getters and setters
    public Set<String> getAuthors() { return authors; }
    public void setAuthors(Set<String> authors) { this.authors = authors; }

    public Set<String> getJournals() { return journals; }
    public void setJournals(Set<String> journals) { this.journals = journals; }

    public Set<String> getKeywords() { return keywords; }
    public void setKeywords(Set<String> keywords) { this.keywords = keywords; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public Set<ReadingStatus> getReadingStatus() { return readingStatus; }
    public void setReadingStatus(Set<ReadingStatus> readingStatus) { this.readingStatus = readingStatus; }

    public boolean isActive() {
        return !authors.isEmpty() || !journals.isEmpty() || !keywords.isEmpty() ||
               year != null || !readingStatus.isEmpty();
    }

    public int getActiveFilterCount() {
        int count = authors.size() + journals.size() + keywords.size() + readingStatus.size();
        if (year != null) count++;
        return count;
    }
}
