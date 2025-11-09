package com.example.labverse.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labverse.R;
import com.example.labverse.activities.PaperDetailActivity;
import com.example.labverse.adapters.CitationAdapter;
import com.example.labverse.database.LabVerseDatabase;
import com.example.labverse.database.dao.CitationDao;
import com.example.labverse.database.dao.PaperDao;
import com.example.labverse.database.entities.CitationEntity;
import com.example.labverse.database.entities.PaperEntity;
import com.example.labverse.utils.CitationFormatter;

import java.util.List;

public class PaperCitationFragment extends Fragment {

    private static final String ARG_PAPER_ID = "paper_id";

    private String paperId;
    private PaperDao paperDao;
    private CitationDao citationDao;

    private TextView tvPaperCitation;
    private LinearLayout layoutCitationButtons;
    private Button btnCopyAPA, btnCopyMLA, btnCopyBibTeX;
    private RecyclerView recyclerViewCitations;
    private CitationAdapter citationAdapter;
    private ProgressBar progressBar;
    private TextView tvEmptyState;

    private PaperEntity paperEntity;

    public static PaperCitationFragment newInstance(String paperId) {
        PaperCitationFragment fragment = new PaperCitationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PAPER_ID, paperId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            paperId = getArguments().getString(ARG_PAPER_ID);
        }
        paperDao = LabVerseDatabase.getDatabase(requireContext()).paperDao();
        citationDao = LabVerseDatabase.getDatabase(requireContext()).citationDao();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_paper_citation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        loadPaperData();
        loadCitations();
        setupClickListeners();
    }

    private void initViews(View view) {
        tvPaperCitation = view.findViewById(R.id.tv_paper_citation);
        layoutCitationButtons = view.findViewById(R.id.layout_citation_buttons);
        btnCopyAPA = view.findViewById(R.id.btn_copy_apa);
        btnCopyMLA = view.findViewById(R.id.btn_copy_mla);
        btnCopyBibTeX = view.findViewById(R.id.btn_copy_bibtex);
        recyclerViewCitations = view.findViewById(R.id.recycler_view_citations);
        progressBar = view.findViewById(R.id.progress_bar);
        tvEmptyState = view.findViewById(R.id.tv_empty_state);

        recyclerViewCitations.setLayoutManager(new LinearLayoutManager(requireContext()));
        citationAdapter = new CitationAdapter(requireContext(), citationDao, paperId);
        recyclerViewCitations.setAdapter(citationAdapter);
    }

    private void loadPaperData() {
        LabVerseDatabase.databaseWriteExecutor.execute(() -> {
            paperEntity = paperDao.getPaperByIdSync(paperId);
            if (paperEntity != null) {
                requireActivity().runOnUiThread(() -> {
                    displayPaperCitation();
                });
            }
        });
    }

    private void displayPaperCitation() {
        if (paperEntity == null) {
            tvPaperCitation.setText("Paper information not available");
            layoutCitationButtons.setVisibility(View.GONE);
            return;
        }

        // Display paper citation in APA format by default
        String apaCitation = CitationFormatter.formatAPA(paperEntity);
        tvPaperCitation.setText(apaCitation);
        layoutCitationButtons.setVisibility(View.VISIBLE);
    }

    private void loadCitations() {
        citationDao.getCitationsByPaper(paperId).observe(getViewLifecycleOwner(), new Observer<List<CitationEntity>>() {
            @Override
            public void onChanged(List<CitationEntity> citations) {
                if (citations == null || citations.isEmpty()) {
                    recyclerViewCitations.setVisibility(View.GONE);
                    tvEmptyState.setVisibility(View.VISIBLE);
                    tvEmptyState.setText("No citations found. Citations will be extracted when PDF is processed.");
                } else {
                    recyclerViewCitations.setVisibility(View.VISIBLE);
                    tvEmptyState.setVisibility(View.GONE);
                    citationAdapter.setCitations(citations);
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void setupClickListeners() {
        btnCopyAPA.setOnClickListener(v -> {
            if (paperEntity != null) {
                String apaCitation = CitationFormatter.formatAPA(paperEntity);
                copyToClipboard(apaCitation, "APA Citation");
            } else {
                Toast.makeText(requireContext(), "Paper information not available", Toast.LENGTH_SHORT).show();
            }
        });

        btnCopyMLA.setOnClickListener(v -> {
            if (paperEntity != null) {
                String mlaCitation = CitationFormatter.formatMLA(paperEntity);
                copyToClipboard(mlaCitation, "MLA Citation");
            } else {
                Toast.makeText(requireContext(), "Paper information not available", Toast.LENGTH_SHORT).show();
            }
        });

        btnCopyBibTeX.setOnClickListener(v -> {
            if (paperEntity != null) {
                String bibtexCitation = CitationFormatter.formatBibTeX(paperEntity);
                copyToClipboard(bibtexCitation, "BibTeX Citation");
            } else {
                Toast.makeText(requireContext(), "Paper information not available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void copyToClipboard(String text, String label) {
        if (getActivity() instanceof PaperDetailActivity) {
            ((PaperDetailActivity) getActivity()).copyToClipboard(text, label);
        }
    }
}

