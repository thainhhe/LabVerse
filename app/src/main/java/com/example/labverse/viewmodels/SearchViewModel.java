package com.example.labverse.viewmodels;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.labverse.models.Paper;
import com.example.labverse.models.SearchFilters;
import com.example.labverse.models.SearchState;
import com.example.labverse.repositories.PaperRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchViewModel extends AndroidViewModel {
    private PaperRepository paperRepository;
    private MutableLiveData<SearchState> searchState = new MutableLiveData<>();
    private MutableLiveData<SearchFilters> activeFilters = new MutableLiveData<>();

    private LiveData<List<String>> allJournals;
    private MediatorLiveData<List<String>> allAuthors = new MediatorLiveData<>();

    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private static final long SEARCH_DELAY_MS = 300;

    public SearchViewModel(@NonNull Application application) {
        super(application);
        paperRepository = new PaperRepository(application);
        activeFilters.setValue(new SearchFilters());
        searchState.setValue(new SearchState.Idle());

        allJournals = paperRepository.getAllJournals();

        // Process authors to split comma-separated values
        allAuthors.addSource(paperRepository.getAllAuthors(), authorStrings -> {
            Set<String> uniqueAuthors = new HashSet<>();
            for (String authorString : authorStrings) {
                String[] names = authorString.split(",");
                for (String name : names) {
                    uniqueAuthors.add(name.trim());
                }
            }
            List<String> sortedAuthors = new ArrayList<>(uniqueAuthors);
            Collections.sort(sortedAuthors);
            allAuthors.setValue(sortedAuthors);
        });
    }

    public LiveData<SearchState> getSearchState() { return searchState; }
    public LiveData<SearchFilters> getActiveFilters() { return activeFilters; }
    public LiveData<List<String>> getAllAuthors() { return allAuthors; }
    public LiveData<List<String>> getAllJournals() { return allJournals; }

    public void updateSearchQuery(String query) {
        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
        searchRunnable = () -> performSearch(query, activeFilters.getValue());
        searchHandler.postDelayed(searchRunnable, SEARCH_DELAY_MS);
    }

    public void updateFilters(SearchFilters newFilters) {
        activeFilters.setValue(newFilters);
        // For simplicity, we assume the user will re-submit the query to apply filters.
    }

    public void clearAllFilters() {
        activeFilters.setValue(new SearchFilters());
    }

    private void performSearch(String query, SearchFilters filters) {
        if (query.isEmpty() && !filters.isActive()) {
            searchState.setValue(new SearchState.Idle());
            return;
        }

        searchState.setValue(new SearchState.Loading());

        paperRepository.advancedSearch(query, filters, new PaperRepository.SearchCallback() {
            @Override
            public void onSearchComplete(List<Paper> results) {
                if (results.isEmpty()) {
                    searchState.setValue(new SearchState.Empty());
                } else {
                    searchState.setValue(new SearchState.Success(results, query));
                }
            }

            @Override
            public void onSearchError(String error) {
                searchState.setValue(new SearchState.Error(error));
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
    }
}
