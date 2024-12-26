package com.example.roadtomerdeka;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar); // Ignore red line errors
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this); // Set listener

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        /*if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }*/
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId(); // Get the ID of the selected menu item

        if (id == R.id.nav_home) {
            // Redirect to HomeActivity
            Intent intent = new Intent(Home.this, Home.class);
            startActivity(intent);
        } else if (id == R.id.nav_user) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ProfileFragment())
                    .commit();
        } else if (id == R.id.nav_quizzes) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new QuizzesFragment())
                    .commit();
        } else if (id == R.id.nav_chapters) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ChaptersFragment())
                    .commit();
        } else if (id == R.id.nav_logout) {
            Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show();
        }

        drawerLayout.closeDrawer(GravityCompat.START); // Close the drawer
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}

