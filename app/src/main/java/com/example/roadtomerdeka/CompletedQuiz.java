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

public class CompletedQuiz extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView pointsEarnedText, bestScoreText, timeTakenText;
    private Button playAgainButton, continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_quiz);

        // Initialize Views
        ImageView confettiGif = findViewById(R.id.confetti_background);
        progressBar = findViewById(R.id.progressBar);
        pointsEarnedText = findViewById(R.id.points_earned_text);
        bestScoreText = findViewById(R.id.best_score_text);
        timeTakenText = findViewById(R.id.time_taken_text);
        playAgainButton = findViewById(R.id.play_again_button);
        continueButton = findViewById(R.id.continue_button);

        // Load Confetti Animation
        Glide.with(this)
                .asGif()
                .load(R.drawable.confetti) // Ensure the confetti GIF is in res/raw or drawable
                .into(confettiGif);

        // Get data from Intent
        int score = getIntent().getIntExtra("score", 0);
        int totalQuestions = getIntent().getIntExtra("totalQuestions", 0);
        String timeTaken = getIntent().getStringExtra("timeTaken");
        int bestScore = getIntent().getIntExtra("bestScore", 0);

        // Update Progress Bar
        progressBar.setProgress((score * 100) / totalQuestions);

        // Update Text Views
        pointsEarnedText.setText("Points Earned: " + score);
        bestScoreText.setText("Best Score: " + Math.max(score, bestScore)); // Compare current score with best
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
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
            finish();
        });

        // Hide Confetti after 5 seconds
        new Handler().postDelayed(() -> confettiGif.setVisibility(ImageView.GONE), 5000);
    }
}
