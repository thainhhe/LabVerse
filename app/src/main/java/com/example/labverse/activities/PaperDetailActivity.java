package com.example.labverse.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.labverse.R;
import com.example.labverse.adapters.PaperDetailPagerAdapter;
import com.example.labverse.database.LabVerseDatabase;
import com.example.labverse.database.dao.PaperDao;
import com.example.labverse.database.entities.PaperEntity;
import com.example.labverse.fragments.PaperCitationFragment;
import com.example.labverse.fragments.PaperDetailsFragment;
import com.example.labverse.models.Paper;
import com.example.labverse.utils.PaperMapper;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class    PaperDetailActivity extends AppCompatActivity {

    private static final String TAG = "PaperDetailActivity";

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private PaperDetailPagerAdapter pagerAdapter;

    private PaperEntity paperEntity;
    private Paper paper;
    private PaperDao paperDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper_detail);

        // Get paper from intent (either as object or paperId)
        paper = (Paper) getIntent().getSerializableExtra("paper");
        String paperId = getIntent().getStringExtra("paperId");

        if (paper == null && paperId != null) {
            // Load paper from database using paperId
            loadPaperById(paperId);
            return;
        }

        if (paper == null) {
            Toast.makeText(this, "Paper not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        setupTabs();
        loadPaperFromDatabase();
    }

    private void loadPaperById(String paperId) {
        initViews();
        paperDao = LabVerseDatabase.getDatabase(this).paperDao();
        
        LabVerseDatabase.databaseWriteExecutor.execute(() -> {
            paperEntity = paperDao.getPaperByIdSync(paperId);
            if (paperEntity != null) {
                paper = PaperMapper.fromEntity(paperEntity);
                runOnUiThread(() -> {
                    setupToolbar();
                    setupTabs();
                });
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Paper not found", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        paperDao = LabVerseDatabase.getDatabase(this).paperDao();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(paper.getTitle() != null ? paper.getTitle() : "Paper Details");
        }
    }

    private void setupTabs() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(PaperDetailsFragment.newInstance(paper));
        fragments.add(PaperCitationFragment.newInstance(paper.getId()));

        pagerAdapter = new PaperDetailPagerAdapter(getSupportFragmentManager(), getLifecycle(), fragments);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Details");
                    break;
                case 1:
                    tab.setText("Citations");
                    break;
            }
        }).attach();
    }

    private void loadPaperFromDatabase() {
        LabVerseDatabase.databaseWriteExecutor.execute(() -> {
            paperEntity = paperDao.getPaperByIdSync(paper.getId());
            if (paperEntity != null) {
                runOnUiThread(() -> {
                    // Update paper object with latest data
                    paper = PaperMapper.fromEntity(paperEntity);
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void copyToClipboard(String text, String label) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, label + " copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    public PaperEntity getPaperEntity() {
        return paperEntity;
    }

    public Paper getPaper() {
        return paper;
    }

    public void switchToCitationTab() {
        if (viewPager != null) {
            viewPager.setCurrentItem(1, true);
        }
    }
}
