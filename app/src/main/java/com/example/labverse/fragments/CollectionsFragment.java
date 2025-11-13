package com.example.labverse.fragments;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.labverse.activities.CollectionDetailActivity;
import com.example.labverse.activities.CreateCollectionActivity;
import com.example.labverse.adapters.CollectionAdapter;
import com.example.labverse.database.LabVerseDatabase;
import com.example.labverse.database.dao.CollectionDao;
import com.example.labverse.database.entities.CollectionEntity;
import com.example.labverse.database.relations.CollectionWithPapers;
import com.example.labverse.models.Collection;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class CollectionsFragment extends Fragment {

    private RecyclerView recyclerView;
    private MaterialButton btnAddNewCollection;
    private CollectionAdapter collectionAdapter;
    private List<Collection> collectionList;

    private CollectionDao collectionDao;
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadCollectionsFromRoom();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_collections);
        btnAddNewCollection = view.findViewById(R.id.btn_add_new_collection);
        progressBar = view.findViewById(R.id.progress_bar);
        tvEmptyState = view.findViewById(R.id.text_view_empty);

        btnAddNewCollection.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CreateCollectionActivity.class);
            startActivity(intent);
        });
    }

    private void setupLogic() {
        if (getContext() != null) {
            collectionDao = LabVerseDatabase.getDatabase(getContext().getApplicationContext()).collectionDao();
        }
        mAuth = FirebaseAuth.getInstance();
    }

    private void setupRecyclerView() {
        collectionList = new ArrayList<>();

        collectionAdapter = new CollectionAdapter(collectionList, collection -> {
            Intent intent = new Intent(getContext(), CollectionDetailActivity.class);
            intent.putExtra("COLLECTION_ID", collection.getId());
            intent.putExtra("COLLECTION_NAME", collection.getName());
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(collectionAdapter);
    }


    private void loadCollectionsFromRoom() {
        showLoading(true);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show();
            showLoading(false);
            return;
        }

        collectionDao.getCollectionsWithPapersForMember(currentUser.getUid()).observe(getViewLifecycleOwner(), relations -> {
            showLoading(false);
            List<Collection> pojos = new ArrayList<>();

            for (CollectionWithPapers rel : relations) {
                CollectionEntity entity = rel.collection;

                int paperCount = (rel.papers != null) ? rel.papers.size() : 0;

                Collection pojo = new Collection(
                        entity.getCollectionId(),
                        entity.getName(),
                        entity.getDescription(),
                        entity.getCreatedBy(),
                        entity.getOwnerId(),
                        entity.getMemberIds(),
                        entity.isPublic()
                );

                pojo.setPaperCount(paperCount);

                pojos.add(pojo);
            }

            collectionAdapter.setData(pojos);
            updateUI(pojos.isEmpty());
        });
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

    private void updateUI(boolean isEmpty) {
        if (isEmpty) {
            tvEmptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}