package com.example.labverse.utils;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PdfStorageManager {
    
    private static final String TAG = "PdfStorageManager";
    private static final String PDF_STORAGE_PATH = "papers/";
    
    private final FirebaseStorage storage;
    private final StorageReference storageRef;
    
    public PdfStorageManager() {
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }
    
    /**
     * Upload PDF to Firebase Storage
     * @param pdfUri Local URI of the PDF file
     * @param userId User ID for organization
     * @param paperId Paper ID for organization
     * @param callback Callback for upload progress and result
     */
    public void uploadPdf(Uri pdfUri, String userId, String paperId, UploadCallback callback) {
        // Create a reference to the file location in Firebase Storage
        String fileName = userId + "/" + paperId + ".pdf";
        StorageReference pdfRef = storageRef.child(PDF_STORAGE_PATH + fileName);
        
        // Start upload
        UploadTask uploadTask = pdfRef.putFile(pdfUri);
        
        // Monitor upload progress
        uploadTask.addOnProgressListener(taskSnapshot -> {
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            callback.onProgress((int) progress);
            Log.d(TAG, "Upload progress: " + progress + "%");
        }).addOnSuccessListener(taskSnapshot -> {
            // Upload successful, get download URL
            pdfRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                Log.d(TAG, "Upload successful. Download URL: " + downloadUri.toString());
                callback.onSuccess(downloadUri.toString());
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Failed to get download URL", e);
                callback.onFailure("Failed to get download URL: " + e.getMessage());
            });
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Upload failed", e);
            callback.onFailure(e.getMessage());
        });
    }
    
    /**
     * Download PDF from Firebase Storage
     * @param pdfUrl Firebase Storage download URL
     * @param callback Callback for download result
     */
    public void downloadPdf(String pdfUrl, DownloadCallback callback) {
        StorageReference pdfRef = storage.getReferenceFromUrl(pdfUrl);
        
        // Download to a temporary file
        pdfRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Log.d(TAG, "Download URL retrieved: " + uri.toString());
            callback.onSuccess(uri);
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to download PDF", e);
            callback.onFailure("Failed to download PDF: " + e.getMessage());
        });
    }
    
    /**
     * Delete PDF from Firebase Storage
     * @param pdfUrl Firebase Storage download URL
     * @param callback Callback for deletion result
     */
    public void deletePdf(String pdfUrl, DeleteCallback callback) {
        try {
            StorageReference pdfRef = storage.getReferenceFromUrl(pdfUrl);
            
            pdfRef.delete().addOnSuccessListener(aVoid -> {
                Log.d(TAG, "PDF deleted successfully");
                callback.onSuccess();
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Failed to delete PDF", e);
                callback.onFailure("Failed to delete PDF: " + e.getMessage());
            });
        } catch (Exception e) {
            Log.e(TAG, "Invalid PDF URL", e);
            callback.onFailure("Invalid PDF URL: " + e.getMessage());
        }
    }
    
    // Callback interfaces
    public interface UploadCallback {
        void onProgress(int progress);
        void onSuccess(String downloadUrl);
        void onFailure(String error);
    }
    
    public interface DownloadCallback {
        void onSuccess(Uri downloadUri);
        void onFailure(String error);
    }
    
    public interface DeleteCallback {
        void onSuccess();
        void onFailure(String error);
    }
}

