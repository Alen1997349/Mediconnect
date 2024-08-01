package com.example.mediconnect_initial;

public class Doctor {
    public String username;
    public String password;
    public String firstName;
    public String lastName;
    public String location;
    public String specialization;
    public String yearOfExperience;
    public String department;
    public String medicalLicenseNumber;

    // Default constructor required for calls to DataSnapshot.getValue(Doctor.class)
    public Doctor() {
    }

    public Doctor(String username, String password, String firstName, String lastName, String location,
                  String specialization, String yearOfExperience, String department, String medicalLicenseNumber) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.location = location;
        this.specialization = specialization;
        this.yearOfExperience = yearOfExperience;
        this.department = department;
        this.medicalLicenseNumber = medicalLicenseNumber;
    }
}
