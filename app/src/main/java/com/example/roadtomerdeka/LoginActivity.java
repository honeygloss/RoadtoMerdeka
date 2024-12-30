package com.example.roadtomerdeka;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        TextView signUpLink = findViewById(R.id.sign_up_link);
        Button loginButton = findViewById(R.id.login_button);

        // Full text
        String fullText = "Don't have an account? Sign Up";

        // Find the start and end indices of "Sign Up"
        int startIndex = fullText.indexOf("Sign Up");
        int endIndex = startIndex + "Sign Up".length();

        // Create a SpannableString
        SpannableString spannableString = new SpannableString(fullText);

        // Make "Sign Up" clickable
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                // Redirect to Sign Up Activity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class); // Change to appropriate Signup Activity
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
        signUpLink.setText(spannableString);
        signUpLink.setMovementMethod(LinkMovementMethod.getInstance());

        // Set the onClickListener for the Login Button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to Home Activity
                Intent intent = new Intent(LoginActivity.this, HomeFragment.class);
                startActivity(intent);
                finish(); // Optional: to close the LoginActivity
            }
        });
    }
}
