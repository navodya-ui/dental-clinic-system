package lk.icbt.dentalclinic.service;

import lk.icbt.dentalclinic.dao.AppointmentDAO;
import lk.icbt.dentalclinic.dao.DataAccessException;
import lk.icbt.dentalclinic.model.Appointment;
import lk.icbt.dentalclinic.model.Dentist;
import lk.icbt.dentalclinic.model.Patient;
import lk.icbt.dentalclinic.model.Treatment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AppointmentService, with AppointmentDAO mocked via Mockito
 * so these tests run without a live database connection - true unit tests
 * of the business logic (validation + Observer notification), isolated
 * from the persistence layer.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AppointmentService")
class AppointmentServiceTest {

    @Mock
    private AppointmentDAO appointmentDAO;

    private AppointmentService appointmentService;

    private Patient patient;
    private Dentist dentist;
    private Treatment treatment;

    @BeforeEach
    void setUp() {
        appointmentService = new AppointmentService(appointmentDAO);
        patient = new Patient("Kasun Silva", "Colombo", "0771234567");
        dentist = new Dentist(1, "Nimal Perera", "General Dentistry");
        treatment = new Treatment(1, "General Checkup", new BigDecimal("1500.00"));
    }

    @Test
    @DisplayName("registerAppointment() saves a valid appointment and returns it")
    void registerAppointment_validInput_savesAndReturnsAppointment() {
        LocalDate date = LocalDate.now().plusDays(1);
        LocalTime time = LocalTime.of(10, 30);

        doNothing().when(appointmentDAO).addAppointment(any(Appointment.class));

        Appointment result = appointmentService.registerAppointment(patient, dentist, treatment, date, time);

        assertNotNull(result);
        assertEquals(patient, result.getPatient());
        verify(appointmentDAO, times(1)).addAppointment(any(Appointment.class));
    }

    @Test
    @DisplayName("registerAppointment() throws IllegalArgumentException for a past date, and never calls the DAO")
    void registerAppointment_pastDate_throwsAndSkipsDao() {
        LocalDate pastDate = LocalDate.now().minusDays(1);

        assertThrows(IllegalArgumentException.class, () ->
                appointmentService.registerAppointment(patient, dentist, treatment, pastDate, LocalTime.of(10, 0)));

        // validation must fail BEFORE the DAO is ever touched
        verify(appointmentDAO, never()).addAppointment(any());
    }

    @Test
    @DisplayName("registerAppointment() propagates the real database error message on save failure")
    void registerAppointment_daoThrows_propagatesMessage() {
        LocalDate date = LocalDate.now().plusDays(1);
        doThrow(new DataAccessException("Database error: unknown treatment_id", null))
                .when(appointmentDAO).addAppointment(any(Appointment.class));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                appointmentService.registerAppointment(patient, dentist, treatment, date, LocalTime.of(10, 0)));

        assertTrue(ex.getMessage().contains("unknown treatment_id"));
    }

    @Test
    @DisplayName("registerAppointment() notifies all registered observers exactly once on success")
    void registerAppointment_notifiesObservers() {
        LocalDate date = LocalDate.now().plusDays(1);
        AppointmentObserver observer1 = mock(AppointmentObserver.class);
        AppointmentObserver observer2 = mock(AppointmentObserver.class);
        appointmentService.registerObserver(observer1);
        appointmentService.registerObserver(observer2);

        doNothing().when(appointmentDAO).addAppointment(any(Appointment.class));

        appointmentService.registerAppointment(patient, dentist, treatment, date, LocalTime.of(10, 0));

        verify(observer1, times(1)).onAppointmentRegistered(any(Appointment.class));
        verify(observer2, times(1)).onAppointmentRegistered(any(Appointment.class));
    }

    @Test
    @DisplayName("registerAppointment() does NOT notify observers when validation fails")
    void registerAppointment_validationFails_observersNotNotified() {
        AppointmentObserver observer = mock(AppointmentObserver.class);
        appointmentService.registerObserver(observer);

        assertThrows(IllegalArgumentException.class, () -> appointmentService.registerAppointment(
                patient, dentist, treatment, LocalDate.now().minusDays(1), LocalTime.of(10, 0)));

        verify(observer, never()).onAppointmentRegistered(any());
    }

    @Test
    @DisplayName("searchAppointment() returns the appointment found by the DAO")
    void searchAppointment_found_returnsAppointment() {
        Appointment expected = new Appointment("APT0001", LocalDate.now().plusDays(1),
                LocalTime.of(9, 0), patient, dentist, treatment);
        when(appointmentDAO.findAppointmentByNo("APT0001")).thenReturn(expected);

        Appointment result = appointmentService.searchAppointment("APT0001");

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("searchAppointment() throws IllegalArgumentException when not found")
    void searchAppointment_notFound_throwsException() {
        when(appointmentDAO.findAppointmentByNo("APT9999")).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> appointmentService.searchAppointment("APT9999"));
    }
}