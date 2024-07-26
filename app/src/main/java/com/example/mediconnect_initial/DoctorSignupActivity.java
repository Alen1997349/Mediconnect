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

public class DoctorSignupActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName, etLocation, etSpecialization, etYearOfExperience, etDepartment, etMedicalLicenseNumber, etUsername, etPassword, etEmail;
    private Button btn_verify;
    private int code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_signup);

        // Initialize views
        etFirstName = findViewById(R.id.et_first_name);
        etLastName = findViewById(R.id.et_last_name);
        etLocation = findViewById(R.id.et_location);
        etSpecialization = findViewById(R.id.et_specialization);
        etYearOfExperience = findViewById(R.id.et_year_of_experience);
        etDepartment = findViewById(R.id.et_department);
        etMedicalLicenseNumber = findViewById(R.id.et_medical_license_number);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etEmail = findViewById(R.id.et_email);
        btn_verify = findViewById(R.id.btn_verify);

        // Set title with styled text
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(getSpannableTitle("Mediconnect"));

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

    private void sendEmail(final String email, final int code) {
        final String username = "mediconnectotp@gmail.com";
        final String password = "fdio bdqo nhwt pous";

        // Capture all the data fields
        final String firstName = etFirstName.getText().toString();
        final String lastName = etLastName.getText().toString();
        final String location = etLocation.getText().toString();
        final String specialization = etSpecialization.getText().toString();
        final String yearOfExperience = etYearOfExperience.getText().toString();
        final String department = etDepartment.getText().toString();
        final String medicalLicenseNumber = etMedicalLicenseNumber.getText().toString();
        final String usernameText = etUsername.getText().toString();
        final String passwordText = etPassword.getText().toString();

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
                Toast.makeText(DoctorSignupActivity.this, "OTP sent to email!", Toast.LENGTH_SHORT).show();

                // Move to verification activity and pass data as extras
                Intent intent = new Intent(DoctorSignupActivity.this, DoctorVerifyActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("otp", String.valueOf(code));
                intent.putExtra("firstName", firstName);
                intent.putExtra("lastName", lastName);
                intent.putExtra("location", location);
                intent.putExtra("specialization", specialization);
                intent.putExtra("yearOfExperience", yearOfExperience);
                intent.putExtra("department", department);
                intent.putExtra("medicalLicenseNumber", medicalLicenseNumber);
                intent.putExtra("username", usernameText);
                intent.putExtra("password", passwordText);
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
        } else if (TextUtils.isEmpty(etLocation.getText().toString().trim())) {
            etLocation.setError("Please enter Location");
            return false;
        } else if (TextUtils.isEmpty(etSpecialization.getText().toString().trim())) {
            etSpecialization.setError("Please enter Specialization");
            return false;
        } else if (TextUtils.isEmpty(etYearOfExperience.getText().toString().trim())) {
            etYearOfExperience.setError("Please enter Years of Experience");
            return false;
        } else if (TextUtils.isEmpty(etDepartment.getText().toString().trim())) {
            etDepartment.setError("Please enter Department");
            return false;
        } else if (TextUtils.isEmpty(etMedicalLicenseNumber.getText().toString().trim())) {
            etMedicalLicenseNumber.setError("Please enter Medical License Number");
            return false;
        } else if (TextUtils.isEmpty(etUsername.getText().toString().trim())) {
            etUsername.setError("Please enter Username");
            return false;
        } else if (TextUtils.isEmpty(etPassword.getText().toString().trim())) {
            etPassword.setError("Please enter Password");
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

