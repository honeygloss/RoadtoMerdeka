package com.example.roadtomerdeka; //ChaptersFragment 3:37

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

    private static final String ARG_CHAPTER_ID = "chapter_id";
    private RecyclerView recyclerView;
    private ChapterAdapter chapterAdapter;
    private List<Chapter> chapterList;
    private FirebaseDatabase db;
    private DatabaseReference databaseReference, userProgressRef;
    private DataSnapshot snapshot;
    private String userId;
    private Map<String, Boolean> userProgress = new HashMap<>();


    public static ChaptersFragment newInstance(String chapterId) {
        ChaptersFragment fragment = new ChaptersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CHAPTER_ID, chapterId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chapters, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view_chapters);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize list and adapter
        chapterList = new ArrayList<>();
        chapterAdapter = new ChapterAdapter(chapterList, getParentFragmentManager(), requireContext(), userProgress);  // Pass user progress here
        recyclerView.setAdapter(chapterAdapter);

        // Get current user ID
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Initialize Firebase Database Reference
        db = FirebaseDatabase.getInstance("https://roadtomerdeka-6a28d-default-rtdb.asia-southeast1.firebasedatabase.app/");
        databaseReference = db.getReference("chapters");
        userProgressRef = db.getReference("user_progress").child(userId);  // Reference to track user's progress

        // Fetch user progress
        fetchUserProgress();

        // Fetch chapters
        fetchChapters();

        return view;
    }

    private void fetchUserProgress() {
        userProgressRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userProgress = new HashMap<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String chapterId = dataSnapshot.getKey();
                    Boolean isCompleted = dataSnapshot.getValue(Boolean.class);
                    if (chapterId != null && isCompleted != null) {
                        userProgress.put(chapterId, isCompleted);
                    }
                }
                // After loading user progress, notify adapter to refresh chapter lock state
                chapterAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ChaptersFragment", "Error fetching user progress: " + error.getMessage());
            }
        });
    }

    private void fetchChapters() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chapterList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chapter chapter = dataSnapshot.getValue(Chapter.class);
                    if (chapter != null) {
                        chapterList.add(chapter);
                        Log.d("ChaptersFragment", "Chapter loaded: " + chapter.getTitle());
                    } else {
                        Log.e("ChaptersFragment", "Chapter is null");
                    }
                }
                chapterAdapter.notifyDataSetChanged(); // Notify adapter after data has been updated
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ChaptersFragment", "Error fetching chapters: " + error.getMessage());
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve chapterId if passed as an argument
        if (getArguments() != null) {
            String chapterId = getArguments().getString(ARG_CHAPTER_ID);
            Log.d("ChaptersFragment", "Received chapterId: " + chapterId);
        }
    }
}

