package com.example.roadtomerdeka;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;

public class CompletedQuiz extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView pointsEarnedText, bestScoreText, timeTakenText;
    private Button playAgainButton, continueButton;
    private ImageView confettiGif;

    private static final String TAG = "CompletedQuiz";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_quiz);

        playSound(R.raw.drumroll);

        // Initialize Views
        confettiGif = findViewById(R.id.confetti_background);
        progressBar = findViewById(R.id.progressBar);
        pointsEarnedText = findViewById(R.id.points_earned_text);
        bestScoreText = findViewById(R.id.best_score_text);
        timeTakenText = findViewById(R.id.time_taken_text);
        playAgainButton = findViewById(R.id.play_again_button);
        continueButton = findViewById(R.id.continue_button);

        // Retrieve data from Intent
        String quizId = getIntent().getStringExtra("quizId");
        int score = getIntent().getIntExtra("score", 0);
        int totalQuestions = getIntent().getIntExtra("totalQuestions", 0);
        String timeTaken = getIntent().getStringExtra("timeTaken");
        int bestScore = getIntent().getIntExtra("bestScore", 0);


        // Validate input
        if (quizId == null || quizId.isEmpty()) {
            Toast.makeText(this, "Error: Quiz ID is missing.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Quiz ID is null or empty.");
        }

        // Load Confetti Animation
        Glide.with(this).load(R.drawable.confetti).into(confettiGif);

        // Update Progress Bar
        if (totalQuestions > 0) {
            progressBar.setProgress((score * 100) / totalQuestions);
        } else {
            progressBar.setProgress(0);
        }

        // Update Text Views
        pointsEarnedText.setText("You've earned +" + score + " points!");
        bestScoreText.setText("Best Score: " + bestScore);
        timeTakenText.setText("Time Taken: " + timeTaken);

        // Play Again Button
        playAgainButton.setOnClickListener(v -> {
            if (quizId != null) {
                Intent intent = new Intent(this, EachQuizFragment.class);
                intent.putExtra("quizId", quizId); // Pass the quiz ID to retake the quiz
                startActivity(intent);
                finish();
            } else {
                Log.e(TAG, "Quiz ID is null.");
                Toast.makeText(this, "Error: Unable to retake the quiz.", Toast.LENGTH_SHORT).show();
            }
        });

        continueButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, Home.class); // Redirect to the Home activity
            startActivity(intent);
            finish(); // Finish the current activity to remove it from the back stack
        });





        // Hide Confetti after 5 seconds
        new Handler().postDelayed(() -> confettiGif.setVisibility(ImageView.GONE), 5000);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    private void playSound(int soundResId) {
        MediaPlayer mp = MediaPlayer.create(CompletedQuiz.this, soundResId);
        if (mp != null) {
            mp.setOnCompletionListener(mediaPlayer -> mediaPlayer.release()); // Release only
            mp.setOnErrorListener((mediaPlayer, what, extra) -> {
                mediaPlayer.release(); // Release in case of errors
                return true;
            });
            mp.start(); // Start the sound
        } else {
            Log.e(TAG, "Failed to create MediaPlayer for sound: " + soundResId);
        }
    }
}
