package com.example.roadtomerdeka;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> {

    private final List<Chapter> chapters;
    private final OnChapterClickListener listener;

    public interface OnChapterClickListener {
        void onChapterClick(int position);
    }

    public ChapterAdapter(List<Chapter> chapters, OnChapterClickListener listener) {
        this.chapters = chapters;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chapter, parent, false);
        return new ChapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        Chapter chapter = chapters.get(position);
        holder.titleTextView.setText(chapter.getTitle());
        holder.lockImageView.setImageResource(chapter.isLocked() ? R.drawable.lock : R.drawable.unlock);

        holder.itemView.setOnClickListener(v -> {
            if (!chapter.isLocked()) {
                listener.onChapterClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chapters.size();
    }

    static class ChapterViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        ImageView lockImageView;

        public ChapterViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.text_chapter_title);
            lockImageView = itemView.findViewById(R.id.image_chapter_lock);
        }
    }
}


