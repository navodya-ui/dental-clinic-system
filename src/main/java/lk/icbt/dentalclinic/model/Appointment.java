package lk.icbt.dentalclinic.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment {

    public enum Status {
        SCHEDULED, COMPLETED, CANCELLED
    }

    private int appointmentId;
    private String appointmentNo;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private Status status;

    private Patient patient;
    private Dentist dentist;
    private Treatment treatment;

    public Appointment() {
    }

    // Used when creating a brand-new appointment (before it has a DB id)
    public Appointment(String appointmentNo, LocalDate appointmentDate, LocalTime appointmentTime,
                        Patient patient, Dentist dentist, Treatment treatment) {
        this.appointmentNo = appointmentNo;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.patient = patient;
        this.dentist = dentist;
        this.treatment = treatment;
        this.status = Status.SCHEDULED;
    }

    // Used when loading an existing appointment from the database
    public Appointment(int appointmentId, String appointmentNo, LocalDate appointmentDate,
                        LocalTime appointmentTime, Status status,
                        Patient patient, Dentist dentist, Treatment treatment) {
        this.appointmentId = appointmentId;
        this.appointmentNo = appointmentNo;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.patient = patient;
        this.dentist = dentist;
        this.treatment = treatment;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getAppointmentNo() {
        return appointmentNo;
    }

    public void setAppointmentNo(String appointmentNo) {
        this.appointmentNo = appointmentNo;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Dentist getDentist() {
        return dentist;
    }

    public void setDentist(Dentist dentist) {
        this.dentist = dentist;
    }

    public Treatment getTreatment() {
        return treatment;
    }

    public void setTreatment(Treatment treatment) {
        this.treatment = treatment;
    }

    /**
     * Basic validation matching the sequence diagram's validateAppointment() step.
     * Checks that the appointment date/time is not in the past and all
     * required associations are present.
     */
    public boolean validateAppointment() {
        if (patient == null || dentist == null || treatment == null) {
            return false;
        }
        if (appointmentDate == null || appointmentTime == null) {
            return false;
        }
        return !appointmentDate.isBefore(LocalDate.now());
    }

    public String displayDetails() {
        return "Appointment No: " + appointmentNo
                + "\nDate: " + appointmentDate
                + "\nTime: " + appointmentTime
                + "\nStatus: " + status
                + "\nPatient: " + (patient != null ? patient.getName() : "N/A")
                + "\nDentist: " + (dentist != null ? dentist.getDentistInfo() : "N/A")
                + "\nTreatment: " + (treatment != null ? treatment.getTreatmentInfo() : "N/A");
    }

    @Override
    public String toString() {
        return displayDetails();
    }
}