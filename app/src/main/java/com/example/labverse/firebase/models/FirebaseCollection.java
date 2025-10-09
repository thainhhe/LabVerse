package com.example.labverse.firebase.models;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;
import java.util.HashMap;
import java.util.Map;
public class FirebaseCollection {
    @DocumentId
    private String collectionId;
    private String createdBy;
    private String name;
    private String description;
    private boolean isPublic;
    @ServerTimestamp
    private Timestamp createdAt;
    @ServerTimestamp
    private Timestamp updatedAt;

    public FirebaseCollection() {}

    public FirebaseCollection(String collectionId, String createdBy, String name) {
        this.collectionId = collectionId;
        this.createdBy = createdBy;
        this.name = name;
        this.isPublic = false;
    }

    // Getters and Setters
    public String getCollectionId() { return collectionId; }
    public void setCollectionId(String collectionId) { this.collectionId = collectionId; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean isPublic) { this.isPublic = isPublic; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("collectionId", collectionId);
        map.put("createdBy", createdBy);
        map.put("name", name);
        map.put("description", description);
        map.put("isPublic", isPublic);
        return map;
    }
}

