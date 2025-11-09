package com.example.labverse.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labverse.R;

public class MockPdfAdapter extends RecyclerView.Adapter<MockPdfAdapter.PageViewHolder> {

    private int totalPages;
    private String paperTitle;

    public MockPdfAdapter(int totalPages, String paperTitle) {
        this.totalPages = totalPages;
        this.paperTitle = paperTitle != null ? paperTitle : "Paper";
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mock_pdf_page, parent, false);
        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        holder.bind(position + 1, totalPages, paperTitle);
    }

    @Override
    public int getItemCount() {
        return totalPages;
    }

    static class PageViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPageNumber;
        private TextView tvPaperTitle;
        private TextView tvContent;

        PageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPageNumber = itemView.findViewById(R.id.tv_page_number);
            tvPaperTitle = itemView.findViewById(R.id.tv_paper_title);
            tvContent = itemView.findViewById(R.id.tv_content);
        }

        void bind(int pageNumber, int totalPages, String paperTitle) {
            tvPageNumber.setText(String.format("Page %d of %d", pageNumber, totalPages));
            tvPaperTitle.setText(paperTitle);
            
            // Generate mock content
            StringBuilder content = new StringBuilder();
            content.append("This is a mock PDF page for testing purposes.\n\n");
            content.append("Page ").append(pageNumber).append(" content:\n\n");
            content.append("Lorem ipsum dolor sit amet, consectetur adipiscing elit. ");
            content.append("Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ");
            content.append("Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris.\n\n");
            content.append("Duis aute irure dolor in reprehenderit in voluptate velit esse ");
            content.append("cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat ");
            content.append("cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\n\n");
            content.append("Swipe left/right or use buttons to navigate between pages.\n\n");
            content.append("Your reading progress will be automatically saved.");
            
            tvContent.setText(content.toString());
        }
    }
}

