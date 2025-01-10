package com.example.roadtomerdeka;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {
    private Context context;
    private List<Quiz> quizList;
    private Map<String, Boolean> userCompletedQuizzes;


    public interface OnQuizClickListener {
        void onQuizClick(Quiz quiz, boolean isLocked);
    }

    private OnQuizClickListener listener;

    public QuizAdapter(Context context, List<Quiz> quizList, Map<String, Boolean> userCompletedQuizzes, OnQuizClickListener listener) {
        this.context = context;
        this.quizList = quizList;
        this.userCompletedQuizzes = userCompletedQuizzes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_quizzes, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        Quiz quiz = quizList.get(position);

        holder.textQuizHeader.setText("Quiz " + (position + 1));
        holder.textQuizTitle.setText(quiz.getTitle());
        holder.textChapterDesc.setText("Take your quiz");

        // Array of colors
        int[] colors = {
                ContextCompat.getColor(context, R.color.palette_color_1),
                ContextCompat.getColor(context, R.color.palette_color_2),
                ContextCompat.getColor(context, R.color.palette_color_3)
        };

        // Calculate the color index based on position
        int color = colors[position % colors.length];

        // Set the CardView background color
        holder.frameLayout.setBackgroundColor(color);


        // Determine if the quiz is locked
        boolean isLocked = false;
        if (position == 0) {
            // First quiz is always unlocked
            isLocked = false;
        } else {
            // Get the previous quiz ID to check its completion
            String previousQuizId = quizList.get(position - 1).getQuizId();
            isLocked = !userCompletedQuizzes.containsKey(previousQuizId) || !userCompletedQuizzes.get(previousQuizId);
        }

        if (isLocked) {
            // Lock the quiz
            holder.imageChapterLock.setVisibility(View.VISIBLE);
            holder.blurOverlay.setVisibility(View.VISIBLE);
            holder.imageArray.setVisibility(View.GONE);
        } else {
            holder.imageChapterLock.setVisibility(View.GONE);
            holder.blurOverlay.setVisibility(View.GONE);
            holder.imageArray.setVisibility(View.VISIBLE);
        }

        boolean finalIsLocked = isLocked;
        holder.cardView.setOnClickListener(v -> {
            listener.onQuizClick(quiz, finalIsLocked);
        });
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    public static class QuizViewHolder extends RecyclerView.ViewHolder {
        TextView textQuizHeader, textQuizTitle, textChapterDesc;
        ImageView imageChapterLock;
        View blurOverlay;
        CardView cardView;
        ImageView imageArray;
        FrameLayout frameLayout;


        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            textQuizHeader = itemView.findViewById(R.id.text_quiz_header);
            textQuizTitle = itemView.findViewById(R.id.text_Quiz_title);
            textChapterDesc = itemView.findViewById(R.id.text_chapter_desc);
            imageChapterLock = itemView.findViewById(R.id.image_chapter_lock);
            blurOverlay = itemView.findViewById(R.id.blur_overlay);
            cardView = itemView.findViewById(R.id.card_view);
            imageArray = itemView.findViewById(R.id.image_array);
            frameLayout = itemView.findViewById(R.id.frameLayout);

        }
    }
}
