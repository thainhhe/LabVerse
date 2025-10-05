package com.example.labverse.database.dao;
import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.labverse.database.entities.PaperTagEntity;
import java.util.List;
@Dao
public interface PaperTagDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(PaperTagEntity paperTag);

    @Delete
    void delete(PaperTagEntity paperTag);

    @Query("SELECT * FROM paper_tags WHERE paper_id = :paperId")
    LiveData<List<PaperTagEntity>> getTagsByPaper(String paperId);

    @Query("SELECT * FROM paper_tags WHERE tag_id = :tagId")
    LiveData<List<PaperTagEntity>> getPapersByTag(String tagId);

    @Query("DELETE FROM paper_tags WHERE paper_id = :paperId")
    void deleteByPaper(String paperId);

    @Query("DELETE FROM paper_tags WHERE paper_id = :paperId AND tag_id = :tagId")
    void removeTagFromPaper(String paperId, String tagId);
}