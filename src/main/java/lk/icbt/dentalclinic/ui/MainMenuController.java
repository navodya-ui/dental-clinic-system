package lk.icbt.dentalclinic.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainMenuController {

    @FXML private Label welcomeLabel;

    @FXML
    public void initialize() {
        welcomeLabel.setText("Welcome, " + Session.getCurrentUsername()
                + "  (" + Session.getCurrentRole() + ")");
    }

    @FXML
    private void goToRegister() throws Exception {
        SceneManager.switchTo("/fxml/register_appointment.fxml", 500, 560);
    }

    @FXML
    private void goToSearch() throws Exception {
        SceneManager.switchTo("/fxml/search_bill.fxml", 560, 560);
    }

    @FXML
    private void goToReports() throws Exception {
        SceneManager.switchTo("/fxml/reports.fxml", 780, 560);
    }

    @FXML
    private void goToHelp() throws Exception {
        SceneManager.switchTo("/fxml/help.fxml", 520, 480);
    }

    @FXML
    private void handleExit() {
        Session.clear();
        Platform.exit();
    }
}