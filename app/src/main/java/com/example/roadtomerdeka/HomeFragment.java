package com.example.roadtomerdeka;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.github.furkankaplan.fkblurview.FKBlurView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeFragment extends Fragment {

    private TextView welcomeText, scoresText, quizNameText;
    private ProgressBar progressBar;
    private View chapterRedirect, quizRedirect;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef, quizzesRef;

    public HomeFragment() {
        // Required empty public constructor
    }
    FKBlurView glass;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Use the 'view' parameter to find the view
        glass = view.findViewById(R.id.glass);

        if (glass != null) {
            // Ensure the method is called with the correct context
            glass.setBlur(requireActivity(), glass);
        }
        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(getContext(), "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        quizzesRef = FirebaseDatabase.getInstance().getReference("quizzes");

        // Initialize views
        welcomeText = view.findViewById(R.id.welcome_text);
        scoresText = view.findViewById(R.id.scores);
        quizNameText = view.findViewById(R.id.quiz_name);
        progressBar = view.findViewById(R.id.progress_bar);
        chapterRedirect = view.findViewById(R.id.chapter_redirect);
        quizRedirect = view.findViewById(R.id.quiz_redirect);

        // Play Now Button
        Button playNowButton = view.findViewById(R.id.play_now_button);
        playNowButton.setOnClickListener(v -> navigateToFragment(new ChaptersFragment()));

        // Fetch user data
        fetchUserData(userId);

        // Set click listeners for navigation
        chapterRedirect.setOnClickListener(v -> navigateToFragment(new ChaptersFragment()));
        quizRedirect.setOnClickListener(v -> navigateToFragment(new QuizzesFragment()));
    }

    private void fetchUserData(String userId) {
        // Fetch user name
        userRef.child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = snapshot.getValue(String.class);
                if (username != null) {
                    welcomeText.setText("Welcome back, " + username);
                } else {
                    welcomeText.setText("Welcome back, User");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to fetch username!", Toast.LENGTH_SHORT).show();
            }
        });

        // Fetch total best scores and calculate progress
        DatabaseReference quizScoresRef = FirebaseDatabase.getInstance().getReference("quiz_scores").child(userId);
        quizScoresRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalBestScores = 0;
                int totalPossibleScores = 600; // Fixed base total marks for all quizzes

                for (DataSnapshot quizSnapshot : snapshot.getChildren()) {
                    // Iterate over quiz keys to access "best_score"
                    Integer bestScore = quizSnapshot.child("best_score").getValue(Integer.class);
                    if (bestScore != null) {
                        totalBestScores += bestScore; // Sum up best scores
                    }
                }

                // Display total best scores
                scoresText.setText(totalBestScores + " scores");

                int progress = (totalBestScores * 100) / totalPossibleScores;
                if (progress > 100) progress = 100; // Cap progress at 1 if it exceeds the total possible score

                progressBar.setProgress(progress);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to fetch scores!", Toast.LENGTH_SHORT).show();
            }
        });

        // Fetch quiz status
        DatabaseReference userProgressRef = FirebaseDatabase.getInstance()
                .getReference("user_progress")
                .child(userId)
                .child("quiz_status");

        userProgressRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int chapterNumber = 1; // Start with Chapter 1
                    int latestUnlockedChapter = -1; // Track the latest unlocked chapter
                    for (DataSnapshot quizSnapshot : snapshot.getChildren()) {
                        Boolean locked = quizSnapshot.child("locked").getValue(Boolean.class);

                        // Debugging Logs
                        System.out.println("Chapter: " + chapterNumber + ", Locked: " + locked);

                        // Check if the quiz is unlocked
                        if (locked != null && !locked) {
                            latestUnlockedChapter = chapterNumber;
                        }

                        chapterNumber++;
                    }

                    // Display the latest unlocked quiz
                    if (latestUnlockedChapter != -1) {
                        quizNameText.setText("Chapter " + latestUnlockedChapter);
                    } else {
                        quizNameText.setText("No unlocked quiz");
                    }
                } else {
                    quizNameText.setText("No unlocked quiz");
                    System.out.println("No quiz status data found for user: " + userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to fetch quiz status!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void navigateToFragment(Fragment fragment) {
        if (getActivity() != null) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
}
