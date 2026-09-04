package lk.icbt.dentalclinic.ui;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    private final ApiClient apiClient = new ApiClient();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both username and password.");
            return;
        }

        try {
            AuthRequestBody body = new AuthRequestBody(username, password);
            ApiClient.ApiResponse response = apiClient.post("/auth/login", body);

            if (response.isSuccess()) {
                JsonNode json = apiClient.getMapper().readTree(response.body);
                Session.setCurrentUser(json.get("username").asText(), json.get("role").asText());
                SceneManager.switchTo("/fxml/main_menu.fxml", 600, 420);
            } else {
                statusLabel.setText("Invalid username or password.");
            }
        } catch (Exception e) {
            statusLabel.setText("Cannot reach server. Is ServerLauncher running?");
            e.printStackTrace();
        }
    }

    /** Small local DTO matching AuthResource.LoginRequest's JSON shape. */
    private static class AuthRequestBody {
        public String username;
        public String password;

        AuthRequestBody(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }
}