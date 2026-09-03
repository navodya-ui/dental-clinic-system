package lk.icbt.dentalclinic.model;

public class User {

    public enum Role {
        RECEPTIONIST, ADMIN
    }

    private int userId;
    private String username;
    private String password; // stored hashed - see PasswordUtil when implemented
    private Role role;

    public User() {
    }

    public User(int userId, String username, String password, Role role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{username='" + username + "', role=" + role + "}";
    }
}