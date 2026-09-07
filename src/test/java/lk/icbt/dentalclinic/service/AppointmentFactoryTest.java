package lk.icbt.dentalclinic.service;

import lk.icbt.dentalclinic.model.Appointment;
import lk.icbt.dentalclinic.model.Dentist;
import lk.icbt.dentalclinic.model.Patient;
import lk.icbt.dentalclinic.model.Treatment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Factory Method pattern implementation.
 * Note: the appointment number is now supplied by the caller (derived from
 * the database via AppointmentDAO.getNextAppointmentNo()) rather than
 * generated internally - this avoids the numbering-collision bug an
 * in-memory counter caused on server restart. These tests confirm the
 * Factory correctly assembles an Appointment from the given inputs.
 */
@DisplayName("AppointmentFactory")
class AppointmentFactoryTest {

    @Test
    @DisplayName("createAppointment() assigns the supplied appointment number unchanged")
    void createAppointment_usesSuppliedNumber() {
        Patient patient = new Patient("Test Patient", "Colombo", "0770000000");
        Dentist dentist = new Dentist(1, "Nimal Perera", "General Dentistry");
        Treatment treatment = new Treatment(1, "General Checkup", new BigDecimal("1500.00"));

        Appointment appointment = AppointmentFactory.createAppointment(
                "APT0042", patient, dentist, treatment, LocalDate.now().plusDays(1), LocalTime.of(10, 0));

        assertEquals("APT0042", appointment.getAppointmentNo());
    }

    @Test
    @DisplayName("createAppointment() correctly assigns patient, dentist, and treatment")
    void createAppointment_assignsAllAssociations() {
        Patient patient = new Patient("Test Patient", "Colombo", "0770000000");
        Dentist dentist = new Dentist(2, "Anusha Fernando", "Orthodontics");
        Treatment treatment = new Treatment(2, "Tooth Extraction", new BigDecimal("3500.00"));
        LocalDate date = LocalDate.now().plusDays(3);
        LocalTime time = LocalTime.of(15, 30);

        Appointment appointment = AppointmentFactory.createAppointment(
                "APT0043", patient, dentist, treatment, date, time);

        assertEquals(patient, appointment.getPatient());
        assertEquals(dentist, appointment.getDentist());
        assertEquals(treatment, appointment.getTreatment());
        assertEquals(date, appointment.getAppointmentDate());
        assertEquals(time, appointment.getAppointmentTime());
        assertEquals(Appointment.Status.SCHEDULED, appointment.getStatus());
    }

    @Test
    @DisplayName("createAppointment() with two different numbers produces two distinct appointments")
    void createAppointment_differentNumbers_areDistinct() {
        Patient patient = new Patient("Test Patient", "Colombo", "0770000000");
        Dentist dentist = new Dentist(1, "Nimal Perera", "General Dentistry");
        Treatment treatment = new Treatment(1, "General Checkup", new BigDecimal("1500.00"));

        Appointment first = AppointmentFactory.createAppointment(
                "APT0044", patient, dentist, treatment, LocalDate.now().plusDays(1), LocalTime.of(9, 0));
        Appointment second = AppointmentFactory.createAppointment(
                "APT0045", patient, dentist, treatment, LocalDate.now().plusDays(1), LocalTime.of(10, 0));

        assertNotEquals(first.getAppointmentNo(), second.getAppointmentNo());
    }
}