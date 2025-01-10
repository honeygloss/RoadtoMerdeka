package com.example.roadtomerdeka;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ImageView toggleIcon = findViewById(R.id.toggle_icon); // Custom toggle button

        // Access the header layout
        View headerView = navigationView.getHeaderView(0); // Get the first header view
        TextView usernameTextView = headerView.findViewById(R.id.username_text_view);

        // Retrieve the username from the Intent
        String username = getIntent().getStringExtra("username");
        if (username != null) {
            usernameTextView.setText(username);
        } else {
            usernameTextView.setText("Welcome, User!");
        }

        // Set NavigationView listener
        navigationView.setNavigationItemSelectedListener(this);

        // Handle custom toggle button clicks
        toggleIcon.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START); // Close drawer
            } else {
                drawerLayout.openDrawer(GravityCompat.START); // Open drawer
            }
        });

        // Load the default fragment (HomeFragment)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation menu item clicks
        Fragment selectedFragment = null;

        if (item.getItemId() == R.id.nav_home) {
            selectedFragment = new HomeFragment();
        } else if (item.getItemId() == R.id.nav_user) {
            selectedFragment = new ProfileFragment();
        } else if (item.getItemId() == R.id.nav_quizzes) {
            selectedFragment = new QuizzesFragment();
        } else if (item.getItemId() == R.id.nav_chapters) {
            selectedFragment = new ChaptersFragment();
        } else if (item.getItemId() == R.id.nav_logout) {
            handleLogout();
        } else if (item.getItemId() == R.id.nav_delete_account) {
            handleDeleteAccount();
        }

        if (selectedFragment != null) {
            replaceFragment(selectedFragment);
        }

        drawerLayout.closeDrawer(GravityCompat.START); // Close the drawer after selection
        return true;
    }

    @Override
    public void onBackPressed() {
        // Handle back press to close drawer if open
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    private void handleLogout() {
        // Sign out from Firebase
        FirebaseAuth.getInstance().signOut();

        // Notify the user
        Toast.makeText(this, "Logged out!", Toast.LENGTH_SHORT).show();

        // Redirect to LoginActivity
        Intent intent = new Intent(Home.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
        startActivity(intent);
        finish(); // Close the current activity
    }

    private void handleDeleteAccount() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();

            // Confirm account deletion
            Toast.makeText(this, "Deleting account...", Toast.LENGTH_SHORT).show();

            // Remove user data from both 'users' and 'user_progress' nodes
            databaseRef.child("users").child(userId).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    databaseRef.child("user_progress").child(userId).removeValue().addOnCompleteListener(progressTask -> {
                        if (progressTask.isSuccessful()) {
                            // Delete the user from Firebase Authentication
                            auth.getCurrentUser().delete().addOnCompleteListener(deleteTask -> {
                                if (deleteTask.isSuccessful()) {
                                    // Notify user of success
                                    Toast.makeText(this, "Account deleted successfully!", Toast.LENGTH_SHORT).show();

                                    // Redirect to LoginActivity
                                    Intent intent = new Intent(Home.this, LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish(); // Close the current activity
                                } else {
                                    // Handle errors during account deletion
                                    Toast.makeText(this, "Error: " + deleteTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            // Handle errors during 'user_progress' deletion
                            Toast.makeText(this, "Failed to delete user progress: " + progressTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    // Handle errors during 'users' deletion
                    Toast.makeText(this, "Failed to delete user data: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

}