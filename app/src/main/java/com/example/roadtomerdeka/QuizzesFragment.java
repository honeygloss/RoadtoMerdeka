package com.example.roadtomerdeka;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizzesFragment extends Fragment {

    private RecyclerView recyclerView;
    private QuizAdapter quizAdapter;
    private List<Quiz> quizList = new ArrayList<>();
    private DatabaseReference quizzesRef, userProgressRef;
    private String userId;
    private Map<String, Boolean> userCompletedQuizzes = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_quizzes, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_quizzes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Get current user ID
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Initialize Firebase references
        quizzesRef = FirebaseDatabase.getInstance().getReference("quizzes");
        userProgressRef = FirebaseDatabase.getInstance().getReference("user_progress").child(userId).child("quiz_status");

        // Fetch user progress first
        userProgressRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot quizSnapshot : snapshot.getChildren()) {
                    String quizKey = quizSnapshot.getKey();
                    boolean completed = quizSnapshot.child("completed").getValue(Boolean.class);
                    userCompletedQuizzes.put(quizKey, completed);
                }

                // Fetch quizzes after getting user progress
                loadQuizzes();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Failed to fetch user progress: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void loadQuizzes() {
        quizzesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                quizList.clear();
                for (DataSnapshot quizSnapshot : snapshot.getChildren()) {
                    Quiz quiz = quizSnapshot.getValue(Quiz.class);
                    quiz.setQuizId(quizSnapshot.getKey());
                    quizList.add(quiz);
                }

                quizAdapter = new QuizAdapter(getContext(), quizList, userCompletedQuizzes, (quiz, isLocked) -> {
                    if (!isLocked) {
                        // Open the quiz
                        Log.d("QuizzesFragment", "Opening quiz with quizId: " + quiz.getQuizId());
                        Intent intent = new Intent(getContext(), EachQuizFragment.class);
                        intent.putExtra("quizId", quiz.getQuizId());
                        startActivity(intent);
                    }

                    else {
                        Toast.makeText(getContext(), "This quiz is locked!", Toast.LENGTH_SHORT).show();
                    }

                });

                recyclerView.setAdapter(quizAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load quizzes: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshQuizState();
    }

    private void refreshQuizState() {
        // Fetch updated user progress
        userProgressRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                userCompletedQuizzes.clear();
                for (DataSnapshot quizSnapshot : snapshot.getChildren()) {
                    String quizKey = quizSnapshot.getKey();
                    boolean completed = quizSnapshot.child("completed").getValue(Boolean.class);
                    userCompletedQuizzes.put(quizKey, completed);
                }

                // Reload quizzes after refreshing progress
                quizzesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        quizList.clear();
                        for (DataSnapshot quizSnapshot : snapshot.getChildren()) {
                            Quiz quiz = quizSnapshot.getValue(Quiz.class);
                            quiz.setQuizId(quizSnapshot.getKey());
                            quizList.add(quiz);
                        }

                        // Notify the adapter of data changes
                        if (quizAdapter != null) {
                            quizAdapter.notifyDataSetChanged();
                        } else {
                            quizAdapter = new QuizAdapter(getContext(), quizList, userCompletedQuizzes, (quiz, isLocked) -> {
                                if (!isLocked) {
                                    // Open the quiz
                                    Intent intent = new Intent(getContext(), EachQuizFragment.class);
                                    intent.putExtra("quizId", quiz.getQuizId());
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(getContext(), "This quiz is locked!", Toast.LENGTH_SHORT).show();
                                }
                            });
                            recyclerView.setAdapter(quizAdapter);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to load quizzes: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Failed to fetch user progress: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
