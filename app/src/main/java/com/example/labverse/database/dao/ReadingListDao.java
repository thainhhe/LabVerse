package com.example.labverse.database.dao;
import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.labverse.database.entities.ReadingListEntity;
import java.util.List;
@Dao
public interface ReadingListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(ReadingListEntity readingList);

    @Update
    void update(ReadingListEntity readingList);

    @Delete
    void delete(ReadingListEntity readingList);

    @Query("SELECT * FROM reading_lists WHERE user_id = :userId ORDER BY created_at DESC")
    LiveData<List<ReadingListEntity>> getReadingListsByUser(String userId);

    @Query("SELECT * FROM reading_lists WHERE reading_list_id = :readingListId")
    LiveData<ReadingListEntity> getReadingListById(String readingListId);

    @Query("SELECT * FROM reading_lists WHERE user_id = :userId AND is_shared = 1")
    LiveData<List<ReadingListEntity>> getSharedReadingLists(String userId);

    @Query("SELECT COUNT(*) FROM reading_lists WHERE user_id = :userId")
    LiveData<Integer> getReadingListCount(String userId);
}
