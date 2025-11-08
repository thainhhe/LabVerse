package com.example.labverse.fragments.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.labverse.R;
import com.example.labverse.adapters.PaperAdapter;
import com.example.labverse.models.Paper;
import com.example.labverse.models.SearchState;
import com.example.labverse.viewmodels.SearchViewModel;

import java.util.ArrayList;
import java.util.List;

public class RecentlyAddedFragment extends Fragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PaperAdapter paperAdapter;
    private SearchViewModel searchViewModel;
    private List<Paper> paperList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recently_added, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_papers);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        setupRecyclerView();

        // When user swipes to refresh, re-run the current search/filter query
        swipeRefreshLayout.setOnRefreshListener(() -> {
            searchViewModel.updateSearchQuery(searchViewModel.getActiveFilters().getValue() != null ? "" : "");
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Use the shared SearchViewModel from the Activity
        searchViewModel = new ViewModelProvider(requireActivity()).get(SearchViewModel.class);

        observeSearchState();

        // Perform an initial search to load all papers when the fragment is first created
        searchViewModel.performInitialSearch();
    }

    private void setupRecyclerView() {
        paperAdapter = new PaperAdapter(paperList, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(paperAdapter);
    }

    private void observeSearchState() {
        searchViewModel.getSearchState().observe(getViewLifecycleOwner(), state -> {
            if (state instanceof SearchState.Loading) {
                swipeRefreshLayout.setRefreshing(true);
            } else if (state instanceof SearchState.Success) {
                swipeRefreshLayout.setRefreshing(false);
                updatePaperList(((SearchState.Success) state).getPapers());
            } else if (state instanceof SearchState.Error || state instanceof SearchState.Empty || state instanceof SearchState.Idle) {
                swipeRefreshLayout.setRefreshing(false);
                updatePaperList(new ArrayList<>()); // Clear the list on error or empty result
            }
        });
    }

    private void updatePaperList(List<Paper> newPapers) {
        paperList.clear();
        paperList.addAll(newPapers);
        paperAdapter.notifyDataSetChanged();
    }
}
