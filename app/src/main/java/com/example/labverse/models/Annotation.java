package com.example.labverse.models;

import java.io.Serializable;

/**
 * Runtime model for PDF annotations
 */
public class Annotation implements Serializable {
    
    public static final String TYPE_HIGHLIGHT = "highlight";
    public static final String TYPE_NOTE = "note";
    public static final String TYPE_DRAWING = "drawing";
    public static final String TYPE_UNDERLINE = "underline";
    
    public static final String COLOR_YELLOW = "#FFFF00";
    public static final String COLOR_GREEN = "#00FF00";
    public static final String COLOR_BLUE = "#00BFFF";
    public static final String COLOR_RED = "#FF6B6B";
    public static final String COLOR_PURPLE = "#BA55D3";
    
    private String annotationId;
    private String paperId;
    private String userId;
    private String type;
    private int pageNumber;
    private String content;
    private String color;
    private float startX;
    private float startY;
    private float endX;
    private float endY;
    private long createdAt;
    private long updatedAt;
    private boolean synced;
    
    // Constructor
    public Annotation() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
        this.synced = false;
    }
    
    public Annotation(String annotationId, String paperId, String userId, String type, int pageNumber) {
        this.annotationId = annotationId;
        this.paperId = paperId;
        this.userId = userId;
        this.type = type;
        this.pageNumber = pageNumber;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
        this.synced = false;
    }
    
    // Getters and Setters
    public String getAnnotationId() {
        return annotationId;
    }
    
    public void setAnnotationId(String annotationId) {
        this.annotationId = annotationId;
    }
    
    public String getPaperId() {
        return paperId;
    }
    
    public void setPaperId(String paperId) {
        this.paperId = paperId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public int getPageNumber() {
        return pageNumber;
    }
    
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public float getStartX() {
        return startX;
    }
    
    public void setStartX(float startX) {
        this.startX = startX;
    }
    
    public float getStartY() {
        return startY;
    }
    
    public void setStartY(float startY) {
        this.startY = startY;
    }
    
    public float getEndX() {
        return endX;
    }
    
    public void setEndX(float endX) {
        this.endX = endX;
    }
    
    public float getEndY() {
        return endY;
    }
    
    public void setEndY(float endY) {
        this.endY = endY;
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
    
    public long getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public boolean isSynced() {
        return synced;
    }
    
    public void setSynced(boolean synced) {
        this.synced = synced;
    }
    
    /**
     * Get position data as JSON string for storage
     */
    public String getPositionDataJson() {
        return String.format("{\"startX\":%.2f,\"startY\":%.2f,\"endX\":%.2f,\"endY\":%.2f}", 
            startX, startY, endX, endY);
    }
    
    /**
     * Parse position data from JSON string
     */
    public void setPositionDataFromJson(String json) {
        if (json != null && !json.isEmpty()) {
            try {
                json = json.replace("{", "").replace("}", "").replace("\"", "");
                String[] pairs = json.split(",");
                for (String pair : pairs) {
                    String[] keyValue = pair.split(":");
                    if (keyValue.length == 2) {
                        String key = keyValue[0].trim();
                        float value = Float.parseFloat(keyValue[1].trim());
                        switch (key) {
                            case "startX":
                                this.startX = value;
                                break;
                            case "startY":
                                this.startY = value;
                                break;
                            case "endX":
                                this.endX = value;
                                break;
                            case "endY":
                                this.endY = value;
                                break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public String toString() {
        return "Annotation{" +
                "annotationId='" + annotationId + '\'' +
                ", type='" + type + '\'' +
                ", pageNumber=" + pageNumber +
                ", content='" + content + '\'' +
                '}';
    }
}

