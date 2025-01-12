package com.example.roadtomerdeka;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

        // Fetch scores and progress
        userRef.child("quiz_scores").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalScores = 0;
                int totalPossibleScores = 0;

                for (DataSnapshot quizSnapshot : snapshot.getChildren()) {
                    int score = quizSnapshot.child("score").getValue(Integer.class);
                    int maxScore = quizSnapshot.child("max_score").getValue(Integer.class);
                    totalScores += score;
                    totalPossibleScores += maxScore;
                }

                scoresText.setText(totalScores + " scores");
                if (totalPossibleScores > 0) {
                    int progress = (totalScores * 100) / totalPossibleScores;
                    progressBar.setProgress(progress);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to fetch scores!", Toast.LENGTH_SHORT).show();
            }
        });

        // Fetch current quiz name
        userRef.child("quiz_status")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            int chapterNumber = 1; // Start with Chapter 1
                            boolean foundCurrentQuiz = false;

                            for (DataSnapshot quizSnapshot : snapshot.getChildren()) {
                                Boolean locked = quizSnapshot.child("locked").getValue(Boolean.class);
                                Boolean completed = quizSnapshot.child("completed").getValue(Boolean.class);

                                // Check if the quiz is unlocked and not completed
                                if (locked != null && !locked && completed != null && !completed) {
                                    quizNameText.setText("Chapter " + chapterNumber);
                                    foundCurrentQuiz = true;
                                    break; // Stop after finding the first matching quiz
                                }

                                // Increment the chapter number for the next quiz
                                chapterNumber++;
                            }

                            if (!foundCurrentQuiz) {
                                quizNameText.setText("No unlocked quiz");
                            }
                        } else {
                            quizNameText.setText("No unlocked quiz");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to fetch quiz!", Toast.LENGTH_SHORT).show();
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
