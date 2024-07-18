package com.example.mediconnect_initial;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mediconnect_initial.PatientHomeActivity;
import com.example.mediconnect_initial.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PatientVerifyActivity extends AppCompatActivity {

    private EditText etOtp;
    private Button btnVerify;
    private String expectedOtp;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_patient);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        etOtp = findViewById(R.id.edittext_otp);
        btnVerify = findViewById(R.id.button_register);

        // Set up title bar with SpannableString
        TextView tvTitle = findViewById(R.id.tv_title);
        SpannableString spannableString = new SpannableString("Mediconnect");
        spannableString.setSpan(new ForegroundColorSpan(Color.GREEN), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), 4, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvTitle.setText(spannableString);

        // Retrieve expected OTP from intent
        Intent intent = getIntent();
        if (intent != null) {
            expectedOtp = intent.getStringExtra("otp");
        }

        // Set click listener for verify button
        btnVerify.setOnClickListener(v -> {
            String otp = etOtp.getText().toString();

            // Perform OTP verification
            if (otp.equals(expectedOtp)) {
                Toast.makeText(PatientVerifyActivity.this, "Verification successful", Toast.LENGTH_SHORT).show();

                // Save patient data to Firebase Database
                savePatientDataToFirebase();

                // Navigate to PatientHomeActivity or any other activity as needed
                Intent homeIntent = new Intent(PatientVerifyActivity.this, PatientHomeActivity.class);
                startActivity(homeIntent);
                finish(); // Finish this activity to prevent going back to it with back button
            } else {
                Toast.makeText(PatientVerifyActivity.this, "Verification failed. Invalid OTP", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void savePatientDataToFirebase() {
        // Get patient details from Intent extras
        String firstName = getIntent().getStringExtra("firstName");
        String lastName = getIntent().getStringExtra("lastName");
        String email = getIntent().getStringExtra("email");
        String emergencyContact = getIntent().getStringExtra("emergencyContact");
        String bloodType = getIntent().getStringExtra("bloodType");
        String diseaseType = getIntent().getStringExtra("diseaseType");
        String username = getIntent().getStringExtra("username");
        String password = getIntent().getStringExtra("password");
        String medicalHistory = getIntent().getStringExtra("medicalHistory");

        // Create a unique key for the patient in Firebase Database
        String patientId = mDatabase.child("patients").push().getKey();

        // Create Patient object to store in Firebase Database
        Patient patient = new Patient(firstName, lastName, email, emergencyContact, bloodType, diseaseType, username, password, medicalHistory);

        // Save patient data to Firebase Database under "patients" node with patientId as key
        mDatabase.child("patients").child(patientId).setValue(patient)
                .addOnSuccessListener(aVoid -> {
                    // Data successfully saved
                    Toast.makeText(PatientVerifyActivity.this, "Patient data saved to Firebase", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Handle any errors
                    Toast.makeText(PatientVerifyActivity.this, "Failed to save patient data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Define the Patient class as a static nested class
    public static class Patient {
        public String firstName;
        public String lastName;
        public String email;
        public String emergencyContact;
        public String bloodType;
        public String diseaseType;
        public String username;
        public String password;
        public String medicalHistory;

        public Patient(String firstName, String lastName, String email, String emergencyContact, String bloodType, String diseaseType, String username, String password, String medicalHistory) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.emergencyContact = emergencyContact;
            this.bloodType = bloodType;
            this.diseaseType = diseaseType;
            this.username = username;
            this.password = password;
            this.medicalHistory = medicalHistory;
        }
    }
}
