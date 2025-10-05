package com.example.labverse.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.labverse.database.entities.PaperEntity;
import java.util.List;

@Dao
public interface PaperDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(PaperEntity paper);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<PaperEntity> papers);

    @Update
    void update(PaperEntity paper);

    @Delete
    void delete(PaperEntity paper);

    @Query("SELECT * FROM papers WHERE user_id = :userId ORDER BY date_added DESC")
    LiveData<List<PaperEntity>> getAllPapersByUser(String userId);

    @Query("SELECT * FROM papers WHERE paper_id = :paperId")
    LiveData<PaperEntity> getPaperById(String paperId);

    @Query("SELECT * FROM papers WHERE paper_id = :paperId")
    PaperEntity getPaperByIdSync(String paperId);

    @Query("SELECT * FROM papers WHERE user_id = :userId AND status = :status ORDER BY date_added DESC")
    LiveData<List<PaperEntity>> getPapersByStatus(String userId, String status);

    @Query("SELECT * FROM papers WHERE user_id = :userId AND is_favorite = 1 ORDER BY date_added DESC")
    LiveData<List<PaperEntity>> getFavoritePapers(String userId);

    @Query("SELECT * FROM papers WHERE user_id = :userId AND last_read > 0 ORDER BY last_read DESC LIMIT 10")
    LiveData<List<PaperEntity>> getRecentlyReadPapers(String userId);

    @Query("SELECT * FROM papers WHERE user_id = :userId ORDER BY date_added DESC LIMIT :limit")
    LiveData<List<PaperEntity>> getRecentlyAddedPapers(String userId, int limit);

    @Query("SELECT * FROM papers WHERE user_id = :userId AND (title LIKE :query OR authors LIKE :query OR journal LIKE :query)")
    LiveData<List<PaperEntity>> searchPapers(String userId, String query);

    @Query("SELECT * FROM papers WHERE user_id = :userId AND sync_status = :status")
    List<PaperEntity> getPapersBySyncStatus(String userId, String status);

    @Query("UPDATE papers SET status = :status WHERE paper_id = :paperId")
    void updateStatus(String paperId, String status);

    @Query("UPDATE papers SET priority = :priority WHERE paper_id = :paperId")
    void updatePriority(String paperId, String priority);

    @Query("UPDATE papers SET is_favorite = :isFavorite WHERE paper_id = :paperId")
    void updateFavorite(String paperId, boolean isFavorite);

    @Query("UPDATE papers SET current_page = :currentPage, last_read = :lastRead WHERE paper_id = :paperId")
    void updateReadingProgress(String paperId, int currentPage, long lastRead);

    @Query("UPDATE papers SET sync_status = :status, last_sync = :lastSync WHERE paper_id = :paperId")
    void updateSyncStatus(String paperId, String status, long lastSync);

    @Query("SELECT COUNT(*) FROM papers WHERE user_id = :userId")
    LiveData<Integer> getPaperCount(String userId);

    @Query("DELETE FROM papers WHERE user_id = :userId")
    void deleteAllByUser(String userId);
}
