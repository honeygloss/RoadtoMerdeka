package com.example.roadtomerdeka;

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

public class ChaptersFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChapterAdapter chapterAdapter;
    private List<Chapter> chapterList = new ArrayList<>();
    private DatabaseReference chaptersRef, userProgressRef;
    private String userId;
    private Map<String, Boolean> userCompletedChapters = new HashMap<>();

    private static final String TAG = "ChaptersFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chapters, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_chapters);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Get current user ID
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Initialize Firebase references
        chaptersRef = FirebaseDatabase.getInstance().getReference("chapters");
        userProgressRef = FirebaseDatabase.getInstance().getReference("user_progress").child(userId).child("chapter_status");

        // Fetch user progress first
        fetchUserProgress();

        return view;
    }

    private void fetchUserProgress() {
        userProgressRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userCompletedChapters.clear();
                Log.d(TAG, "Fetching user progress...");
                for (DataSnapshot chapterSnapshot : snapshot.getChildren()) {
                    String chapterKey = chapterSnapshot.getKey();
                    Boolean locked = chapterSnapshot.child("locked").getValue(Boolean.class);

                    // Log the locked status for each chapter
                    Log.d(TAG, "Chapter ID: " + chapterKey + ", Locked: " + locked);

                    userCompletedChapters.put(chapterKey, locked != null ? locked : true); // Default locked if not found
                }

                // Fetch chapters after getting user progress
                loadChapters();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to fetch user progress: " + error.getMessage());
                Toast.makeText(getContext(), "Failed to fetch user progress: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadChapters() {
        chaptersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chapterList.clear();
                Log.d(TAG, "Fetching chapters...");
                for (DataSnapshot chapterSnapshot : snapshot.getChildren()) {
                    Chapter chapter = chapterSnapshot.getValue(Chapter.class);
                    if (chapter != null) {
                        chapter.setId(chapterSnapshot.getKey());
                        chapterList.add(chapter);

                        // Log the chapter data
                        Log.d(TAG, "Loaded Chapter: " + chapter.getTitle() + ", ID: " + chapter.getId());
                    } else {
                        Log.e(TAG, "Chapter data is null for one of the entries.");
                    }
                }

                // Log user progress map to confirm locked/unlocked statuses
                Log.d(TAG, "User Progress Map: " + userCompletedChapters);

                chapterAdapter = new ChapterAdapter(chapterList, getParentFragmentManager(), getContext(), userCompletedChapters);
                recyclerView.setAdapter(chapterAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load chapters: " + error.getMessage());
                Toast.makeText(getContext(), "Failed to load chapters: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshChapterState();
    }

    private void refreshChapterState() {
        // Fetch updated user progress
        userProgressRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userCompletedChapters.clear();
                Log.d(TAG, "Refreshing user progress...");
                for (DataSnapshot chapterSnapshot : snapshot.getChildren()) {
                    String chapterKey = chapterSnapshot.getKey();
                    Boolean locked = chapterSnapshot.child("locked").getValue(Boolean.class);

                    // Log the locked status for each chapter
                    Log.d(TAG, "Chapter ID: " + chapterKey + ", Locked: " + locked);

                    userCompletedChapters.put(chapterKey, locked != null ? locked : true); // Default locked if not found
                }

                // Reload chapters after refreshing progress
                loadChapters();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to fetch user progress: " + error.getMessage());
                Toast.makeText(getContext(), "Failed to fetch user progress: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
