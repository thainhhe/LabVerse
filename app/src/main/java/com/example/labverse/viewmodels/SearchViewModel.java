package com.example.labverse.viewmodels;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.labverse.database.entities.PaperEntity;
import com.example.labverse.models.Paper;
import com.example.labverse.models.SearchFilters;
import com.example.labverse.models.SearchState;
import com.example.labverse.repositories.PaperRepository;
import com.example.labverse.utils.PaperMapper;

import java.util.List;

public class SearchViewModel extends AndroidViewModel {
    private PaperRepository paperRepository;
    private MutableLiveData<SearchState> searchState = new MutableLiveData<>();
    private MutableLiveData<SearchFilters> activeFilters = new MutableLiveData<>();

    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private static final long SEARCH_DELAY_MS = 300;

    public SearchViewModel(@NonNull Application application) {
        super(application);
        paperRepository = new PaperRepository(application);
        activeFilters.setValue(new SearchFilters());
        searchState.setValue(new SearchState.Idle());
    }

    public LiveData<SearchState> getSearchState() { return searchState; }
    public LiveData<SearchFilters> getActiveFilters() { return activeFilters; }

    public void updateSearchQuery(String query) {
        // Cancel previous search
        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }

        // Schedule new search with debounce
        searchRunnable = () -> performSearch(query, activeFilters.getValue());
        searchHandler.postDelayed(searchRunnable, SEARCH_DELAY_MS);
    }

    public void updateFilters(SearchFilters newFilters) {
        activeFilters.setValue(newFilters);
        // For simplicity, we assume the user will re-submit the query to apply filters.
        // Or we can trigger it immediately:
        // performSearch("", newFilters); 
    }

    public void clearAllFilters() {
        activeFilters.setValue(new SearchFilters());
        // performSearch("", new SearchFilters());
    }

    private void performSearch(String query, SearchFilters filters) {
        if (query.isEmpty() && !filters.isActive()) {
            searchState.setValue(new SearchState.Idle());
            return;
        }

        searchState.setValue(new SearchState.Loading());

        paperRepository.advancedSearch(query, filters, new PaperRepository.SearchCallback() {
            @Override
            public void onSearchComplete(List<PaperEntity> results) {
                if (results.isEmpty()) {
                    searchState.setValue(new SearchState.Empty());
                } else {
                    // Convert PaperEntity to Paper model for the UI
                    List<Paper> paperModels = PaperMapper.fromEntities(results);
                    searchState.setValue(new SearchState.Success(paperModels, query));
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
