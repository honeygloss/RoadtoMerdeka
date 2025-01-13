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
import java.util.List;

public class ChaptersFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChapterAdapter chapterAdapter;
    private List<Chapter> chapterList = new ArrayList<>();
    private DatabaseReference chaptersRef;
    private String userId;

    private static final String TAG = "ChaptersFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chapters, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_chapters);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Get current user ID
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Initialize Firebase reference
        chaptersRef = FirebaseDatabase.getInstance().getReference("chapters");

        // Fetch chapters
        loadChapters();

        return view;
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

                // Set the adapter without any lock functionality
                chapterAdapter = new ChapterAdapter(chapterList, getParentFragmentManager(), getContext(), null);
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
        // Reload chapters when the fragment resumes
        loadChapters();
    }
}
