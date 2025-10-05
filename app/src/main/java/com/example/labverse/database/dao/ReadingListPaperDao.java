package com.example.labverse.database.dao;
import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.labverse.database.entities.ReadingListPaperEntity;
import java.util.List;
@Dao
public interface ReadingListPaperDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(ReadingListPaperEntity readingListPaper);

    @Delete
    void delete(ReadingListPaperEntity readingListPaper);

    @Query("SELECT * FROM reading_list_papers WHERE reading_list_id = :readingListId ORDER BY order_index ASC")
    LiveData<List<ReadingListPaperEntity>> getPapersByReadingList(String readingListId);

    @Query("SELECT * FROM reading_list_papers WHERE paper_id = :paperId")
    LiveData<List<ReadingListPaperEntity>> getReadingListsByPaper(String paperId);

    @Query("UPDATE reading_list_papers SET order_index = :orderIndex WHERE reading_list_paper_id = :id")
    void updateOrderIndex(String id, int orderIndex);

    @Query("SELECT COUNT(*) FROM reading_list_papers WHERE reading_list_id = :readingListId")
    LiveData<Integer> getPaperCount(String readingListId);

    @Query("DELETE FROM reading_list_papers WHERE reading_list_id = :readingListId")
    void deleteByReadingList(String readingListId);
}
