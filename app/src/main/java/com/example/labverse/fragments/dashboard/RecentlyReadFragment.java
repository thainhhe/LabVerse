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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RecentlyReadFragment extends Fragment implements MainActivity.SearchListener {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PaperAdapter paperAdapter;
    private List<Paper> paperList;
    private List<Paper> filteredPaperList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recently_read, container, false);

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
        // TODO: Implement logic to load recently read papers from the database
        swipeRefreshLayout.setRefreshing(true);
        paperList.clear();

        // Mock data for testing
        long currentTime = System.currentTimeMillis();
        List<Paper> allPapers = new ArrayList<>();
        allPapers.add(new Paper("1", "The impact of AI on software development", "John Doe", "IEEE Software", "2023", "reading"));
        allPapers.get(0).setLastRead(currentTime - TimeUnit.HOURS.toMillis(2));
        allPapers.get(0).setFavorite(true);

        allPapers.add(new Paper("2", "A new approach to quantum computing", "Jane Smith", "Nature Physics", "2022", "unread"));

        allPapers.add(new Paper("3", "Machine Learning in Healthcare", "Emily White", "The Lancet", "2023", "finished"));
        allPapers.get(2).setLastRead(currentTime - TimeUnit.DAYS.toMillis(5));

        allPapers.add(new Paper("4", "The future of mobile applications", "Michael Brown", "ACM", "2021", "reading"));
        allPapers.get(3).setLastRead(currentTime - TimeUnit.MINUTES.toMillis(30));

        allPapers.add(new Paper("5", "Cybersecurity in the IoT era", "Chris Green", "WIRED", "2023", "unread"));
        allPapers.get(4).setFavorite(true);

        allPapers.add(new Paper("6", "A study on renewable energy sources", "Jessica Blue", "Energy Journal", "2020", "finished"));
        allPapers.get(5).setLastRead(currentTime - TimeUnit.DAYS.toMillis(10));

        allPapers.add(new Paper("7", "The role of blockchain in finance", "David Black", "Journal of Finance", "2023", "reading"));
        allPapers.get(6).setLastRead(currentTime - TimeUnit.DAYS.toMillis(1));

        allPapers.add(new Paper("8", "Exploring the depths of the ocean", "Olivia Purple", "National Geographic", "2019", "unread"));

        allPapers.add(new Paper("9", "The psychology of user experience", "William Yellow", "UX Magazine", "2023", "finished"));
        allPapers.get(8).setLastRead(currentTime - TimeUnit.DAYS.toMillis(14));
        allPapers.get(8).setFavorite(true);

        allPapers.add(new Paper("10", "Advancements in gene editing", "Sophia Orange", "Science", "2023", "reading"));
        allPapers.get(9).setLastRead(currentTime - TimeUnit.HOURS.toMillis(5));

        for (Paper paper : allPapers) {
            if (paper.getLastRead() > 0) {
                paperList.add(paper);
            }
        }

        // Sort by lastRead in descending order
        Collections.sort(paperList, (p1, p2) -> Long.compare(p2.getLastRead(), p1.getLastRead()));

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
