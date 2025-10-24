package com.example.labverse.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.labverse.database.entities.PaperEntity;
import java.util.List;

@Dao
public interface PaperDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPapers(List<PaperEntity> papers);

    @Query("SELECT * FROM papers ORDER BY date_added DESC LIMIT 50")
    LiveData<List<PaperEntity>> getRecentlyAdded();

    @Query("SELECT * FROM papers WHERE last_read IS NOT NULL ORDER BY last_read DESC LIMIT 50")
    LiveData<List<PaperEntity>> getRecentlyRead();

    @Query("SELECT * FROM papers WHERE is_favorite = 1 ORDER BY date_added DESC")
    LiveData<List<PaperEntity>> getFavorites();

    @Query("UPDATE papers SET status = :status, last_read = CASE WHEN :status = 'reading' THEN :timestamp ELSE last_read END WHERE paper_id = :paperId")
    void updateReadingStatus(String paperId, String status, long timestamp);

    @Query("UPDATE papers SET is_favorite = NOT is_favorite WHERE paper_id = :paperId")
    void toggleFavorite(String paperId);

    @Query("SELECT DISTINCT authors FROM papers WHERE authors IS NOT NULL AND authors != ''")
    LiveData<List<String>> getAllAuthors();

    @Query("SELECT DISTINCT journal FROM papers WHERE journal IS NOT NULL AND journal != ''")
    LiveData<List<String>> getAllJournals();

    // Keeping existing methods
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(PaperEntity paper);

    @Update
    void update(PaperEntity paper);

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

    @Query("SELECT COUNT(*) FROM papers WHERE user_id = :userId")
    int getPaperCountSync(String userId);

    @Query("DELETE FROM papers WHERE user_id = :userId")
    void deleteAllByUser(String userId);

    @Query("DELETE FROM papers WHERE paper_id IN (:paperIds)")
    void deletePapersByIds(List<String> paperIds);

    @Query("SELECT paper_id FROM papers")
    List<String> getAllPaperIds();

    @Query("UPDATE papers SET status = :status WHERE paper_id IN (:paperIds)")
    void updateStatusForPapers(List<String> paperIds, String status);

    @Query("SELECT * FROM papers WHERE " +
       "(:query IS NULL OR title LIKE '%' || :query || '%' OR authors LIKE '%' || :query || '%' OR journal LIKE '%' || :query || '%') " +
       "AND (:authorFilter IS NULL OR authors LIKE '%' || :authorFilter || '%') " +
       "AND (:journalFilter IS NULL OR journal IN (:journalFilter)) " +
       "AND (:year IS NULL OR year = :year) " +
       "AND (:readingStatus IS NULL OR status IN (:readingStatus)) " +
       "ORDER BY " +
       "CASE WHEN :query IS NOT NULL AND title LIKE '%' || :query || '%' THEN 1 " +
       "     WHEN :query IS NOT NULL AND authors LIKE '%' || :query || '%' THEN 2 " +
       "     ELSE 3 END, " +
       "date_added DESC")
    List<PaperEntity> advancedSearch(String query, String authorFilter, List<String> journalFilter, 
                          Integer year, List<String> readingStatus);
}
