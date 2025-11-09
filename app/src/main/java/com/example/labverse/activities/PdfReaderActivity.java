package com.example.labverse.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.example.labverse.R;
import com.example.labverse.adapters.MockPdfAdapter;
import com.example.labverse.database.LabVerseDatabase;
import com.example.labverse.database.dao.PaperDao;
import com.example.labverse.database.entities.PaperEntity;
import com.example.labverse.firebase.FirebaseSyncManager;
import com.example.labverse.models.Paper;
import com.example.labverse.utils.PaperMapper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PdfReaderActivity extends AppCompatActivity {

    private static final String TAG = "PdfReaderActivity";
    private static final int DEFAULT_TOTAL_PAGES = 50; // Default number of pages for testing

    private Toolbar toolbar;
    private ViewPager2 viewPager;
    private ProgressBar progressBar;
    private TextView tvPageInfo;
    private TextView tvStatus;
    private Button btnNextPage, btnPrevPage;
    private FloatingActionButton fabNext, fabPrev;

    private Paper paper;
    private PaperEntity paperEntity;
    private PaperDao paperDao;
    private FirebaseSyncManager firebaseSyncManager;

    private int totalPages = DEFAULT_TOTAL_PAGES;
    private int currentPage = 0;
    private boolean isInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_reader);

        // Get paper from intent
        paper = (Paper) getIntent().getSerializableExtra("paper");
        if (paper == null) {
            Toast.makeText(this, "Paper not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        loadPaperFromDatabase();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.view_pager);
        progressBar = findViewById(R.id.progress_bar);
        tvPageInfo = findViewById(R.id.tv_page_info);
        tvStatus = findViewById(R.id.tv_status);
        btnNextPage = findViewById(R.id.btn_next_page);
        btnPrevPage = findViewById(R.id.btn_prev_page);
        fabNext = findViewById(R.id.fab_next);
        fabPrev = findViewById(R.id.fab_prev);

        paperDao = LabVerseDatabase.getDatabase(this).paperDao();
        firebaseSyncManager = new FirebaseSyncManager(this);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(paper.getTitle() != null ? paper.getTitle() : "PDF Reader");
        }
    }

    private void loadPaperFromDatabase() {
        LabVerseDatabase.databaseWriteExecutor.execute(() -> {
            paperEntity = paperDao.getPaperByIdSync(paper.getId());
            if (paperEntity != null) {
                paper = PaperMapper.fromEntity(paperEntity);
                
                // Use saved total pages if available, otherwise use default
                if (paperEntity.totalPages > 0) {
                    totalPages = paperEntity.totalPages;
                }
                currentPage = paperEntity.currentPage;

                runOnUiThread(() -> {
                    initializePdfViewer();
                });
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Paper not found in database", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    private void initializePdfViewer() {
        showLoading(true);
        tvStatus.setText("Initializing PDF viewer...");

        // Simulate loading delay
        viewPager.postDelayed(() -> {
            // Setup ViewPager with mock pages
            setupViewPager();
            
            // Restore saved page position
            if (currentPage > 0 && currentPage < totalPages) {
                viewPager.setCurrentItem(currentPage, false);
            }

            showLoading(false);
            tvStatus.setText("PDF ready - Swipe to navigate");
            tvStatus.setVisibility(View.GONE);
            updatePageInfo();
            isInitialized = true;

            // Update status to "reading" if not already
            if (paperEntity != null && !"reading".equals(paperEntity.status) && !"finished".equals(paperEntity.status)) {
                updateReadingStatus("reading");
            }
        }, 500);
    }

    private void setupViewPager() {
        // Create a simple adapter that shows page numbers
        MockPdfAdapter adapter = new MockPdfAdapter(totalPages, paper.getTitle());
        viewPager.setAdapter(adapter);
        
        // Listen to page changes
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                updatePageInfo();
                saveProgress(position, totalPages);
            }
        });

        setupClickListeners();
    }

    private void setupClickListeners() {
        btnNextPage.setOnClickListener(v -> {
            if (currentPage < totalPages - 1) {
                viewPager.setCurrentItem(currentPage + 1, true);
            } else {
                Toast.makeText(this, "You've reached the last page", Toast.LENGTH_SHORT).show();
            }
        });

        btnPrevPage.setOnClickListener(v -> {
            if (currentPage > 0) {
                viewPager.setCurrentItem(currentPage - 1, true);
            } else {
                Toast.makeText(this, "You're on the first page", Toast.LENGTH_SHORT).show();
            }
        });

        fabNext.setOnClickListener(v -> {
            if (currentPage < totalPages - 1) {
                viewPager.setCurrentItem(currentPage + 1, true);
            }
        });

        fabPrev.setOnClickListener(v -> {
            if (currentPage > 0) {
                viewPager.setCurrentItem(currentPage - 1, true);
            }
        });
    }

    private void updatePageInfo() {
        if (totalPages > 0) {
            tvPageInfo.setText(String.format("Page %d of %d", currentPage + 1, totalPages));
            tvPageInfo.setVisibility(View.VISIBLE);
        } else {
            tvPageInfo.setVisibility(View.GONE);
        }
    }

    private void saveProgress(int page, int pageCount) {
        if (paperEntity == null) return;

        // Save progress to database
        LabVerseDatabase.databaseWriteExecutor.execute(() -> {
            paperEntity.currentPage = page;
            paperEntity.totalPages = pageCount;
            paperEntity.lastRead = System.currentTimeMillis();

            // Update status based on progress
            if (page >= pageCount - 1) {
                // Last page, mark as finished
                paperEntity.status = "finished";
            } else if (page >= 0 && !"finished".equals(paperEntity.status)) {
                // Reading in progress (even page 0 means started reading)
                paperEntity.status = "reading";
            }

            // Update all fields including totalPages
            paperDao.update(paperEntity);

            // Sync to Firebase
            firebaseSyncManager.syncPaperToFirebase(paperEntity);

            Log.d(TAG, "Progress saved: Page " + (page + 1) + " of " + pageCount);
        });
    }

    private void updateReadingStatus(String status) {
        if (paperEntity == null) return;

        LabVerseDatabase.databaseWriteExecutor.execute(() -> {
            paperEntity.status = status;
            paperEntity.lastRead = System.currentTimeMillis();
            paperDao.update(paperEntity);
            firebaseSyncManager.syncPaperToFirebase(paperEntity);
        });
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        viewPager.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        if (!isLoading) {
            tvStatus.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save progress when activity is paused
        if (isInitialized && paperEntity != null && currentPage >= 0) {
            saveProgress(currentPage, totalPages);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Save progress when activity is destroyed
        if (isInitialized && paperEntity != null && currentPage >= 0) {
            saveProgress(currentPage, totalPages);
        }
    }
}
