package com.example.labverse.database.dao;
import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.labverse.database.entities.CollectionMemberEntity;
import java.util.List;
@Dao
public interface CollectionMemberDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(CollectionMemberEntity member);

    @Update
    void update(CollectionMemberEntity member);

    @Delete
    void delete(CollectionMemberEntity member);

    @Query("SELECT * FROM collection_members WHERE collection_id = :collectionId")
    LiveData<List<CollectionMemberEntity>> getMembersByCollection(String collectionId);

    @Query("SELECT * FROM collection_members WHERE user_id = :userId")
    LiveData<List<CollectionMemberEntity>> getCollectionsByUser(String userId);

    @Query("SELECT * FROM collection_members WHERE collection_id = :collectionId AND user_id = :userId")
    CollectionMemberEntity getMember(String collectionId, String userId);

    @Query("UPDATE collection_members SET access_level = :accessLevel WHERE member_id = :memberId")
    void updateAccessLevel(String memberId, String accessLevel);
}
