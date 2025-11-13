package com.example.labverse.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.labverse.R;
import com.example.labverse.database.LabVerseDatabase;
import com.example.labverse.database.dao.CollectionDao;
import com.example.labverse.database.entities.CollectionEntity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Collections;
import java.util.List; // Thêm
import java.util.UUID;
import java.util.concurrent.ExecutorService;

public class CreateCollectionActivity extends AppCompatActivity {

    private TextInputEditText etName, etDescription;
    private Button btnCreate;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private RadioGroup rgCollectionType;
    private RadioButton rbPublic;

    private CollectionDao collectionDao;
    private ExecutorService databaseWriteExecutor;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_collection);

        etName = findViewById(R.id.et_collection_name);
        etDescription = findViewById(R.id.et_collection_description);
        btnCreate = findViewById(R.id.btn_create);
        progressBar = findViewById(R.id.progress_bar_create);
        toolbar = findViewById(R.id.toolbar_create);
        rgCollectionType = findViewById(R.id.rg_collection_type);
        rbPublic = findViewById(R.id.rb_public);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        LabVerseDatabase db = LabVerseDatabase.getDatabase(this);
        collectionDao = db.collectionDao();
        databaseWriteExecutor = LabVerseDatabase.databaseWriteExecutor;

        mAuth = FirebaseAuth.getInstance();
        btnCreate.setOnClickListener(v -> createCollection());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void createCollection() {
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        boolean isPublic = rbPublic.isChecked();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (TextUtils.isEmpty(name)) {
            etName.setError("Name is required");
            return;
        }

        if (currentUser == null) {
            Toast.makeText(this, "Error: You are not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);

        String creatorName = currentUser.getDisplayName();
        if (creatorName == null || creatorName.isEmpty()) {
            creatorName = currentUser.getEmail(); // Dùng email nếu không có "nick"
        }

        String collectionId = UUID.randomUUID().toString();
        String ownerId = currentUser.getUid();
        List<String> memberIds = Collections.singletonList(ownerId); // Tự thêm mình làm thành viên

        CollectionEntity newCollection = new CollectionEntity(
                collectionId,
                name,
                description,
                creatorName,
                ownerId,
                memberIds,
                isPublic
        );


        newCollection.setSyncStatus("synced");

        databaseWriteExecutor.execute(() -> {
            collectionDao.insert(newCollection);

            runOnUiThread(() -> {
                showLoading(false);
                Toast.makeText(this, "Collection created locally!", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            btnCreate.setEnabled(false);
            btnCreate.setText("Creating...");
        } else {
            progressBar.setVisibility(View.GONE);
            btnCreate.setEnabled(true);
            btnCreate.setText("Create Collection");
        }
    }
}