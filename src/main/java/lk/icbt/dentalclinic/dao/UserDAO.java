package lk.icbt.dentalclinic.dao;

import lk.icbt.dentalclinic.model.User;
import lk.icbt.dentalclinic.util.DatabaseConnectionManager;

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
     * Looks up a user by username/password for login.
     * NOTE: the seed data in schema.sql stores plain-text passwords for
     * simplicity during development. Before submission, switch this to
     * compare hashed passwords (e.g. BCrypt) - worth a sentence in your
     * report under "ethical/secure coding practice".
     */
    public User findByCredentials(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            User.Role.valueOf(rs.getString("role"))
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}