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
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
}
