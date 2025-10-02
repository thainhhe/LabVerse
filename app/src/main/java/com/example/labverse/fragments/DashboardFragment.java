package com.example.labverse.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.labverse.R;
import com.example.labverse.adapters.PaperAdapter;
import com.example.labverse.models.Paper;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PaperAdapter paperAdapter;
    private List<Paper> paperList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        initViews(view);
        setupRecyclerView();
        loadPapers();
        return view;
    }

    // ... (Các hàm initViews, setupRecyclerView, loadPapers, loadMockPapers giữ nguyên)

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_papers);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this::loadPapers);
    }

    private void setupRecyclerView() {
        paperList = new ArrayList<>();
        paperAdapter = new PaperAdapter(paperList, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(paperAdapter);
    }

    private void loadPapers() {
        loadMockPapers();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void loadMockPapers() {
        paperList.clear();
        paperList.add(new Paper("1", "Deep Learning for Natural Language Processing", "John Smith, Jane Doe", "Nature Machine Intelligence", "2024", "unread"));
        paperList.add(new Paper("2", "Advances in Computer Vision Applications", "Alice Johnson, Bob Wilson", "IEEE Transactions on Pattern Analysis", "2024", "reading"));
        paperList.add(new Paper("3", "Machine Learning in Healthcare: A Comprehensive Review", "Carol Davis, David Brown", "Journal of Medical Internet Research", "2023", "finished"));
        paperAdapter.notifyDataSetChanged();
    }

    // --- PHẦN CẬP NHẬT ---
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.dashboard_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getContext(), "Searching for: " + query, Toast.LENGTH_SHORT).show();
                // TODO: Gọi hàm lọc danh sách với từ khóa "query"
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO: (Tùy chọn) Lọc danh sách ngay khi người dùng gõ
                return true;
            }
        });
    }

    // Phương thức này không còn cần thiết vì logic đã được chuyển vào onCreateOptionsMenu
    // nhưng bạn có thể giữ lại để xử lý các item menu khác trong tương lai.
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}