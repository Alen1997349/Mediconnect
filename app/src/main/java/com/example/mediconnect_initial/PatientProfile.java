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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class PatientProfile extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;

    private EditText etName;
    private EditText etEmergencyContact;
    private EditText etMedicalHistory;
    private EditText etDiseaseType;
    private EditText etBloodType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_profile);

        // Initialize Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Initialize UI elements
        initializeViews();

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

        // Load and display patient profile data
        Intent intent = getIntent();
        if (intent.hasExtra("username")) {
            String username = intent.getStringExtra("username");
            loadPatientProfile(username);
        } else {
            Toast.makeText(this, "Username not found", Toast.LENGTH_SHORT).show();
        }

        // Handle Update Button Click
        Button updateButton = findViewById(R.id.save_button);
        updateButton.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String emergencyContact = etEmergencyContact.getText().toString().trim();
            String medicalHistory = etMedicalHistory.getText().toString().trim();
            String diseaseType = etDiseaseType.getText().toString().trim();
            String bloodType = etBloodType.getText().toString().trim();

            // Update Firebase database
            updatePatientProfile(name, emergencyContact, medicalHistory, diseaseType, bloodType);
        });
    }

    private void initializeViews() {
        etName = findViewById(R.id.et_name);
        etEmergencyContact = findViewById(R.id.et_emergency);
        etMedicalHistory = findViewById(R.id.et_history);
        etDiseaseType = findViewById(R.id.et_disease);
        etBloodType = findViewById(R.id.et_blood);
    }

    private void loadPatientProfile(String username) {
        mDatabase.child("patients").orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        // Get unique key for the patient
                        String patientKey = dataSnapshot.getKey();

                        // Fetch patient data
                        String name = dataSnapshot.child("firstName").getValue(String.class);
                        String emergencyContact = dataSnapshot.child("emergencyContact").getValue(String.class);
                        String medicalHistory = dataSnapshot.child("medicalHistory").getValue(String.class);
                        String diseaseType = dataSnapshot.child("diseaseType").getValue(String.class);
                        String bloodType = dataSnapshot.child("bloodType").getValue(String.class);

                        // Update UI with fetched data
                        etName.setText(name);
                        etEmergencyContact.setText(emergencyContact);
                        etMedicalHistory.setText(medicalHistory);
                        etDiseaseType.setText(diseaseType);
                        etBloodType.setText(bloodType);

                        // Store the patient key for later use
                        etName.setTag(patientKey);
                    }
                } else {
                    Toast.makeText(PatientProfile.this, "Patient data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PatientProfile.this, "Failed to load patient data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePatientProfile(String name, String emergencyContact, String medicalHistory, String diseaseType, String bloodType) {
        String patientKey = (String) etName.getTag();
        if (patientKey != null) {
            DatabaseReference patientRef = mDatabase.child("patients").child(patientKey);

            // Use HashMap to update multiple fields in one go
            Map<String, Object> updates = new HashMap<>();
            updates.put("firstName", name);
            updates.put("emergencyContact", emergencyContact);
            updates.put("medicalHistory", medicalHistory);
            updates.put("diseaseType", diseaseType);
            updates.put("bloodType", bloodType);

            patientRef.updateChildren(updates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(PatientProfile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(PatientProfile.this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Failed to update profile: Patient key not found", Toast.LENGTH_SHORT).show();
        }
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
        // Handle menu item clicks
        int itemId = item.getItemId();
        if (itemId == R.id.action_home) {
            showToast("Home clicked");
            Intent intent = new Intent(PatientProfile.this, PatientHomeActivity.class);
            intent.putExtra("username", getIntent().getStringExtra("username"));
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_profile) {
            showToast("Profile clicked");
            Intent intent = new Intent(PatientProfile.this, PatientProfile.class);
            intent.putExtra("username", getIntent().getStringExtra("username"));
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_view_appointment) {
            showToast("View Appointment clicked");
//            Intent intent = new Intent(PatientProfile.this, MyAppointmentsActivity.class);
//            intent.putExtra("username", getIntent().getStringExtra("username"));
//            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_view_prescription) {
            showToast("View Prescription clicked");
            return true;
        } else if (itemId == R.id.action_prediction) {
            showToast("Order prediction clicked");
            return true;
        } else if (itemId == R.id.patient_logout) {
            showToast("Logout clicked");
            FirebaseAuth.getInstance().signOut(); // Sign out current user
            startActivity(new Intent(PatientProfile.this, MainActivity.class));
            finish(); // Close current activity
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
