package com.example.labverse.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.labverse.R;
import com.example.labverse.activities.PaperDetailActivity;
import com.example.labverse.activities.PdfReaderActivity;
import com.example.labverse.database.LabVerseDatabase;
import com.example.labverse.database.dao.PaperDao;
import com.example.labverse.database.entities.PaperEntity;
import com.example.labverse.models.Paper;
import com.example.labverse.utils.PaperMapper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PaperDetailsFragment extends Fragment {

    private static final String ARG_PAPER = "paper";

    private Paper paper;
    private PaperEntity paperEntity;
    private PaperDao paperDao;

    private TextView tvTitle, tvAuthors, tvJournalYear, tvAbstract, tvStatus, tvPriority;
    private TextView tvProgress, tvCurrentPage;
    private ProgressBar progressBar;
    private LinearLayout layoutStatusPriority;
    private Button btnReadPdf, btnCite;

    public static PaperDetailsFragment newInstance(Paper paper) {
        PaperDetailsFragment fragment = new PaperDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PAPER, paper);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            paper = (Paper) getArguments().getSerializable(ARG_PAPER);
        }
        paperDao = LabVerseDatabase.getDatabase(requireContext()).paperDao();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_paper_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        loadPaperData();
        setupClickListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload paper data when fragment resumes (e.g., after returning from PDF reader)
        loadPaperData();
    }

    private void initViews(View view) {
        tvTitle = view.findViewById(R.id.tv_paper_title);
        tvAuthors = view.findViewById(R.id.tv_authors);
        tvJournalYear = view.findViewById(R.id.tv_journal_year);
        tvAbstract = view.findViewById(R.id.tv_abstract);
        tvStatus = view.findViewById(R.id.tv_status);
        tvPriority = view.findViewById(R.id.tv_priority);
        tvProgress = view.findViewById(R.id.tv_progress);
        tvCurrentPage = view.findViewById(R.id.tv_current_page);
        progressBar = view.findViewById(R.id.progress_bar_reading);
        layoutStatusPriority = view.findViewById(R.id.layout_status_priority);
        btnReadPdf = view.findViewById(R.id.btn_read_pdf);
        btnCite = view.findViewById(R.id.btn_cite);
    }

    private void loadPaperData() {
        if (paper == null) return;

        // Load from database to get latest data
        LabVerseDatabase.databaseWriteExecutor.execute(() -> {
            paperEntity = paperDao.getPaperByIdSync(paper.getId());
            if (paperEntity != null) {
                requireActivity().runOnUiThread(() -> {
                    paper = PaperMapper.fromEntity(paperEntity);
                    updateUI();
                });
            } else {
                requireActivity().runOnUiThread(this::updateUI);
            }
        });
    }

    private void updateUI() {
        if (paper == null) return;

        // Title
        tvTitle.setText(paper.getTitle() != null ? paper.getTitle() : "Untitled");

        // Authors
        tvAuthors.setText(paper.getAuthors() != null ? paper.getAuthors() : "Unknown authors");

        // Journal and Year
        String journalYear = "";
        if (paper.getJournal() != null && !paper.getJournal().isEmpty()) {
            journalYear = paper.getJournal();
        }
        if (paper.getYear() != null && !paper.getYear().isEmpty()) {
            if (!journalYear.isEmpty()) {
                journalYear += " • ";
            }
            journalYear += paper.getYear();
        }
        tvJournalYear.setText(journalYear.isEmpty() ? "No journal information" : journalYear);

        // Abstract
        if (paper.getAbstractText() != null && !paper.getAbstractText().isEmpty()) {
            tvAbstract.setText(paper.getAbstractText());
        } else {
            tvAbstract.setText("No abstract available");
        }

        // Status and Priority
        updateStatusAndPriority();

        // Reading Progress
        updateReadingProgress();

    }

    private void updateStatusAndPriority() {
        String status = paper.getStatus() != null ? paper.getStatus() : "unread";
        String priority = paper.getPriority() != null ? paper.getPriority() : "medium";

        // Status
        switch (status.toLowerCase()) {
            case "unread":
                tvStatus.setText("Unread");
                tvStatus.setBackgroundResource(R.drawable.bg_status_tag);
                break;
            case "reading":
                tvStatus.setText("Reading");
                tvStatus.setBackgroundResource(R.drawable.bg_status_tag);
                break;
            case "finished":
                tvStatus.setText("Finished");
                tvStatus.setBackgroundResource(R.drawable.bg_status_tag);
                break;
            default:
                tvStatus.setText("Unknown");
                break;
        }

        // Priority
        switch (priority.toLowerCase()) {
            case "high":
                tvPriority.setText("HIGH");
                tvPriority.setVisibility(View.VISIBLE);
                tvPriority.setBackgroundResource(R.drawable.bg_priority_tag);
                break;
            case "medium":
                tvPriority.setText("MED");
                tvPriority.setVisibility(View.VISIBLE);
                tvPriority.setBackgroundResource(R.drawable.bg_priority_tag);
                break;
            case "low":
                tvPriority.setText("LOW");
                tvPriority.setVisibility(View.VISIBLE);
                tvPriority.setBackgroundResource(R.drawable.bg_priority_tag);
                break;
            default:
                tvPriority.setVisibility(View.GONE);
                break;
        }
    }

    private void updateReadingProgress() {
        if (paperEntity != null && paperEntity.totalPages > 0 && paperEntity.currentPage >= 0) {
            int currentPage = paperEntity.currentPage;
            int totalPages = paperEntity.totalPages;
            int progress = totalPages > 0 ? (int) ((currentPage / (float) totalPages) * 100) : 0;

            // Show progress section
            View progressSection = getView().findViewById(R.id.layout_progress);
            if (progressSection != null) {
                progressSection.setVisibility(View.VISIBLE);
            }
            
            progressBar.setMax(100);
            progressBar.setProgress(progress);
            progressBar.setVisibility(View.VISIBLE);
            tvProgress.setVisibility(View.VISIBLE);
            tvCurrentPage.setVisibility(View.VISIBLE);

            tvProgress.setText(progress + "%");
            tvCurrentPage.setText("Page " + currentPage + " of " + totalPages);
        } else {
            View progressSection = getView().findViewById(R.id.layout_progress);
            if (progressSection != null) {
                progressSection.setVisibility(View.GONE);
            }
            progressBar.setVisibility(View.GONE);
            tvProgress.setVisibility(View.GONE);
            tvCurrentPage.setVisibility(View.GONE);
        }
    }

    private void setupClickListeners() {
        btnReadPdf.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), PdfReaderActivity.class);
            intent.putExtra("paper", paper);
            startActivity(intent);
        });

        btnCite.setOnClickListener(v -> {
            // Switch to Citation tab
            if (getActivity() instanceof PaperDetailActivity) {
                PaperDetailActivity activity = (PaperDetailActivity) getActivity();
                activity.switchToCitationTab();
            }
        });
    }
}

