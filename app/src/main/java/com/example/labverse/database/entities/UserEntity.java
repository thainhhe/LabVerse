package com.example.labverse.database.entities;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "users",
        indices = {@Index(value = "email", unique = true)})
public class UserEntity {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "user_id")
    private String userId;

    @NonNull
    private String email;

    @ColumnInfo(name = "password_hash")
    private String passwordHash;

    @ColumnInfo(name = "full_name")
    private String fullName;

    private String affiliation;
    private String role;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    @ColumnInfo(name = "last_login")
    private long lastLogin;

    @ColumnInfo(name = "is_active")
    private boolean isActive;

    @ColumnInfo(name = "firebase_uid")
    private String firebaseUid;

    @ColumnInfo(name = "sync_status")
    private String syncStatus; // "synced", "pending", "conflict"

    @ColumnInfo(name = "last_sync")
    private long lastSync;

    // Constructor
    public UserEntity(@NonNull String userId, @NonNull String email) {
        this.userId = userId;
        this.email = email;
        this.createdAt = System.currentTimeMillis();
        this.isActive = true;
        this.syncStatus = "pending";
    }

    // Getters and Setters
    @NonNull
    public String getUserId() { return userId; }
    public void setUserId(@NonNull String userId) { this.userId = userId; }

    @NonNull
    public String getEmail() { return email; }
    public void setEmail(@NonNull String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getAffiliation() { return affiliation; }
    public void setAffiliation(String affiliation) { this.affiliation = affiliation; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getLastLogin() { return lastLogin; }
    public void setLastLogin(long lastLogin) { this.lastLogin = lastLogin; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getFirebaseUid() { return firebaseUid; }
    public void setFirebaseUid(String firebaseUid) { this.firebaseUid = firebaseUid; }

    public String getSyncStatus() { return syncStatus; }
    public void setSyncStatus(String syncStatus) { this.syncStatus = syncStatus; }

    public long getLastSync() { return lastSync; }
    public void setLastSync(long lastSync) { this.lastSync = lastSync; }
}
