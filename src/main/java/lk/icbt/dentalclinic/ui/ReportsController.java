package lk.icbt.dentalclinic.ui;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;

public class ReportsController {

    @FXML private DatePicker reportDatePicker;
    @FXML private TextField dentistIdField;
    @FXML private DatePicker revenueStartPicker;
    @FXML private DatePicker revenueEndPicker;
    @FXML private Label summaryLabel;

    @FXML private TableView<AppointmentRow> appointmentsTable;
    @FXML private TableColumn<AppointmentRow, String> colApptNo;
    @FXML private TableColumn<AppointmentRow, String> colTime;
    @FXML private TableColumn<AppointmentRow, String> colPatient;
    @FXML private TableColumn<AppointmentRow, String> colContact;
    @FXML private TableColumn<AppointmentRow, String> colDentist;
    @FXML private TableColumn<AppointmentRow, String> colTreatment;
    @FXML private TableColumn<AppointmentRow, String> colStatus;

    private final ApiClient apiClient = new ApiClient();

    @FXML
    public void initialize() {
        colApptNo.setCellValueFactory(new PropertyValueFactory<>("appointmentNo"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        colPatient.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("patientContact"));
        colDentist.setCellValueFactory(new PropertyValueFactory<>("dentistName"));
        colTreatment.setCellValueFactory(new PropertyValueFactory<>("treatmentType"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        reportDatePicker.setValue(LocalDate.now());
    }

    @FXML
    private void loadDailyReport() {
        try {
            LocalDate date = reportDatePicker.getValue() != null ? reportDatePicker.getValue() : LocalDate.now();
            ApiClient.ApiResponse response = apiClient.get("/reports/daily?date=" + date);
            populateTable(response.body);
            summaryLabel.setText("Daily Appointments Report for " + date);
        } catch (Exception e) {
            summaryLabel.setText("Error loading report: " + e.getMessage());
        }
    }

    @FXML
    private void loadDentistSchedule() {
        try {
            if (dentistIdField.getText().trim().isEmpty()) {
                summaryLabel.setText("Enter a Dentist ID first.");
                return;
            }
            LocalDate date = reportDatePicker.getValue() != null ? reportDatePicker.getValue() : LocalDate.now();
            int dentistId = Integer.parseInt(dentistIdField.getText().trim());
            ApiClient.ApiResponse response = apiClient.get(
                    "/reports/dentist/" + dentistId + "/schedule?date=" + date);
            populateTable(response.body);
            summaryLabel.setText("Schedule for Dentist ID " + dentistId + " on " + date);
        } catch (Exception e) {
            summaryLabel.setText("Error loading schedule: " + e.getMessage());
        }
    }

    @FXML
    private void loadRevenueSummary() {
        try {
            if (revenueStartPicker.getValue() == null || revenueEndPicker.getValue() == null) {
                summaryLabel.setText("Select both a start and end date.");
                return;
            }
            String url = "/reports/revenue?start=" + revenueStartPicker.getValue()
                    + "&end=" + revenueEndPicker.getValue();
            ApiClient.ApiResponse response = apiClient.get(url);
            JsonNode json = apiClient.getMapper().readTree(response.body);

            summaryLabel.setText(String.format(
                    "Revenue Summary (%s to %s): %d bills, Total Rs. %s, Average Rs. %s",
                    revenueStartPicker.getValue(), revenueEndPicker.getValue(),
                    json.get("totalBills").asInt(),
                    json.get("totalRevenue").asText(),
                    json.get("averageBill").asText()));
            appointmentsTable.getItems().clear();
        } catch (Exception e) {
            summaryLabel.setText("Error loading revenue summary: " + e.getMessage());
        }
    }

    @FXML
    private void loadPopularTreatments() {
        try {
            ApiClient.ApiResponse response = apiClient.get("/reports/popular-treatments");
            JsonNode json = apiClient.getMapper().readTree(response.body);

            StringBuilder sb = new StringBuilder("Most Requested Treatments:  ");
            for (JsonNode node : json) {
                sb.append(node.get("treatmentType").asText())
                  .append(" (").append(node.get("timesBooked").asInt()).append(" bookings)   ");
            }
            summaryLabel.setText(sb.toString());
            appointmentsTable.getItems().clear();
        } catch (Exception e) {
            summaryLabel.setText("Error loading popular treatments: " + e.getMessage());
        }
    }

    private void populateTable(String jsonBody) throws Exception {
        JsonNode array = apiClient.getMapper().readTree(jsonBody);
        ObservableList<AppointmentRow> rows = FXCollections.observableArrayList();

        for (JsonNode node : array) {
            rows.add(new AppointmentRow(
                    node.get("appointmentNo").asText(),
                    node.get("time").asText(),
                    node.get("patientName").asText(),
                    node.get("patientContact").asText(),
                    node.get("dentistName").asText(),
                    node.get("treatmentType").asText(),
                    node.get("status").asText()
            ));
        }
        appointmentsTable.setItems(rows);
    }

    @FXML
    private void handleBack() throws Exception {
        SceneManager.switchTo("/fxml/main_menu.fxml", 600, 420);
    }

    /** Row model backing the TableView - property names must match the FXML PropertyValueFactory strings. */
    public static class AppointmentRow {
        private final String appointmentNo, time, patientName, patientContact, dentistName, treatmentType, status;

        public AppointmentRow(String appointmentNo, String time, String patientName, String patientContact,
                               String dentistName, String treatmentType, String status) {
            this.appointmentNo = appointmentNo;
            this.time = time;
            this.patientName = patientName;
            this.patientContact = patientContact;
            this.dentistName = dentistName;
            this.treatmentType = treatmentType;
            this.status = status;
        }

        public String getAppointmentNo() { return appointmentNo; }
        public String getTime() { return time; }
        public String getPatientName() { return patientName; }
        public String getPatientContact() { return patientContact; }
        public String getDentistName() { return dentistName; }
        public String getTreatmentType() { return treatmentType; }
        public String getStatus() { return status; }
    }
}