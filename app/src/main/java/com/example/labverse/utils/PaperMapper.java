package com.example.labverse.utils;

import com.example.labverse.database.entities.PaperEntity;
import com.example.labverse.models.Paper;

public class PaperMapper {

    public static Paper fromEntity(PaperEntity entity) {
        if (entity == null) {
            return null;
        }
        Paper paper = new Paper();
        paper.setId(entity.paperId);
        paper.setTitle(entity.title);
        paper.setAuthors(entity.authors);
        paper.setJournal(entity.journal);
        paper.setYear(entity.year);
        paper.setStatus(entity.status);
        paper.setPriority(entity.priority);
        paper.setPdfPath(entity.pdfPath);
        paper.setDoi(entity.doi);
        paper.setAbstractText(entity.abstractText);
        paper.setFavorite(entity.isFavorite);
        paper.setDateAdded(entity.dateAdded);
        paper.setLastRead(entity.lastRead);
        return paper;
    }

    public static PaperEntity toEntity(Paper paper) {
        if (paper == null) {
            return null;
        }
        PaperEntity entity = new PaperEntity();
        entity.paperId = paper.getId();
        entity.title = paper.getTitle();
        entity.authors = paper.getAuthors();
        entity.journal = paper.getJournal();
        entity.year = paper.getYear();
        entity.status = paper.getStatus();
        entity.priority = paper.getPriority();
        entity.pdfPath = paper.getPdfPath();
        entity.doi = paper.getDoi();
        entity.abstractText = paper.getAbstractText();
        entity.isFavorite = paper.isFavorite();
        entity.dateAdded = paper.getDateAdded();
        entity.lastRead = paper.getLastRead();
        // You need to set the userId when converting to an entity
        // entity.userId = "test_user"; // Replace with actual user ID
        return entity;
    }
}
