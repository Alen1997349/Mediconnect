package com.example.mediconnect_initial;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Properties;
import java.util.Random;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class PatientSignupActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName, etEmergencyContact, etBloodType, etDiseaseType, etUsername, etPassword, etMedicalHistory, etEmail;
    private Button btn_verify;
    private int code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_signup);

        // Initialize views
        etFirstName = findViewById(R.id.et_first_name);
        etLastName = findViewById(R.id.et_last_name);
        etEmergencyContact = findViewById(R.id.et_emergency_contact);
        etBloodType = findViewById(R.id.et_blood_type);
        etDiseaseType = findViewById(R.id.et_disease_type);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etMedicalHistory = findViewById(R.id.et_medical_history);
        etEmail = findViewById(R.id.et_email);
        btn_verify = findViewById(R.id.btn_verify);

        // Set title with styled text
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(getSpannableTitle("Patient Signup"));

        // Set click listener for verify button
        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate fields
                if (validateFields()) {
                    // Proceed to send OTP
                    Random random = new Random();
                    code = random.nextInt(8999) + 1000;
                    sendEmail(etEmail.getText().toString(), code);
                }
            }
        });
    }

    private SpannableString getSpannableTitle(String title) {
        SpannableString spannableString = new SpannableString(title);

        // Span for "Patient" (Green color)
        ForegroundColorSpan greenColorSpan = new ForegroundColorSpan(Color.GREEN);
        spannableString.setSpan(greenColorSpan, 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Span for "Signup" (Blue color)
        ForegroundColorSpan blueColorSpan = new ForegroundColorSpan(Color.BLUE);
        spannableString.setSpan(blueColorSpan, 7, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 7, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;
    }

    private void sendEmail(final String email, final int code) {
        final String username = "sanalk00000@gmail.com";
        final String password = "dmso pyyr jebq iqvb";

        // Capture all the data fields
        final String firstName = etFirstName.getText().toString();
        final String lastName = etLastName.getText().toString();
        final String emergencyContact = etEmergencyContact.getText().toString();
        final String bloodType = etBloodType.getText().toString();
        final String diseaseType = etDiseaseType.getText().toString();
        final String usernameText = etUsername.getText().toString();
        final String passwordText = etPassword.getText().toString();
        final String medicalHistory = etMedicalHistory.getText().toString();

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(username));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
                    message.setSubject("OTP Verification");
                    message.setText("Your OTP code is: " + code);

                    Transport.send(message);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(PatientSignupActivity.this, "OTP sent to email!", Toast.LENGTH_SHORT).show();

                // Move to verification activity and pass data as extras
                Intent intent = new Intent(PatientSignupActivity.this, com.example.mediconnect_initial.PatientVerifyActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("otp", String.valueOf(code));
                intent.putExtra("firstName", firstName);
                intent.putExtra("lastName", lastName);
                intent.putExtra("emergencyContact", emergencyContact);
                intent.putExtra("bloodType", bloodType);
                intent.putExtra("diseaseType", diseaseType);
                intent.putExtra("username", usernameText);
                intent.putExtra("password", passwordText);
                intent.putExtra("medicalHistory", medicalHistory);
                startActivity(intent);
            }
        };

        task.execute();
    }

    private boolean validateFields() {
        if (TextUtils.isEmpty(etFirstName.getText().toString().trim())) {
            etFirstName.setError("Please enter First Name");
            return false;
        } else if (TextUtils.isEmpty(etLastName.getText().toString().trim())) {
            etLastName.setError("Please enter Last Name");
            return false;
        } else if (TextUtils.isEmpty(etEmergencyContact.getText().toString().trim())) {
            etEmergencyContact.setError("Please enter Emergency Contact");
            return false;
        } else if (TextUtils.isEmpty(etBloodType.getText().toString().trim())) {
            etBloodType.setError("Please enter Blood Type");
            return false;
        } else if (TextUtils.isEmpty(etDiseaseType.getText().toString().trim())) {
            etDiseaseType.setError("Please enter Disease Type");
            return false;
        } else if (TextUtils.isEmpty(etUsername.getText().toString().trim())) {
            etUsername.setError("Please enter Username");
            return false;
        } else if (TextUtils.isEmpty(etPassword.getText().toString().trim())) {
            etPassword.setError("Please enter Password");
            return false;
        } else if (TextUtils.isEmpty(etMedicalHistory.getText().toString().trim())) {
            etMedicalHistory.setError("Please enter Medical History");
            return false;
        } else if (TextUtils.isEmpty(etEmail.getText().toString().trim())) {
            etEmail.setError("Please enter Email");
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString().trim()).matches()) {
            etEmail.setError("Enter a valid email address");
            return false;
        }
        return true;
    }
}
