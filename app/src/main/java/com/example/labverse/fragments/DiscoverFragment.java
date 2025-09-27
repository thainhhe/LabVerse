package com.example.labverse.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.labverse.R;
import com.example.labverse.adapters.PaperAdapter;
import com.example.labverse.models.Paper;
import java.util.ArrayList;
import java.util.List;

public class DiscoverFragment extends Fragment {

    private RecyclerView recyclerView;
    private PaperAdapter paperAdapter;
    private List<Paper> recommendedPapers;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);

        initViews(view);
        setupRecyclerView();
        loadRecommendedPapers();

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_recommended);
    }

    private void setupRecyclerView() {
        recommendedPapers = new ArrayList<>();
        paperAdapter = new PaperAdapter(recommendedPapers, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(paperAdapter);
    }

    private void loadRecommendedPapers() {
        // TODO: Load recommended papers from API based on user's interests
        loadMockRecommendedPapers();
    }

    private void loadMockRecommendedPapers() {
        recommendedPapers.clear();

        recommendedPapers.add(new Paper(
                "rec1",
                "Neural Networks for Time Series Analysis",
                "Emily Chen, Michael Zhang",
                "Neural Computing and Applications",
                "2024",
                "unread"
        ));

        recommendedPapers.add(new Paper(
                "rec2",
                "Quantum Computing Applications in Machine Learning",
                "Robert Kim, Sarah Lee",
                "Quantum Information Processing",
                "2024",
                "unread"
        ));

        paperAdapter.notifyDataSetChanged();
    }
}
