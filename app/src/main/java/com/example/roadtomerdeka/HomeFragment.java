package com.example.roadtomerdeka;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class HomeFragment extends Fragment {

    private TextView leftBox;
    private TextView rightBox;

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

        // Initialize views
        leftBox = view.findViewById(R.id.left_box);
        rightBox = view.findViewById(R.id.right_box);

        // Set onClickListeners
        leftBox.setOnClickListener(v -> {
            // Navigate to ChaptersFragment
            if (getActivity() != null) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, new ChaptersFragment());
                transaction.addToBackStack(null); // Optional: Allows back navigation
                transaction.commit();
            }
        });

        rightBox.setOnClickListener(v -> {
            // Navigate to QuizzesFragment
            if (getActivity() != null) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, new QuizzesFragment());
                transaction.addToBackStack(null); // Optional: Allows back navigation
                transaction.commit();
            }
        });
    }
}
