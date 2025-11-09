package com.example.labverse.models;

import java.util.List;

/**
 * Represents the different states of the search UI.
 */
public class SearchState {
    // Private constructor to prevent instantiation of the base class
    private SearchState() {}

    /** The initial state before any search is performed. */
    public static final class Idle extends SearchState {}

    /** The state when a search is in progress. */
    public static final class Loading extends SearchState {}

    /** The state when a search returns successfully with results. */
    public static final class Success extends SearchState {
        private final List<Paper> papers;
        private final String query;

        public Success(List<Paper> papers, String query) {
            this.papers = papers;
            this.query = query;
        }

        public List<Paper> getPapers() { return papers; }
        public String getQuery() { return query; }
    }

    /** The state when an error occurs during a search. */
    public static final class Error extends SearchState {
        private final String message;

        public Error(String message) {
            this.message = message;
        }

        public String getMessage() { return message; }
    }

    /** The state when a search completes with no results. */
    public static final class Empty extends SearchState {}
}
