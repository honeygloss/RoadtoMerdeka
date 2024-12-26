package com.example.roadtomerdeka;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Random;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> {

    private final List<Chapter> chapterList;
    private Context context;
    private int[] imageResources = {
            R.drawable.abdulrahman, // Replace with your actual drawable resource names
            R.drawable.tugu,
            R.drawable.jebat,
            R.drawable.bendera,
    };
    private int[] paletteColors;

    public ChapterAdapter(List<Chapter> chapterList) {
        this.chapterList = chapterList;
        initializeColors(); // Initialize colors
    }

    // Initialize colors from color resources
    private void initializeColors() {
        paletteColors = new int[] {
                context.getResources().getColor(R.color.palette_color_1),
                context.getResources().getColor(R.color.palette_color_2),
                context.getResources().getColor(R.color.palette_color_3),
                context.getResources().getColor(R.color.palette_color_4),
        };
    }

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chapter, parent, false);
        return new ChapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        Chapter chapter = chapterList.get(position);

        holder.chapterTitle.setText(chapter.getTitle());
        holder.chapterDesc.setText(chapter.getDescription());
        holder.chapterImage.setImageResource(chapter.getImageResId()); // Set image resource from the Chapter object

        // Set random background color for the chapter content container
        int randomColor = paletteColors[new Random().nextInt(paletteColors.length)];
        holder.chapterContent.setBackgroundColor(randomColor);

        // Handle lock state
        if (chapter.isLocked()) {
            holder.lockIcon.setVisibility(View.VISIBLE);
            holder.blurOverlay.setVisibility(View.VISIBLE);
            holder.chapterContent.setEnabled(false);
        } else {
            holder.lockIcon.setVisibility(View.GONE);
            holder.blurOverlay.setVisibility(View.GONE);
            holder.chapterContent.setEnabled(true);
        }
        // Set click listener for chapter content to navigate to the respective fragment
        holder.chapterContent.setOnClickListener(v -> {
            int chapterId = chapter.getId(); // Assuming each chapter has an ID
            navigateToChapterFragment(chapterId);
        });
    }
    private void navigateToChapterFragment(int chapterId) {
        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Replace the current fragment with the ChapterFragment
        ChapterFragment chapterFragment = ChapterFragment.newInstance(chapterId);
        transaction.replace(R.id.fragment_container, chapterFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    @Override
    public int getItemCount() {
        return chapterList.size();
    }

    public static class ChapterViewHolder extends RecyclerView.ViewHolder {

        LinearLayout chapterContent;
        ImageView lockIcon;
        View blurOverlay;
        ImageView chapterImage; // ImageView for the image
        TextView chapterTitle, chapterDesc;
        CardView cardView;

        public ChapterViewHolder(@NonNull View itemView) {
            super(itemView);
            chapterContent = itemView.findViewById(R.id.chapter_content);
            chapterTitle = itemView.findViewById(R.id.text_chapter_title);
            chapterDesc = itemView.findViewById(R.id.text_chapter_desc);
            chapterImage = itemView.findViewById(R.id.image_array); // Use the ImageView ID from your layout
            cardView = itemView.findViewById(R.id.card_view); // Ensure this has the correct ID
            lockIcon = itemView.findViewById(R.id.image_chapter_lock);
            blurOverlay = itemView.findViewById(R.id.blur_overlay);
        }
    }
}
