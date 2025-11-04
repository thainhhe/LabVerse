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

import com.example.labverse.MainActivity;
import com.example.labverse.R;
import com.example.labverse.adapters.PaperAdapter;
import com.example.labverse.models.Paper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RecentlyAddedFragment extends Fragment implements MainActivity.SearchListener {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PaperAdapter paperAdapter;
    private List<Paper> paperList;
    private List<Paper> filteredPaperList;

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
        filteredPaperList = new ArrayList<>();
        paperAdapter = new PaperAdapter(filteredPaperList, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(paperAdapter);
    }

    private void loadPapers() {
        // TODO: Implement logic to load recently added papers from the database
        swipeRefreshLayout.setRefreshing(true);
        paperList.clear();
        
        // Mock data for testing
        long currentTime = System.currentTimeMillis();
        paperList.add(new Paper("1", "The impact of AI on software development", "John Doe", "IEEE Software", "2023", "reading"));
        paperList.get(0).setLastRead(currentTime - TimeUnit.HOURS.toMillis(2));
        paperList.get(0).setFavorite(true);

        paperList.add(new Paper("2", "A new approach to quantum computing", "Jane Smith", "Nature Physics", "2022", "unread"));

        paperList.add(new Paper("3", "Machine Learning in Healthcare", "Emily White", "The Lancet", "2023", "finished"));
        paperList.get(2).setLastRead(currentTime - TimeUnit.DAYS.toMillis(5));


        paperList.add(new Paper("4", "The future of mobile applications", "Michael Brown", "ACM", "2021", "reading"));
        paperList.get(3).setLastRead(currentTime - TimeUnit.MINUTES.toMillis(30));

        paperList.add(new Paper("5", "Cybersecurity in the IoT era", "Chris Green", "WIRED", "2023", "unread"));
        paperList.get(4).setFavorite(true);

        paperList.add(new Paper("6", "A study on renewable energy sources", "Jessica Blue", "Energy Journal", "2020", "finished"));
        paperList.get(5).setLastRead(currentTime - TimeUnit.DAYS.toMillis(10));

        paperList.add(new Paper("7", "The role of blockchain in finance", "David Black", "Journal of Finance", "2023", "reading"));
        paperList.get(6).setLastRead(currentTime - TimeUnit.DAYS.toMillis(1));


        paperList.add(new Paper("8", "Exploring the depths of the ocean", "Olivia Purple", "National Geographic", "2019", "unread"));

        paperList.add(new Paper("9", "The psychology of user experience", "William Yellow", "UX Magazine", "2023", "finished"));
        paperList.get(8).setLastRead(currentTime - TimeUnit.DAYS.toMillis(14));
        paperList.get(8).setFavorite(true);

        paperList.add(new Paper("10", "Advancements in gene editing", "Sophia Orange", "Science", "2023", "reading"));
        paperList.get(9).setLastRead(currentTime - TimeUnit.HOURS.toMillis(5));

        performSearch("");
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void performSearch(String query) {
        filteredPaperList.clear();
        if (query.isEmpty()) {
            filteredPaperList.addAll(paperList);
        } else {
            for (Paper paper : paperList) {
                if (paper.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        paper.getAuthors().toLowerCase().contains(query.toLowerCase()) ||
                        paper.getJournal().toLowerCase().contains(query.toLowerCase())) {
                    filteredPaperList.add(paper);
                }
            }
        }
        paperAdapter.notifyDataSetChanged();
    }
}
