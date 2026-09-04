package lk.icbt.dentalclinic.dao;

import lk.icbt.dentalclinic.util.DatabaseConnectionManager;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for reporting queries. Kept separate from
 * AppointmentDAO/BillDAO since these are read-only, cross-cutting
 * queries rather than CRUD operations on a single entity - this
 * mirrors the "Come up with a suitable set of reports" requirement
 * in the brief, and demonstrates calling stored procedures/views
 * from JDBC (the "advanced database features" criterion).
 */
public class ReportDAO {

    private final DatabaseConnectionManager connectionManager;

    public ReportDAO() {
        this.connectionManager = DatabaseConnectionManager.getInstance();
    }

    /**
     * Daily Appointments Report - all appointments scheduled for a given date,
     * read from the daily_appointments_view.
     */
    public List<DailyAppointmentRow> getDailyAppointments(LocalDate date) {
        String sql = "SELECT * FROM daily_appointments_view WHERE appointment_date = ? ORDER BY appointment_time";
        List<DailyAppointmentRow> results = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(date));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(new DailyAppointmentRow(
                            rs.getString("appointment_no"),
                            rs.getTime("appointment_time").toLocalTime().toString(),
                            rs.getString("patient_name"),
                            rs.getString("patient_contact"),
                            rs.getString("dentist_name"),
                            rs.getString("treatment_type"),
                            rs.getString("status")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    /**
     * Dentist Schedule View - a single dentist's appointments for a given date.
     * Reuses the same view, filtered further by dentist_id.
     */
    public List<DailyAppointmentRow> getDentistSchedule(int dentistId, LocalDate date) {
        String sql = "SELECT * FROM daily_appointments_view "
                   + "WHERE dentist_id = ? AND appointment_date = ? ORDER BY appointment_time";
        List<DailyAppointmentRow> results = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, dentistId);
            ps.setDate(2, Date.valueOf(date));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(new DailyAppointmentRow(
                            rs.getString("appointment_no"),
                            rs.getTime("appointment_time").toLocalTime().toString(),
                            rs.getString("patient_name"),
                            rs.getString("patient_contact"),
                            rs.getString("dentist_name"),
                            rs.getString("treatment_type"),
                            rs.getString("status")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    /**
     * Revenue Summary Report - calls the sp_get_revenue_summary stored procedure.
     */
    public RevenueSummary getRevenueSummary(LocalDate startDate, LocalDate endDate) {
        String sql = "{CALL sp_get_revenue_summary(?, ?)}";

        try (Connection conn = connectionManager.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setDate(1, Date.valueOf(startDate));
            cs.setDate(2, Date.valueOf(endDate));

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    return new RevenueSummary(
                            rs.getInt("total_bills"),
                            rs.getBigDecimal("total_revenue"),
                            rs.getBigDecimal("average_bill")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new RevenueSummary(0, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    /**
     * Most-Requested Treatments Report - calls sp_most_requested_treatments.
     */
    public List<TreatmentPopularity> getMostRequestedTreatments() {
        String sql = "{CALL sp_most_requested_treatments()}";
        List<TreatmentPopularity> results = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
             CallableStatement cs = conn.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                results.add(new TreatmentPopularity(
                        rs.getString("treatment_type"),
                        rs.getBigDecimal("consultation_fee"),
                        rs.getInt("times_booked")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    // ---------- simple row DTOs for report results ----------

    public static class DailyAppointmentRow {
        public String appointmentNo;
        public String time;
        public String patientName;
        public String patientContact;
        public String dentistName;
        public String treatmentType;
        public String status;

        public DailyAppointmentRow(String appointmentNo, String time, String patientName,
                                    String patientContact, String dentistName,
                                    String treatmentType, String status) {
            this.appointmentNo = appointmentNo;
            this.time = time;
            this.patientName = patientName;
            this.patientContact = patientContact;
            this.dentistName = dentistName;
            this.treatmentType = treatmentType;
            this.status = status;
        }
    }

    public static class RevenueSummary {
        public int totalBills;
        public BigDecimal totalRevenue;
        public BigDecimal averageBill;

        public RevenueSummary(int totalBills, BigDecimal totalRevenue, BigDecimal averageBill) {
            this.totalBills = totalBills;
            this.totalRevenue = totalRevenue;
            this.averageBill = averageBill;
        }
    }

    public static class TreatmentPopularity {
        public String treatmentType;
        public BigDecimal consultationFee;
        public int timesBooked;

        public TreatmentPopularity(String treatmentType, BigDecimal consultationFee, int timesBooked) {
            this.treatmentType = treatmentType;
            this.consultationFee = consultationFee;
            this.timesBooked = timesBooked;
        }
    }
}