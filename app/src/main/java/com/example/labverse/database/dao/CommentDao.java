package com.example.labverse.database.dao;
import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.labverse.database.entities.CommentEntity;
import java.util.List;
@Dao
public interface CommentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(CommentEntity comment);

    @Update
    void update(CommentEntity comment);

    @Delete
    void delete(CommentEntity comment);

    @Query("SELECT * FROM comments WHERE paper_id = :paperId ORDER BY created_at DESC")
    LiveData<List<CommentEntity>> getCommentsByPaper(String paperId);

    @Query("SELECT * FROM comments WHERE collection_id = :collectionId ORDER BY created_at DESC")
    LiveData<List<CommentEntity>> getCommentsByCollection(String collectionId);

    @Query("SELECT * FROM comments WHERE parent_comment_id = :parentId ORDER BY created_at ASC")
    LiveData<List<CommentEntity>> getReplies(String parentId);

    @Query("SELECT * FROM comments WHERE user_id = :userId ORDER BY created_at DESC")
    LiveData<List<CommentEntity>> getCommentsByUser(String userId);

    @Query("SELECT COUNT(*) FROM comments WHERE paper_id = :paperId")
    LiveData<Integer> getCommentCount(String paperId);
}
