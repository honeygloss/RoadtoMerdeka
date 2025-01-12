package com.example.roadtomerdeka;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.animation.ObjectAnimator;
import android.animation.ArgbEvaluator;
import android.content.res.ColorStateList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EachQuizFragment extends AppCompatActivity {

    private TextView questionNumber, scoreText, questionText, streakText, timerText;;
    private ProgressBar progressBar, loadingIndicator;
    private Button answer1, answer2, answer3, answer4;
    private List<Question> questionList = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int score = 0;
    private String quizId;
    private String userId;
    private DatabaseReference quizzesRef, userProgressRef;
    private int streakCount = 0;
    private int highestStreak = 0;
    private int basePoints = 10; // Base points for each correct answer
    private Handler timerHandler = new Handler(); // To update the timer
    private long quizStartTime; // To store the start time in milliseconds
    private Runnable timerRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_each_quiz);

        scoreText = findViewById(R.id.score);
        scoreText.setText("0");

        Log.d("EachQuizFragment", "onCreate: EachQuizFragment is launched successfully");

        // Initialize UI elements
        questionNumber = findViewById(R.id.question_number);
        scoreText = findViewById(R.id.score);
        streakText = findViewById(R.id.streak_text); // Add this in your layout XML
        questionText = findViewById(R.id.question_text);
        progressBar = findViewById(R.id.progressBar);
        loadingIndicator = findViewById(R.id.loading_indicator);
        answer1 = findViewById(R.id.answer1);
        answer2 = findViewById(R.id.answer2);
        answer3 = findViewById(R.id.answer3);
        answer4 = findViewById(R.id.answer4);

        streakText.setText("");

        // Get quizId from intent
        quizId = getIntent().getStringExtra("quizId");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Initialize Firebase references
        quizzesRef = FirebaseDatabase.getInstance().getReference("quizzes").child(quizId).child("questions").child("question1");
        userProgressRef = FirebaseDatabase.getInstance().getReference("user_progress").child(userId).child("quiz_status");

        showLoadingIndicator();

        // Find the timer TextView
        timerText = findViewById(R.id.timer_text);

        // Record the quiz start time
        quizStartTime = System.currentTimeMillis();

        // Initialize the timer Runnable
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                long elapsedTimeMillis = System.currentTimeMillis() - quizStartTime;
                int minutes = (int) (elapsedTimeMillis / 1000) / 60;
                int seconds = (int) (elapsedTimeMillis / 1000) % 60;

                // Update the timer TextView
                timerText.setText(String.format("%02d:%02d", minutes, seconds));

                // Post the Runnable again after 1 second
                timerHandler.postDelayed(this, 1000);
            }
        };

        // Start the timer
        timerHandler.post(timerRunnable);
        loadQuestions();

        // Set click listeners for answer buttons
        answer1.setOnClickListener(v -> checkAnswer("A"));
        answer2.setOnClickListener(v -> checkAnswer("B"));
        answer3.setOnClickListener(v -> checkAnswer("C"));
        answer4.setOnClickListener(v -> checkAnswer("D"));
    }

    private void loadQuestions() {
        resetButtonStyles();
        quizzesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                questionList.clear();
                for (DataSnapshot questionSnapshot : snapshot.getChildren()) {
                    Question question = questionSnapshot.getValue(Question.class);
                    questionList.add(question);
                }
                hideLoadingIndicator();
                if (questionList.isEmpty()) {
                    Toast.makeText(EachQuizFragment.this, "No questions found for this quiz.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    displayQuestion();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                hideLoadingIndicator();
                Toast.makeText(EachQuizFragment.this, "Failed to load questions: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetButtonStyles() {
        // Reset backgrounds
        answer1.setBackgroundResource(R.drawable.button_background_quiz);
        answer2.setBackgroundResource(R.drawable.button_background_quiz);
        answer3.setBackgroundResource(R.drawable.button_background_quiz);
        answer4.setBackgroundResource(R.drawable.button_background_quiz);

        // Reset text color
        int textColor = getResources().getColor(R.color.blue3);
        answer1.setTextColor(textColor);
        answer2.setTextColor(textColor);
        answer3.setTextColor(textColor);
        answer4.setTextColor(textColor);
    }


    private void displayQuestion() {
        resetButtonColors();
        if (currentQuestionIndex < questionList.size()) {
            Question currentQuestion = questionList.get(currentQuestionIndex);
            questionNumber.setText("Question "+ (currentQuestionIndex + 1) + " of " + questionList.size());
            questionText.setText(currentQuestion.getQuestion_text());
            answer1.setText(currentQuestion.getA());
            answer2.setText(currentQuestion.getB());
            answer3.setText(currentQuestion.getC());
            answer4.setText(currentQuestion.getD());
            // Animate progress bar
            int progress = (currentQuestionIndex * 100) / questionList.size();
            animateProgressBar(progress);
        } else {
            showResult();
        }
    }

    private void checkAnswer(String selectedOption) {
        Question currentQuestion = questionList.get(currentQuestionIndex);
        Button selectedButton = null;
        Button correctButton = null;

        if (selectedOption.equals("A")) selectedButton = answer1;
        else if (selectedOption.equals("B")) selectedButton = answer2;
        else if (selectedOption.equals("C")) selectedButton = answer3;
        else if (selectedOption.equals("D")) selectedButton = answer4;

        if (currentQuestion.getCorrect_ans().equals("A")) correctButton = answer1;
        else if (currentQuestion.getCorrect_ans().equals("B")) correctButton = answer2;
        else if (currentQuestion.getCorrect_ans().equals("C")) correctButton = answer3;
        else if (currentQuestion.getCorrect_ans().equals("D")) correctButton = answer4;

        if (currentQuestion.getCorrect_ans().equals(selectedOption)) {
            streakCount++;
            int bonusPoints = streakCount * 5;
            int questionPoints = basePoints + bonusPoints;
            score += questionPoints;

            if (streakCount > highestStreak) {
                highestStreak = streakCount;
            }

            playSound(R.raw.correct);
            playColorAnimation(selectedButton, getResources().getColor(R.color.white), getResources().getColor(R.color.green));

            Toast.makeText(this, "Correct! +" + questionPoints + " points! (Streak: " + streakCount + ")", Toast.LENGTH_SHORT).show();
        } else {
            streakCount = 0;
            playSound(R.raw.wrong);
            playColorAnimation(selectedButton, getResources().getColor(R.color.white), getResources().getColor(R.color.red));
            playColorAnimation(correctButton, getResources().getColor(R.color.white), getResources().getColor(R.color.green));

            Toast.makeText(this, "Incorrect! Streak reset.", Toast.LENGTH_SHORT).show();
        }
        if (score == 0){
            scoreText.setText("0");
        }
        else {
            scoreText.setText("+" + score);
        }
        updateStreakText();

        // Hide irrelevant buttons
        hideIrrelevantButtons(selectedButton, correctButton);

        disableButtons();
        new Handler().postDelayed(() -> {
            currentQuestionIndex += 1;
            enableButtons();
            displayQuestionWithTransition();
        }, 1500);
    }
    private void updateStreakText() {
        StringBuilder fireEmojis = new StringBuilder();
        for (int i = 0; i < streakCount; i++) {
            fireEmojis.append("🔥");
        }
        if (streakCount == 0){
            streakText.setText("");
        }
        else{
            streakText.setText("+ "+fireEmojis);
        }
    }


    private void playSound(int soundResId) {
        MediaPlayer mp = MediaPlayer.create(EachQuizFragment.this, soundResId);
        mp.setOnCompletionListener(MediaPlayer::release); // Automatically release after playing
        mp.setOnErrorListener((mediaPlayer, what, extra) -> {
            mediaPlayer.release(); // Release in case of errors
            return true;
        });
        mp.start();
    }


    private void playColorAnimation(Button button, int startColor, int endColor) {
        if (button != null) {
            ValueAnimator colorAnim = ValueAnimator.ofArgb(startColor, endColor);
            colorAnim.setDuration(500); // Duration in milliseconds
            colorAnim.addUpdateListener(animator -> {
                int animatedColor = (int) animator.getAnimatedValue();
                button.setBackgroundTintList(ColorStateList.valueOf(animatedColor));
            });
            colorAnim.start();
        }
    }

    private void hideIrrelevantButtons(Button selectedButton, Button correctButton) {
        if (answer1 != selectedButton && answer1 != correctButton) answer1.setVisibility(View.INVISIBLE);
        if (answer2 != selectedButton && answer2 != correctButton) answer2.setVisibility(View.INVISIBLE);
        if (answer3 != selectedButton && answer3 != correctButton) answer3.setVisibility(View.INVISIBLE);
        if (answer4 != selectedButton && answer4 != correctButton) answer4.setVisibility(View.INVISIBLE);
    }
    private void displayQuestionWithTransition() {
        View questionLayout = findViewById(R.id.question_layout);
        questionLayout.animate()
                .translationX(-questionLayout.getWidth())
                .alpha(0)
                .setDuration(300)
                .withEndAction(() -> {
                    if (currentQuestionIndex < questionList.size()) {
                        displayQuestion();
                        questionLayout.setTranslationX(questionLayout.getWidth());
                        questionLayout.animate()
                                .translationX(0)
                                .alpha(1)
                                .setDuration(300)
                                .start();
                    } else {
                        showResult();
                    }
                }).start();
    }
    private void resetButtonColors() {
        resetButton(answer1);
        resetButton(answer2);
        resetButton(answer3);
        resetButton(answer4);
    }

    private void resetButton(Button button) {
        button.setVisibility(View.VISIBLE);
        button.setBackgroundTintList(getResources().getColorStateList(R.color.white));
        button.setTranslationY(0);
    }

    private void disableButtons() {
        answer1.setEnabled(false);
        answer2.setEnabled(false);
        answer3.setEnabled(false);
        answer4.setEnabled(false);
    }

    private void enableButtons() {
        answer1.setEnabled(true);
        answer2.setEnabled(true);
        answer3.setEnabled(true);
        answer4.setEnabled(true);
    }

    private void animateProgressBar(int targetProgress) {
        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", progressBar.getProgress(), targetProgress);
        progressAnimator.setDuration(500);
        progressAnimator.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());
        progressAnimator.start();

        // Change color dynamically based on progress
        if (targetProgress < 50) {
            progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
        } else if (targetProgress < 80) {
            progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.orange)));
        } else {
            progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
        }
    }

    private void showResult() {
        // Stop the timer
        timerHandler.removeCallbacks(timerRunnable);

        // Calculate time taken
        long quizEndTime = System.currentTimeMillis();
        long timeTakenMillis = quizEndTime - quizStartTime;
        int minutes = (int) (timeTakenMillis / 1000) / 60;
        int seconds = (int) (timeTakenMillis / 1000) % 60;

        String timeTakenFormatted = String.format("%02d:%02d", minutes, seconds);

        // Mark quiz as completed in Firebase
        userProgressRef.child(quizId).child("completed").setValue(true);

        // Reference to the quiz scores for the current user and quiz
        DatabaseReference quizScoreRef = FirebaseDatabase.getInstance()
                .getReference("quiz_scores")
                .child(userId)
                .child(quizId);

        // Update best score, attempts, and time taken
        quizScoreRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int bestScore = 0;
                long attemptCount = 0;

                if (snapshot.exists()) {
                    // Retrieve existing best score
                    if (snapshot.child("best_score").exists()) {
                        bestScore = snapshot.child("best_score").getValue(Integer.class);
                    }

                    // Retrieve existing attempt count
                    if (snapshot.child("attempts").exists()) {
                        attemptCount = snapshot.child("attempts").getChildrenCount();
                    }
                }

                // Update best score if the current score is higher
                if (score >= bestScore) {
                    quizScoreRef.child("best_score").setValue(score);
                }

                // Add the current attempt to the database
                Map<String, Object> attemptData = new HashMap<>();
                attemptData.put("score", score);
                attemptData.put("timeTaken", timeTakenFormatted);

                quizScoreRef.child("attempts").child("attempt_" + (attemptCount + 1)).setValue(attemptData);

                // Pass data to the CompletedQuiz activity
                Intent intent = new Intent(EachQuizFragment.this, CompletedQuiz.class);
                intent.putExtra("score", score);
                intent.putExtra("totalQuestions", questionList.size());
                intent.putExtra("timeTaken", timeTakenFormatted);
                intent.putExtra("bestScore", Math.max(score, bestScore)); // Pass the updated best score
                startActivity(intent);

                // Close the current activity
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EachQuizFragment.this, "Failed to update score: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void showLoadingIndicator() {
        loadingIndicator.setVisibility(View.VISIBLE);
        findViewById(R.id.question_layout).setVisibility(View.GONE);
    }

    private void hideLoadingIndicator() {
        loadingIndicator.setVisibility(View.GONE);
        findViewById(R.id.question_layout).setVisibility(View.VISIBLE);
    }
}
