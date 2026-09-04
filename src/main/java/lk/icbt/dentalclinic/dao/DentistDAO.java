package lk.icbt.dentalclinic.dao;

import lk.icbt.dentalclinic.model.Dentist;
import lk.icbt.dentalclinic.util.DatabaseConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DentistDAO {

    private final DatabaseConnectionManager connectionManager;

    public DentistDAO() {
        this.connectionManager = DatabaseConnectionManager.getInstance();
    }

    public Dentist findById(int dentistId) {
        String sql = "SELECT * FROM dentists WHERE dentist_id = ?";
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, dentistId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Dentist(
                            rs.getInt("dentist_id"),
                            rs.getString("name"),
                            rs.getString("specialization")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns every dentist - used to populate the dropdown
     * in the "Register New Appointment" UI screen.
     */
    public List<Dentist> findAll() {
        List<Dentist> dentists = new ArrayList<>();
        String sql = "SELECT * FROM dentists";
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                dentists.add(new Dentist(
                        rs.getInt("dentist_id"),
                        rs.getString("name"),
                        rs.getString("specialization")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dentists;
    }
}