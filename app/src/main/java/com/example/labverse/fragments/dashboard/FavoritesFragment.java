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
import com.example.labverse.database.entities.PaperEntity;
import com.example.labverse.models.Paper;
import com.example.labverse.utils.PaperMapper;
import com.example.labverse.viewmodels.LibraryViewModel;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PaperAdapter paperAdapter;
    private LibraryViewModel viewModel;
    private List<Paper> paperList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_papers);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        setupRecyclerView();

        swipeRefreshLayout.setOnRefreshListener(() -> viewModel.refreshData());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(LibraryViewModel.class);
        observeViewModel();
    }

    private void setupRecyclerView() {
        paperAdapter = new PaperAdapter(paperList, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(paperAdapter);
    }

    private void observeViewModel() {
        viewModel.getFavorites().observe(getViewLifecycleOwner(), paperEntities -> {
            if (paperEntities != null) {
                updatePaperList(paperEntities);
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                swipeRefreshLayout.setRefreshing(isLoading);
            }
        });
    }

    private void updatePaperList(List<PaperEntity> paperEntities) {
        paperList.clear();
        for (PaperEntity entity : paperEntities) {
            paperList.add(PaperMapper.fromEntity(entity));
        }
        paperAdapter.notifyDataSetChanged();
    }
}
