package lk.icbt.dentalclinic.dao;

import lk.icbt.dentalclinic.model.Appointment;
import lk.icbt.dentalclinic.model.Dentist;
import lk.icbt.dentalclinic.model.Patient;
import lk.icbt.dentalclinic.model.Treatment;
import lk.icbt.dentalclinic.util.DatabaseConnectionManager;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Data Access Object for Appointment records.
 * Encapsulates all JDBC/SQL logic so the service (business) layer
 * never talks to the database directly - this is the DAO pattern,
 * and it is what makes the "data" tier of the 3-tier architecture concrete.
 */
public class AppointmentDAO {

    private final DatabaseConnectionManager connectionManager;

    public AppointmentDAO() {
        this.connectionManager = DatabaseConnectionManager.getInstance();
    }

    /**
     * Inserts a new appointment along with its related patient record, and
     * derives the appointment number from the database's own AUTO_INCREMENT
     * appointment_id - inserted with a temporary placeholder number first
     * (to satisfy the NOT NULL/UNIQUE constraint), then immediately updated
     * to the final "APTnnnn" value once the real id is known, all within
     * the same transaction.
     *
     * This replaces an earlier approach that queried MAX(appointment_no)
     * as a separate step before inserting: under concurrent requests (e.g.
     * a fast double-click, or two staff registering appointments at the
     * same moment), two requests could read the same "next number" before
     * either committed, causing a duplicate-key failure. AUTO_INCREMENT
     * allocation is atomic at the database level, so deriving the number
     * from it instead removes the race condition entirely - no
     * application-level locking is needed.
     *
     * Throws a DataAccessException (unchecked) with the real database message
     * on failure, instead of silently returning false - this lets the UI show
     * the actual cause (e.g. "unknown treatment_id") rather than a generic error.
     */
    public void addAppointment(Appointment appointment) {
        String insertPatientSql =
                "INSERT INTO patients (name, address, contact_number) VALUES (?, ?, ?)";
        String insertAppointmentSql =
                "INSERT INTO appointments (appointment_no, appointment_date, appointment_time, "
              + "status, patient_id, dentist_id, treatment_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String updateAppointmentNoSql =
                "UPDATE appointments SET appointment_no = ? WHERE appointment_id = ?";

        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            conn.setAutoCommit(false); // treat patient + appointment insert as one transaction

            int patientId;
            try (PreparedStatement psPatient = conn.prepareStatement(insertPatientSql,
                    Statement.RETURN_GENERATED_KEYS)) {
                psPatient.setString(1, appointment.getPatient().getName());
                psPatient.setString(2, appointment.getPatient().getAddress());
                psPatient.setString(3, appointment.getPatient().getContactNumber());
                psPatient.executeUpdate();

                try (ResultSet keys = psPatient.getGeneratedKeys()) {
                    if (keys.next()) {
                        patientId = keys.getInt(1);
                        appointment.getPatient().setPatientId(patientId);
                    } else {
                        throw new SQLException("Failed to obtain generated patient_id");
                    }
                }
            }

            // Temporary placeholder - guaranteed unique because it embeds nanoTime().
            // Overwritten with the real "APTnnnn" number two statements below.
            String placeholderNo = "TMP-" + System.nanoTime();
            int generatedAppointmentId;

            try (PreparedStatement psAppt = conn.prepareStatement(insertAppointmentSql,
                    Statement.RETURN_GENERATED_KEYS)) {
                psAppt.setString(1, placeholderNo);
                psAppt.setDate(2, Date.valueOf(appointment.getAppointmentDate()));
                psAppt.setTime(3, Time.valueOf(appointment.getAppointmentTime()));
                psAppt.setString(4, appointment.getStatus().name());
                psAppt.setInt(5, patientId);
                psAppt.setInt(6, appointment.getDentist().getDentistId());
                psAppt.setInt(7, appointment.getTreatment().getTreatmentId());
                psAppt.executeUpdate();

                try (ResultSet keys = psAppt.getGeneratedKeys()) {
                    if (keys.next()) {
                        generatedAppointmentId = keys.getInt(1);
                        appointment.setAppointmentId(generatedAppointmentId);
                    } else {
                        throw new SQLException("Failed to obtain generated appointment_id");
                    }
                }
            }

            String finalAppointmentNo = String.format("APT%04d", generatedAppointmentId);
            try (PreparedStatement psUpdate = conn.prepareStatement(updateAppointmentNoSql)) {
                psUpdate.setString(1, finalAppointmentNo);
                psUpdate.setInt(2, generatedAppointmentId);
                psUpdate.executeUpdate();
            }
            appointment.setAppointmentNo(finalAppointmentNo);

            conn.commit();

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            e.printStackTrace();
            throw new DataAccessException("Database error: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Finds a single appointment by its business-facing appointment number,
     * joining across patients, dentists and treatments so the returned
     * Appointment object is fully populated.
     */
    public Appointment findAppointmentByNo(String appointmentNo) {
        String sql =
                "SELECT a.appointment_id, a.appointment_no, a.appointment_date, a.appointment_time, a.status, "
              + "p.patient_id, p.name AS patient_name, p.address, p.contact_number, "
              + "d.dentist_id, d.name AS dentist_name, d.specialization, "
              + "t.treatment_id, t.treatment_type, t.consultation_fee "
              + "FROM appointments a "
              + "JOIN patients p ON a.patient_id = p.patient_id "
              + "JOIN dentists d ON a.dentist_id = d.dentist_id "
              + "JOIN treatments t ON a.treatment_id = t.treatment_id "
              + "WHERE a.appointment_no = ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, appointmentNo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToAppointment(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // not found
    }

    /**
     * Updates the status of an appointment (e.g., marking it COMPLETED after billing).
     */
    public boolean updateStatus(int appointmentId, Appointment.Status status) {
        String sql = "UPDATE appointments SET status = ? WHERE appointment_id = ?";
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.name());
            ps.setInt(2, appointmentId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Appointment mapRowToAppointment(ResultSet rs) throws SQLException {
        Patient patient = new Patient(
                rs.getInt("patient_id"),
                rs.getString("patient_name"),
                rs.getString("address"),
                rs.getString("contact_number")
        );

        Dentist dentist = new Dentist(
                rs.getInt("dentist_id"),
                rs.getString("dentist_name"),
                rs.getString("specialization")
        );

        Treatment treatment = new Treatment(
                rs.getInt("treatment_id"),
                rs.getString("treatment_type"),
                rs.getBigDecimal("consultation_fee")
        );

        LocalDate date = rs.getDate("appointment_date").toLocalDate();
        LocalTime time = rs.getTime("appointment_time").toLocalTime();
        Appointment.Status status = Appointment.Status.valueOf(rs.getString("status"));

        return new Appointment(
                rs.getInt("appointment_id"),
                rs.getString("appointment_no"),
                date,
                time,
                status,
                patient,
                dentist,
                treatment
        );
    }
}