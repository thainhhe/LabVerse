package com.example.labverse.firebase.models;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;
import java.util.HashMap;
import java.util.Map;
public class FirebaseUser {
    @DocumentId
    private String userId;
    private String email;
    private String fullName;
    private String affiliation;
    private String role;
    @ServerTimestamp
    private Timestamp createdAt;
    @ServerTimestamp
    private Timestamp updatedAt;
    private boolean isActive;

    public FirebaseUser() {}

    public FirebaseUser(String userId, String email, String fullName, String role) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.isActive = true;
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getAffiliation() { return affiliation; }
    public void setAffiliation(String affiliation) { this.affiliation = affiliation; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("email", email);
        map.put("fullName", fullName);
        map.put("affiliation", affiliation);
        map.put("role", role);
        map.put("isActive", isActive);
        return map;
    }
}
