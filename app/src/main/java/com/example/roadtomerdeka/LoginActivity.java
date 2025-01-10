package com.example.roadtomerdeka;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText inputUsername;
    private EditText inputPassword;
    private Button loginButton;
    private TextView signUpLink;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        // 2) Link UI elements
        inputUsername = findViewById(R.id.input_username);
        inputPassword = findViewById(R.id.input_password);
        loginButton   = findViewById(R.id.login_button);
        signUpLink    = findViewById(R.id.sign_up_link);

        // 3) Create clickable Sign Up text
        String fullText = "Don't have an account? Sign Up";
        int startIndex  = fullText.indexOf("Sign Up");
        int endIndex    = startIndex + "Sign Up".length();
        SpannableString spannableString = new SpannableString(fullText);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                // Navigate to your Signup Activity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.blue));
                ds.setUnderlineText(false);
            }
        };
        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        signUpLink.setText(spannableString);
        signUpLink.setMovementMethod(LinkMovementMethod.getInstance());

        // 4) Login button click
        loginButton.setOnClickListener(v -> {
            // Grab username/email & password
            String email = inputUsername.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                inputUsername.setError("Email is required!");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                inputPassword.setError("Password is required!");
                return;
            }

            // 5) Attempt sign-in via Firebase Auth
            loginUser(email, password);
        });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Login successful
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();

                            // Retrieve username from Firebase Database
                            DatabaseReference userRef = FirebaseDatabase.getInstance()
                                    .getReference("users").child(userId);
                            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String username = snapshot.child("username").getValue(String.class);
                                    if (username == null) {
                                        username = "User"; // Fallback if username is not set
                                    }

                                    // Pass username to Home activity
                                    Toast.makeText(LoginActivity.this,
                                            "Login successful! Welcome " + username,
                                            Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(LoginActivity.this, Home.class);
                                    intent.putExtra("username", username); // Send username to Home
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(LoginActivity.this,
                                            "Failed to retrieve username: " + error.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } else {
                        // Login failed
                        String error = (task.getException() != null)
                                ? task.getException().getMessage()
                                : "Unknown error";

                        Toast.makeText(LoginActivity.this,
                                "Login failed: " + error,
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

}
