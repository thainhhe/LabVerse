package com.example.labverse.utils;

import com.example.labverse.database.entities.CitationEntity;
import com.example.labverse.database.entities.PaperEntity;

/**
 * Utility class for formatting citations in different styles (APA, MLA, BibTeX)
 */
public class CitationFormatter {

    /**
     * Format paper citation in APA style
     * Format: Author, A. A., & Author, B. B. (Year). Title of article. Journal Name, Volume(Issue), pages. https://doi.org/DOI
     */
    public static String formatAPA(PaperEntity paper) {
        StringBuilder citation = new StringBuilder();

        // Authors
        if (paper.authors != null && !paper.authors.isEmpty()) {
            citation.append(formatAuthorsAPA(paper.authors));
        } else {
            citation.append("(n.d.)");
        }

        // Year
        if (paper.year != null && !paper.year.isEmpty()) {
            citation.append(" (").append(paper.year).append("). ");
        } else {
            citation.append(" (n.d.). ");
        }

        // Title
        if (paper.title != null && !paper.title.isEmpty()) {
            citation.append(paper.title);
            if (!paper.title.endsWith(".")) {
                citation.append(".");
            }
            citation.append(" ");
        }

        // Journal
        if (paper.journal != null && !paper.journal.isEmpty()) {
            citation.append(paper.journal);
            if (!paper.journal.endsWith(".")) {
                citation.append(".");
            }
        }

        // DOI
        if (paper.doi != null && !paper.doi.isEmpty()) {
            citation.append(" https://doi.org/").append(paper.doi);
        }

        return citation.toString().trim();
    }

    /**
     * Format paper citation in MLA style
     * Format: Author, First Name, and Author, First Name. "Title of Article." Journal Name, vol. Volume, no. Issue, Year, pages. DOI.
     */
    public static String formatMLA(PaperEntity paper) {
        StringBuilder citation = new StringBuilder();

        // Authors
        if (paper.authors != null && !paper.authors.isEmpty()) {
            citation.append(formatAuthorsMLA(paper.authors));
            citation.append(". ");
        }

        // Title
        if (paper.title != null && !paper.title.isEmpty()) {
            citation.append("\"").append(paper.title).append(".\" ");
        }

        // Journal
        if (paper.journal != null && !paper.journal.isEmpty()) {
            citation.append(paper.journal);
        }

        // Year
        if (paper.year != null && !paper.year.isEmpty()) {
            citation.append(", ").append(paper.year);
        }

        // DOI
        if (paper.doi != null && !paper.doi.isEmpty()) {
            citation.append(", https://doi.org/").append(paper.doi).append(".");
        } else {
            citation.append(".");
        }

        return citation.toString().trim();
    }

    /**
     * Format paper citation in BibTeX format
     */
    public static String formatBibTeX(PaperEntity paper) {
        StringBuilder bibtex = new StringBuilder();
        String citeKey = generateCiteKey(paper);

        bibtex.append("@article{").append(citeKey).append(",\n");
        
        if (paper.authors != null && !paper.authors.isEmpty()) {
            bibtex.append("  author = {").append(paper.authors).append("},\n");
        }
        
        if (paper.title != null && !paper.title.isEmpty()) {
            bibtex.append("  title = {").append(paper.title).append("},\n");
        }
        
        if (paper.journal != null && !paper.journal.isEmpty()) {
            bibtex.append("  journal = {").append(paper.journal).append("},\n");
        }
        
        if (paper.year != null && !paper.year.isEmpty()) {
            bibtex.append("  year = {").append(paper.year).append("},\n");
        }
        
        if (paper.doi != null && !paper.doi.isEmpty()) {
            bibtex.append("  doi = {").append(paper.doi).append("},\n");
        }

        // Remove trailing comma and newline
        if (bibtex.length() > 0 && bibtex.charAt(bibtex.length() - 2) == ',') {
            bibtex.deleteCharAt(bibtex.length() - 2);
        }

        bibtex.append("}");

        return bibtex.toString();
    }

    /**
     * Format citation entity in APA style
     */
    public static String formatCitationAPA(CitationEntity citation) {
        StringBuilder result = new StringBuilder();

        if (citation.getAuthors() != null && !citation.getAuthors().isEmpty()) {
            result.append(formatAuthorsAPA(citation.getAuthors()));
        }

        if (citation.getYear() != null && !citation.getYear().isEmpty()) {
            result.append(" (").append(citation.getYear()).append("). ");
        }

        if (citation.getTitle() != null && !citation.getTitle().isEmpty()) {
            result.append(citation.getTitle());
            if (!citation.getTitle().endsWith(".")) {
                result.append(".");
            }
            result.append(" ");
        }

        if (citation.getJournal() != null && !citation.getJournal().isEmpty()) {
            result.append(citation.getJournal());
            if (!citation.getJournal().endsWith(".")) {
                result.append(".");
            }
        }

        if (citation.getDoi() != null && !citation.getDoi().isEmpty()) {
            result.append(" https://doi.org/").append(citation.getDoi());
        }

        return result.toString().trim();
    }

    /**
     * Format citation entity in MLA style
     */
    public static String formatCitationMLA(CitationEntity citation) {
        StringBuilder result = new StringBuilder();

        if (citation.getAuthors() != null && !citation.getAuthors().isEmpty()) {
            result.append(formatAuthorsMLA(citation.getAuthors())).append(". ");
        }

        if (citation.getTitle() != null && !citation.getTitle().isEmpty()) {
            result.append("\"").append(citation.getTitle()).append(".\" ");
        }

        if (citation.getJournal() != null && !citation.getJournal().isEmpty()) {
            result.append(citation.getJournal());
        }

        if (citation.getYear() != null && !citation.getYear().isEmpty()) {
            result.append(", ").append(citation.getYear());
        }

        if (citation.getDoi() != null && !citation.getDoi().isEmpty()) {
            result.append(", https://doi.org/").append(citation.getDoi()).append(".");
        } else {
            result.append(".");
        }

        return result.toString().trim();
    }

    /**
     * Get BibTeX from citation entity (if available) or format it
     */
    public static String formatCitationBibTeX(CitationEntity citation) {
        if (citation.getBibtex() != null && !citation.getBibtex().isEmpty()) {
            return citation.getBibtex();
        }

        // Generate BibTeX if not available
        StringBuilder bibtex = new StringBuilder();
        String citeKey = generateCiteKeyFromCitation(citation);

        bibtex.append("@article{").append(citeKey).append(",\n");

        if (citation.getAuthors() != null && !citation.getAuthors().isEmpty()) {
            bibtex.append("  author = {").append(citation.getAuthors()).append("},\n");
        }

        if (citation.getTitle() != null && !citation.getTitle().isEmpty()) {
            bibtex.append("  title = {").append(citation.getTitle()).append("},\n");
        }

        if (citation.getJournal() != null && !citation.getJournal().isEmpty()) {
            bibtex.append("  journal = {").append(citation.getJournal()).append("},\n");
        }

        if (citation.getYear() != null && !citation.getYear().isEmpty()) {
            bibtex.append("  year = {").append(citation.getYear()).append("},\n");
        }

        if (citation.getDoi() != null && !citation.getDoi().isEmpty()) {
            bibtex.append("  doi = {").append(citation.getDoi()).append("},\n");
        }

        if (bibtex.length() > 0 && bibtex.charAt(bibtex.length() - 2) == ',') {
            bibtex.deleteCharAt(bibtex.length() - 2);
        }

        bibtex.append("}");

        return bibtex.toString();
    }

    // Helper methods

    private static String formatAuthorsAPA(String authors) {
        if (authors == null || authors.isEmpty()) {
            return "(n.d.)";
        }

        String[] authorList = authors.split("(,|and|&)");
        StringBuilder formatted = new StringBuilder();

        for (int i = 0; i < authorList.length; i++) {
            String author = authorList[i].trim();
            if (author.isEmpty()) continue;

            String[] parts = author.split("\\s+");
            if (parts.length >= 2) {
                // Last name, First initial
                formatted.append(parts[parts.length - 1]).append(", ");
                formatted.append(parts[0].charAt(0)).append(".");
                if (parts.length > 2) {
                    formatted.append(parts[1].charAt(0)).append(".");
                }
            } else {
                formatted.append(author);
            }

            if (i < authorList.length - 1) {
                formatted.append(", ");
            }
        }

        return formatted.toString();
    }

    private static String formatAuthorsMLA(String authors) {
        if (authors == null || authors.isEmpty()) {
            return "Unknown";
        }

        String[] authorList = authors.split("(,|and|&)");
        StringBuilder formatted = new StringBuilder();

        for (int i = 0; i < authorList.length; i++) {
            String author = authorList[i].trim();
            if (author.isEmpty()) continue;

            String[] parts = author.split("\\s+");
            if (parts.length >= 2) {
                // Last name, First name
                formatted.append(parts[parts.length - 1]).append(", ");
                for (int j = 0; j < parts.length - 1; j++) {
                    formatted.append(parts[j]);
                    if (j < parts.length - 2) {
                        formatted.append(" ");
                    }
                }
            } else {
                formatted.append(author);
            }

            if (i < authorList.length - 1) {
                formatted.append(", and ");
            }
        }

        return formatted.toString();
    }

    private static String generateCiteKey(PaperEntity paper) {
        StringBuilder key = new StringBuilder();

        if (paper.authors != null && !paper.authors.isEmpty()) {
            String firstAuthor = paper.authors.split("(,|and|&)")[0].trim();
            String[] parts = firstAuthor.split("\\s+");
            if (parts.length > 0) {
                key.append(parts[parts.length - 1].toLowerCase());
            }
        }

        if (paper.year != null && !paper.year.isEmpty()) {
            key.append(paper.year);
        }

        if (key.length() == 0) {
            key.append("unknown");
        }

        return key.toString();
    }

    private static String generateCiteKeyFromCitation(CitationEntity citation) {
        StringBuilder key = new StringBuilder();

        if (citation.getAuthors() != null && !citation.getAuthors().isEmpty()) {
            String firstAuthor = citation.getAuthors().split("(,|and|&)")[0].trim();
            String[] parts = firstAuthor.split("\\s+");
            if (parts.length > 0) {
                key.append(parts[parts.length - 1].toLowerCase());
            }
        }

        if (citation.getYear() != null && !citation.getYear().isEmpty()) {
            key.append(citation.getYear());
        }

        if (key.length() == 0) {
            key.append("unknown");
        }

        return key.toString();
    }
}

