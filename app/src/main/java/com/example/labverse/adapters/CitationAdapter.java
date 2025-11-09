package com.example.labverse.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labverse.R;
import com.example.labverse.database.dao.CitationDao;
import com.example.labverse.database.entities.CitationEntity;
import com.example.labverse.utils.CitationFormatter;

import java.util.ArrayList;
import java.util.List;

public class CitationAdapter extends RecyclerView.Adapter<CitationAdapter.CitationViewHolder> {

    private List<CitationEntity> citations;
    private Context context;
    private CitationDao citationDao;
    private String paperId;

    public CitationAdapter(Context context, CitationDao citationDao, String paperId) {
        this.context = context;
        this.citationDao = citationDao;
        this.paperId = paperId;
        this.citations = new ArrayList<>();
    }

    public void setCitations(List<CitationEntity> citations) {
        this.citations = citations;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CitationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_citation, parent, false);
        return new CitationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CitationViewHolder holder, int position) {
        CitationEntity citation = citations.get(position);
        
        // Display citation information
        StringBuilder citationText = new StringBuilder();
        
        if (citation.getAuthors() != null && !citation.getAuthors().isEmpty()) {
            citationText.append(citation.getAuthors());
        }
        
        if (citation.getYear() != null && !citation.getYear().isEmpty()) {
            if (citationText.length() > 0) {
                citationText.append(" (").append(citation.getYear()).append("). ");
            } else {
                citationText.append("(").append(citation.getYear()).append("). ");
            }
        }
        
        if (citation.getTitle() != null && !citation.getTitle().isEmpty()) {
            citationText.append(citation.getTitle());
            if (!citation.getTitle().endsWith(".")) {
                citationText.append(".");
            }
            citationText.append(" ");
        }
        
        if (citation.getJournal() != null && !citation.getJournal().isEmpty()) {
            citationText.append(citation.getJournal());
            if (!citation.getJournal().endsWith(".")) {
                citationText.append(".");
            }
        }
        
        holder.tvCitation.setText(citationText.toString().trim());
        holder.tvCitationNumber.setText(String.valueOf(citation.getCitationOrder()));

        // Setup copy buttons
        holder.btnCopyAPA.setOnClickListener(v -> {
            String apa = CitationFormatter.formatCitationAPA(citation);
            copyToClipboard(apa, "APA Citation");
        });

        holder.btnCopyMLA.setOnClickListener(v -> {
            String mla = CitationFormatter.formatCitationMLA(citation);
            copyToClipboard(mla, "MLA Citation");
        });

        holder.btnCopyBibTeX.setOnClickListener(v -> {
            String bibtex = CitationFormatter.formatCitationBibTeX(citation);
            copyToClipboard(bibtex, "BibTeX Citation");
        });
    }

    @Override
    public int getItemCount() {
        return citations.size();
    }

    private void copyToClipboard(String text, String label) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, label + " copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    static class CitationViewHolder extends RecyclerView.ViewHolder {
        TextView tvCitationNumber, tvCitation;
        Button btnCopyAPA, btnCopyMLA, btnCopyBibTeX;

        CitationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCitationNumber = itemView.findViewById(R.id.tv_citation_number);
            tvCitation = itemView.findViewById(R.id.tv_citation);
            btnCopyAPA = itemView.findViewById(R.id.btn_copy_apa);
            btnCopyMLA = itemView.findViewById(R.id.btn_copy_mla);
            btnCopyBibTeX = itemView.findViewById(R.id.btn_copy_bibtex);
        }
    }
}

