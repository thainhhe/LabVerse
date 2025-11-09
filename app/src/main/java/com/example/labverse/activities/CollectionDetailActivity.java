package com.example.labverse.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.EditText;

import com.example.labverse.R;
import com.example.labverse.adapters.CollectionPaperAdapter;
import com.example.labverse.database.LabVerseDatabase;
import com.example.labverse.database.dao.CollectionDao;
import com.example.labverse.database.dao.PaperDao;
import com.example.labverse.database.dao.UserDao;
import com.example.labverse.database.entities.PaperEntity;
import com.example.labverse.fragments.UpdateStatusBottomSheet;
import com.example.labverse.models.Paper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.content.Intent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class CollectionDetailActivity extends AppCompatActivity implements UpdateStatusBottomSheet.OnStatusUpdatedListener {

    private static final String TAG = "CollectionDetail";

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private FloatingActionButton fabAddPaper;

    private CollectionPaperAdapter adapter;
    private List<Paper> paperList;

    private CollectionDao collectionDao;
    private PaperDao paperDao;
    private UserDao userDao;
    private ExecutorService databaseWriteExecutor;

    private String collectionId;
    private String collectionName;
    private Paper paperToUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_detail);

        collectionId = getIntent().getStringExtra("COLLECTION_ID");
        collectionName = getIntent().getStringExtra("COLLECTION_NAME");

        if (collectionId == null) {
            Toast.makeText(this, "Error: Collection ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();

        LabVerseDatabase db = LabVerseDatabase.getDatabase(this);
        collectionDao = db.collectionDao();
        paperDao = db.paperDao();
        userDao = db.userDao();
        databaseWriteExecutor = LabVerseDatabase.databaseWriteExecutor;

        setupRecyclerView();
        loadPapers();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar_detail);
        recyclerView = findViewById(R.id.recycler_view_papers);
        progressBar = findViewById(R.id.progress_bar_papers);
        tvEmptyState = findViewById(R.id.tv_empty_papers);
        fabAddPaper = findViewById(R.id.fab_add_paper);

        fabAddPaper.setOnClickListener(v -> {
            Intent intent = new Intent(CollectionDetailActivity.this, SelectPaperActivity.class);
            intent.putExtra("COLLECTION_ID", collectionId);
            startActivity(intent);
        });
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(collectionName);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.collection_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            finish();
            return true;
        } else if (itemId == R.id.menu_invite_member) {
            showInviteMemberDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showInviteMemberDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Invite Member");
        builder.setMessage("Enter the email of the user to invite:");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        input.setHint("user@example.com");

        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        input.setPadding(padding, padding, padding, padding);
        builder.setView(input);

        builder.setPositiveButton("Invite", (dialog, which) -> {
            String email = input.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            inviteUserByEmail(email);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void inviteUserByEmail(String email) {

        databaseWriteExecutor.execute(() -> {
            String userIdToInvite = userDao.findUserIdByEmail(email);

            if (userIdToInvite == null || userIdToInvite.isEmpty()) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error: User " + email + " not found", Toast.LENGTH_LONG).show();
                });
                return;
            }

            List<String> currentMembers = collectionDao.getMemberIdsSync(collectionId);
            if (currentMembers == null) {
                currentMembers = new ArrayList<>();
            }

            if (!currentMembers.contains(userIdToInvite)) {
                currentMembers.add(userIdToInvite);

                collectionDao.updateMemberIds(collectionId, currentMembers);

                runOnUiThread(() -> {
                    Toast.makeText(this, "User " + email + " added to collection.", Toast.LENGTH_SHORT).show();
                });
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(this, "User " + email + " is already a member.", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void setupRecyclerView() {
        paperList = new ArrayList<>();

        adapter = new CollectionPaperAdapter(paperList, this, paper -> {
            paperToUpdate = paper;

            UpdateStatusBottomSheet bottomSheet = UpdateStatusBottomSheet.newInstance(
                    collectionId,
                    paper.getId(),
                    paper.getStatus(),
                    paper.getPriority()
            );
            bottomSheet.show(getSupportFragmentManager(), "UpdateStatusSheet");
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadPapers() {
        showLoading(true);

        collectionDao.getCollectionWithPapers(collectionId).observe(this, collectionWithPapers -> {
            showLoading(false);

            if (collectionWithPapers == null) {
                paperList.clear();
                updateUI();
                return;
            }

            paperList.clear();
            List<PaperEntity> papersFromDb = collectionWithPapers.papers;

            for (PaperEntity entity : papersFromDb) {
                Paper p = new Paper();
                p.setId(entity.paperId);
                p.setTitle(entity.title);
                p.setAuthors(entity.authors);
                p.setJournal(entity.journal);
                p.setYear(entity.year);
                p.setStatus(entity.status);
                p.setPriority(entity.priority);
                paperList.add(p);
            }

            updateUI();
        });
    }

    @Override
    public void onStatusUpdated(String newStatus, String newPriority) {
        if (paperToUpdate == null) {
            return;
        }

        paperToUpdate.setStatus(newStatus);
        paperToUpdate.setPriority(newPriority);

        databaseWriteExecutor.execute(() -> {
            paperDao.updateStatusAndPriority(paperToUpdate.getId(), newStatus, newPriority);
        });

        Toast.makeText(this, "Status updated!", Toast.LENGTH_SHORT).show();
        paperToUpdate = null;
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void updateUI() {
        adapter.notifyDataSetChanged();

        if (paperList.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}