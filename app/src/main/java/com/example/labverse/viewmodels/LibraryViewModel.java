package com.example.labverse.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.labverse.database.entities.PaperEntity;
import com.example.labverse.models.ReadingStatus;
import com.example.labverse.repositories.PaperRepository;
import com.example.labverse.utils.SecureAuthManager;

import java.util.List;

public class LibraryViewModel extends AndroidViewModel {
    private PaperRepository paperRepository;
    private LiveData<List<PaperEntity>> recentlyAdded;
    private LiveData<List<PaperEntity>> recentlyRead;
    private LiveData<List<PaperEntity>> favorites;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private SecureAuthManager authManager;

    public LibraryViewModel(@NonNull Application application) {
        super(application);
        paperRepository = new PaperRepository(application);
        authManager = new SecureAuthManager(application);
        loadLibraryData();
    }

    public LiveData<List<PaperEntity>> getRecentlyAdded() {
        if (recentlyAdded == null) {
            recentlyAdded = paperRepository.getRecentlyAdded();
        }
        return recentlyAdded;
    }

    public LiveData<List<PaperEntity>> getRecentlyRead() {
        if (recentlyRead == null) {
            recentlyRead = paperRepository.getRecentlyRead();
        }
        return recentlyRead;
    }

    public LiveData<List<PaperEntity>> getFavorites() {
        if (favorites == null) {
            favorites = paperRepository.getFavorites();
        }
        return favorites;
    }

    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void refreshData() {
        loadLibraryData();
    }

    private void loadLibraryData() {
        isLoading.setValue(true);
        String userId = authManager.getUserId();

        if (userId != null && !userId.isEmpty()) {
            paperRepository.syncWithServer(userId, new PaperRepository.SyncCallback() {
                @Override
                public void onSyncComplete() {
                    isLoading.postValue(false);
                }

                @Override
                public void onSyncError(String error) {
                    isLoading.postValue(false);
                }
            });
        }
    }

    public void updateReadingStatus(String paperId, ReadingStatus status) {
        paperRepository.updateReadingStatus(paperId, status, new PaperRepository.UpdateCallback() {
            @Override
            public void onUpdateComplete() {
                // UI will automatically update via LiveData observers
            }

            @Override
            public void onUpdateError(String error) {
                // Handle error (show toast, etc.)
            }
        });
    }

    public void toggleFavorite(String paperId) {
        paperRepository.toggleFavorite(paperId, new PaperRepository.UpdateCallback() {
            @Override
            public void onUpdateComplete() {
                // Success handling
            }

            @Override
            public void onUpdateError(String error) {
                // Error handling
            }
        });
    }
}
