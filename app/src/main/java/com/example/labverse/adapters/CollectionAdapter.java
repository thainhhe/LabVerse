package com.example.labverse.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.labverse.R;
import com.example.labverse.activities.CollectionDetailActivity;
import com.example.labverse.models.Collection;
import java.util.List;

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
        View view = LayoutInflater.from(context).inflate(R.layout.item_collection, parent, false);
        return new CollectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionViewHolder holder, int position) {
        Collection collection = collectionList.get(position);

        holder.tvCollectionName.setText(collection.getName());
        holder.tvCollectionDescription.setText(collection.getDescription());

        String paperText = collection.getPaperCount() + " Papers";
        holder.tvPaperCount.setText(paperText);

        int memberCount = 0;
        if (collection.getMemberIds() != null) {
            memberCount = collection.getMemberIds().size();
        }
        String memberText = memberCount + " Members";
        holder.tvMemberCount.setText(memberText);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CollectionDetailActivity.class);

            intent.putExtra("COLLECTION_ID", collection.getId());
            intent.putExtra("COLLECTION_NAME", collection.getName());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return collectionList.size();
    }

    public static class CollectionViewHolder extends RecyclerView.ViewHolder {
        TextView tvCollectionName, tvCollectionDescription, tvPaperCount, tvMemberCount;

        public CollectionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCollectionName = itemView.findViewById(R.id.tv_collection_name);
            tvCollectionDescription = itemView.findViewById(R.id.tv_collection_description);
            tvPaperCount = itemView.findViewById(R.id.tv_paper_count);
            tvMemberCount = itemView.findViewById(R.id.tv_member_count);
        }
    }
}