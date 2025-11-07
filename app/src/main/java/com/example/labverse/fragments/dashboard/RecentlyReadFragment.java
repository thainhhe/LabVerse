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
import com.example.labverse.models.ReadingStatus;
import com.example.labverse.models.SearchFilters;
import com.example.labverse.models.SearchState;
import com.example.labverse.viewmodels.SearchViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RecentlyReadFragment extends Fragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PaperAdapter paperAdapter;
    private SearchViewModel searchViewModel;
    private List<Paper> paperList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recently_read, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_papers);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        setupRecyclerView();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            searchViewModel.updateSearchQuery(searchViewModel.getActiveFilters().getValue() != null ? "" : "");
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchViewModel = new ViewModelProvider(requireActivity()).get(SearchViewModel.class);
        observeSearchState();

        // The initial search is likely already triggered by RecentlyAddedFragment, so we don't need to call it again.
        // If this fragment can be displayed first, you might need to add: searchViewModel.performInitialSearch();
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

                List<Paper> papersFromSearch = ((SearchState.Success) state).getPapers();
                SearchFilters appliedFilters = searchViewModel.getActiveFilters().getValue();

                List<Paper> papersToDisplay;

                // If the user has NOT applied a status filter from the dialog, then apply this tab's default filter.
                if (appliedFilters == null || appliedFilters.getReadingStatus().isEmpty()) {
                    papersToDisplay = filterForRecentlyRead(papersFromSearch);
                } else {
                    // If the user DID apply a status filter, we honor it and display the results directly,
                    // overriding the tab's default 'reading' or 'finished' logic.
                    papersToDisplay = papersFromSearch;
                }
                updatePaperList(papersToDisplay);

            } else {
                swipeRefreshLayout.setRefreshing(false);
                updatePaperList(new ArrayList<>()); // Clear list on error or empty state
            }
        });
    }

    private List<Paper> filterForRecentlyRead(List<Paper> papers) {
        // Filter to only include papers that are 'reading' or 'finished'
        return papers.stream()
                .filter(paper -> "reading".equals(paper.getStatus()) || "finished".equals(paper.getStatus()))
                .collect(Collectors.toList());
    }

    private void updatePaperList(List<Paper> newPapers) {
        paperList.clear();
        paperList.addAll(newPapers);
        paperAdapter.notifyDataSetChanged();
    }
}
