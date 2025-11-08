package com.example.labverse.models;

import java.util.HashSet;
import java.util.Set;

public class SearchFilters {
    private Set<String> authors = new HashSet<>();
    private Set<String> journals = new HashSet<>();
    // keywords field removed
    private Integer year;
    private Set<ReadingStatus> readingStatus = new HashSet<>();

    // Default constructor
    public SearchFilters() {}

    // Full constructor - keywords removed
    public SearchFilters(Set<String> authors, Set<String> journals, Set<String> keywords, 
                        Integer year, Set<ReadingStatus> readingStatus) {
        this.authors = authors != null ? authors : new HashSet<>();
        this.journals = journals != null ? journals : new HashSet<>();
        this.year = year;
        this.readingStatus = readingStatus != null ? readingStatus : new HashSet<>();
    }

    // Getters and Setters
    public Set<String> getAuthors() { return authors; }
    public void setAuthors(Set<String> authors) { this.authors = authors; }

    public Set<String> getJournals() { return journals; }
    public void setJournals(Set<String> journals) { this.journals = journals; }

    // keywords getter/setter removed

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public Set<ReadingStatus> getReadingStatus() { return readingStatus; }
    public void setReadingStatus(Set<ReadingStatus> readingStatus) { this.readingStatus = readingStatus; }

    public boolean isActive() {
        return !authors.isEmpty() || !journals.isEmpty() || 
               year != null || !readingStatus.isEmpty();
    }

    public int getActiveFilterCount() {
        int count = authors.size() + journals.size() + readingStatus.size();
        if (year != null) count++;
        return count;
    }
}