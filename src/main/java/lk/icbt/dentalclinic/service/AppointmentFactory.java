package lk.icbt.dentalclinic.service;

import lk.icbt.dentalclinic.model.Appointment;
import lk.icbt.dentalclinic.model.Dentist;
import lk.icbt.dentalclinic.model.Patient;
import lk.icbt.dentalclinic.model.Treatment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Factory pattern: centralizes the logic for constructing a new
 * Appointment (including generating its business-facing appointment
 * number), so the UI/controller layer never has to know those rules.
 * If a second appointment "type" were ever needed (e.g. emergency vs
 * routine), this is also the natural extension point.
 */
public class AppointmentFactory {

    // simple in-memory counter for demo purposes; in production this
    // would be derived from the database (MAX(appointment_id) + 1)
    private static final AtomicInteger sequence = new AtomicInteger(1);

    public static Appointment createAppointment(Patient patient, Dentist dentist, Treatment treatment,
                                                  LocalDate date, LocalTime time) {
        String appointmentNo = generateAppointmentNo();
        return new Appointment(appointmentNo, date, time, patient, dentist, treatment);
    }

    private static String generateAppointmentNo() {
        return String.format("APT%04d", sequence.getAndIncrement());
    }
}