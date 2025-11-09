package com.example.labverse.firebase;
import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.example.labverse.database.LabVerseDatabase;
import com.example.labverse.database.entities.*;
import com.example.labverse.firebase.models.*;

import java.util.List;
import java.util.Map;
public class FirebaseSyncManager {
    private static final String TAG = "FirebaseSyncManager";
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private LabVerseDatabase database;

    // Firestore Collections
    private static final String USERS_COLLECTION = "users";
    private static final String PAPERS_COLLECTION = "papers";
    private static final String COLLECTIONS_COLLECTION = "collections";
    private static final String ANNOTATIONS_COLLECTION = "annotations";

    public FirebaseSyncManager(Context context) {
        this.firestore = FirebaseFirestore.getInstance();
        this.storage = FirebaseStorage.getInstance();
        this.database = LabVerseDatabase.getDatabase(context);
    }

    // ==================== SYNC PAPERS ====================
    public void syncPaperToFirebase(PaperEntity paper) {
        FirebasePaper firebasePaper = convertToFirebasePaper(paper);

        // Corrected path: users/{userId}/papers/{paperId}
        firestore.collection(USERS_COLLECTION).document(paper.userId)
                .collection(PAPERS_COLLECTION)
                .document(paper.paperId)
                .set(firebasePaper.toMap())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Paper synced to Firebase: " + paper.paperId);
                    updateLocalSyncStatus(paper.paperId, "synced");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to sync paper: " + e.getMessage());
                    updateLocalSyncStatus(paper.paperId, "failed");
                });
    }

    public void syncPapersFromFirebase(String userId) {
        // Corrected path: users/{userId}/papers
        firestore.collection(USERS_COLLECTION).document(userId)
                .collection(PAPERS_COLLECTION)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        LabVerseDatabase.databaseWriteExecutor.execute(() -> {
                            for (DocumentSnapshot document : task.getResult()) {
                                FirebasePaper firebasePaper = document.toObject(FirebasePaper.class);
                                if (firebasePaper != null && firebasePaper.getPaperId() != null) {
                                    // Check if paper already exists before inserting
                                    PaperEntity existingPaper = database.paperDao().getPaperByIdSync(firebasePaper.getPaperId());
                                    if (existingPaper == null) {
                                        PaperEntity localPaper = convertToPaperEntity(firebasePaper, userId);
                                        database.paperDao().insert(localPaper);
                                        Log.d(TAG, "New paper from Firebase inserted: " + localPaper.paperId);
                                    } else {
                                        Log.d(TAG, "Paper from Firebase already exists, skipping: " + existingPaper.paperId);
                                    }
                                }
                            }
                        });
                        Log.d(TAG, "Finished syncing papers from Firebase.");
                    }
                });
    }


    // ==================== SYNC COLLECTIONS ====================
    public void syncCollectionToFirebase(CollectionEntity collection) {
        FirebaseCollection firebaseCollection = convertToFirebaseCollection(collection);

        firestore.collection(COLLECTIONS_COLLECTION)
                .document(collection.getCollectionId())
                .set(firebaseCollection.toMap())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Collection synced to Firebase: " + collection.getCollectionId());
                    updateCollectionSyncStatus(collection.getCollectionId(), "synced");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to sync collection: " + e.getMessage());
                });
    }

    public void syncCollectionsFromFirebase(String userId) {
        firestore.collection(COLLECTIONS_COLLECTION)
                .whereArrayContains("memberIds", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (DocumentSnapshot document : task.getResult()) {
                            FirebaseCollection firebaseCollection = document.toObject(FirebaseCollection.class);
                            if (firebaseCollection != null) {
                                CollectionEntity localCollection = convertToCollectionEntity(firebaseCollection);
                                LabVerseDatabase.databaseWriteExecutor.execute(() -> {
                                    database.collectionDao().insert(localCollection);
                                });
                            }
                        }
                        Log.d(TAG, "Collections synced from Firebase");
                    }
                });
    }

    public void findUserByEmail(String email, OnCompleteListener<QuerySnapshot> listener) {
        firestore.collection("users")
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .addOnCompleteListener(listener);
    }

    public void addMemberToCollection(String collectionId, String newMemberUid, OnCompleteListener<Void> listener) {
        firestore.collection(COLLECTIONS_COLLECTION).document(collectionId)
                .update("memberIds", com.google.firebase.firestore.FieldValue.arrayUnion(newMemberUid))
                .addOnCompleteListener(listener);
    }

    // ==================== SYNC ANNOTATIONS ====================
    public void syncAnnotationToFirebase(AnnotationEntity annotation) {
        FirebaseAnnotation firebaseAnnotation = convertToFirebaseAnnotation(annotation);

        firestore.collection(ANNOTATIONS_COLLECTION)
                .document(annotation.getAnnotationId())
                .set(firebaseAnnotation.toMap())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Annotation synced to Firebase: " + annotation.getAnnotationId());
                    updateAnnotationSyncStatus(annotation.getAnnotationId(), "synced");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to sync annotation: " + e.getMessage());
                });
    }

    public void syncAnnotationsFromFirebase(String paperId) {
        firestore.collection(ANNOTATIONS_COLLECTION)
                .whereEqualTo("paperId", paperId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (DocumentSnapshot document : task.getResult()) {
                            FirebaseAnnotation firebaseAnnotation = document.toObject(FirebaseAnnotation.class);
                            if (firebaseAnnotation != null) {
                                AnnotationEntity localAnnotation = convertToAnnotationEntity(firebaseAnnotation);
                                LabVerseDatabase.databaseWriteExecutor.execute(() -> {
                                    database.annotationDao().insert(localAnnotation);
                                });
                            }
                        }
                        Log.d(TAG, "Annotations synced from Firebase");
                    }
                });
    }

    // ==================== SYNC ALL PENDING ====================
    public void syncAllPendingData(String userId) {
        LabVerseDatabase.databaseWriteExecutor.execute(() -> {
            // Sync pending papers
            List<PaperEntity> pendingPapers = database.paperDao().getPapersBySyncStatus(userId, "pending");
            for (PaperEntity paper : pendingPapers) {
                syncPaperToFirebase(paper);
            }

            // Sync pending collections
            List<CollectionEntity> pendingCollections = database.collectionDao().getCollectionsBySyncStatus(userId, "pending");
            for (CollectionEntity collection : pendingCollections) {
                syncCollectionToFirebase(collection);
            }

            // Sync pending annotations
            List<AnnotationEntity> pendingAnnotations = database.annotationDao().getAnnotationsBySyncStatus(userId, "pending");
            for (AnnotationEntity annotation : pendingAnnotations) {
                syncAnnotationToFirebase(annotation);
            }
        });
    }

    // ==================== CONVERSION METHODS ====================
    private FirebasePaper convertToFirebasePaper(PaperEntity entity) {
        FirebasePaper paper = new FirebasePaper(entity.paperId, entity.userId, entity.title);
        paper.setAuthors(entity.authors);
        paper.setJournal(entity.journal);
        paper.setYear(entity.year);
        paper.setDoi(entity.doi);
        paper.setAbstractText(entity.abstractText);
        paper.setPdfUrl(entity.pdfUrl);
        paper.setStatus(entity.status);
        paper.setPriority(entity.priority);
        paper.setFavorite(entity.isFavorite);
        paper.setCurrentPage(entity.currentPage);
        paper.setTotalPages(entity.totalPages);
        return paper;
    }

    private PaperEntity convertToPaperEntity(FirebasePaper firebasePaper, String userId) {
        PaperEntity entity = new PaperEntity();
        entity.paperId = firebasePaper.getPaperId();
        entity.userId = userId; // Use the provided userId
        entity.title = firebasePaper.getTitle();
        entity.authors = firebasePaper.getAuthors();
        entity.journal = firebasePaper.getJournal();
        entity.year = firebasePaper.getYear();
        entity.doi = firebasePaper.getDoi();
        entity.abstractText = firebasePaper.getAbstractText();
        entity.pdfUrl = firebasePaper.getPdfUrl();
        entity.status = firebasePaper.getStatus();
        entity.priority = firebasePaper.getPriority();
        entity.isFavorite = firebasePaper.isFavorite();
        entity.currentPage = firebasePaper.getCurrentPage();
        entity.totalPages = firebasePaper.getTotalPages();
        entity.syncStatus = "synced";
        entity.lastSync = System.currentTimeMillis();
        if (firebasePaper.getDateAdded() != null) {
            entity.dateAdded = firebasePaper.getDateAdded().toDate().getTime();
        }
        return entity;
    }

    private FirebaseCollection convertToFirebaseCollection(CollectionEntity entity) {
        FirebaseCollection collection = new FirebaseCollection();

        collection.setCollectionId(entity.getCollectionId());
        collection.setCreatedBy(entity.getCreatedBy());
        collection.setName(entity.getName());
        collection.setDescription(entity.getDescription());
        collection.setPublic(entity.isPublic());
        collection.setOwnerId(entity.getOwnerId());
        collection.setMemberIds(entity.getMemberIds());
        return collection;
    }

    private CollectionEntity convertToCollectionEntity(FirebaseCollection fb) {

        CollectionEntity entity = new CollectionEntity(
                fb.getCollectionId(),
                fb.getName(),
                fb.getDescription(),
                fb.getCreatedBy(),
                fb.getOwnerId(),
                fb.getMemberIds(),
                fb.isPublic()
        );

        entity.setSyncStatus("synced");
        if (fb.getCreatedAt() != null) {
            entity.setCreatedAt(fb.getCreatedAt().toDate().getTime());
        }
        if (fb.getUpdatedAt() != null) {
            entity.setUpdatedAt(fb.getUpdatedAt().toDate().getTime());
        }

        return entity;
    }

    public void createNewCollectionOnFirebase(String name, String description, boolean isPublic, com.google.firebase.auth.FirebaseUser user, OnCompleteListener<Void> listener) {
        String newId = firestore.collection(COLLECTIONS_COLLECTION).document().getId();
        String createdByName = (user.getDisplayName() == null || user.getDisplayName().isEmpty()) ? user.getEmail() : user.getDisplayName();

        List<String> initialMembers = new java.util.ArrayList<>();
        initialMembers.add(user.getUid());

        FirebaseCollection collection = new FirebaseCollection();
        collection.setCollectionId(newId);
        collection.setName(name);
        collection.setDescription(description);
        collection.setCreatedBy(createdByName);
        collection.setOwnerId(user.getUid());
        collection.setMemberIds(initialMembers);
        collection.setPublic(isPublic);

        firestore.collection(COLLECTIONS_COLLECTION)
                .document(newId)
                .set(collection.toMap())
                .addOnCompleteListener(listener);
    }

    public void getPapersForCollection(String collectionId, OnCompleteListener<QuerySnapshot> listener) {
        firestore.collection(COLLECTIONS_COLLECTION)
                .document(collectionId)
                .collection("papers")
                .get()
                .addOnCompleteListener(listener);
    }

    public void updatePaperStatusInCollection(String collectionId, String paperId, String newStatus, String newPriority, OnCompleteListener<Void> listener) {
        Map<String, Object> updates = new java.util.HashMap<>();
        if (newStatus != null) {
            updates.put("status", newStatus);
        }
        if (newPriority != null) {
            updates.put("priority", newPriority);
        }

        if (updates.isEmpty()) {
            return;
        }

        updates.put("updatedAt", com.google.firebase.firestore.FieldValue.serverTimestamp());

        firestore.collection(COLLECTIONS_COLLECTION).document(collectionId)
                .collection("papers").document(paperId)
                .update(updates)
                .addOnCompleteListener(listener);
    }

    public void getCollectionsForUser(String userId, OnCompleteListener<QuerySnapshot> listener) {
        firestore.collection(COLLECTIONS_COLLECTION)
                .whereArrayContains("memberIds", userId)
                .get()
                .addOnCompleteListener(listener);
    }

    private FirebaseAnnotation convertToFirebaseAnnotation(AnnotationEntity entity) {
        FirebaseAnnotation annotation = new FirebaseAnnotation(
                entity.getAnnotationId(),
                entity.getPaperId(),
                entity.getUserId(),
                entity.getType()
        );
        annotation.setPageNumber(entity.getPageNumber());
        annotation.setContent(entity.getContent());
        annotation.setColor(entity.getColor());
        // Convert JSON string to Map if needed
        return annotation;
    }

    private AnnotationEntity convertToAnnotationEntity(FirebaseAnnotation firebaseAnnotation) {
        AnnotationEntity entity = new AnnotationEntity(
                firebaseAnnotation.getAnnotationId(),
                firebaseAnnotation.getPaperId(),
                firebaseAnnotation.getUserId(),
                firebaseAnnotation.getType()
        );
        entity.setPageNumber(firebaseAnnotation.getPageNumber());
        entity.setContent(firebaseAnnotation.getContent());
        entity.setColor(firebaseAnnotation.getColor());
        entity.setSyncStatus("synced");
        if (firebaseAnnotation.getCreatedAt() != null) {
            entity.setCreatedAt(firebaseAnnotation.getCreatedAt().toDate().getTime());
        }
        return entity;
    }

    // ==================== UPDATE SYNC STATUS ====================
    private void updateLocalSyncStatus(String paperId, String status) {
        LabVerseDatabase.databaseWriteExecutor.execute(() -> {
            database.paperDao().updateSyncStatus(paperId, status, System.currentTimeMillis());
        });
    }

    private void updateCollectionSyncStatus(String collectionId, String status) {
        LabVerseDatabase.databaseWriteExecutor.execute(() -> {
            database.collectionDao().updateSyncStatus(collectionId, status, System.currentTimeMillis());
        });
    }

    private void updateAnnotationSyncStatus(String annotationId, String status) {
        LabVerseDatabase.databaseWriteExecutor.execute(() -> {
            database.annotationDao().updateSyncStatus(annotationId, status, System.currentTimeMillis());
        });
    }
}
