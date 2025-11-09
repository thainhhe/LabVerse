package com.example.labverse.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.labverse.database.entities.CitationEntity;
import com.example.labverse.database.entities.PaperEntity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for extracting metadata and citations from PDF files
 * Note: This is a basic implementation. For production, consider using Grobid or similar libraries
 */
public class PDFMetadataExtractor {
    private static final String TAG = "PDFMetadataExtractor";

    /**
     * Extract metadata from PDF file
     * This is a simplified implementation. In production, use Grobid or similar service
     */
    public static PaperEntity extractMetadata(Context context, Uri pdfUri, String userId, String paperId) {
        PaperEntity paper = new PaperEntity(paperId, userId, "Untitled Paper");
        
        try {
            // Read PDF content (simplified - in production use PDF parsing library)
            String pdfContent = readPDFContent(context, pdfUri);
            
            // Extract title
            String title = extractTitle(pdfContent);
            if (title != null && !title.isEmpty()) {
                paper.title = title;
            }
            
            // Extract authors
            String authors = extractAuthors(pdfContent);
            if (authors != null && !authors.isEmpty()) {
                paper.authors = authors;
            }
            
            // Extract journal
            String journal = extractJournal(pdfContent);
            if (journal != null && !journal.isEmpty()) {
                paper.journal = journal;
            }
            
            // Extract year
            String year = extractYear(pdfContent);
            if (year != null && !year.isEmpty()) {
                paper.year = year;
            }
            
            // Extract DOI
            String doi = extractDOI(pdfContent);
            if (doi != null && !doi.isEmpty()) {
                paper.doi = doi;
            }
            
            // Extract abstract
            String abstractText = extractAbstract(pdfContent);
            if (abstractText != null && !abstractText.isEmpty()) {
                paper.abstractText = abstractText;
            }
            
            Log.d(TAG, "Extracted metadata: " + paper.title);
            
        } catch (Exception e) {
            Log.e(TAG, "Error extracting metadata: " + e.getMessage(), e);
        }
        
        return paper;
    }

    /**
     * Extract citations/references from PDF
     * This is a simplified implementation. In production, use Grobid or similar service
     */
    public static List<CitationEntity> extractCitations(Context context, Uri pdfUri, String paperId) {
        List<CitationEntity> citations = new ArrayList<>();
        
        try {
            String pdfContent = readPDFContent(context, pdfUri);
            
            // Extract references section
            String referencesSection = extractReferencesSection(pdfContent);
            
            if (referencesSection != null && !referencesSection.isEmpty()) {
                // Parse individual citations
                List<String> citationStrings = parseCitationStrings(referencesSection);
                
                int order = 1;
                for (String citationString : citationStrings) {
                    CitationEntity citation = parseCitation(citationString, paperId, order++);
                    if (citation != null) {
                        citations.add(citation);
                    }
                }
            }
            
            Log.d(TAG, "Extracted " + citations.size() + " citations");
            
        } catch (Exception e) {
            Log.e(TAG, "Error extracting citations: " + e.getMessage(), e);
        }
        
        return citations;
    }

    // Helper methods for extraction

    private static String readPDFContent(Context context, Uri pdfUri) {
        // Simplified: In production, use a proper PDF parsing library
        // For now, return empty string - actual implementation would use Apache PDFBox or similar
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(pdfUri);
            if (inputStream != null) {
                // Read PDF text content (simplified)
                // In production: Use PDFBox, iText, or similar library
                inputStream.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error reading PDF: " + e.getMessage());
        }
        return "";
    }

    private static String extractTitle(String content) {
        // Pattern to find title (usually at the beginning, large font)
        Pattern pattern = Pattern.compile("(?i)(?:title|^)\\s*:?\\s*([A-Z][^\\n]{10,200})");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    private static String extractAuthors(String content) {
        // Pattern to find authors (usually after title)
        Pattern pattern = Pattern.compile("(?i)(?:authors?|by)\\s*:?\\s*([A-Z][^\\n]{5,200})");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    private static String extractJournal(String content) {
        // Pattern to find journal name
        Pattern pattern = Pattern.compile("(?i)(?:journal|publication)\\s*:?\\s*([A-Z][^\\n]{3,100})");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    private static String extractYear(String content) {
        // Pattern to find year (4 digits, usually between 1900-2100)
        Pattern pattern = Pattern.compile("\\b(19|20)\\d{2}\\b");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    private static String extractDOI(String content) {
        // Pattern to find DOI
        Pattern pattern = Pattern.compile("(?i)(?:doi|digital object identifier)\\s*:?\\s*(10\\.\\d+/[^\\s]+)");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    private static String extractAbstract(String content) {
        // Pattern to find abstract section
        Pattern pattern = Pattern.compile("(?i)abstract\\s*:?\\s*([^\\n]{50,2000})", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    private static String extractReferencesSection(String content) {
        // Pattern to find references section
        Pattern pattern = Pattern.compile("(?i)(?:references?|bibliography)\\s*:?\\s*(.+?)(?:\\n\\n|$)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    private static List<String> parseCitationStrings(String referencesSection) {
        List<String> citations = new ArrayList<>();
        
        // Split by numbered references (e.g., [1], 1., etc.)
        String[] parts = referencesSection.split("\\[\\d+\\]|^\\d+\\.|^\\[\\d+\\]");
        
        for (String part : parts) {
            String citation = part.trim();
            if (citation.length() > 20) { // Minimum length for a valid citation
                citations.add(citation);
            }
        }
        
        return citations;
    }

    private static CitationEntity parseCitation(String citationString, String paperId, int order) {
        CitationEntity citation = new CitationEntity(UUID.randomUUID().toString(), paperId);
        citation.setCitationOrder(order);
        
        // Try to extract components from citation string
        // This is simplified - in production, use a proper citation parser
        
        // Extract authors (usually at the beginning)
        Pattern authorPattern = Pattern.compile("^([A-Z][^,]{2,50}(?:,\\s*[A-Z][^,]{2,50})*?)\\s*[,.]");
        Matcher authorMatcher = authorPattern.matcher(citationString);
        if (authorMatcher.find()) {
            citation.setAuthors(authorMatcher.group(1).trim());
        }
        
        // Extract year
        Pattern yearPattern = Pattern.compile("\\b(19|20)\\d{2}\\b");
        Matcher yearMatcher = yearPattern.matcher(citationString);
        if (yearMatcher.find()) {
            citation.setYear(yearMatcher.group());
        }
        
        // Extract title (usually in quotes or after authors)
        Pattern titlePattern = Pattern.compile("[\"']([^\"']{10,200})[\"']|([A-Z][^,]{10,200})\\.");
        Matcher titleMatcher = titlePattern.matcher(citationString);
        if (titleMatcher.find()) {
            citation.setTitle(titleMatcher.group(1) != null ? titleMatcher.group(1) : titleMatcher.group(2));
        }
        
        // Extract journal
        Pattern journalPattern = Pattern.compile("(?i)(?:in|journal of|proceedings of)\\s+([A-Z][^,]{3,100})");
        Matcher journalMatcher = journalPattern.matcher(citationString);
        if (journalMatcher.find()) {
            citation.setJournal(journalMatcher.group(1).trim());
        }
        
        // Extract DOI
        Pattern doiPattern = Pattern.compile("(?i)(?:doi|digital object identifier)\\s*:?\\s*(10\\.\\d+/[^\\s]+)");
        Matcher doiMatcher = doiPattern.matcher(citationString);
        if (doiMatcher.find()) {
            citation.setDoi(doiMatcher.group(1).trim());
        }
        
        return citation;
    }
}

