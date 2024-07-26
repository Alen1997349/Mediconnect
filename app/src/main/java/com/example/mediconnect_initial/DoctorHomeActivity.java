package com.example.mediconnect_initial;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class DoctorHomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private String username; // Store username here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_home);

        // Receive username from intent
        username = getIntent().getStringExtra("username");

        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set Spannable Text as Toolbar Title
        getSupportActionBar().setDisplayShowTitleEnabled(false); // Hide default title
        TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getSpannableTitle("Mediconnect"));

        // Handle Toolbar Menu Icon Click
        ImageView menuIcon = toolbar.findViewById(R.id.toolbar_menu_icon);
        menuIcon.setOnClickListener(v -> showMenu());

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Set Navigation Item Click Listener
        navigationView.setNavigationItemSelectedListener(item -> {
            // Handle navigation view item clicks here
            int itemId = item.getItemId();
            if (itemId == R.id.action_home) {
                showToast("Home clicked");
                // Pass username to DoctorHomeActivity when navigating to itself
                Intent intent = new Intent(DoctorHomeActivity.this, DoctorHomeActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            } else if (itemId == R.id.action_profile) {
                showToast("Profile clicked");
                Intent intent = new Intent(DoctorHomeActivity.this, ProfileActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            } else if (itemId == R.id.action_edit_patient) {
                showToast("Edit Patient Details clicked");
            } else if (itemId == R.id.action_patient_records) {
                showToast("Patient Records clicked");
            } else if (itemId == R.id.action_pre_approval) {
                showToast("Patient Records clicked");
            } else if (itemId == R.id.action_logout) {
                showToast("Logout clicked");
                startActivity(new Intent(DoctorHomeActivity.this, MainActivity.class));
            }

            // Close the drawer
            drawerLayout.closeDrawers();

            return true;
        });
    }

    private SpannableString getSpannableTitle(String title) {
        SpannableString spannableString = new SpannableString(title);

        // Span for "Medi" (Green color)
        ForegroundColorSpan greenColorSpan = new ForegroundColorSpan(Color.GREEN);
        spannableString.setSpan(greenColorSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Span for "connect" (Blue color)
        ForegroundColorSpan blueColorSpan = new ForegroundColorSpan(Color.BLUE);
        spannableString.setSpan(blueColorSpan, 4, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 4, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle menu item clicks (if any)
        return super.onOptionsItemSelected(item);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showMenu() {
        drawerLayout.openDrawer(findViewById(R.id.nav_view));
    }
}
