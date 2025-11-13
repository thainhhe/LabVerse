package com.example.labverse.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.labverse.database.entities.CollectionEntity;
import com.example.labverse.database.entities.CollectionPaperCrossRef;
import com.example.labverse.database.relations.CollectionWithPapers;
import java.util.List;

@Dao
public interface CollectionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(CollectionEntity collection);

    @Update
    void update(CollectionEntity collection);

    @Delete
    void delete(CollectionEntity collection);

    @Query("SELECT * FROM collections WHERE member_ids LIKE '%' || :userId || '%' ORDER BY created_at DESC")
    LiveData<List<CollectionEntity>> getCollectionsForMember(String userId);

    @Query("SELECT * FROM collections WHERE created_by = :userId ORDER BY created_at DESC")
    LiveData<List<CollectionEntity>> getCollectionsOwnedByUser(String userId);

    @Query("SELECT * FROM collections WHERE collection_id = :collectionId")
    LiveData<CollectionEntity> getCollectionById(String collectionId);

    @Query("SELECT * FROM collections WHERE collection_id = :collectionId")
    CollectionEntity getCollectionByIdSync(String collectionId);

    @Query("SELECT * FROM collections WHERE is_public = 1 ORDER BY created_at DESC")
    LiveData<List<CollectionEntity>> getPublicCollections();

    @Query("SELECT * FROM collections WHERE created_by = :userId AND sync_status = :status")
    List<CollectionEntity> getCollectionsBySyncStatus(String userId, String status);

    @Query("UPDATE collections SET sync_status = :status, updated_at = :updatedAt WHERE collection_id = :collectionId")
    void updateSyncStatus(String collectionId, String status, long updatedAt);

    @Query("SELECT COUNT(*) FROM collections WHERE created_by = :userId")
    LiveData<Integer> getCollectionCount(String userId);

    @Query("DELETE FROM collections WHERE created_by = :userId")
    void deleteAllByUser(String userId);

    @Transaction
    @Query("SELECT * FROM collections WHERE collection_id = :collectionId")
    LiveData<CollectionWithPapers> getCollectionWithPapers(String collectionId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void addPaperToCollection(CollectionPaperCrossRef crossRef);

    @Transaction
    @Query("SELECT * FROM collections WHERE member_ids LIKE '%' || :userId || '%' ORDER BY created_at DESC")
    LiveData<List<CollectionWithPapers>> getCollectionsWithPapersForMember(String userId);

    @Query("UPDATE collections SET member_ids = :memberIds WHERE collection_id = :collectionId")
    void updateMemberIds(String collectionId, List<String> memberIds);

    @Query("SELECT member_ids FROM collections WHERE collection_id = :collectionId")
    List<String> getMemberIdsSync(String collectionId);
}