package com.example.mediconnect_initial;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BookAppointmentActivity extends AppCompatActivity implements DoctorAdapter.OnAppointmentClickListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView menuIcon;
    private Toolbar toolbar;
    private EditText specializationEditText;
    private ImageButton searchButton;
    private RecyclerView recyclerViewDoctors;
    private DoctorAdapter doctorAdapter;
    private List<Doctor> doctorList;
    private DatabaseReference doctorDatabaseReference;
    private FirebaseAuth mAuth;

    private Button datePickerButton, timePickerButton;
    private Calendar myCalendar;
    private String username; // Added to store username

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appoinments);

        // Retrieve username from Intent extras
        username = getIntent().getStringExtra("username");

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        specializationEditText = findViewById(R.id.entry_specialization);
        searchButton = findViewById(R.id.search_button);
        recyclerViewDoctors = findViewById(R.id.recycler_view_doctors);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Setup toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Set Spannable title
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        SpannableString spannableTitle = new SpannableString("Mediconnect");
        spannableTitle.setSpan(new ForegroundColorSpan(Color.GREEN), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableTitle.setSpan(new ForegroundColorSpan(Color.BLUE), 4, 11, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableTitle.setSpan(new StyleSpan(Typeface.BOLD), 0, spannableTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        toolbarTitle.setText(spannableTitle);

        // Set Menu Icon click listener
        menuIcon = findViewById(R.id.toolbar_menu_icon);
        menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(navigationView));

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int itemId = menuItem.getItemId();
            if (itemId == R.id.action_home) {
                showToast("Home clicked");
                Intent intent = new Intent(BookAppointmentActivity.this, PatientHomeActivity.class);
                intent.putExtra("username", getIntent().getStringExtra("username"));
                startActivity(intent);
                return true;
            } else if (itemId == R.id.action_profile) {
                Intent intent = new Intent(BookAppointmentActivity.this, PatientProfile.class);
                intent.putExtra("username", getIntent().getStringExtra("username"));
                startActivity(intent);
                return true;
            } else if (itemId == R.id.action_view_appointment) {
                showToast("View Appointment clicked");

                return true;
            } else if (itemId == R.id.action_view_prescription) {
                showToast("View Prescription clicked");

                return true;
            } else if (itemId == R.id.action_prediction) {
                showToast("Order Medicine clicked");
                return true;
            } else if (itemId == R.id.patient_logout) {
                showToast("Logout clicked");
                FirebaseAuth.getInstance().signOut(); // Sign out current user
                startActivity(new Intent(BookAppointmentActivity.this, MainActivity.class));
                finish(); // Close current activity
                return true;
            }
            drawerLayout.closeDrawer(navigationView);
            return true;
        });

        recyclerViewDoctors.setLayoutManager(new LinearLayoutManager(this));
        doctorList = new ArrayList<>();
        doctorAdapter = new DoctorAdapter(this, doctorList, this);
        recyclerViewDoctors.setAdapter(doctorAdapter);

        doctorDatabaseReference = FirebaseDatabase.getInstance().getReference("doctors");

        // Initialize date picker button and calendar
        datePickerButton = findViewById(R.id.datePickerButton);
        timePickerButton = findViewById(R.id.timePickerButton);
        myCalendar = Calendar.getInstance(); // Initialize calendar instance

        // Set initial date on the button
        updateDateLabel(); // Call updateDateLabel() to set today's date

        // Date Picker Button click listener
        datePickerButton.setOnClickListener(v -> showDatePicker());

        // Time Picker Button click listener
        timePickerButton.setOnClickListener(v -> showTimePicker());

        searchButton.setOnClickListener(v -> searchDoctors());
    }

    // Date Picker dialog listener
    private void showDatePicker() {
        new DatePickerDialog(this, date,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateLabel();
        }
    };

    // Update the date label on the button
    private void updateDateLabel() {
        String myFormat = "MMM dd yyyy"; // Format of the date shown on button
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        datePickerButton.setText(sdf.format(myCalendar.getTime())); // Set today's date
    }

    // Time Picker dialog listener
    private void showTimePicker() {
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        myCalendar.set(Calendar.MINUTE, minute);
                        updateTimeLabel();
                    }
                }, hour, minute, false);

        timePickerDialog.show();
    }

    // Update the time label on the button
    private void updateTimeLabel() {
        String myFormat = "hh:mm a"; // Format of the time shown on button
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        timePickerButton.setText(sdf.format(myCalendar.getTime()));
    }

    private void searchDoctors() {
        String specialization = specializationEditText.getText().toString().trim();
        if (!specialization.isEmpty()) {
            doctorDatabaseReference.orderByChild("specialization").equalTo(specialization).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    doctorList.clear();
                    for (DataSnapshot doctorSnapshot : snapshot.getChildren()) {
                        Doctor doctor = doctorSnapshot.getValue(Doctor.class);
                        doctorList.add(doctor);
                    }
                    doctorAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(BookAppointmentActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Please enter a specialization", Toast.LENGTH_SHORT).show();
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAppointmentClick(Doctor doctor) {


        }
    }
