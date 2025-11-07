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

    public PaperRepository(Application application) {
        LabVerseDatabase database = LabVerseDatabase.getDatabase(application);
        paperDao = database.paperDao();
        firebaseSyncManager = new FirebaseSyncManager(application);
        executorService = Executors.newFixedThreadPool(4);
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
                List<PaperEntity> localResultEntities = searchLocalDatabase(query, filters);
                List<Paper> localResults = PaperMapper.fromEntities(localResultEntities);
                notifySearchSuccess(localResults, callback);

            } catch (Exception e) {
                notifySearchError(e.getMessage(), callback);
            }
        });
    }

    private List<PaperEntity> searchLocalDatabase(String query, SearchFilters filters) {
        String authorFilter = filters.getAuthors().isEmpty() ? null :
                filters.getAuthors().iterator().next();

        List<String> journalFilter = filters.getJournals().isEmpty() ? null :
                new ArrayList<>(filters.getJournals());

        Integer year = filters.getYear();

        List<String> readingStatus = filters.getReadingStatus().isEmpty() ? null :
                filters.getReadingStatus().stream()
                        .map(status -> status.name().toLowerCase())
                        .collect(Collectors.toList());

        return paperDao.advancedSearch(
                query.isEmpty() ? null : query,
                authorFilter,
                journalFilter,
                year,
                readingStatus
        );
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
        return true; // Placeholder
    }
}
