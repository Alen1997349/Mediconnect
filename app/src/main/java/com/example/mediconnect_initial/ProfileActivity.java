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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String username;
    private EditText etName;
    private TextView tvSpecialization, tvDepartment, tvExperience, tvMedicalLicense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        username = getIntent().getStringExtra("username");

        // Initialize Firebase Auth and Database Reference
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

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

        // Initialize EditText and TextViews
        etName = findViewById(R.id.et_name);
        tvSpecialization = findViewById(R.id.tv_specialization);
        tvDepartment = findViewById(R.id.tv_department);
        tvExperience = findViewById(R.id.tv_experience);
        tvMedicalLicense = findViewById(R.id.tv_medical_license);

        // Fetch and display doctor details
        fetchDoctorDetails();

        // Setup Navigation Drawer Menu
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this::onOptionsItemSelected);

        // Set up the Update button
        Button btnUpdate = findViewById(R.id.btn_update);
        btnUpdate.setOnClickListener(v -> updateDoctorName());
    }

    private void fetchDoctorDetails() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("username")) {
            String username = intent.getStringExtra("username");

            mDatabase.child("doctors").orderByChild("username").equalTo(username)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    String name = snapshot.child("firstName").getValue(String.class);
                                    String specialization = snapshot.child("specialization").getValue(String.class);
                                    String department = snapshot.child("department").getValue(String.class);
                                    String experience = snapshot.child("yearOfExperience").getValue(String.class);
                                    String medicalLicense = snapshot.child("medicalLicenseNumber").getValue(String.class);

                                    etName.setText(name);
                                    tvSpecialization.setText("Specialization: " + specialization);
                                    tvDepartment.setText("Department: " + department);
                                    tvExperience.setText("Experience: " + experience);
                                    tvMedicalLicense.setText("Medical License: " + medicalLicense);
                                }
                            } else {
                                showToast("Doctor details not found");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            showToast("Failed to fetch doctor details");
                        }
                    });
        } else {
            showToast("Username not found");
        }
    }

    private void updateDoctorName() {
        String newName = etName.getText().toString().trim();

        if (!newName.isEmpty()) {
            mDatabase.child("doctors").orderByChild("username").equalTo(username)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    String doctorKey = snapshot.getKey();
                                    if (doctorKey != null) {
                                        mDatabase.child("doctors").child(doctorKey).child("firstName").setValue(newName)
                                                .addOnCompleteListener(task -> {
                                                    if (task.isSuccessful()) {
                                                        showToast("Name updated successfully");
                                                    } else {
                                                        showToast("Failed to update name");
                                                    }
                                                });
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            showToast("Failed to update name");
                        }
                    });
        } else {
            showToast("Name cannot be empty");
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


    private void showMenu() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.openDrawer(findViewById(R.id.nav_view));
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle menu item clicks
        int itemId = item.getItemId();
        if (itemId == R.id.action_home) {
            showToast("Home clicked");
            // Pass username to DoctorHomeActivity when navigating to it
            Intent intent = new Intent(ProfileActivity.this, DoctorHomeActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        } else if (itemId == R.id.action_profile) {
            showToast("Profile clicked");
            // Pass username to ProfileActivity when navigating to it
            Intent profileIntent = new Intent(ProfileActivity.this, ProfileActivity.class);
            profileIntent.putExtra("username", username);
            startActivity(profileIntent);
        } else if (itemId == R.id.action_edit_patient) {
            showToast("Edit Patient Details clicked");
            // Handle edit patient details logic here

        } else if (itemId == R.id.action_patient_records) {
            showToast("Patient Records clicked");
            // Handle patient records logic here

        } else if (itemId == R.id.action_pre_approval) {
            showToast("Pre-Approval Request clicked");
            // Handle pre-approval request logic here

        } else if (itemId == R.id.action_logout) {
            showToast("Logout clicked");
            mAuth.signOut();
            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            finish(); // Close this activity to prevent back button from returning here
        }

        return super.onOptionsItemSelected(item);
    }
}
