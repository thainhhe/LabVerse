package com.example.labverse.fragments.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.labverse.MainActivity;
import com.example.labverse.R;
import com.example.labverse.adapters.PaperAdapter;
import com.example.labverse.models.Paper;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment implements MainActivity.SearchListener {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PaperAdapter paperAdapter;
    private List<Paper> paperList;
    private List<Paper> filteredPaperList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_papers);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        setupRecyclerView();
        loadPapers();

        swipeRefreshLayout.setOnRefreshListener(this::loadPapers);

        return view;
    }

    private void setupRecyclerView() {
        paperList = new ArrayList<>();
        filteredPaperList = new ArrayList<>();
        paperAdapter = new PaperAdapter(filteredPaperList, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(paperAdapter);
    }

    private void loadPapers() {
        // TODO: Implement logic to load favorite papers from the database
        swipeRefreshLayout.setRefreshing(true);
        paperList.clear();
        // Mock data for now
        paperList.add(new Paper("4", "My Favorite Paper", "Author D", "Journal W", "2022", "finished"));
        performSearch("");
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void performSearch(String query) {
        filteredPaperList.clear();
        if (query.isEmpty()) {
            filteredPaperList.addAll(paperList);
        } else {
            for (Paper paper : paperList) {
                if (paper.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        paper.getAuthors().toLowerCase().contains(query.toLowerCase()) ||
                        paper.getJournal().toLowerCase().contains(query.toLowerCase())) {
                    filteredPaperList.add(paper);
                }
            }
        }
        paperAdapter.notifyDataSetChanged();
    }
}
