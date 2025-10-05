package com.example.labverse.database.dao;
import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.labverse.database.entities.CitationEntity;
import java.util.List;
@Dao
public interface CitationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(CitationEntity citation);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CitationEntity> citations);

    @Update
    void update(CitationEntity citation);

    @Delete
    void delete(CitationEntity citation);

    @Query("SELECT * FROM citations WHERE paper_id = :paperId ORDER BY citation_order ASC")
    LiveData<List<CitationEntity>> getCitationsByPaper(String paperId);

    @Query("SELECT * FROM citations WHERE citation_id = :citationId")
    LiveData<CitationEntity> getCitationById(String citationId);

    @Query("SELECT COUNT(*) FROM citations WHERE paper_id = :paperId")
    LiveData<Integer> getCitationCount(String paperId);

    @Query("DELETE FROM citations WHERE paper_id = :paperId")
    void deleteByPaper(String paperId);
}
