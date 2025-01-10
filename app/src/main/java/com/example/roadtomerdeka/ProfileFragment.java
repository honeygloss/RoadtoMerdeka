package com.example.roadtomerdeka;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
    private ImageButton editButton;
    private Button saveButton;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

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
        valueName = view.findViewById(R.id.value_name);
        inputEmail = view.findViewById(R.id.input_email);
        inputPassword = view.findViewById(R.id.input_password);
        editButton = view.findViewById(R.id.edit_button);
        saveButton = view.findViewById(R.id.save_button);

        // Set initial state to non-editable
        setEditable(false);

        // Load user data
        loadUserData();

        // Edit button toggle logic
        editButton.setOnClickListener(v -> {
            boolean isEditable = valueName.isEnabled();
            setEditable(!isEditable);
            saveButton.setVisibility(isEditable ? View.GONE : View.VISIBLE);
        });

        // Save button logic
        saveButton.setOnClickListener(v -> {
            if (validateInput()) {
                saveProfileData();
                setEditable(false);
                saveButton.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    // Fetches and displays the logged-in user's data
    private void loadUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Display basic data from FirebaseAuth
            inputEmail.setText(currentUser.getEmail());

            // Fetch additional data from the database
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("username").getValue(String.class);
                        String password = snapshot.child("password").getValue(String.class); // Assuming passwords are stored (not recommended)

                        valueName.setText(name);
                        inputPassword.setText("********"); // Mask the password for security
                    } else {
                        Toast.makeText(getActivity(), "No additional profile data found!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("ProfileFragment", "Database error: " + error.getMessage());
                }
            });
        }
    }

    // Enables or disables editing of profile fields
    private void setEditable(boolean isEditable) {
        valueName.setEnabled(isEditable);
        inputEmail.setEnabled(false); // Email should not be editable
        inputPassword.setEnabled(isEditable);
    }

    // Saves updated profile data to Firebase
    private void saveProfileData() {
        String name = valueName.getText().toString();
        String password = inputPassword.getText().toString();

        // Update the data in the database
        userRef.child("username").setValue(name);
        userRef.child("password").setValue(password);

        Log.d("ProfileFragment", "Profile updated: Name=" + name + ", Password=****");
    }

    // Validates the input fields
    private boolean validateInput() {
        String name = valueName.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (name.isEmpty()) {
            valueName.setError("Name is required!");
            return false;
        }

        if (password.isEmpty()) {
            inputPassword.setError("Password is required!");
            return false;
        }

        return true;
    }
}
