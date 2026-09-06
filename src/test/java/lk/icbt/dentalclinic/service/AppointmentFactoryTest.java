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
 */
@DisplayName("AppointmentFactory")
class AppointmentFactoryTest {

    @Test
    @DisplayName("createAppointment() generates an appointment number matching the APTnnnn format")
    void createAppointment_generatesFormattedNumber() {
        Patient patient = new Patient("Test Patient", "Colombo", "0770000000");
        Dentist dentist = new Dentist(1, "Nimal Perera", "General Dentistry");
        Treatment treatment = new Treatment(1, "General Checkup", new BigDecimal("1500.00"));

        Appointment appointment = AppointmentFactory.createAppointment(
                patient, dentist, treatment, LocalDate.now().plusDays(1), LocalTime.of(10, 0));

        assertNotNull(appointment.getAppointmentNo());
        assertTrue(appointment.getAppointmentNo().matches("APT\\d{4}"),
                "Expected format APTnnnn but was: " + appointment.getAppointmentNo());
    }

    @Test
    @DisplayName("createAppointment() correctly assigns patient, dentist, and treatment")
    void createAppointment_assignsAllAssociations() {
        Patient patient = new Patient("Test Patient", "Colombo", "0770000000");
        Dentist dentist = new Dentist(2, "Anusha Fernando", "Orthodontics");
        Treatment treatment = new Treatment(2, "Tooth Extraction", new BigDecimal("3500.00"));
        LocalDate date = LocalDate.now().plusDays(3);
        LocalTime time = LocalTime.of(15, 30);

        Appointment appointment = AppointmentFactory.createAppointment(patient, dentist, treatment, date, time);

        assertEquals(patient, appointment.getPatient());
        assertEquals(dentist, appointment.getDentist());
        assertEquals(treatment, appointment.getTreatment());
        assertEquals(date, appointment.getAppointmentDate());
        assertEquals(time, appointment.getAppointmentTime());
        assertEquals(Appointment.Status.SCHEDULED, appointment.getStatus());
    }

    @Test
    @DisplayName("createAppointment() produces unique, incrementing appointment numbers")
    void createAppointment_generatesUniqueNumbers() {
        Patient patient = new Patient("Test Patient", "Colombo", "0770000000");
        Dentist dentist = new Dentist(1, "Nimal Perera", "General Dentistry");
        Treatment treatment = new Treatment(1, "General Checkup", new BigDecimal("1500.00"));

        Appointment first = AppointmentFactory.createAppointment(
                patient, dentist, treatment, LocalDate.now().plusDays(1), LocalTime.of(9, 0));
        Appointment second = AppointmentFactory.createAppointment(
                patient, dentist, treatment, LocalDate.now().plusDays(1), LocalTime.of(10, 0));

        assertNotEquals(first.getAppointmentNo(), second.getAppointmentNo());
    }
}