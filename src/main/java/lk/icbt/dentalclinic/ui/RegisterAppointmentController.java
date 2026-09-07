package lk.icbt.dentalclinic.ui;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class RegisterAppointmentController {

    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private TextField contactField;
    @FXML private ComboBox<Option> dentistComboBox;
    @FXML private ComboBox<Option> treatmentComboBox;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<LocalTime> timeComboBox;
    @FXML private Label statusLabel;

    private final ApiClient apiClient = new ApiClient();
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("hh:mm a");

    // Clinic hours: 9:00 AM to 5:00 PM, in 30-minute slots.
    private static final LocalTime CLINIC_OPEN = LocalTime.of(9, 0);
    private static final LocalTime CLINIC_CLOSE = LocalTime.of(17, 0);

    @FXML
    public void initialize() {
        loadDentists();
        loadTreatments();
        loadTimeSlots();
    }

    /**
     * Populates the dentist dropdown from GET /api/dentists, so staff pick a
     * dentist by name instead of typing a raw dentist_id from memory.
     */
    private void loadDentists() {
        try {
            ApiClient.ApiResponse response = apiClient.get("/dentists");
            JsonNode array = apiClient.getMapper().readTree(response.body);

            var options = FXCollections.<Option>observableArrayList();
            for (JsonNode node : array) {
                int id = node.get("dentistId").asInt();
                String label = "Dr. " + node.get("name").asText() + " (" + node.get("specialization").asText() + ")";
                options.add(new Option(id, label));
            }
            dentistComboBox.setItems(options);
        } catch (Exception e) {
            statusLabel.setText("Could not load dentist list. Is ServerLauncher running?");
        }
    }

    /**
     * Populates the treatment dropdown from GET /api/treatments, showing the
     * treatment type and fee so staff can see the cost while selecting it.
     */
    private void loadTreatments() {
        try {
            ApiClient.ApiResponse response = apiClient.get("/treatments");
            JsonNode array = apiClient.getMapper().readTree(response.body);

            var options = FXCollections.<Option>observableArrayList();
            for (JsonNode node : array) {
                int id = node.get("treatmentId").asInt();
                String label = node.get("treatmentType").asText() + " - Rs. " + node.get("consultationFee").asText();
                options.add(new Option(id, label));
            }
            treatmentComboBox.setItems(options);
        } catch (Exception e) {
            statusLabel.setText("Could not load treatment list. Is ServerLauncher running?");
        }
    }

    /**
     * Populates the time dropdown with fixed 30-minute slots within clinic
     * hours (9:00 AM - 5:00 PM), replacing free-text time entry entirely -
     * staff select a slot instead of typing "HH:mm" by hand, which also
     * removes an entire category of invalid input (typos, bad formats,
     * out-of-hours times) at the source.
     */
    private void loadTimeSlots() {
        var slots = FXCollections.<LocalTime>observableArrayList();
        LocalTime current = CLINIC_OPEN;
        while (!current.isAfter(CLINIC_CLOSE.minusMinutes(30))) {
            slots.add(current);
            current = current.plusMinutes(30);
        }
        timeComboBox.setItems(slots);
        timeComboBox.setConverter(new StringConverter<LocalTime>() {
            @Override
            public String toString(LocalTime time) {
                return time == null ? "" : time.format(DISPLAY_FORMAT);
            }

            @Override
            public LocalTime fromString(String string) {
                return LocalTime.parse(string, DISPLAY_FORMAT);
            }
        });
    }

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
            body.dentistId = dentistComboBox.getValue().id;
            body.treatmentId = treatmentComboBox.getValue().id;
            body.appointmentDate = datePicker.getValue().toString();
            body.appointmentTime = timeComboBox.getValue().toString();

            ApiClient.ApiResponse response = apiClient.post("/appointments", body);

            if (response.isSuccess()) {
                JsonNode json = apiClient.getMapper().readTree(response.body);
                statusLabel.setStyle("-fx-text-fill: #2F7A5C;");
                statusLabel.setText("Success! Appointment No: " + json.get("appointmentNo").asText());
                clearForm();
            } else {
                // Surfaces the real database/validation error instead of a generic message.
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
        if (dentistComboBox.getValue() == null) return "Please select a dentist.";
        if (treatmentComboBox.getValue() == null) return "Please select a treatment.";
        if (datePicker.getValue() == null) return "Please select an appointment date.";
        if (datePicker.getValue().isBefore(LocalDate.now())) return "Appointment date cannot be in the past.";
        if (timeComboBox.getValue() == null) return "Please select an appointment time.";
        return null;
    }

    private void clearForm() {
        nameField.clear();
        addressField.clear();
        contactField.clear();
        dentistComboBox.setValue(null);
        treatmentComboBox.setValue(null);
        datePicker.setValue(null);
        timeComboBox.setValue(null);
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

    /** Wraps an id + display label for the ComboBox; toString() drives what's shown. */
    private static class Option {
        final int id;
        final String label;

        Option(int id, String label) {
            this.id = id;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}