package lk.icbt.dentalclinic.ui;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class SearchBillController {

    @FXML private TextField appointmentNoField;
    @FXML private TextArea detailsArea;
    @FXML private TextArea receiptArea;
    @FXML private Label statusLabel;

    private final ApiClient apiClient = new ApiClient();
    private String lastSearchedAppointmentNo;

    @FXML
    private void handleSearch() {
        String appointmentNo = appointmentNoField.getText().trim();
        if (appointmentNo.isEmpty()) {
            setStatus("Please enter an appointment number.", true);
            return;
        }

        try {
            ApiClient.ApiResponse response = apiClient.get("/appointments/" + appointmentNo);

            if (response.isSuccess()) {
                JsonNode json = apiClient.getMapper().readTree(response.body);
                detailsArea.setText(formatDetails(json));
                lastSearchedAppointmentNo = appointmentNo;
                setStatus("Appointment found.", false);
            } else {
                detailsArea.clear();
                setStatus("No appointment found with number " + appointmentNo, true);
            }
        } catch (Exception e) {
            setStatus("Error contacting server: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGenerateBill() {
        if (lastSearchedAppointmentNo == null) {
            setStatus("Search for an appointment first.", true);
            return;
        }

        try {
            ApiClient.ApiResponse response = apiClient.post("/bills/" + lastSearchedAppointmentNo, new Object());

            if (response.isSuccess()) {
                JsonNode json = apiClient.getMapper().readTree(response.body);
                receiptArea.setText(json.get("message").asText());
                setStatus("Bill generated successfully.", false);
            } else {
                JsonNode error = apiClient.getMapper().readTree(response.body);
                setStatus("Failed to generate bill: " + error.get("error").asText(), true);
            }
        } catch (Exception e) {
            setStatus("Error contacting server: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    @FXML
    private void handlePrintReceipt() {
        if (receiptArea.getText().isEmpty()) {
            setStatus("Generate the bill first before printing.", true);
            return;
        }
        // Simple "print" - in a full deployment this could open the system print dialog
        // via javafx.print.PrinterJob. For this assessment, we simulate the printed
        // receipt by writing it to a local file, matching the FileHandler concept.
        try {
            java.nio.file.Files.writeString(
                    java.nio.file.Path.of("receipt_" + lastSearchedAppointmentNo + ".txt"),
                    receiptArea.getText());
            setStatus("Receipt saved as receipt_" + lastSearchedAppointmentNo + ".txt", false);
        } catch (Exception e) {
            setStatus("Failed to save receipt: " + e.getMessage(), true);
        }
    }

    private String formatDetails(JsonNode a) {
        return "Appointment No: " + a.get("appointmentNo").asText()
                + "\nDate: " + a.get("appointmentDate").asText()
                + "\nTime: " + a.get("appointmentTime").asText()
                + "\nStatus: " + a.get("status").asText()
                + "\nPatient: " + a.get("patient").get("name").asText()
                + "\nDentist: " + a.get("dentist").get("name").asText()
                + "\nTreatment: " + a.get("treatment").get("treatmentType").asText();
    }

    private void setStatus(String message, boolean isError) {
        statusLabel.setStyle(isError ? "-fx-text-fill: #A5544B;" : "-fx-text-fill: #2F7A5C;");
        statusLabel.setText(message);
    }

    @FXML
    private void handleBack() throws Exception {
        SceneManager.switchTo("/fxml/main_menu.fxml", 600, 420);
    }
}