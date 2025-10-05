package com.example.labverse.database.dao;
import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.labverse.database.entities.CollectionPaperEntity;
import java.util.List;
@Dao
public interface CollectionPaperDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(CollectionPaperEntity collectionPaper);

    @Delete
    void delete(CollectionPaperEntity collectionPaper);

    @Query("SELECT * FROM collection_papers WHERE collection_id = :collectionId ORDER BY added_at DESC")
    LiveData<List<CollectionPaperEntity>> getPapersByCollection(String collectionId);

    @Query("SELECT * FROM collection_papers WHERE paper_id = :paperId")
    LiveData<List<CollectionPaperEntity>> getCollectionsByPaper(String paperId);

    @Query("SELECT * FROM collection_papers WHERE collection_id = :collectionId AND paper_id = :paperId")
    CollectionPaperEntity getCollectionPaper(String collectionId, String paperId);

    @Query("UPDATE collection_papers SET status = :status WHERE collection_paper_id = :id")
    void updateStatus(String id, String status);

    @Query("UPDATE collection_papers SET priority = :priority WHERE collection_paper_id = :id")
    void updatePriority(String id, String priority);

    @Query("SELECT COUNT(*) FROM collection_papers WHERE collection_id = :collectionId")
    LiveData<Integer> getPaperCount(String collectionId);
}
