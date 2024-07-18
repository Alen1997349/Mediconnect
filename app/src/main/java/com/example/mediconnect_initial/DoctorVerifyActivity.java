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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DoctorVerifyActivity extends AppCompatActivity {


    private EditText et_otp;
    private Button btn_verify;
    private String email;
    private String expectedOtp;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_verify);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        et_otp = findViewById(R.id.edittext_otp);
        btn_verify = findViewById(R.id.button_register);

        // Set up title bar with SpannableString
        TextView tvTitle = findViewById(R.id.tv_title);
        SpannableString spannableString = new SpannableString("Mediconnect");
        spannableString.setSpan(new ForegroundColorSpan(Color.GREEN), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), 4, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvTitle.setText(spannableString);

        // Retrieve email and expected OTP from intent
        Intent intent = getIntent();
        if (intent != null) {
            email = intent.getStringExtra("email");
            expectedOtp = intent.getStringExtra("otp");
        }

        // Set click listener for verify button
        btn_verify.setOnClickListener(v -> {
            String otp = et_otp.getText().toString();

            // Perform OTP verification
            if (otp.equals(expectedOtp)) {
                Toast.makeText(DoctorVerifyActivity.this, "Verification successful", Toast.LENGTH_SHORT).show();

                // Save doctor details to Firebase Database
                saveDoctorDataToFirebase();

                // Navigate to DoctorHomeActivity or any other activity as needed
                Intent homeIntent = new Intent(DoctorVerifyActivity.this, MainActivity.class);
                startActivity(homeIntent);
                finish(); // Finish this activity to prevent going back to it with back button
            } else {
                Toast.makeText(DoctorVerifyActivity.this, "Verification failed. Invalid OTP", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveDoctorDataToFirebase() {
        // Get doctor details from Intent extras
        String firstName = getIntent().getStringExtra("firstName");
        String lastName = getIntent().getStringExtra("lastName");
        String location = getIntent().getStringExtra("location");
        String specialization = getIntent().getStringExtra("specialization");
        String yearOfExperience = getIntent().getStringExtra("yearOfExperience");
        String department = getIntent().getStringExtra("department");
        String medicalLicenseNumber = getIntent().getStringExtra("medicalLicenseNumber");
        String username = getIntent().getStringExtra("username");
        String password = getIntent().getStringExtra("password");

        // Create a unique key for the doctor in Firebase Database
        String doctorId = mDatabase.child("doctors").push().getKey();

        // Create Doctor object to store in Firebase Database
        Doctor doctor = new Doctor(firstName, lastName, location, specialization, yearOfExperience, department, medicalLicenseNumber, username, password);

        // Save doctor data to Firebase Database under "doctors" node with doctorId as key
        mDatabase.child("doctors").child(doctorId).setValue(doctor)
                .addOnSuccessListener(aVoid -> {
                    // Data successfully saved
                    Toast.makeText(DoctorVerifyActivity.this, "Doctor data saved to Firebase", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Handle any errors
                    Toast.makeText(DoctorVerifyActivity.this, "Failed to save doctor data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Define the Doctor class as a static nested class
    public static class Doctor {
        public String firstName;
        public String lastName;
        public String location;
        public String specialization;
        public String yearOfExperience;
        public String department;
        public String medicalLicenseNumber;
        public String username;
        public String password;

        public Doctor(String firstName, String lastName, String location, String specialization, String yearOfExperience, String department, String medicalLicenseNumber, String username, String password) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.location = location;
            this.specialization = specialization;
            this.yearOfExperience = yearOfExperience;
            this.department = department;
            this.medicalLicenseNumber = medicalLicenseNumber;
            this.username = username;
            this.password = password;
        }
    }
}
