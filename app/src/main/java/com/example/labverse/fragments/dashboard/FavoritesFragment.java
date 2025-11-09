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
import java.util.stream.Collectors;

public class FavoritesFragment extends Fragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PaperAdapter paperAdapter;
    private SearchViewModel searchViewModel;
    private List<Paper> paperList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

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
                // Apply the specific filter for this fragment
                List<Paper> filteredPapers = filterForFavorites(((SearchState.Success) state).getPapers());
                updatePaperList(filteredPapers);
            } else {
                swipeRefreshLayout.setRefreshing(false);
                updatePaperList(new ArrayList<>()); // Clear list on error or empty state
            }
        });
    }

    private List<Paper> filterForFavorites(List<Paper> papers) {
        // Filter to only include papers that are favorites, regardless of their reading status
        return papers.stream()
                .filter(Paper::isFavorite)
                .collect(Collectors.toList());
    }

    private void updatePaperList(List<Paper> newPapers) {
        paperList.clear();
        paperList.addAll(newPapers);
        paperAdapter.notifyDataSetChanged();
    }
}
