package com.example.labverse.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.labverse.MainActivity;
import com.example.labverse.R;
import com.example.labverse.auth.FirebaseAuthManager;
import com.example.labverse.database.LabVerseDatabase;
import com.example.labverse.database.dao.CitationDao;
import com.example.labverse.database.dao.PaperDao;
import com.example.labverse.database.entities.CitationEntity;
import com.example.labverse.database.entities.PaperEntity;
import com.example.labverse.firebase.FirebaseSyncManager;
import com.example.labverse.utils.PDFMetadataExtractor;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public class PdfProcessingActivity extends AppCompatActivity {

    private static final String TAG = "PdfProcessingActivity";

    private Toolbar toolbar;
    private TextView tvStatus;
    private ProgressBar progressBar;
    private Button btnContinue, btnCancel;

    private Uri pdfUri;
    private PaperEntity paperEntity;
    private String userId;
    private PaperDao paperDao;
    private CitationDao citationDao;
    private FirebaseSyncManager firebaseSyncManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_processing);

        pdfUri = getIntent().getData();
        if (pdfUri == null) {
            Toast.makeText(this, "No PDF file selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        setupClickListeners();

        FirebaseAuthManager authManager = new FirebaseAuthManager(this);
        FirebaseUser user = authManager.getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        paperDao = LabVerseDatabase.getDatabase(this).paperDao();
        citationDao = LabVerseDatabase.getDatabase(this).citationDao();
        firebaseSyncManager = new FirebaseSyncManager(this);

        processPDF();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvStatus = findViewById(R.id.tv_status);
        progressBar = findViewById(R.id.progress_bar);
        btnContinue = findViewById(R.id.btn_continue);
        btnCancel = findViewById(R.id.btn_cancel);

        btnContinue.setEnabled(false);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Processing PDF");
        }
    }

    private void setupClickListeners() {
        btnContinue.setOnClickListener(v -> {
            if (paperEntity != null) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnCancel.setOnClickListener(v -> finish());
    }

    private void processPDF() {
        updateStatus("Extracting metadata from PDF...");
        progressBar.setVisibility(View.VISIBLE);

        // Generate paper ID
        String paperId = UUID.randomUUID().toString();

        // Extract metadata
        LabVerseDatabase.databaseWriteExecutor.execute(() -> {
            try {
                // Extract metadata from PDF
                paperEntity = PDFMetadataExtractor.extractMetadata(this, pdfUri, userId, paperId);

                // If title is still "Untitled Paper", use filename
                if (paperEntity.title == null || paperEntity.title.equals("Untitled Paper")) {
                    String fileName = getFileName(pdfUri);
                    if (fileName != null && !fileName.isEmpty()) {
                        paperEntity.title = fileName.replace(".pdf", "").replace(".PDF", "");
                    }
                }

                // Copy PDF to internal storage
                String pdfPath = copyPdfToInternalStorage(pdfUri, paperId);
                paperEntity.pdfPath = pdfPath;

                // Set default values
                if (paperEntity.status == null) {
                    paperEntity.status = "unread";
                }
                if (paperEntity.priority == null) {
                    paperEntity.priority = "medium";
                }
                paperEntity.syncStatus = "pending";

                // Save paper to database
                paperDao.insert(paperEntity);

                // Extract citations
                runOnUiThread(() -> {
                    updateStatus("Extracting citations...");
                });

                List<CitationEntity> citations = PDFMetadataExtractor.extractCitations(this, pdfUri, paperId);
                
                if (citations != null && !citations.isEmpty()) {
                    citationDao.insertAll(citations);
                    Log.d(TAG, "Saved " + citations.size() + " citations");
                }

                // Sync to Firebase
                runOnUiThread(() -> {
                    updateStatus("Syncing to cloud...");
                });

                firebaseSyncManager.syncPaperToFirebase(paperEntity);

                runOnUiThread(() -> {
                    updateStatus("Processing complete!");
                    progressBar.setVisibility(View.GONE);
                    btnContinue.setEnabled(true);
                    Toast.makeText(this, "PDF processed successfully", Toast.LENGTH_SHORT).show();
                });

            } catch (Exception e) {
                Log.e(TAG, "Error processing PDF: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    updateStatus("Error: " + e.getMessage());
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Error processing PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private String copyPdfToInternalStorage(Uri sourceUri, String paperId) {
        try {
            File internalDir = new File(getFilesDir(), "papers");
            if (!internalDir.exists()) {
                internalDir.mkdirs();
            }

            File destFile = new File(internalDir, paperId + ".pdf");
            
            InputStream inputStream = getContentResolver().openInputStream(sourceUri);
            FileOutputStream outputStream = new FileOutputStream(destFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            inputStream.close();
            outputStream.close();

            return destFile.getAbsolutePath();
        } catch (Exception e) {
            Log.e(TAG, "Error copying PDF: " + e.getMessage(), e);
            return null;
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void updateStatus(String status) {
        tvStatus.setText(status);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
