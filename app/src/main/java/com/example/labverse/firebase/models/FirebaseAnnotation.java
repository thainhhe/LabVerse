package com.example.labverse.firebase.models;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;
import java.util.HashMap;
import java.util.Map;
public class FirebaseAnnotation {
    @DocumentId
    private String annotationId;
    private String paperId;
    private String userId;
    private String type;
    private int pageNumber;
    private String content;
    private String color;
    private Map<String, Object> positionData;
    @ServerTimestamp
    private Timestamp createdAt;
    @ServerTimestamp
    private Timestamp updatedAt;

    public FirebaseAnnotation() {}

    public FirebaseAnnotation(String annotationId, String paperId, String userId, String type) {
        this.annotationId = annotationId;
        this.paperId = paperId;
        this.userId = userId;
        this.type = type;
    }

    // Getters and Setters
    public String getAnnotationId() { return annotationId; }
    public void setAnnotationId(String annotationId) { this.annotationId = annotationId; }

    public String getPaperId() { return paperId; }
    public void setPaperId(String paperId) { this.paperId = paperId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getPageNumber() { return pageNumber; }
    public void setPageNumber(int pageNumber) { this.pageNumber = pageNumber; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public Map<String, Object> getPositionData() { return positionData; }
    public void setPositionData(Map<String, Object> positionData) { this.positionData = positionData; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("annotationId", annotationId);
        map.put("paperId", paperId);
        map.put("userId", userId);
        map.put("type", type);
        map.put("pageNumber", pageNumber);
        map.put("content", content);
        map.put("color", color);
        map.put("positionData", positionData);
        return map;
    }
}
