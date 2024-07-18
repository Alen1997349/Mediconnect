// MainActivity.java
package com.example.mediconnect_initial;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private TextView appNameCentered, titleText;
    private LinearLayout loginForm;
    private RelativeLayout titleBar;
    private EditText etUsername, etPassword;
    private Button btnSign;
    private TextView tvDoctorSignup, tvPatientSignup;

    private DatabaseReference database;

    private static final String TAG = "MainActivity"; // Tag for logging

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Hide the title bar
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Database reference
        try {
            database = FirebaseDatabase.getInstance().getReference();
            Log.d(TAG, "Firebase Database reference initialized");
        } catch (Exception e) {
            Log.e(TAG, "Firebase Database initialization failed: " + e.getMessage());
        }

        // Initialize views
        appNameCentered = findViewById(R.id.app_name_centered);
        titleText = findViewById(R.id.title_text);
        loginForm = findViewById(R.id.login_form);
        titleBar = findViewById(R.id.title_bar);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnSign = findViewById(R.id.btn_sign);
        tvDoctorSignup = findViewById(R.id.tv_doctor_signup);
        tvPatientSignup = findViewById(R.id.tv_patient_signup);

        // Create a SpannableString with "Medi" in green and "connect" in blue
        SpannableString spannableString = new SpannableString("Mediconnect");
        spannableString.setSpan(new ForegroundColorSpan(Color.GREEN), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), 4, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        appNameCentered.setText(spannableString);

        // Set up fade-out animation for the centered app name
        new Handler().postDelayed(() -> {
            appNameCentered.animate().alpha(0f).setDuration(1000).withEndAction(() -> {
                appNameCentered.setVisibility(View.GONE);
                titleBar.setVisibility(View.VISIBLE);
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                loginForm.setVisibility(View.VISIBLE);
                loginForm.animate().alpha(1f).setDuration(1000);
                titleText.setText(spannableString); // Set the spannable string to the title text
            });
        }, 3000);

        // Set click listener for sign-in button
        btnSign.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            Log.d(TAG, "Sign-in button clicked");
            Log.d(TAG, "Username - " + username + ", Password - " + password);

            LoginCallback callback = new LoginCallback() {
                boolean isPatientChecked = false;
                boolean isDoctorChecked = false;

                @Override
                public void onLoginSuccess(String userType) {
                    Log.d(TAG, "onLoginSuccess - UserType: " + userType);
                    if (userType.equals("patient")) {
                        // Navigate to PatientHomeActivity
                        Intent intent = new Intent(MainActivity.this, PatientHomeActivity.class);
                        intent.putExtra("username", username);
                        startActivity(intent);
                        finish(); // Optionally, finish MainActivity to prevent back navigation
                    } else if (userType.equals("doctor")) {
                        // Navigate to DoctorHomeActivity
                        Intent intent = new Intent(MainActivity.this, DoctorHomeActivity.class);
                        intent.putExtra("username", username);
                        startActivity(intent);
                        finish(); // Optionally, finish MainActivity to prevent back navigation
                    }
                }

                @Override
                public void onLoginFailure() {
                    Log.d(TAG, "onLoginFailure");
                    if (isPatientChecked && isDoctorChecked) {
                        // Handle incorrect credentials scenario
                        Toast.makeText(MainActivity.this, "Invalid credentials. Please try again.", Toast.LENGTH_SHORT).show();
                    } else if (!isPatientChecked) {
                        isPatientChecked = true;
                        checkPatientCredentials(username, password, this);
                    } else if (!isDoctorChecked) {
                        isDoctorChecked = true;
                        checkDoctorCredentials(username, password, this);
                    }
                }
            };

            // Start with patient credentials check
            checkPatientCredentials(username, password, callback);
        });

        // Set click listener for doctor signup text
        tvDoctorSignup.setOnClickListener(v -> {
            Intent intent = new Intent(this, DoctorSignupActivity.class);
            startActivity(intent);
        });

        // Set click listener for patient signup text
        tvPatientSignup.setOnClickListener(v -> {
            Intent intent = new Intent(this, PatientSignupActivity.class);
            startActivity(intent);
        });
    }

    public interface LoginCallback {
        void onLoginSuccess(String userType);
        void onLoginFailure();
    }

    private void checkPatientCredentials(String username, String password, LoginCallback callback) {
        Log.d(TAG, "checkPatientCredentials - Username: " + username);
        database.child("patients").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "checkPatientCredentials: onDataChange");
                boolean isUserFound = false;
                for (DataSnapshot patientSnapshot : dataSnapshot.getChildren()) {
                    String dbUsername = patientSnapshot.child("username").getValue(String.class);
                    String dbPassword = patientSnapshot.child("password").getValue(String.class);
                    if (dbUsername != null && dbUsername.equals(username) && dbPassword != null && dbPassword.equals(password)) {
                        isUserFound = true;
                        callback.onLoginSuccess("patient");
                        break;
                    }
                }
                if (!isUserFound) {
                    callback.onLoginFailure();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "checkPatientCredentials: onCancelled - " + databaseError.getMessage());
                // Handle database error (optional)
                Toast.makeText(MainActivity.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkDoctorCredentials(String username, String password, LoginCallback callback) {
        Log.d(TAG, "checkDoctorCredentials - Username: " + username);
        database.child("doctors").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "checkDoctorCredentials: onDataChange");
                boolean isUserFound = false;
                for (DataSnapshot doctorSnapshot : dataSnapshot.getChildren()) {
                    String dbUsername = doctorSnapshot.child("username").getValue(String.class);
                    String dbPassword = doctorSnapshot.child("password").getValue(String.class);
                    if (dbUsername != null && dbUsername.equals(username) && dbPassword != null && dbPassword.equals(password)) {
                        isUserFound = true;
                        callback.onLoginSuccess("doctor");
                        break;
                    }
                }
                if (!isUserFound) {
                    callback.onLoginFailure();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "checkDoctorCredentials: onCancelled - " + databaseError.getMessage());
                // Handle database error (optional)
                Toast.makeText(MainActivity.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
