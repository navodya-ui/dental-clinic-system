package lk.icbt.dentalclinic.dao;

import lk.icbt.dentalclinic.model.User;
import lk.icbt.dentalclinic.util.DatabaseConnectionManager;
import lk.icbt.dentalclinic.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    private final DatabaseConnectionManager connectionManager;

    public UserDAO() {
        this.connectionManager = DatabaseConnectionManager.getInstance();
    }

    /**
     * Looks up a user by username, then compares the SHA-256 hash of the
     * supplied password against the stored hash (via PasswordUtil) rather
     * than comparing plain text in SQL. The plain-text password never
     * touches the database in this method - only its hash does.
     */
    public User findByCredentials(String username, String plainTextPassword) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");

                    if (PasswordUtil.matches(plainTextPassword, storedHash)) {
                        return new User(
                                rs.getInt("user_id"),
                                rs.getString("username"),
                                storedHash,
                                User.Role.valueOf(rs.getString("role"))
                        );
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // either username not found, or password did not match
    }
}