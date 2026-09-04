package lk.icbt.dentalclinic.dao;

import lk.icbt.dentalclinic.model.Treatment;
import lk.icbt.dentalclinic.util.DatabaseConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TreatmentDAO {

    private final DatabaseConnectionManager connectionManager;

    public TreatmentDAO() {
        this.connectionManager = DatabaseConnectionManager.getInstance();
    }

    public Treatment findById(int treatmentId) {
        String sql = "SELECT * FROM treatments WHERE treatment_id = ?";
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, treatmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Treatment(
                            rs.getInt("treatment_id"),
                            rs.getString("treatment_type"),
                            rs.getBigDecimal("consultation_fee")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns every treatment type - used to populate the dropdown
     * in the "Register New Appointment" UI screen.
     */
    public List<Treatment> findAll() {
        List<Treatment> treatments = new ArrayList<>();
        String sql = "SELECT * FROM treatments";
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                treatments.add(new Treatment(
                        rs.getInt("treatment_id"),
                        rs.getString("treatment_type"),
                        rs.getBigDecimal("consultation_fee")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return treatments;
    }
}