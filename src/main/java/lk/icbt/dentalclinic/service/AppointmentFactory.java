package lk.icbt.dentalclinic.service;

import lk.icbt.dentalclinic.model.Appointment;
import lk.icbt.dentalclinic.model.Dentist;
import lk.icbt.dentalclinic.model.Patient;
import lk.icbt.dentalclinic.model.Treatment;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Factory pattern: centralizes the logic for constructing a new
 * Appointment object, so the UI/controller layer never has to know
 * the construction rules. The appointment number itself is generated
 * by AppointmentDAO.getNextAppointmentNo() (derived from the database)
 * and passed in here, rather than kept as in-memory state in this
 * factory - an in-memory counter would reset to APT0001 on every
 * server restart and collide with existing records.
 */
public class AppointmentFactory {

    public static Appointment createAppointment(String appointmentNo, Patient patient, Dentist dentist,
                                                  Treatment treatment, LocalDate date, LocalTime time) {
        return new Appointment(appointmentNo, date, time, patient, dentist, treatment);
    }
}