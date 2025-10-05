package com.example.labverse.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.labverse.database.entities.UserEntity;
import java.util.List;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserEntity user);

    @Update
    void update(UserEntity user);

    @Delete
    void delete(UserEntity user);

    @Query("SELECT * FROM users WHERE user_id = :userId")
    LiveData<UserEntity> getUserById(String userId);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    UserEntity getUserByEmail(String email);

    @Query("SELECT * FROM users WHERE email = :email AND password_hash = :passwordHash LIMIT 1")
    UserEntity authenticate(String email, String passwordHash);

    @Query("SELECT * FROM users WHERE firebase_uid = :firebaseUid LIMIT 1")
    UserEntity getUserByFirebaseUid(String firebaseUid);

    @Query("SELECT * FROM users WHERE sync_status = :status")
    List<UserEntity> getUsersBySyncStatus(String status);

    @Query("UPDATE users SET sync_status = :status, last_sync = :lastSync WHERE user_id = :userId")
    void updateSyncStatus(String userId, String status, long lastSync);

    @Query("UPDATE users SET last_login = :lastLogin WHERE user_id = :userId")
    void updateLastLogin(String userId, long lastLogin);
}
