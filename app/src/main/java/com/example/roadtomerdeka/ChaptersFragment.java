package com.example.roadtomerdeka;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
    private List<Chapter> chapterList;
    private DatabaseReference databaseReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("Chapters");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chapters, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_chapters);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        chapterList = new ArrayList<>();
        chapterAdapter = new ChapterAdapter(chapterList, position -> {
            // Handle chapter click logic
            if (!chapterList.get(position).isLocked()) {
                // Navigate to the corresponding chapter fragment if the chapter is not locked
                Navigation.findNavController(view).navigate(R.id.action_chaptersFragment_to_chapterDetailFragment);
            }
        });

        recyclerView.setAdapter(chapterAdapter);

        fetchChapters(); // Call method to fetch chapters from Firebase

        return view;
    }

    private void fetchChapters() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chapterList.clear(); // Clear existing data
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chapter chapter = dataSnapshot.getValue(Chapter.class);
                    if (chapter != null) {
                        chapterList.add(chapter);
                    }
                }
                chapterAdapter.notifyDataSetChanged(); // Notify the adapter
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database errors if any
            }
        });
    }
}

