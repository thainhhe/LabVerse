package com.example.labverse.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labverse.R;
import com.example.labverse.models.Paper; // <-- DÙNG MODEL CŨ
import com.google.android.material.chip.Chip;
import java.util.List;

public class CollectionPaperAdapter extends RecyclerView.Adapter<CollectionPaperAdapter.PaperViewHolder> {

    private List<Paper> paperList; // <-- Sửa
    private Context context;

    public interface OnPaperStatusClickListener {
        void onStatusClick(Paper paper); // <-- Sửa
    }
    private OnPaperStatusClickListener clickListener;

    public CollectionPaperAdapter(List<Paper> paperList, Context context, OnPaperStatusClickListener listener) {
        this.paperList = paperList;
        this.context = context;
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public PaperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_collection_paper, parent, false);
        return new PaperViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaperViewHolder holder, int position) {
        Paper paper = paperList.get(position);

        holder.tvTitle.setText(paper.getTitle());
        holder.tvAuthors.setText(paper.getAuthors());

        // --- Logic Mục 7: Status (ĐÃ SỬA ĐỂ DÙNG MÀU CỦA BẠN) ---
        if (paper.getStatus() != null) {
            switch (paper.getStatus().toLowerCase()) {
                case "reading":
                    holder.chipStatus.setText("Reading");
                    // Sửa tên màu:
                    holder.chipStatus.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.status_reading)));
                    break;
                case "finished":
                    holder.chipStatus.setText("Finished");
                    // Sửa tên màu:
                    holder.chipStatus.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.status_finished)));
                    break;
                case "unread":
                default:
                    holder.chipStatus.setText("To Read");
                    // Sửa tên màu:
                    holder.chipStatus.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.status_unread)));
                    break;
            }
        }

        // --- Logic Mục 7: Priority ---
        if (paper.getPriority() != null) {
            String priority = paper.getPriority().toLowerCase();
            holder.ivPriority.setVisibility(View.VISIBLE);

            switch (priority) {
                case "high":
                    holder.ivPriority.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.priority_high)));
                    break;
                case "medium":
                    holder.ivPriority.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.priority_medium)));
                    break;
                case "low":
                    holder.ivPriority.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.priority_low)));
                    break;
                default:
                    holder.ivPriority.setVisibility(View.GONE);
                    break;
            }
        } else {
            holder.ivPriority.setVisibility(View.GONE);
        }

        holder.chipStatus.setOnClickListener(v -> {
            clickListener.onStatusClick(paper);
        });
    }

    @Override
    public int getItemCount() {
        return paperList.size();
    }

    public static class PaperViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAuthors;
        ImageView ivPriority;
        Chip chipStatus;

        public PaperViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_paper_title);
            tvAuthors = itemView.findViewById(R.id.tv_paper_authors);
            ivPriority = itemView.findViewById(R.id.iv_priority);
            chipStatus = itemView.findViewById(R.id.chip_status);
        }
    }
}