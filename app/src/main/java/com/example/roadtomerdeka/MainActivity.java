package com.example.roadtomerdeka;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextPaint;
import android.util.Log;
import android.widget.TextView;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword, inputPassword2, inputUsername;
    private Button buttonRegister;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private DatabaseReference quizzesRef;
    private TextView signInLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI components
        inputEmail = findViewById(R.id.input_email);
        inputPassword = findViewById(R.id.input_password);
        inputPassword2 = findViewById(R.id.input_password2);
        inputUsername = findViewById(R.id.input_username);
        buttonRegister = findViewById(R.id.register_button);
        signInLink = findViewById(R.id.sign_in_link);
        progressBar = findViewById(R.id.registration_progress_bar);

        // Initialize Firebase Database References
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        quizzesRef = FirebaseDatabase.getInstance().getReference("quizzes");

        // Make "Sign In" clickable
        String fullText = "Already have an account? Sign In";
        SpannableString spannableString = new SpannableString(fullText);
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setUnderlineText(false);
                ds.setColor(getResources().getColor(R.color.blue));
            }
        }, fullText.indexOf("Sign In"), fullText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        signInLink.setText(spannableString);
        signInLink.setMovementMethod(LinkMovementMethod.getInstance());

        // Register button click listener
        buttonRegister.setOnClickListener(v -> {
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();
            String confirmPassword = inputPassword2.getText().toString().trim();
            String username = inputUsername.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                inputEmail.setError("Email is required!");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                inputPassword.setError("Password is required!");
                return;
            }
            if (password.length() < 6) {
                inputPassword.setError("Password must be at least 6 characters!");
                Toast.makeText(this, "Password must be at least 6 characters!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(confirmPassword)) {
                inputPassword2.setError("Confirm Password is required!");
                Toast.makeText(this, "Confirm Password is required!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(confirmPassword)) {
                inputPassword2.setError("Passwords do not match!");
                Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();

                return;
            }
            if (TextUtils.isEmpty(username)) {
                inputUsername.setError("Username is required!");
                Toast.makeText(this, "Username is required!", Toast.LENGTH_SHORT).show();
                return;
            }

            registerUser(email, password, username);
        });
    }

    private void registerUser(String email, String password, String username) {
        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();

                            // Save user data and initialize progress
                            saveUserData(userId, username, email, password);

                            // Redirect to LoginActivity after successful registration
                            Toast.makeText(MainActivity.this, "Registration successful! Please log in.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);

                            // Apply the slide-in-right animation
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

                            // Finish the current activity so the user can't go back here
                            finish();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserData(String userId, String username, String email, String password) {
        Users user = new Users(userId, username, email, password);
        databaseReference.child(userId).setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                initializeUserQuizProgress(userId);
            } else {
                Toast.makeText(MainActivity.this, "Failed to save user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeUserQuizProgress(String userId) {
        DatabaseReference userProgressRef = FirebaseDatabase.getInstance()
                .getReference("user_progress")
                .child(userId)
                .child("quiz_status");

        quizzesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                Map<String, Object> quizProgress = new HashMap<>();
                boolean firstQuiz = true;

                for (DataSnapshot quizSnapshot : task.getResult().getChildren()) {
                    String quizKey = quizSnapshot.getKey();

                    // Initialize quiz progress
                    boolean finalFirstQuiz = firstQuiz;
                    quizProgress.put(quizKey, new HashMap<String, Object>() {{
                        put("completed", false);
                        put("locked", !finalFirstQuiz);
                        put("best_score", 0); // Add best_score to user_progress
                        put("attempts", new HashMap<String, Object>()); // Add attempts to user_progress
                    }});

                    firstQuiz = false; // Only the first quiz will be unlocked
                }

                // Save quiz progress
                userProgressRef.setValue(quizProgress).addOnCompleteListener(progressTask -> {
                    if (progressTask.isSuccessful()) {
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to initialize quiz progress: " + progressTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(MainActivity.this, "Failed to fetch quizzes: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Initialize chapter_status
        DatabaseReference chapterStatusRef = FirebaseDatabase.getInstance()
                .getReference("user_progress")
                .child(userId)
                .child("chapter_status");

        DatabaseReference chaptersRef = FirebaseDatabase.getInstance().getReference("chapters");
        chaptersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                Map<String, Object> chapterProgress = new HashMap<>();
                boolean firstChapter = true;

                for (DataSnapshot chapterSnapshot : task.getResult().getChildren()) {
                    String chapterKey = chapterSnapshot.getKey();

                    // Initialize chapter progress
                    boolean finalFirstChapter = firstChapter;
                    chapterProgress.put(chapterKey, new HashMap<String, Object>() {{
                        put("locked", !finalFirstChapter); // Unlock the first chapter
                    }});

                    firstChapter = false; // Lock subsequent chapters
                }

                chapterStatusRef.setValue(chapterProgress).addOnCompleteListener(progressTask -> {
                    if (progressTask.isSuccessful()) {
                    } else {
                        Log.e("FirebaseWrite", "Failed to initialize chapter progress: " + progressTask.getException().getMessage());
                    }
                });
            } else {
                Log.e("FirebaseRead", "Failed to fetch chapters: " + (task.getException() != null ? task.getException().getMessage() : "No data exists"));
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

}
