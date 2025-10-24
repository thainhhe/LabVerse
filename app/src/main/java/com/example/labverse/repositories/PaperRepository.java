package com.example.labverse.repositories;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import androidx.lifecycle.LiveData;
import com.example.labverse.database.LabVerseDatabase;
import com.example.labverse.database.dao.PaperDao;
import com.example.labverse.database.entities.PaperEntity;
import com.example.labverse.firebase.FirebaseSyncManager;
import com.example.labverse.models.Paper;
import com.example.labverse.models.ReadingStatus;
import com.example.labverse.models.SearchFilters;
import com.example.labverse.utils.PaperMapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class PaperRepository {
    private PaperDao paperDao;
    private FirebaseSyncManager firebaseSyncManager;
    private ExecutorService executorService;
    // Assume you have a remote data source for server searches
    // private PaperRemoteDataSource remoteDataSource; 

    public PaperRepository(Application application) {
        LabVerseDatabase database = LabVerseDatabase.getDatabase(application);
        paperDao = database.paperDao();
        firebaseSyncManager = new FirebaseSyncManager(application);
        executorService = Executors.newFixedThreadPool(4);
        // remoteDataSource = new PaperRemoteDataSource();
    }

    public interface SyncCallback {
        void onSyncComplete();
        void onSyncError(String error);
    }

    public interface UpdateCallback {
        void onUpdateComplete();
        void onUpdateError(String error);
    }

    public interface SearchCallback {
        void onSearchComplete(List<Paper> results);
        void onSearchError(String error);
    }

    public LiveData<List<String>> getAllAuthors() {
        return paperDao.getAllAuthors();
    }

    public LiveData<List<String>> getAllJournals() {
        return paperDao.getAllJournals();
    }

    public void syncWithServer(String userId, SyncCallback callback) {
        executorService.execute(() -> {
            try {
                firebaseSyncManager.syncPapersFromFirebase(userId);
                new Handler(Looper.getMainLooper()).post(callback::onSyncComplete);
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onSyncError(e.getMessage()));
            }
        });
    }

    public LiveData<List<PaperEntity>> getRecentlyAdded() {
        return paperDao.getRecentlyAdded();
    }

    public LiveData<List<PaperEntity>> getRecentlyRead() {
        return paperDao.getRecentlyRead();
    }

    public LiveData<List<PaperEntity>> getFavorites() {
        return paperDao.getFavorites();
    }

    public void updateReadingStatus(String paperId, ReadingStatus status, UpdateCallback callback) {
        executorService.execute(() -> {
            try {
                paperDao.updateReadingStatus(paperId, status.name().toLowerCase(), System.currentTimeMillis());
                new Handler(Looper.getMainLooper()).post(callback::onUpdateComplete);

                PaperEntity paper = paperDao.getPaperByIdSync(paperId);
                if (paper != null) {
                    firebaseSyncManager.syncPaperToFirebase(paper);
                }
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onUpdateError(e.getMessage()));
            }
        });
    }

    public void toggleFavorite(String paperId, UpdateCallback callback) {
        executorService.execute(() -> {
            try {
                paperDao.toggleFavorite(paperId);
                new Handler(Looper.getMainLooper()).post(callback::onUpdateComplete);

                PaperEntity paper = paperDao.getPaperByIdSync(paperId);
                if (paper != null) {
                    firebaseSyncManager.syncPaperToFirebase(paper);
                }
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onUpdateError(e.getMessage()));
            }
        });
    }

    public void advancedSearch(String query, SearchFilters filters, SearchCallback callback) {
        executorService.execute(() -> {
            try {
                // Step 1: Search local database first
                List<PaperEntity> localResultEntities = searchLocalDatabase(query, filters);
                List<Paper> localResults = PaperMapper.fromEntities(localResultEntities);

                // Step 2: If online, search server for more comprehensive results
                if (isOnline()) {
                    try {
                        // This is a placeholder for your actual remote search call
                        // List<Paper> serverResults = remoteDataSource.advancedSearch(query, filters);
                        // List<Paper> mergedResults = mergeSearchResults(localResults, serverResults);
                        // notifySearchSuccess(mergedResults, callback);
                        // For now, we'll just return local results.
                        notifySearchSuccess(localResults, callback);
                        return;
                    } catch (Exception e) {
                        // Fallback to local results if server fails
                    }
                }
                
                // Return local results (offline mode or server failure)
                notifySearchSuccess(localResults, callback);

            } catch (Exception e) {
                notifySearchError(e.getMessage(), callback);
            }
        });
    }

    private List<PaperEntity> searchLocalDatabase(String query, SearchFilters filters) {
        String authorFilter = filters.getAuthors().isEmpty() ? null :
                String.join(",", filters.getAuthors());

        List<String> journalFilter = filters.getJournals().isEmpty() ? null :
                new java.util.ArrayList<>(filters.getJournals());

        Integer year = filters.getYear();

        List<String> readingStatus = filters.getReadingStatus().isEmpty() ? null :
                filters.getReadingStatus().stream()
                        .map(Enum::name)
                        .collect(Collectors.toList());

        return paperDao.advancedSearch(
                query.isEmpty() ? null : query,
                authorFilter,
                journalFilter,
                year,
                readingStatus
        );
    }

    private List<Paper> mergeSearchResults(List<Paper> local, List<Paper> remote) {
        Map<String, Paper> mergedMap = new LinkedHashMap<>();
        
        // Add all local papers
        for (Paper paper : local) {
            mergedMap.put(paper.getId(), paper);
        }
        
        // Add remote papers (will override local if same ID)
        for (Paper paper : remote) {
            mergedMap.put(paper.getId(), paper);
        }
        
        return new ArrayList<>(mergedMap.values());
    }

    private void notifySearchSuccess(List<Paper> results, SearchCallback callback) {
        new Handler(Looper.getMainLooper()).post(() -> 
            callback.onSearchComplete(results));
    }

    private void notifySearchError(String error, SearchCallback callback) {
        new Handler(Looper.getMainLooper()).post(() -> 
            callback.onSearchError(error));
    }

    private boolean isOnline() {
        // Implement network connectivity check
        return true; // Placeholder
    }
}
