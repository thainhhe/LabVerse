package com.example.labverse.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.labverse.R;
import com.example.labverse.database.entities.PaperEntity; // Dùng Entity

import java.util.List;

public class SelectPaperAdapter extends RecyclerView.Adapter<SelectPaperAdapter.SelectViewHolder> {

    private List<PaperEntity> paperList;
    private final OnPaperSelectedListener clickListener;

    public interface OnPaperSelectedListener {
        void onPaperSelected(PaperEntity paper);
    }

    public SelectPaperAdapter(List<PaperEntity> paperList, OnPaperSelectedListener listener) {
        this.paperList = paperList;
        this.clickListener = listener;
    }

    public void setData(List<PaperEntity> newList) {
        this.paperList.clear();
        this.paperList.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SelectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_paper, parent, false);
        return new SelectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectViewHolder holder, int position) {
        PaperEntity paper = paperList.get(position);
        holder.bind(paper, clickListener);
    }

    @Override
    public int getItemCount() {
        return paperList.size();
    }

    static class SelectViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAuthors;

        public SelectViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_paper_title);
            tvAuthors = itemView.findViewById(R.id.tv_paper_authors);
        }

        public void bind(final PaperEntity paper, final OnPaperSelectedListener listener) {
            tvTitle.setText(paper.title);
            tvAuthors.setText(paper.authors);

            // Khi click, báo cho Activity biết
            itemView.setOnClickListener(v -> listener.onPaperSelected(paper));
        }
    }
}