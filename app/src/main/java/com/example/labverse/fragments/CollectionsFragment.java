package com.example.labverse.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labverse.R;
import com.example.labverse.activities.CreateCollectionActivity;
import com.example.labverse.adapters.CollectionAdapter;
import com.example.labverse.firebase.FirebaseSyncManager;
import com.example.labverse.firebase.models.FirebaseCollection;
import com.example.labverse.models.Collection;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CollectionsFragment extends Fragment {

    private static final String TAG = "CollectionsFragment";

    private RecyclerView recyclerView;
    private FloatingActionButton fabCreateCollection;
    private CollectionAdapter collectionAdapter;
    private List<Collection> collectionList;

    private FirebaseSyncManager firebaseSyncManager;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private TextView tvEmptyState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collections, container, false);

        initViews(view);
        setupLogic();
        setupRecyclerView();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCollections();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_collections);
        fabCreateCollection = view.findViewById(R.id.fab_create_collection);
        progressBar = view.findViewById(R.id.progress_bar);
        tvEmptyState = view.findViewById(R.id.text_view_empty);

        fabCreateCollection.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CreateCollectionActivity.class);
            startActivity(intent);
        });
    }

    private void setupLogic() {
        if (getContext() != null) {
            firebaseSyncManager = new FirebaseSyncManager(getContext().getApplicationContext());
        }
        mAuth = FirebaseAuth.getInstance();
    }

    private void setupRecyclerView() {
        collectionList = new ArrayList<>();
        collectionAdapter = new CollectionAdapter(collectionList, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(collectionAdapter);
    }

    private void loadCollections() {
        showLoading(true);

        loadMockCollections();

        /*
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(getContext(), "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
            showLoading(false);
            updateUI();
            return;
        }

        firebaseSyncManager.getCollectionsForUser(currentUser.getUid(), task -> {
            showLoading(false);
            if (task.isSuccessful()) {
                collectionList.clear();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    FirebaseCollection fbCollection = document.toObject(FirebaseCollection.class);

                    fbCollection.setCollectionId(document.getId());

                    Collection uiCollection = new Collection(
                            fbCollection.getCollectionId(),
                            fbCollection.getName(),
                            fbCollection.getDescription(),
                            fbCollection.getCreatedBy(),
                            fbCollection.getOwnerId(),
                            fbCollection.getMemberIds(),
                            fbCollection.isPublic()
                    );

                    collectionList.add(uiCollection);
                }

                updateUI();

            } else {
                Log.w(TAG, "Error getting documents.", task.getException());
                Toast.makeText(getContext(), "Lỗi khi tải bộ sưu tập", Toast.LENGTH_SHORT).show();
            }
        });
        */
    }

    private void loadMockCollections() {
        collectionList.clear();

        List<String> members1 = new ArrayList<>();
        members1.add("user1");
        members1.add("user2");
        members1.add("user3");

        List<String> members2 = new ArrayList<>();
        members2.add("user1");

        collectionList.add(new Collection(
                "col1",
                "AI Research Papers",
                "Tổng hợp các bài báo về AI và Deep Learning mới nhất.",
                "Dr. John Smith",
                "uid_john_smith",
                members1,
                false
        ));

        collectionList.add(new Collection(
                "col2",
                "Healthcare ML",
                "Các ứng dụng của Machine Learning trong Y tế.",
                "Prof. Jane Doe",
                "uid_jane_doe",
                members2,
                true
        ));

        collectionList.get(0).setPaperCount(15);
        collectionList.get(1).setPaperCount(8);

        showLoading(false);
        updateUI();
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            tvEmptyState.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void updateUI() {
        collectionAdapter.notifyDataSetChanged();
        if (collectionList.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}