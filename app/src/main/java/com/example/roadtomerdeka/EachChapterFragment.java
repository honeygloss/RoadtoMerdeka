package com.example.roadtomerdeka;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EachChapterFragment extends AppCompatActivity {

    private static final String TAG = "EachChapterFragment";
    private String chapterId;
    private String userId;
    private ImageView chapterImage;
    private ImageView quizIcon;
    private TextView chapterTitle;
    private TextView chapterContent;
    private TextView takeQuizText;
    private ImageButton playAudioButton;

    private FirebaseDatabase database;
    private DatabaseReference chapterRef;
    private DatabaseReference userProgressRef;
    private FirebaseUser currentUser;

    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_each_chapter);

        // Retrieve the chapter ID from the Intent
        Intent intent = getIntent();
        chapterId = intent.getStringExtra("chapter_id");
        Log.d(TAG, "Retrieved chapterId: " + chapterId);

        /*if (chapterId == null || chapterId.isEmpty()) {
            Toast.makeText(this, "Invalid Chapter ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }*/

        // Initialize Firebase
        database = FirebaseDatabase.getInstance("https://roadtomerdeka-6a28d-default-rtdb.asia-southeast1.firebasedatabase.app/");
        chapterRef = database.getReference("chapters").child(chapterId);

        // Initialize UI components
        initializeUI();

        // Get the current user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "User is not authenticated!");
            Toast.makeText(this, "Please log in to access this feature.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        userId = currentUser.getUid();
        userProgressRef = database.getReference("user_progress").child(userId);

        // Load chapter data from Firebase
        fetchChapterData();

        // Handle audio playback
        playAudioButton.setOnClickListener(v -> handleAudioPlayback());

        // Handle "Take a Quiz" click action
        View.OnClickListener quizClickListener = v -> {
            Intent quizIntent = new Intent(this, QuizAdapter.class);
            quizIntent.putExtra("chapterId", chapterId);
            startActivity(quizIntent);
        };
        quizIcon.setOnClickListener(quizClickListener);
        takeQuizText.setOnClickListener(quizClickListener);
        updateChapterProgress();
    }

    private void initializeUI() {
        chapterImage = findViewById(R.id.chapter_image);
        chapterTitle = findViewById(R.id.chapter_title);
        chapterContent = findViewById(R.id.chapter_content);
        playAudioButton = findViewById(R.id.play_audio_button);
        quizIcon = findViewById(R.id.quiz_icon);
        takeQuizText = findViewById(R.id.take_quiz_text);
    }

    private void fetchChapterData() {
        chapterRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    parseChapterData(snapshot);
                } else {
                    Toast.makeText(EachChapterFragment.this, "Chapter data not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EachChapterFragment.this, "Failed to load chapter data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void parseChapterData(DataSnapshot snapshot) {
        String title = snapshot.child("title").getValue(String.class);
        String content = snapshot.child("content").getValue(String.class);
        String imageUrl = snapshot.child("imageChapter").getValue(String.class);
        String audioUrl = snapshot.child("audio").getValue(String.class);

        updateUI(title, content, imageUrl, audioUrl);
    }

    private void updateUI(String title, String content, String imageUrl, String audioUrl) {
        chapterTitle.setText(title != null ? title : getString(R.string.no_title));
        chapterContent.setText(content != null ? content : getString(R.string.no_content));

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(chapterImage);

        setupAudio(audioUrl);
    }

    private void setupAudio(String audioUrl) {
        if (audioUrl != null && !audioUrl.isEmpty()) {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(audioUrl);
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(mp -> playAudioButton.setEnabled(true));
                mediaPlayer.setOnCompletionListener(mp -> {
                    isPlaying = false;
                    playAudioButton.setImageResource(R.drawable.play_icon);
                });
            } catch (Exception e) {
                Toast.makeText(this, "Failed to prepare audio.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Audio error: " + e.getMessage());
            }
        } else {
            playAudioButton.setEnabled(false);
        }
    }

    private void handleAudioPlayback() {
        if (mediaPlayer != null) {
            if (isPlaying) {
                mediaPlayer.pause();
                isPlaying = false;
                playAudioButton.setImageResource(R.drawable.play_icon);
            } else {
                mediaPlayer.start();
                isPlaying = true;
                playAudioButton.setImageResource(R.drawable.pause_icon);
            }
        }
    }

    private void updateChapterProgress() {
        userProgressRef.child(chapterId).setValue(true)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Chapter progress updated"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to update chapter progress: " + e.getMessage()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && isPlaying) {
            mediaPlayer.pause();
        }
    }
}
