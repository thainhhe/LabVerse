package com.example.labverse.fragments.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.labverse.R;
import com.example.labverse.adapters.PaperAdapter;
import com.example.labverse.models.Paper;

import java.util.ArrayList;
import java.util.List;

public class RecentlyAddedFragment extends Fragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PaperAdapter paperAdapter;
    private List<Paper> paperList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recently_added, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_papers);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        setupRecyclerView();
        loadPapers();

        swipeRefreshLayout.setOnRefreshListener(this::loadPapers);

        return view;
    }

    private void setupRecyclerView() {
        paperList = new ArrayList<>();
        paperAdapter = new PaperAdapter(paperList, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(paperAdapter);
    }

    private void loadPapers() {
        // TODO: Implement logic to load recently added papers from the database
        swipeRefreshLayout.setRefreshing(true);
        paperList.clear();
        // Mock data for now
        paperList.add(new Paper("1", "Paper Added Yesterday", "Author A", "Journal X", "2024", "unread"));
        paperList.add(new Paper("2", "Paper Added Today", "Author B", "Journal Y", "2024", "reading"));
        paperAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }
}
