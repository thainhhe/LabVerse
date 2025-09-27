package com.example.labverse.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.card.MaterialCardView;
import com.example.labverse.R;

public class ImportPaperActivity extends AppCompatActivity {

    private static final int PICK_PDF_REQUEST = 1001;
    private static final int PICK_BIBTEX_REQUEST = 1002;

    private MaterialCardView cardUploadPdf, cardImportBibtex, cardManualEntry;
    private Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_paper);

        setupToolbar();
        initViews();
        setupClickListeners();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Import Paper");
        }
    }

    private void initViews() {
        cardUploadPdf = findViewById(R.id.card_upload_pdf);
        cardImportBibtex = findViewById(R.id.card_import_bibtex);
        cardManualEntry = findViewById(R.id.card_manual_entry);
        btnCancel = findViewById(R.id.btn_cancel);
    }

    private void setupClickListeners() {
        cardUploadPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPdfPicker();
            }
        });

        cardImportBibtex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBibtexPicker();
            }
        });

        cardManualEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openManualEntry();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void openPdfPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), PICK_PDF_REQUEST);
    }

    private void openBibtexPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select BibTeX file"), PICK_BIBTEX_REQUEST);
    }

    private void openManualEntry() {
        Intent intent = new Intent(this, ManualEntryActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            Uri selectedFile = data.getData();

            switch (requestCode) {
                case PICK_PDF_REQUEST:
                    handlePdfSelection(selectedFile);
                    break;
                case PICK_BIBTEX_REQUEST:
                    handleBibtexSelection(selectedFile);
                    break;
            }
        }
    }

    private void handlePdfSelection(Uri pdfUri) {
        // TODO: Process selected PDF file
        Toast.makeText(this, "PDF selected: " + pdfUri.getLastPathSegment(), Toast.LENGTH_SHORT).show();

        // Navigate to PDF processing activity
        Intent intent = new Intent(this, PdfProcessingActivity.class);
        intent.setData(pdfUri);
        startActivity(intent);
    }

    private void handleBibtexSelection(Uri bibtexUri) {
        // TODO: Process selected BibTeX file
        Toast.makeText(this, "BibTeX file selected: " + bibtexUri.getLastPathSegment(), Toast.LENGTH_SHORT).show();

        // Navigate to BibTeX processing activity
        Intent intent = new Intent(this, BibtexProcessingActivity.class);
        intent.setData(bibtexUri);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}