package lk.icbt.dentalclinic.ui;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class RegisterAppointmentController {

    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private TextField contactField;
    @FXML private TextField dentistIdField;
    @FXML private TextField treatmentIdField;
    @FXML private DatePicker datePicker;
    @FXML private TextField timeField;
    @FXML private Label statusLabel;

    private final ApiClient apiClient = new ApiClient();

    @FXML
    private void handleRegister() {
        String validationError = validateInputs();
        if (validationError != null) {
            statusLabel.setStyle("-fx-text-fill: #A5544B;");
            statusLabel.setText(validationError);
            return;
        }

        try {
            AppointmentRequestBody body = new AppointmentRequestBody();
            body.patientName = nameField.getText().trim();
            body.patientAddress = addressField.getText().trim();
            body.patientContact = contactField.getText().trim();
            body.dentistId = Integer.parseInt(dentistIdField.getText().trim());
            body.treatmentId = Integer.parseInt(treatmentIdField.getText().trim());
            body.appointmentDate = datePicker.getValue().toString();
            body.appointmentTime = LocalTime.parse(timeField.getText().trim(),
                    DateTimeFormatter.ofPattern("HH:mm")).toString();

            ApiClient.ApiResponse response = apiClient.post("/appointments", body);

            if (response.isSuccess()) {
                JsonNode json = apiClient.getMapper().readTree(response.body);
                statusLabel.setStyle("-fx-text-fill: #2F7A5C;");
                statusLabel.setText("Success! Appointment No: " + json.get("appointmentNo").asText());
                clearForm();
            } else {
                JsonNode error = apiClient.getMapper().readTree(response.body);
                statusLabel.setStyle("-fx-text-fill: #A5544B;");
                statusLabel.setText("Failed: " + error.get("error").asText());
            }
        } catch (Exception e) {
            statusLabel.setStyle("-fx-text-fill: #A5544B;");
            statusLabel.setText("Error contacting server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Client-side validation, matching "implement proper validation mechanisms
     * in order to restrict invalid entries" from the assessment brief.
     */
    private String validateInputs() {
        if (nameField.getText().trim().isEmpty()) return "Patient name is required.";
        if (addressField.getText().trim().isEmpty()) return "Address is required.";
        if (!contactField.getText().trim().matches("\\d{9,10}")) return "Contact number must be 9-10 digits.";
        if (!dentistIdField.getText().trim().matches("\\d+")) return "Dentist ID must be a number.";
        if (!treatmentIdField.getText().trim().matches("\\d+")) return "Treatment ID must be a number.";
        if (datePicker.getValue() == null) return "Please select an appointment date.";
        if (datePicker.getValue().isBefore(LocalDate.now())) return "Appointment date cannot be in the past.";
        try {
            LocalTime.parse(timeField.getText().trim(), DateTimeFormatter.ofPattern("HH:mm"));
        } catch (Exception e) {
            return "Time must be in HH:mm format (e.g. 10:30).";
        }
        return null;
    }

    private void clearForm() {
        nameField.clear();
        addressField.clear();
        contactField.clear();
        dentistIdField.clear();
        treatmentIdField.clear();
        datePicker.setValue(null);
        timeField.clear();
    }

    @FXML
    private void handleBack() throws Exception {
        SceneManager.switchTo("/fxml/main_menu.fxml", 600, 420);
    }

    /** Local DTO matching AppointmentRequest's JSON shape on the server. */
    private static class AppointmentRequestBody {
        public String patientName;
        public String patientAddress;
        public String patientContact;
        public int dentistId;
        public int treatmentId;
        public String appointmentDate;
        public String appointmentTime;
    }
}