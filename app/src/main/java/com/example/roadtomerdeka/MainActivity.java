package com.example.roadtomerdeka;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextPaint;
import android.widget.TextView;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword, inputPassword2, inputUsername;
    private Button buttonRegister;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private DatabaseReference databaseReference;
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
        buttonRegister = findViewById(R.id.button);
        progressBar = findViewById(R.id.progressBar);
        signInLink = findViewById(R.id.sign_in_link);

        // Initialize Firebase Database Reference
        db = FirebaseDatabase.getInstance("https://roadtomerdeka-6a28d-default-rtdb.asia-southeast1.firebasedatabase.app/");
        databaseReference = db.getReference("users");

        // Full text
        String fullText = "Already have an account? Sign In";

        // Find the start and end indices of "Sign In"
        int startIndex = fullText.indexOf("Sign In");
        int endIndex = startIndex + "Sign In".length();

        // Create a SpannableString
        SpannableString spannableString = new SpannableString(fullText);
        // Make "Sign In" clickable
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                // Redirect to Login Activity
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.blue)); // Optional: set color
                ds.setUnderlineText(false); // Optional: remove underline
            }
        };

        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Set the spannable text on the TextView
        signInLink.setText(spannableString);
        signInLink.setMovementMethod(LinkMovementMethod.getInstance());


        // Register button click listener
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    return;
                }

                if (TextUtils.isEmpty(confirmPassword)) {
                    inputPassword2.setError("Confirm Password is required!");
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    inputPassword2.setError("Passwords do not match!");
                    return;
                }

                if (TextUtils.isEmpty(username)) {
                    inputUsername.setError("Username is required!");
                    return;
                }

                registerUser(email, password, username);
            }
        });
    }

    private void registerUser(String username, String email, String password) {
        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        // Get the current user
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();

                            // Store user data in Realtime Database
                            saveUserData(userId, username, email, password);

                            // Navigate to the  Login Page
                            Toast.makeText(MainActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            finish();
                        }
                    } else {
                        // Handle failure
                        Toast.makeText(MainActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserData(String userId, String username, String email, String password) {
        // Create a User object
        Users user = new Users(userId, username, email, password);

        // Save the user object in the database
        databaseReference.child(userId).setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(MainActivity.this, "User data saved to Firebase!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Failed to save user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



    }

}

