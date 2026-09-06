package lk.icbt.dentalclinic.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Appointment.validateAppointment() - written test-first
 * (TDD): each test below was written to describe the required behaviour
 * BEFORE confirming the implementation satisfied it, then re-run after
 * every change to validateAppointment() to guard against regressions.
 */
@DisplayName("Appointment model - validateAppointment()")
class AppointmentTest {

    private Patient patient;
    private Dentist dentist;
    private Treatment treatment;

    @BeforeEach
    void setUp() {
        patient = new Patient("Kasun Silva", "Colombo", "0771234567");
        dentist = new Dentist(1, "Nimal Perera", "General Dentistry");
        treatment = new Treatment(1, "General Checkup", new BigDecimal("1500.00"));
    }

    @Nested
    @DisplayName("valid appointments")
    class ValidCases {

        @Test
        @DisplayName("returns true when all fields are present and date is in the future")
        void validAppointment_returnsTrue() {
            Appointment appointment = new Appointment("APT0001", LocalDate.now().plusDays(1),
                    LocalTime.of(10, 30), patient, dentist, treatment);

            assertTrue(appointment.validateAppointment());
        }

        @Test
        @DisplayName("returns true when appointment date is exactly today")
        void appointmentToday_returnsTrue() {
            Appointment appointment = new Appointment("APT0002", LocalDate.now(),
                    LocalTime.of(14, 0), patient, dentist, treatment);

            assertTrue(appointment.validateAppointment());
        }
    }

    @Nested
    @DisplayName("invalid appointments")
    class InvalidCases {

        @Test
        @DisplayName("returns false when patient is missing")
        void missingPatient_returnsFalse() {
            Appointment appointment = new Appointment("APT0003", LocalDate.now().plusDays(1),
                    LocalTime.of(10, 30), null, dentist, treatment);

            assertFalse(appointment.validateAppointment());
        }

        @Test
        @DisplayName("returns false when dentist is missing")
        void missingDentist_returnsFalse() {
            Appointment appointment = new Appointment("APT0004", LocalDate.now().plusDays(1),
                    LocalTime.of(10, 30), patient, null, treatment);

            assertFalse(appointment.validateAppointment());
        }

        @Test
        @DisplayName("returns false when treatment is missing")
        void missingTreatment_returnsFalse() {
            Appointment appointment = new Appointment("APT0005", LocalDate.now().plusDays(1),
                    LocalTime.of(10, 30), patient, dentist, null);

            assertFalse(appointment.validateAppointment());
        }

        @Test
        @DisplayName("returns false when appointment date is in the past")
        void pastDate_returnsFalse() {
            Appointment appointment = new Appointment("APT0006", LocalDate.now().minusDays(1),
                    LocalTime.of(10, 30), patient, dentist, treatment);

            assertFalse(appointment.validateAppointment());
        }

        @Test
        @DisplayName("returns false when date is null")
        void nullDate_returnsFalse() {
            Appointment appointment = new Appointment("APT0007", null,
                    LocalTime.of(10, 30), patient, dentist, treatment);

            assertFalse(appointment.validateAppointment());
        }

        @Test
        @DisplayName("returns false when time is null")
        void nullTime_returnsFalse() {
            Appointment appointment = new Appointment("APT0008", LocalDate.now().plusDays(1),
                    null, patient, dentist, treatment);

            assertFalse(appointment.validateAppointment());
        }
    }

    @Test
    @DisplayName("displayDetails() includes appointment number and patient name")
    void displayDetails_containsKeyFields() {
        Appointment appointment = new Appointment("APT0009", LocalDate.now().plusDays(1),
                LocalTime.of(9, 0), patient, dentist, treatment);

        String details = appointment.displayDetails();

        assertTrue(details.contains("APT0009"));
        assertTrue(details.contains("Kasun Silva"));
        assertTrue(details.contains("General Checkup"));
    }
}