package com.example.labverse.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.labverse.R;
import com.example.labverse.models.Collection; // Dùng POJO Model

import java.util.List;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.CollectionViewHolder> {

    private List<Collection> collectionList;
    private final OnCollectionClickListener clickListener;

    // Interface để gửi click về Fragment
    public interface OnCollectionClickListener {
        void onCollectionClick(Collection collection);
    }

    // Constructor (đã sửa)
    public CollectionAdapter(List<Collection> list, OnCollectionClickListener listener) {
        this.collectionList = list;
        this.clickListener = listener;
    }

    // Hàm "dễ" để cập nhật dữ liệu (giống code cũ của bạn)
    public void setData(List<Collection> newList) {
        this.collectionList.clear();
        this.collectionList.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CollectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // (Sửa R.layout.item_collection nếu file XML của bạn tên khác)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_collection, parent, false);
        return new CollectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionViewHolder holder, int position) {
        Collection collection = collectionList.get(position);
        holder.bind(collection, clickListener);
    }

    @Override
    public int getItemCount() {
        return collectionList.size();
    }

    // ViewHolder
    static class CollectionViewHolder extends RecyclerView.ViewHolder {
        // (Thay ID cho đúng với file item_collection.xml của bạn)
        TextView tvName, tvDescription, tvOwner, tvMemberCount, tvPaperCount;

        public CollectionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_collection_name);
            tvDescription = itemView.findViewById(R.id.tv_collection_description);
            tvOwner = itemView.findViewById(R.id.tv_collection_owner);
            tvMemberCount = itemView.findViewById(R.id.tv_member_count);
            tvPaperCount = itemView.findViewById(R.id.tv_paper_count);
        }

        public void bind(final Collection collection, final OnCollectionClickListener listener) {
            tvName.setText(collection.getName());
            tvDescription.setText(collection.getDescription());
            tvOwner.setText("by " + collection.getCreatedBy());

            if (collection.getMemberIds() != null) {
                tvMemberCount.setText(String.valueOf(collection.getMemberIds().size()));
            } else {
                tvMemberCount.setText("0");
            }
            tvPaperCount.setText(String.valueOf(collection.getPaperCount()));

            // Xử lý click
            itemView.setOnClickListener(v -> listener.onCollectionClick(collection));
        }
    }
}