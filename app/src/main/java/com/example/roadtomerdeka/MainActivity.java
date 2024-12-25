package com.example.roadtomerdeka;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        TextView signInLink = findViewById(R.id.sign_in_link);

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


    }
}