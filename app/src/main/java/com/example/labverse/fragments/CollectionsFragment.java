package com.example.labverse.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.labverse.R;
import com.example.labverse.activities.CreateCollectionActivity;
import com.example.labverse.adapters.CollectionAdapter;
import com.example.labverse.models.Collection;
import java.util.ArrayList;
import java.util.List;

public class CollectionsFragment extends Fragment {

    private RecyclerView recyclerView;
    private FloatingActionButton fabCreateCollection;
    private CollectionAdapter collectionAdapter;
    private List<Collection> collectionList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collections, container, false);

        initViews(view);
        setupRecyclerView();
        loadCollections();

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_collections);
        fabCreateCollection = view.findViewById(R.id.fab_create_collection);

        fabCreateCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CreateCollectionActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupRecyclerView() {
        collectionList = new ArrayList<>();
        collectionAdapter = new CollectionAdapter(collectionList, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(collectionAdapter);
    }

    private void loadCollections() {
        // TODO: Load collections from database/API
        loadMockCollections();
    }

    private void loadMockCollections() {
        collectionList.clear();

        collectionList.add(new Collection(
                "col1",
                "AI Research Papers",
                "Collection of papers related to Artificial Intelligence research",
                "Dr. John Smith",
                15,
                false
        ));

        collectionList.add(new Collection(
                "col2",
                "Healthcare ML",
                "Machine Learning applications in healthcare",
                "Prof. Jane Doe",
                8,
                true
        ));

        collectionAdapter.notifyDataSetChanged();
    }
}
