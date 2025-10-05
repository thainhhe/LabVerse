package com.example.labverse.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.labverse.database.entities.AnnotationEntity;
import java.util.List;

@Dao
public interface AnnotationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(AnnotationEntity annotation);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<AnnotationEntity> annotations);

    @Update
    void update(AnnotationEntity annotation);

    @Delete
    void delete(AnnotationEntity annotation);

    @Query("SELECT * FROM annotations WHERE paper_id = :paperId ORDER BY page_number ASC, created_at ASC")
    LiveData<List<AnnotationEntity>> getAnnotationsByPaper(String paperId);

    @Query("SELECT * FROM annotations WHERE paper_id = :paperId AND page_number = :pageNumber ORDER BY created_at ASC")
    LiveData<List<AnnotationEntity>> getAnnotationsByPage(String paperId, int pageNumber);

    @Query("SELECT * FROM annotations WHERE annotation_id = :annotationId")
    LiveData<AnnotationEntity> getAnnotationById(String annotationId);

    @Query("SELECT * FROM annotations WHERE user_id = :userId AND paper_id = :paperId ORDER BY created_at DESC")
    LiveData<List<AnnotationEntity>> getUserAnnotationsForPaper(String userId, String paperId);

    @Query("SELECT * FROM annotations WHERE user_id = :userId AND sync_status = :status")
    List<AnnotationEntity> getAnnotationsBySyncStatus(String userId, String status);

    @Query("UPDATE annotations SET sync_status = :status, updated_at = :updatedAt WHERE annotation_id = :annotationId")
    void updateSyncStatus(String annotationId, String status, long updatedAt);

    @Query("DELETE FROM annotations WHERE paper_id = :paperId")
    void deleteByPaper(String paperId);

    @Query("SELECT COUNT(*) FROM annotations WHERE paper_id = :paperId")
    LiveData<Integer> getAnnotationCount(String paperId);
}
