package com.example.labverse.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.labverse.R;
import com.example.labverse.models.ReadingStatus;
import com.example.labverse.models.SearchFilters;
import com.example.labverse.viewmodels.SearchViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.HashSet;
import java.util.Set;

public class FilterBottomSheetDialog extends BottomSheetDialogFragment {

    private SearchViewModel searchViewModel;

    private AutoCompleteTextView etAuthor, etJournal;
    private EditText etYear;
    private ChipGroup chipGroupStatus;
    private Chip chipUnread, chipReading, chipFinished;
    private Button btnApplyFilters, btnClearFilters;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_bottom_sheet_filter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchViewModel = new ViewModelProvider(requireActivity()).get(SearchViewModel.class);

        // Find views
        etAuthor = view.findViewById(R.id.et_author);
        etJournal = view.findViewById(R.id.et_journal);
        etYear = view.findViewById(R.id.et_year);
        chipGroupStatus = view.findViewById(R.id.chip_group_status);
        chipUnread = view.findViewById(R.id.chip_unread);
        chipReading = view.findViewById(R.id.chip_reading);
        chipFinished = view.findViewById(R.id.chip_finished);
        btnApplyFilters = view.findViewById(R.id.btn_apply_filters);
        btnClearFilters = view.findViewById(R.id.btn_clear_filters);

        // Allow multiple chips to be selected
        chipGroupStatus.setSingleSelection(false);

        // Load current filters into the UI
        loadCurrentFilters();

        // Setup autocomplete adapters
        setupAutocomplete();

        // Set click listeners
        btnApplyFilters.setOnClickListener(v -> applyFilters());
        btnClearFilters.setOnClickListener(v -> clearFilters());
    }

    private void setupAutocomplete() {
        searchViewModel.getAllAuthors().observe(getViewLifecycleOwner(), authors -> {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_dropdown_item_1line, authors);
            etAuthor.setAdapter(adapter);
        });

        searchViewModel.getAllJournals().observe(getViewLifecycleOwner(), journals -> {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_dropdown_item_1line, journals);
            etJournal.setAdapter(adapter);
        });
    }

    private void loadCurrentFilters() {
        SearchFilters currentFilters = searchViewModel.getActiveFilters().getValue();
        if (currentFilters == null) return;

        if (!currentFilters.getAuthors().isEmpty()) {
            etAuthor.setText(String.join(", ", currentFilters.getAuthors()));
        }
        if (!currentFilters.getJournals().isEmpty()) {
            etJournal.setText(String.join(", ", currentFilters.getJournals()));
        }
        if (currentFilters.getYear() != null) {
            etYear.setText(String.valueOf(currentFilters.getYear()));
        }

        for (ReadingStatus status : currentFilters.getReadingStatus()) {
            if (status == ReadingStatus.UNREAD) chipUnread.setChecked(true);
            if (status == ReadingStatus.READING) chipReading.setChecked(true);
            if (status == ReadingStatus.FINISHED) chipFinished.setChecked(true);
        }
    }

    private void applyFilters() {
        Set<String> authors = new HashSet<>();
        if (etAuthor.getText() != null && !etAuthor.getText().toString().trim().isEmpty()) {
            authors.add(etAuthor.getText().toString().trim());
        }

        Set<String> journals = new HashSet<>();
        if (etJournal.getText() != null && !etJournal.getText().toString().trim().isEmpty()) {
            journals.add(etJournal.getText().toString().trim());
        }

        Integer year = (etYear.getText() == null || etYear.getText().toString().isEmpty()) ? null : Integer.parseInt(etYear.getText().toString());

        Set<ReadingStatus> statuses = new HashSet<>();
        if (chipUnread.isChecked()) statuses.add(ReadingStatus.UNREAD);
        if (chipReading.isChecked()) statuses.add(ReadingStatus.READING);
        if (chipFinished.isChecked()) statuses.add(ReadingStatus.FINISHED);

        SearchFilters newFilters = new SearchFilters(authors, journals, new HashSet<>(), year, statuses);
        searchViewModel.updateFilters(newFilters);

        dismiss();
    }

    private void clearFilters() {
        etAuthor.setText("");
        etJournal.setText("");
        etYear.setText("");
        chipGroupStatus.clearCheck();
        searchViewModel.clearAllFilters();
        dismiss();
    }
}
