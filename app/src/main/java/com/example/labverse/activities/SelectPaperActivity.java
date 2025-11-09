package com.example.labverse.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labverse.R;
import com.example.labverse.adapters.SelectPaperAdapter;
import com.example.labverse.database.LabVerseDatabase;
import com.example.labverse.database.dao.CollectionDao;
import com.example.labverse.database.dao.PaperDao;
import com.example.labverse.database.entities.CollectionPaperCrossRef;
import com.example.labverse.database.entities.PaperEntity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class SelectPaperActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private TextView tvEmptyState;

    private SelectPaperAdapter adapter;
    private List<PaperEntity> paperList = new ArrayList<>();

    // Database
    private PaperDao paperDao;
    private CollectionDao collectionDao;
    private ExecutorService databaseWriteExecutor;

    private String currentCollectionId;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_paper);

        currentCollectionId = getIntent().getStringExtra("COLLECTION_ID");
        if (currentCollectionId == null) {
            Toast.makeText(this, "Error: Collection ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        LabVerseDatabase db = LabVerseDatabase.getDatabase(this);
        paperDao = db.paperDao();
        collectionDao = db.collectionDao();
        databaseWriteExecutor = LabVerseDatabase.databaseWriteExecutor;
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Ánh xạ
        toolbar = findViewById(R.id.toolbar_select_paper);
        recyclerView = findViewById(R.id.recycler_view_select_paper);
        tvEmptyState = findViewById(R.id.tv_empty_select);

        setupToolbar();
        setupRecyclerView();
        loadAllPapers();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView() {
        adapter = new SelectPaperAdapter(paperList, paper -> {
            addPaperToCollection(paper);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }


    private void loadAllPapers() {
        if (currentUser == null) return;

        paperDao.getAllPapersForUser(currentUser.getUid()).observe(this, entities -> {
            if (entities == null || entities.isEmpty()) {
                tvEmptyState.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                // Có data
                tvEmptyState.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter.setData(entities);
            }

        });
    }

    private void addPaperToCollection(PaperEntity paper) {
        CollectionPaperCrossRef crossRef = new CollectionPaperCrossRef(
                currentCollectionId,
                paper.paperId
        );

        databaseWriteExecutor.execute(() -> {
            collectionDao.addPaperToCollection(crossRef);
        });

        Toast.makeText(this, "'" + paper.title + "' added.", Toast.LENGTH_SHORT).show();
        finish();
    }
}