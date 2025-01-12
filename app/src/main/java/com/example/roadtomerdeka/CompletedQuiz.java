package com.example.roadtomerdeka;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CompletedQuiz extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView pointsEarnedText, bestScoreText, timeTakenText, attemptsText;
    private Button playAgainButton, continueButton;

    ImageView confettiGif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_quiz);

        // Initialize Views
        confettiGif = findViewById(R.id.confetti_background);
        progressBar = findViewById(R.id.progressBar);
        pointsEarnedText = findViewById(R.id.points_earned_text);
        bestScoreText = findViewById(R.id.best_score_text);
        timeTakenText = findViewById(R.id.time_taken_text);
        playAgainButton = findViewById(R.id.play_again_button);
        continueButton = findViewById(R.id.continue_button);

        // Load Confetti Animation
        Glide.with(this).load(R.drawable.confetti).into(confettiGif);

        // Get data from Intent
        int score = getIntent().getIntExtra("score", 0);
        int totalQuestions = getIntent().getIntExtra("totalQuestions", 0);
        String timeTaken = getIntent().getStringExtra("timeTaken");
        int bestScore = getIntent().getIntExtra("bestScore", 0);
        ArrayList<String> attempts = getIntent().getStringArrayListExtra("attempts");

        // Update Progress Bar
        progressBar.setProgress(score/totalQuestions*10);

        // Update Text Views
        pointsEarnedText.setText("Points Earned: " + score);
        bestScoreText.setText("Best Score: " + bestScore);
        timeTakenText.setText("Time Taken: " + timeTaken);


        // Play Again Button
        playAgainButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, EachQuizFragment.class);
            intent.putExtra("quizId", getIntent().getStringExtra("quizId")); // Pass the quiz ID if needed
            startActivity(intent);
            finish();
        });

        // Continue Journey Button
        continueButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChaptersFragment.class);
            startActivity(intent);
            finish();
        });

        // Hide Confetti after 5 seconds
        new Handler().postDelayed(() -> confettiGif.setVisibility(ImageView.GONE), 5000);
    }
}
