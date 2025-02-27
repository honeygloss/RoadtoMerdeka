package com.example.roadtomerdeka; //ChapterAdapter 3:37pm 11/1/2024

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> {

    private final List<Chapter> chapterList;
    private FragmentManager fragmentManager;
    private Context context;
    private Map<String, Boolean> userCompletedChapters;
    private int[] imageResources = {
            R.drawable.abdulrahman, // Replace with your actual drawable resource names
            R.drawable.tugu,
            R.drawable.jebat,
            R.drawable.bendera,
    };

    public ChapterAdapter(List<Chapter> chapterList, FragmentManager fragmentManager, Context context, Map<String, Boolean> userCompletedChapters) {
        this.chapterList = chapterList;
        this.fragmentManager = fragmentManager;
        this.context = context;
        this.userCompletedChapters = userCompletedChapters;
    }


    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chapter, parent, false);
        return new ChapterViewHolder(view);
    }

    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        Chapter chapter = chapterList.get(position);

        // Debug logs to track chapter data
        Log.d("ChapterAdapter", "Chapter: " + chapter.getId());

        // Bind data to the view
        holder.bind(chapter);

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EachChapterFragment.class);
            intent.putExtra("chapter_id", chapter.getId());
            context.startActivity(intent);
        });

        // Set the background color
        int[] colors = {
                ContextCompat.getColor(context, R.color.palette_color_1),
                ContextCompat.getColor(context, R.color.palette_color_2),
                ContextCompat.getColor(context, R.color.palette_color_3)
        };
        int color = colors[position % colors.length];
        holder.frameLayout.setBackgroundColor(color);
    }

    @Override
    public int getItemCount() {
        return chapterList.size();
    }

    public static class ChapterViewHolder extends RecyclerView.ViewHolder {

        FrameLayout frameLayout;
        ImageView lockIcon;
        View blurOverlay;
        ImageView chapterImage;
        TextView chapterId, chapterTitle, chapterDesc, chapterHeader;
        CardView cardView;

        public ChapterViewHolder(@NonNull View itemView) {
            super(itemView);
            frameLayout = itemView.findViewById(R.id.frameLayout);
            chapterTitle = itemView.findViewById(R.id.text_chapter_title);
            chapterHeader = itemView.findViewById(R.id.text_chapter_header);
            chapterDesc = itemView.findViewById(R.id.text_chapter_desc);
            chapterImage = itemView.findViewById(R.id.image_array);
            cardView = itemView.findViewById(R.id.card_view);
            lockIcon = itemView.findViewById(R.id.image_chapter_lock);
            blurOverlay = itemView.findViewById(R.id.blur_overlay);
        }

        public void bind(Chapter chapter) {
            chapterTitle.setText(chapter.getTitle());
            chapterDesc.setText(chapter.getDescription());
            chapterHeader.setText(chapter.getChapterHeader());
            // Load the image from the URL using Glide
            Glide.with(itemView.getContext())
                    .load(chapter.getImageResId()) // URL from the database
                    .placeholder(R.drawable.placeholder_image) // Optional: Placeholder image while loading
                    .error(R.drawable.error_image) // Optional: Error image if the load fails
                    .into(chapterImage); // The ImageView to display the image
            // Use the method to get the correct image resource ID
            /*int imageResId = getDrawableResourceId(chapter.getId());

            // Set the image in the ImageView
            chapterImage.setImageResource(imageResId);*/
        }
    }
}

