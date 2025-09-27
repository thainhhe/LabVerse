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
import androidx.recyclerview.widget.RecyclerView;
import com.example.labverse.R;
import com.example.labverse.activities.CollectionDetailActivity;
import com.example.labverse.models.Collection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.CollectionViewHolder> {

    private List<Collection> collectionList;
    private Context context;

    public CollectionAdapter(List<Collection> collectionList, Context context) {
        this.collectionList = collectionList;
        this.context = context;
    }

    @NonNull
    @Override
    public CollectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_collection, parent, false);
        return new CollectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionViewHolder holder, int position) {
        Collection collection = collectionList.get(position);

        holder.tvCollectionName.setText(collection.getName());
        holder.tvCollectionDescription.setText(collection.getDescription());
        holder.tvCreatedBy.setText("Created by " + collection.getCreatedBy());
        holder.tvPaperCount.setText(collection.getPaperCount() + " papers");

        // Set visibility indicator
        holder.ivVisibility.setImageResource(collection.isPublic() ?
                R.drawable.ic_public : R.drawable.ic_private);

        // Set date created
        if (collection.getDateCreated() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            holder.tvDateCreated.setText(sdf.format(new Date(collection.getDateCreated())));
        }

        // Set access level
        holder.tvAccessLevel.setText(collection.getAccessLevel().toUpperCase());

        // Set click listener
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CollectionDetailActivity.class);
                intent.putExtra("collection", collection);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return collectionList.size();
    }

    public static class CollectionViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvCollectionName, tvCollectionDescription, tvCreatedBy, tvPaperCount, tvDateCreated, tvAccessLevel;
        ImageView ivVisibility;

        public CollectionViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            tvCollectionName = itemView.findViewById(R.id.tv_collection_name);
            tvCollectionDescription = itemView.findViewById(R.id.tv_collection_description);
            tvCreatedBy = itemView.findViewById(R.id.tv_created_by);
            tvPaperCount = itemView.findViewById(R.id.tv_paper_count);
            tvDateCreated = itemView.findViewById(R.id.tv_date_created);
            tvAccessLevel = itemView.findViewById(R.id.tv_access_level);
            ivVisibility = itemView.findViewById(R.id.iv_visibility);
        }
    }
}
