package com.example.roadtomerdeka;

import android.animation.ValueAnimator;
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
import java.util.List;

public class EachQuizFragment extends AppCompatActivity {

    private TextView questionNumber, scoreText, questionText;
    private ProgressBar progressBar, loadingIndicator;
    private Button answer1, answer2, answer3, answer4;
    private List<Question> questionList = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int score = 0;
    private String quizId;
    private String userId;
    private DatabaseReference quizzesRef, userProgressRef;

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
        questionText = findViewById(R.id.question_text);
        progressBar = findViewById(R.id.progressBar);
        loadingIndicator = findViewById(R.id.loading_indicator);
        answer1 = findViewById(R.id.answer1);
        answer2 = findViewById(R.id.answer2);
        answer3 = findViewById(R.id.answer3);
        answer4 = findViewById(R.id.answer4);

        // Get quizId from intent
        quizId = getIntent().getStringExtra("quizId");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Initialize Firebase references
        quizzesRef = FirebaseDatabase.getInstance().getReference("quizzes").child(quizId).child("questions").child("question1");
        userProgressRef = FirebaseDatabase.getInstance().getReference("user_progress").child(userId).child("quiz_status");

        showLoadingIndicator();
        loadQuestions();

        // Set click listeners for answer buttons
        answer1.setOnClickListener(v -> checkAnswer("A"));
        answer2.setOnClickListener(v -> checkAnswer("B"));
        answer3.setOnClickListener(v -> checkAnswer("C"));
        answer4.setOnClickListener(v -> checkAnswer("D"));
    }

    private void loadQuestions() {
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

    private void displayQuestion() {
        resetButtonColors();
        if (currentQuestionIndex < questionList.size()) {
            Question currentQuestion = questionList.get(currentQuestionIndex);
            questionNumber.setText((currentQuestionIndex + 1) + "/" + questionList.size());
            questionText.setText(currentQuestion.getQuestion_text());
            answer1.setText(currentQuestion.getA());
            answer2.setText(currentQuestion.getB());
            answer3.setText(currentQuestion.getC());
            answer4.setText(currentQuestion.getD());
            adjustButtonHeight(answer1);
            adjustButtonHeight(answer2);
            adjustButtonHeight(answer3);
            adjustButtonHeight(answer4);
            progressBar.setProgress((currentQuestionIndex * 100) / questionList.size());
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
            score += 1;
            playSound(R.raw.correct);
            playColorAnimation(selectedButton, getResources().getColor(R.color.orange), getResources().getColor(R.color.green));

        } else {
            playSound(R.raw.wrong);
            playColorAnimation(selectedButton, getResources().getColor(R.color.orange), getResources().getColor(R.color.red));
            playColorAnimation(correctButton, getResources().getColor(R.color.orange), getResources().getColor(R.color.green));
        }
        // Update score TextView
        scoreText.setText("Score: " + score);

        // Hide irrelevant buttons
        hideIrrelevantButtons(selectedButton, correctButton);

        disableButtons();
        new Handler().postDelayed(() -> {
            currentQuestionIndex += 1;
            enableButtons();
            displayQuestionWithTransition();
        }, 1500);
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

    private void adjustButtonHeight(Button button) {
        button.setMinHeight(0); // Adjust default button height
        button.setPadding(20, 30, 20, 30);
        button.setSingleLine(false); // Allow multiple lines for long text
    }

    private void resetButtonColors() {
        resetButton(answer1);
        resetButton(answer2);
        resetButton(answer3);
        resetButton(answer4);
    }

    private void resetButton(Button button) {
        button.setVisibility(View.VISIBLE);
        button.setBackgroundTintList(getResources().getColorStateList(R.color.orange));
        button.setTranslationY(0);
        adjustButtonHeight(button);
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

    private void showResult() {
        userProgressRef.child(quizId).child("completed").setValue(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quiz Completed");
        builder.setMessage("Your score: " + score + "/" + questionList.size());
        builder.setPositiveButton("OK", (dialog, which) -> finish());
        builder.show();
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
