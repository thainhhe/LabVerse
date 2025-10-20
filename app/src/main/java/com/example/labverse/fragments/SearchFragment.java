package com.example.labverse.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labverse.R;
import com.example.labverse.adapters.PaperAdapter;
import com.example.labverse.models.SearchState;
import com.example.labverse.viewmodels.SearchViewModel;

public class SearchFragment extends Fragment {

    private SearchViewModel viewModel;
    private RecyclerView recyclerView;
    private PaperAdapter paperAdapter;
    private ProgressBar progressBar;
    private TextView messageTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.progress_bar);
        messageTextView = view.findViewById(R.id.text_message);
        recyclerView = view.findViewById(R.id.recycler_view_search_results);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize adapter with an empty list
        paperAdapter = new PaperAdapter(new java.util.ArrayList<>(), getContext());
        recyclerView.setAdapter(paperAdapter);

        viewModel = new ViewModelProvider(requireActivity()).get(SearchViewModel.class);

        observeSearchState();
    }

    private void observeSearchState() {
        viewModel.getSearchState().observe(getViewLifecycleOwner(), state -> {
            if (state instanceof SearchState.Idle) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                messageTextView.setVisibility(View.VISIBLE);
                messageTextView.setText("Type in the search bar to find papers.");
            } else if (state instanceof SearchState.Loading) {
                progressBar.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                messageTextView.setVisibility(View.GONE);
            } else if (state instanceof SearchState.Success) {
                progressBar.setVisibility(View.GONE);
                messageTextView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                paperAdapter.setPapers(((SearchState.Success) state).getPapers());
            } else if (state instanceof SearchState.Empty) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                messageTextView.setVisibility(View.VISIBLE);
                messageTextView.setText("No papers found for your query.");
            } else if (state instanceof SearchState.Error) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                messageTextView.setVisibility(View.VISIBLE);
                messageTextView.setText("Error: " + ((SearchState.Error) state).getMessage());
            }
        });
    }
}
