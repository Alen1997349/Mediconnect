package com.example.mediconnect_initial;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {

    private Context context;
    private List<Doctor> doctorList;
    private OnAppointmentClickListener appointmentClickListener;

    public DoctorAdapter(Context context, List<Doctor> doctorList, OnAppointmentClickListener appointmentClickListener) {
        this.context = context;
        this.doctorList = doctorList;
        this.appointmentClickListener = appointmentClickListener;
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.doctor_item, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        Doctor doctor = doctorList.get(position);
        holder.doctorDetails.setText("Name: " + doctor.firstName + " " + doctor.lastName +
                "\nLocation: " + doctor.location +
                "\nYears of Experience: " + doctor.yearOfExperience +
                "\nDepartment: " + doctor.department +
                "\nMedical License Number: " + doctor.medicalLicenseNumber);

        holder.buttonBookDoctor.setOnClickListener(v -> {
            appointmentClickListener.onAppointmentClick(doctor);
        });
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    public interface OnAppointmentClickListener {
        void onAppointmentClick(Doctor doctor);
    }

    public static class DoctorViewHolder extends RecyclerView.ViewHolder {
        TextView doctorDetails;
        Button buttonBookDoctor;

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            doctorDetails = itemView.findViewById(R.id.doctor_details);
            buttonBookDoctor = itemView.findViewById(R.id.button_book_doctor);
        }
    }
}
