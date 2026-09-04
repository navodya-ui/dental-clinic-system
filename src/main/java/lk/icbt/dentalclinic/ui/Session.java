package lk.icbt.dentalclinic.ui;

/**
 * Holds the currently logged-in staff member's context for the
 * duration of the JavaFX session. Kept as simple static state since
 * this is a single-user desktop client, not a multi-session server.
 */
public class Session {

    private static String currentUsername;
    private static String currentRole;

    public static void setCurrentUser(String username, String role) {
        currentUsername = username;
        currentRole = role;
    }

    public static String getCurrentUsername() {
        return currentUsername;
    }

    public static String getCurrentRole() {
        return currentRole;
    }

    public static void clear() {
        currentUsername = null;
        currentRole = null;
    }
}