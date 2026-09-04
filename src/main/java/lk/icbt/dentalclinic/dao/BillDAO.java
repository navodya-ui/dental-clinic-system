package lk.icbt.dentalclinic.dao;

import lk.icbt.dentalclinic.model.Appointment;
import lk.icbt.dentalclinic.model.Bill;
import lk.icbt.dentalclinic.model.Dentist;
import lk.icbt.dentalclinic.model.Patient;
import lk.icbt.dentalclinic.model.Treatment;
import lk.icbt.dentalclinic.util.DatabaseConnectionManager;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class BillDAO {

    private final DatabaseConnectionManager connectionManager;

    public BillDAO() {
        this.connectionManager = DatabaseConnectionManager.getInstance();
    }

    public boolean saveBill(Bill bill) {
        String sql = "INSERT INTO bills (appointment_id, total_amount, bill_date) VALUES (?, ?, ?)";
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, bill.getAppointment().getAppointmentId());
            ps.setBigDecimal(2, bill.getTotalAmount());
            ps.setDate(3, Date.valueOf(bill.getBillDate()));
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    bill.setBillId(keys.getInt(1));
                }
            }
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Bill findByAppointmentNo(String appointmentNo) {
        String sql =
                "SELECT b.bill_id, b.total_amount, b.bill_date, "
              + "a.appointment_id, a.appointment_no, a.appointment_date, a.appointment_time, a.status, "
              + "p.patient_id, p.name AS patient_name, p.address, p.contact_number, "
              + "d.dentist_id, d.name AS dentist_name, d.specialization, "
              + "t.treatment_id, t.treatment_type, t.consultation_fee "
              + "FROM bills b "
              + "JOIN appointments a ON b.appointment_id = a.appointment_id "
              + "JOIN patients p ON a.patient_id = p.patient_id "
              + "JOIN dentists d ON a.dentist_id = d.dentist_id "
              + "JOIN treatments t ON a.treatment_id = t.treatment_id "
              + "WHERE a.appointment_no = ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, appointmentNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Patient patient = new Patient(rs.getInt("patient_id"), rs.getString("patient_name"),
                            rs.getString("address"), rs.getString("contact_number"));
                    Dentist dentist = new Dentist(rs.getInt("dentist_id"), rs.getString("dentist_name"),
                            rs.getString("specialization"));
                    Treatment treatment = new Treatment(rs.getInt("treatment_id"), rs.getString("treatment_type"),
                            rs.getBigDecimal("consultation_fee"));

                    LocalDate apptDate = rs.getDate("appointment_date").toLocalDate();
                    LocalTime apptTime = rs.getTime("appointment_time").toLocalTime();
                    Appointment appointment = new Appointment(
                            rs.getInt("appointment_id"), rs.getString("appointment_no"),
                            apptDate, apptTime, Appointment.Status.valueOf(rs.getString("status")),
                            patient, dentist, treatment
                    );

                    Bill bill = new Bill(rs.getInt("bill_id"), appointment,
                            rs.getBigDecimal("total_amount"), rs.getDate("bill_date").toLocalDate());
                    return bill;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}