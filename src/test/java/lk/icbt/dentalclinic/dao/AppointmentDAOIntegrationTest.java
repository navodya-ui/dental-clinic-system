package lk.icbt.dentalclinic.dao;

import lk.icbt.dentalclinic.model.Appointment;
import lk.icbt.dentalclinic.model.Dentist;
import lk.icbt.dentalclinic.model.Patient;
import lk.icbt.dentalclinic.model.Treatment;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test - exercises AppointmentDAO against a REAL MySQL database
 * (dental_clinic_db), unlike AppointmentServiceTest which mocks the DAO.
 * This is deliberately kept separate from the unit tests: it is slower,
 * requires local MySQL to be running with the seeded dentist_id=1 and
 * treatment_id=1, and is EXCLUDED from the automated CI pipeline (see
 * .github/workflows/maven-tests.yml) since GitHub Actions has no access
 * to your local database.
 *
 * To run this locally: remove/comment out the @Disabled annotation,
 * make sure MySQL is running with schema.sql applied, then run this
 * class directly in Eclipse (Run As > JUnit Test).
 */
@Disabled("Integration test - requires a running local MySQL instance. Enable manually to run.")
@DisplayName("AppointmentDAO (integration - requires local MySQL)")
class AppointmentDAOIntegrationTest {

    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    @Test
    @DisplayName("addAppointment() then findAppointmentByNo() round-trips correctly")
    void addAndFindAppointment_roundTrips() {
        Patient patient = new Patient("Integration Test Patient", "Test Address", "0770000001");
        Dentist dentist = new Dentist(1, null, null);   // id only - full record loaded on read
        Treatment treatment = new Treatment(1, null, null);

        String uniqueNo = "APTTEST" + System.currentTimeMillis() % 100000;
        Appointment appointment = new Appointment(uniqueNo, LocalDate.now().plusDays(5),
                LocalTime.of(13, 0), patient, dentist, treatment);

        assertDoesNotThrow(() -> appointmentDAO.addAppointment(appointment));

        Appointment found = appointmentDAO.findAppointmentByNo(uniqueNo);

        assertNotNull(found);
        assertEquals(uniqueNo, found.getAppointmentNo());
        assertEquals("Integration Test Patient", found.getPatient().getName());
        assertEquals(Appointment.Status.SCHEDULED, found.getStatus());
    }

    @Test
    @DisplayName("addAppointment() throws DataAccessException for a non-existent treatment_id")
    void addAppointment_invalidTreatmentId_throwsDataAccessException() {
        Patient patient = new Patient("Bad Treatment Test", "Test Address", "0770000002");
        Dentist dentist = new Dentist(1, null, null);
        Treatment nonExistentTreatment = new Treatment(9999, null, null); // does not exist

        Appointment appointment = new Appointment("APTBAD001", LocalDate.now().plusDays(1),
                LocalTime.of(10, 0), patient, dentist, nonExistentTreatment);

        assertThrows(DataAccessException.class, () -> appointmentDAO.addAppointment(appointment));
    }

    @Test
    @DisplayName("findAppointmentByNo() returns null for an appointment number that does not exist")
    void findAppointmentByNo_notFound_returnsNull() {
        Appointment result = appointmentDAO.findAppointmentByNo("APT_DOES_NOT_EXIST");

        assertNull(result);
    }
}