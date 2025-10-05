package com.example.labverse.database.dao;
import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.labverse.database.entities.TagEntity;
import java.util.List;
@Dao
public interface TagDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(TagEntity tag);

    @Update
    void update(TagEntity tag);

    @Delete
    void delete(TagEntity tag);

    @Query("SELECT * FROM tags ORDER BY name ASC")
    LiveData<List<TagEntity>> getAllTags();

    @Query("SELECT * FROM tags WHERE tag_id = :tagId")
    LiveData<TagEntity> getTagById(String tagId);

    @Query("SELECT * FROM tags WHERE name = :name LIMIT 1")
    TagEntity getTagByName(String name);

    @Query("SELECT * FROM tags WHERE name LIKE :query ORDER BY name ASC")
    LiveData<List<TagEntity>> searchTags(String query);
}
