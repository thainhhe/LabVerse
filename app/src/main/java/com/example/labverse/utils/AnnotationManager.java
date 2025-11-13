package com.example.labverse.utils;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.labverse.database.LabVerseDatabase;
import com.example.labverse.database.entities.AnnotationEntity;
import com.example.labverse.firebase.models.FirebaseAnnotation;
import com.example.labverse.models.Annotation;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;

/**
 * Manager class for handling annotation operations
 */
public class AnnotationManager {
    
    private static final String TAG = "AnnotationManager";
    private static final String COLLECTION_ANNOTATIONS = "annotations";
    
    private final Context context;
    private final LabVerseDatabase database;
    private final FirebaseFirestore firestore;
    
    public AnnotationManager(Context context) {
        this.context = context;
        this.database = LabVerseDatabase.getDatabase(context);
        this.firestore = FirebaseFirestore.getInstance();
    }
    
    /**
     * Save annotation to local database and sync to Firebase
     */
    public void saveAnnotation(Annotation annotation, SaveCallback callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Generate ID if not exists
                if (annotation.getAnnotationId() == null || annotation.getAnnotationId().isEmpty()) {
                    annotation.setAnnotationId(UUID.randomUUID().toString());
                }
                
                // Convert to entity
                AnnotationEntity entity = toEntity(annotation);
                
                // Save to local database
                database.annotationDao().insert(entity);
                
                // Sync to Firebase
                syncToFirebase(annotation, new SyncCallback() {
                    @Override
                    public void onSuccess() {
                        // Update sync status
                        database.annotationDao().updateSyncStatus(
                            annotation.getAnnotationId(), 
                            "synced", 
                            System.currentTimeMillis()
                        );
                        callback.onSuccess(annotation.getAnnotationId());
                    }
                    
                    @Override
                    public void onFailure(String error) {
                        Log.w(TAG, "Failed to sync annotation: " + error);
                        // Still return success as it's saved locally
                        callback.onSuccess(annotation.getAnnotationId());
                    }
                });
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to save annotation", e);
                callback.onFailure(e.getMessage());
            }
        });
    }
    
    /**
     * Update existing annotation
     */
    public void updateAnnotation(Annotation annotation, UpdateCallback callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                annotation.setUpdatedAt(System.currentTimeMillis());
                
                AnnotationEntity entity = toEntity(annotation);
                database.annotationDao().update(entity);
                
                // Sync to Firebase
                syncToFirebase(annotation, new SyncCallback() {
                    @Override
                    public void onSuccess() {
                        database.annotationDao().updateSyncStatus(
                            annotation.getAnnotationId(),
                            "synced",
                            System.currentTimeMillis()
                        );
                        callback.onSuccess();
                    }
                    
                    @Override
                    public void onFailure(String error) {
                        Log.w(TAG, "Failed to sync annotation update: " + error);
                        callback.onSuccess(); // Local update succeeded
                    }
                });
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to update annotation", e);
                callback.onFailure(e.getMessage());
            }
        });
    }
    
    /**
     * Delete annotation
     */
    public void deleteAnnotation(String annotationId, DeleteCallback callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Delete from Firebase
                firestore.collection(COLLECTION_ANNOTATIONS)
                    .document(annotationId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Annotation deleted from Firebase");
                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Failed to delete from Firebase: " + e.getMessage());
                    });
                
                // Delete from local database
                AnnotationEntity entity = new AnnotationEntity(annotationId, "", "", "");
                database.annotationDao().delete(entity);
                
                callback.onSuccess();
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to delete annotation", e);
                callback.onFailure(e.getMessage());
            }
        });
    }
    
    /**
     * Get annotations for a specific paper and page
     */
    public LiveData<List<AnnotationEntity>> getAnnotationsForPage(String paperId, int pageNumber) {
        return database.annotationDao().getAnnotationsByPage(paperId, pageNumber);
    }
    
    /**
     * Get all annotations for a paper
     */
    public LiveData<List<AnnotationEntity>> getAnnotationsForPaper(String paperId) {
        return database.annotationDao().getAnnotationsByPaper(paperId);
    }
    
    /**
     * Sync annotation to Firebase
     */
    private void syncToFirebase(Annotation annotation, SyncCallback callback) {
        try {
            FirebaseAnnotation firebaseAnnotation = toFirebaseModel(annotation);
            Map<String, Object> data = firebaseAnnotation.toMap();
            
            firestore.collection(COLLECTION_ANNOTATIONS)
                .document(annotation.getAnnotationId())
                .set(data)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Annotation synced to Firebase");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to sync to Firebase", e);
                    callback.onFailure(e.getMessage());
                });
                
        } catch (Exception e) {
            Log.e(TAG, "Error preparing Firebase sync", e);
            callback.onFailure(e.getMessage());
        }
    }
    
    /**
     * Convert Annotation model to AnnotationEntity
     */
    private AnnotationEntity toEntity(Annotation annotation) {
        AnnotationEntity entity = new AnnotationEntity(
            annotation.getAnnotationId(),
            annotation.getPaperId(),
            annotation.getUserId(),
            annotation.getType()
        );
        
        entity.setPageNumber(annotation.getPageNumber());
        entity.setContent(annotation.getContent());
        entity.setColor(annotation.getColor());
        entity.setPositionData(annotation.getPositionDataJson());
        entity.setCreatedAt(annotation.getCreatedAt());
        entity.setUpdatedAt(annotation.getUpdatedAt());
        entity.setSyncStatus(annotation.isSynced() ? "synced" : "pending");
        
        return entity;
    }
    
    /**
     * Convert Annotation model to FirebaseAnnotation
     */
    private FirebaseAnnotation toFirebaseModel(Annotation annotation) {
        FirebaseAnnotation firebase = new FirebaseAnnotation(
            annotation.getAnnotationId(),
            annotation.getPaperId(),
            annotation.getUserId(),
            annotation.getType()
        );
        
        firebase.setPageNumber(annotation.getPageNumber());
        firebase.setContent(annotation.getContent());
        firebase.setColor(annotation.getColor());
        
        // Convert position data to Map
        Map<String, Object> positionData = new HashMap<>();
        positionData.put("startX", annotation.getStartX());
        positionData.put("startY", annotation.getStartY());
        positionData.put("endX", annotation.getEndX());
        positionData.put("endY", annotation.getEndY());
        firebase.setPositionData(positionData);
        
        return firebase;
    }
    
    /**
     * Convert AnnotationEntity to Annotation model
     */
    public static Annotation fromEntity(AnnotationEntity entity) {
        Annotation annotation = new Annotation(
            entity.getAnnotationId(),
            entity.getPaperId(),
            entity.getUserId(),
            entity.getType(),
            entity.getPageNumber()
        );
        
        annotation.setContent(entity.getContent());
        annotation.setColor(entity.getColor());
        annotation.setPositionDataFromJson(entity.getPositionData());
        annotation.setCreatedAt(entity.getCreatedAt());
        annotation.setUpdatedAt(entity.getUpdatedAt());
        annotation.setSynced("synced".equals(entity.getSyncStatus()));
        
        return annotation;
    }
    
    /**
     * Convert list of entities to list of models
     */
    public static List<Annotation> fromEntityList(List<AnnotationEntity> entities) {
        List<Annotation> annotations = new ArrayList<>();
        for (AnnotationEntity entity : entities) {
            annotations.add(fromEntity(entity));
        }
        return annotations;
    }
    
    // Callback interfaces
    public interface SaveCallback {
        void onSuccess(String annotationId);
        void onFailure(String error);
    }
    
    public interface UpdateCallback {
        void onSuccess();
        void onFailure(String error);
    }
    
    public interface DeleteCallback {
        void onSuccess();
        void onFailure(String error);
    }
    
    private interface SyncCallback {
        void onSuccess();
        void onFailure(String error);
    }
}

