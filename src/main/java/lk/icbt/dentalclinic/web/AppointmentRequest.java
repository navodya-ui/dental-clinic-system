package lk.icbt.dentalclinic.web;

import lk.icbt.dentalclinic.model.Appointment;
import lk.icbt.dentalclinic.model.Dentist;
import lk.icbt.dentalclinic.model.Patient;
import lk.icbt.dentalclinic.model.Treatment;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Data Transfer Object representing the JSON body a client sends
 * when registering a new appointment. Kept separate from the domain
 * model (Appointment) so the API contract can evolve independently
 * of the internal object graph.
 *
 * Example JSON:
 * {
 *   "patientName": "Kasun Silva",
 *   "patientAddress": "12 Galle Road, Colombo",
 *   "patientContact": "0771234567",
 *   "dentistId": 1,
 *   "treatmentId": 2,
 *   "appointmentDate": "2026-09-10",
 *   "appointmentTime": "10:30:00"
 * }
 */
public class AppointmentRequest {

    public String patientName;
    public String patientAddress;
    public String patientContact;
    public int dentistId;
    public int treatmentId;
    public LocalDate appointmentDate;
    public LocalTime appointmentTime;

    public AppointmentRequest() {
        // required no-arg constructor for Jackson deserialization
    }

    /**
     * Converts this request into a domain Appointment object.
     * Dentist/Treatment are populated with just their id here -
     * AppointmentDAO only needs the id (as a foreign key) to persist
     * the appointment; the full lookup happens on read via the JOIN query.
     */
    public Appointment toAppointment() {
        Patient patient = new Patient(patientName, patientAddress, patientContact);
        Dentist dentist = new Dentist(dentistId, null, null);
        Treatment treatment = new Treatment(treatmentId, null, null);

        return new Appointment(null, appointmentDate, appointmentTime, patient, dentist, treatment);
    }
}