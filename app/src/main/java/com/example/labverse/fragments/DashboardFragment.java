package com.example.labverse.fragments;

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
import com.google.android.material.tabs.TabLayout;
import com.example.labverse.R;
import com.example.labverse.adapters.PaperAdapter;
import com.example.labverse.models.Paper;
import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PaperAdapter paperAdapter;
    private List<Paper> paperList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        initViews(view);
        setupTabs();
        setupRecyclerView();
        loadPapers();

        return view;
    }

    private void initViews(View view) {
        tabLayout = view.findViewById(R.id.tab_layout);
        recyclerView = view.findViewById(R.id.recycler_view_papers);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPapers();
            }
        });
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Recently Added"));
        tabLayout.addTab(tabLayout.newTab().setText("Recently Read"));
        tabLayout.addTab(tabLayout.newTab().setText("Favorites"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                loadPapersForTab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupRecyclerView() {
        paperList = new ArrayList<>();
        paperAdapter = new PaperAdapter(paperList, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(paperAdapter);
    }

    private void loadPapers() {
        // TODO: Load papers from database/API
        // For now, using mock data
        loadMockPapers();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void loadPapersForTab(int tabPosition) {
        // TODO: Filter papers based on selected tab
        loadMockPapers();
    }

    private void loadMockPapers() {
        paperList.clear();

        // Add mock papers
        paperList.add(new Paper(
                "1",
                "Deep Learning for Natural Language Processing",
                "John Smith, Jane Doe",
                "Nature Machine Intelligence",
                "2024",
                "unread"
        ));

        paperList.add(new Paper(
                "2",
                "Advances in Computer Vision Applications",
                "Alice Johnson, Bob Wilson",
                "IEEE Transactions on Pattern Analysis",
                "2024",
                "reading"
        ));

        paperList.add(new Paper(
                "3",
                "Machine Learning in Healthcare: A Comprehensive Review",
                "Carol Davis, David Brown",
                "Journal of Medical Internet Research",
                "2023",
                "finished"
        ));

        paperAdapter.notifyDataSetChanged();
    }
}
