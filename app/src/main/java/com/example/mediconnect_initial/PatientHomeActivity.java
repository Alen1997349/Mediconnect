package com.example.mediconnect_initial;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.mediconnect_initial.MainActivity;
import com.google.android.material.navigation.NavigationView;

public class PatientHomeActivity extends AppCompatActivity {

    private static final String TAG = "PatientHomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_home);

        // Retrieve username from Intent extras
        String username = getIntent().getStringExtra("username");
        if (username == null) {
            Log.e(TAG, "Username is null in Intent extras");
            // Optionally handle this case, such as redirecting to MainActivity
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

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

        // Setup Navigation Drawer Menu
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this::onOptionsItemSelected);

        // Initialize Book Appointment Button
        Button buttonBookAppointment = findViewById(R.id.button_book_appointment);

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

    // Handle Navigation Drawer Menu Item Clicks
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle menu item clicks
        int itemId = item.getItemId();
        if (itemId == R.id.action_home) {
            showToast("Home clicked");
            startActivity(new Intent(PatientHomeActivity.this, PatientHomeActivity.class));
            return true;
        } else if (itemId == R.id.action_profile) {
            return true;
        } else if (itemId == R.id.action_view_appointment) {
            showToast("View Appointment clicked");
            return true;
        } else if (itemId == R.id.action_view_prescription) {
            showToast("View Prescription clicked");
            return true;
        } else if (itemId == R.id.action_order_medicine) {
            showToast("Order Medicine clicked");
            return true;
        } else if (itemId == R.id.patient_logout) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showMenu() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.openDrawer(findViewById(R.id.nav_view));
    }
}
