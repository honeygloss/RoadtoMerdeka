package com.example.roadtomerdeka;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private EditText valueName, inputEmail, inputPassword;
    private TextView usernameText;
    private ImageButton editButton;
    private Button saveButton, cancelButton;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    private String actualPassword = ""; // Store the real password

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize FirebaseAuth and DatabaseReference
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            userRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(currentUser.getUid());
        }

        // Initialize UI components
        valueName = view.findViewById(R.id.value_name); // Username field
        inputEmail = view.findViewById(R.id.input_email);
        inputPassword = view.findViewById(R.id.input_password);
        usernameText = view.findViewById(R.id.username_text);
        editButton = view.findViewById(R.id.edit_button);
        saveButton = view.findViewById(R.id.save_button);
        cancelButton = view.findViewById(R.id.cancel_button);
        progressBar = view.findViewById(R.id.progress_bar);

        // Set initial state
        setEditable(false);
        saveButton.setVisibility(View.GONE);
        cancelButton.setVisibility(View.GONE);

        // Show progress bar until data loads
        progressBar.setVisibility(View.VISIBLE);
        view.findViewById(R.id.main_content).setVisibility(View.GONE);

        // Load user data
        loadUserData();

        // Edit button logic
        editButton.setOnClickListener(v -> {
            setEditable(true);
            inputPassword.setText(actualPassword); // Show actual password for editing
            editButton.setVisibility(View.GONE);
            saveButton.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.VISIBLE);
        });

        // Save button logic
        saveButton.setOnClickListener(v -> {
            if (validateInput()) {
                saveProfileData();
                setEditable(false);
                saveButton.setVisibility(View.GONE);
                cancelButton.setVisibility(View.GONE);
                editButton.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
            }
        });

        // Cancel button logic
        cancelButton.setOnClickListener(v -> {
            setEditable(false);
            loadUserData(); // Reload original data
            saveButton.setVisibility(View.GONE);
            cancelButton.setVisibility(View.GONE);
            editButton.setVisibility(View.VISIBLE);
        });

        return view;
    }

    // Fetches and displays the logged-in user's data
    private void loadUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String username = snapshot.child("username").getValue(String.class);
                        String password = snapshot.child("password").getValue(String.class);

                        if (username != null) {
                            usernameText.setText("@" + username); // Display username
                            valueName.setText(username); // Set in editable field
                        }

                        if (password != null) {
                            actualPassword = password; // Store the real password
                            inputPassword.setText(maskPassword(password.length())); // Display masked password
                        }

                        // Hide progress bar and show content
                        progressBar.setVisibility(View.GONE);
                        getView().findViewById(R.id.main_content).setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(getActivity(), "No profile data found.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("ProfileFragment", "Database error: " + error.getMessage());
                }
            });
        }
    }

    // Creates a masked password based on the length of the original password
    private String maskPassword(int length) {
        StringBuilder maskedPassword = new StringBuilder();
        for (int i = 0; i < length; i++) {
            maskedPassword.append("*");
        }
        return maskedPassword.toString();
    }

    // Enables or disables editing of profile fields
    private void setEditable(boolean isEditable) {
        valueName.setEnabled(isEditable);
        inputPassword.setEnabled(isEditable);
    }

    // Saves updated profile data to Firebase
    private void saveProfileData() {
        String username = valueName.getText().toString().trim();
        String password = inputPassword.getText().toString();

        userRef.child("username").setValue(username);
        userRef.child("password").setValue(password);

        usernameText.setText("@" + username);
        actualPassword = password; // Update the actual password
        inputPassword.setText(maskPassword(password.length())); // Reapply masking
    }

    // Validates the input fields
    private boolean validateInput() {
        String username = valueName.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (username.isEmpty()) {
            valueName.setError("Username is required!");
            return false;
        }

        if (password.isEmpty()) {
            inputPassword.setError("Password is required!");
            return false;
        }

        return true;
    }
}
