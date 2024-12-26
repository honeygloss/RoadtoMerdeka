package com.example.roadtomerdeka;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // Declare the EditText views, ImageButton (Edit), and Button (Save)
    private EditText valueName, inputEmail, inputPassword;
    private ImageButton editButton;
    private Button saveButton;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // You can get the arguments here if needed
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize the EditText fields, Edit button, and Save button
        valueName = view.findViewById(R.id.value_name);
        inputEmail = view.findViewById(R.id.input_email);
        inputPassword = view.findViewById(R.id.input_password);
        editButton = view.findViewById(R.id.edit_button);
        saveButton = view.findViewById(R.id.save_button);

        // Set the initial state to non-editable
        setEditable(false);

        // Set up the onClickListener for the Edit button
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle between editable and non-editable states
                boolean isEditable = valueName.isEnabled();
                setEditable(!isEditable);

                // Show or hide the Save button based on the edit state
                saveButton.setVisibility(isEditable ? View.GONE : View.VISIBLE);
            }
        });

        // Set up the onClickListener for the Save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Here you can save the data, for example, to a database or shared preferences
                // For now, just a log to show that the data is being saved
                saveProfileData();

                // After saving, disable editing and hide the Save button
                setEditable(false);
                saveButton.setVisibility(View.GONE);
            }
        });

        return view;
    }

    // Method to toggle editability of the EditText fields
    private void setEditable(boolean isEditable) {
        valueName.setEnabled(isEditable);
        inputEmail.setEnabled(isEditable);
        inputPassword.setEnabled(isEditable);
    }

    // Method to simulate saving the profile data (for example, to SharedPreferences or a database)
    private void saveProfileData() {
        // You can retrieve the values from the EditTexts and save them
        String name = valueName.getText().toString();
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        // Save the data (this is just a placeholder, you can implement actual saving logic)
        // For example, save to SharedPreferences or a database

        // Log the saved data (for testing purposes)
        System.out.println("Profile saved: Name=" + name + ", Email=" + email + ", Password=" + password);
    }
}
