package com.example.labverse.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.labverse.R;
import com.example.labverse.activities.PaperDetailActivity;
import com.example.labverse.database.LabVerseDatabase;
import com.example.labverse.database.dao.PaperDao;
import com.example.labverse.database.entities.PaperEntity;
import com.example.labverse.models.Paper;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PaperAdapter extends RecyclerView.Adapter<PaperAdapter.PaperViewHolder> {

    private List<Paper> paperList;
    private Context context;
    private PaperDao paperDao;

    public PaperAdapter(List<Paper> paperList, Context context) {
        this.paperList = paperList;
        this.context = context;
        this.paperDao = LabVerseDatabase.getDatabase(context).paperDao();
    }

    public void setPapers(List<Paper> newPaperList) {
        this.paperList.clear();
        this.paperList.addAll(newPaperList);
        notifyDataSetChanged(); // This tells the adapter to refresh the view
    }

    @NonNull
    @Override
    public PaperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_paper, parent, false);
        return new PaperViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaperViewHolder holder, int position) {
        Paper paper = paperList.get(position);

        holder.tvTitle.setText(paper.getTitle());
        holder.tvAuthors.setText(paper.getAuthors());
        holder.tvJournal.setText(paper.getJournal() + " (" + paper.getYear() + ")");

        // Set status indicator
        setStatusIndicator(holder, paper.getStatus());

        // Set priority indicator
        setPriorityIndicator(holder, paper.getPriority());

        // Set favorite indicator
        updateFavoriteIcon(holder, paper.isFavorite());

        // Set date added
        if (paper.getDateAdded() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            holder.tvDateAdded.setText(sdf.format(new Date(paper.getDateAdded())));
        }

        // Set reading progress
        updateReadingProgress(holder, paper.getId());

        // Set click listener
        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PaperDetailActivity.class);
            intent.putExtra("paper", paper);
            context.startActivity(intent);
        });

        holder.ivFavorite.setOnClickListener(v -> {
            paper.setFavorite(!paper.isFavorite());
            updateFavoriteIcon(holder, paper.isFavorite());
            LabVerseDatabase.databaseWriteExecutor.execute(() -> {
                paperDao.updateFavorite(paper.getId(), paper.isFavorite());
            });
        });
    }

    private void updateFavoriteIcon(PaperViewHolder holder, boolean isFavorite) {
        if (isFavorite) {
            holder.ivFavorite.setImageResource(R.drawable.ic_favorite);
            holder.ivFavorite.setColorFilter(ContextCompat.getColor(context, R.color.red));
        } else {
            holder.ivFavorite.setImageResource(R.drawable.ic_favorite_border);
            holder.ivFavorite.setColorFilter(ContextCompat.getColor(context, R.color.grey));
        }
    }

    private void setStatusIndicator(PaperViewHolder holder, String status) {
        if (status == null) {
            status = "unknown"; // Default to unknown if status is null
        }
        switch (status.toLowerCase()) {
            case "unread":
                holder.viewStatusIndicator.setBackgroundColor(ContextCompat.getColor(context, R.color.status_unread));
                holder.tvStatus.setText("Unread");
                holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_unread));
                break;
            case "reading":
                holder.viewStatusIndicator.setBackgroundColor(ContextCompat.getColor(context, R.color.status_reading));
                holder.tvStatus.setText("Reading");
                holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_reading));
                break;
            case "finished":
                holder.viewStatusIndicator.setBackgroundColor(ContextCompat.getColor(context, R.color.status_finished));
                holder.tvStatus.setText("Finished");
                holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_finished));
                break;
            default:
                holder.viewStatusIndicator.setBackgroundColor(ContextCompat.getColor(context, R.color.status_unread));
                holder.tvStatus.setText("Unknown");
                holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_unread));
                break;
        }
    }

    private void setPriorityIndicator(PaperViewHolder holder, String priority) {
        if (priority != null) {
            switch (priority.toLowerCase()) {
                case "high":
                    holder.tvPriority.setVisibility(View.VISIBLE);
                    holder.tvPriority.setText("HIGH");
                    holder.tvPriority.setBackgroundColor(ContextCompat.getColor(context, R.color.priority_high));
                    break;
                case "medium":
                    holder.tvPriority.setVisibility(View.VISIBLE);
                    holder.tvPriority.setText("MED");
                    holder.tvPriority.setBackgroundColor(ContextCompat.getColor(context, R.color.priority_medium));
                    break;
                case "low":
                    holder.tvPriority.setVisibility(View.VISIBLE);
                    holder.tvPriority.setText("LOW");
                    holder.tvPriority.setBackgroundColor(ContextCompat.getColor(context, R.color.priority_low));
                    break;
                default:
                    holder.tvPriority.setVisibility(View.GONE);
                    break;
            }
        } else {
            holder.tvPriority.setVisibility(View.GONE);
        }
    }

    private void updateReadingProgress(PaperViewHolder holder, String paperId) {
        // Load paper entity to get progress information
        LabVerseDatabase.databaseWriteExecutor.execute(() -> {
            PaperEntity paperEntity = paperDao.getPaperByIdSync(paperId);
            if (paperEntity != null && paperEntity.totalPages > 0 && paperEntity.currentPage > 0) {
                int currentPage = paperEntity.currentPage;
                int totalPages = paperEntity.totalPages;
                int progress = (int) ((currentPage / (float) totalPages) * 100);

                // Update UI on main thread
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    holder.layoutProgress.setVisibility(View.VISIBLE);
                    holder.progressBar.setMax(100);
                    holder.progressBar.setProgress(progress);
                    holder.tvProgressPercent.setText(progress + "%");
                    holder.tvProgressText.setText("Page " + currentPage + " of " + totalPages);
                });
            } else {
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    holder.layoutProgress.setVisibility(View.GONE);
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return paperList.size();
    }

    public static class PaperViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvTitle, tvAuthors, tvJournal, tvStatus, tvPriority, tvDateAdded;
        TextView tvProgressPercent, tvProgressText;
        ImageView ivFavorite;
        View viewStatusIndicator;
        LinearLayout layoutProgress;
        ProgressBar progressBar;

        public PaperViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvAuthors = itemView.findViewById(R.id.tv_authors);
            tvJournal = itemView.findViewById(R.id.tv_journal);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvPriority = itemView.findViewById(R.id.tv_priority);
            tvDateAdded = itemView.findViewById(R.id.tv_date_added);
            ivFavorite = itemView.findViewById(R.id.iv_favorite);
            viewStatusIndicator = itemView.findViewById(R.id.view_status_indicator);
            layoutProgress = itemView.findViewById(R.id.layout_progress);
            progressBar = itemView.findViewById(R.id.progress_bar_reading);
            tvProgressPercent = itemView.findViewById(R.id.tv_progress_percent);
            tvProgressText = itemView.findViewById(R.id.tv_progress_text);
        }
    }
}
