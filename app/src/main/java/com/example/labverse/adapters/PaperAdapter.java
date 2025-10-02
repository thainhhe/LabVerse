package com.example.labverse.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.labverse.R;
import com.example.labverse.activities.PaperDetailActivity;
import com.example.labverse.models.Paper;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PaperAdapter extends RecyclerView.Adapter<PaperAdapter.PaperViewHolder> {

    private List<Paper> paperList;
    private Context context;

    public PaperAdapter(List<Paper> paperList, Context context) {
        this.paperList = paperList;
        this.context = context;
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
        holder.ivFavorite.setVisibility(paper.isFavorite() ? View.VISIBLE : View.GONE);

        // Set date added
        if (paper.getDateAdded() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            holder.tvDateAdded.setText(sdf.format(new Date(paper.getDateAdded())));
        }

        // Set click listener
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PaperDetailActivity.class);
                intent.putExtra("paper", paper);
                context.startActivity(intent);
            }
        });
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

    @Override
    public int getItemCount() {
        return paperList.size();
    }

    public static class PaperViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvTitle, tvAuthors, tvJournal, tvStatus, tvPriority, tvDateAdded;
        ImageView ivFavorite;
        View viewStatusIndicator;

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
        }
    }
}
