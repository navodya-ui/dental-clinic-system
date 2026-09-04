package lk.icbt.dentalclinic.ui;

import javafx.fxml.FXML;

public class HelpController {

    @FXML
    private void handleBack() throws Exception {
        SceneManager.switchTo("/fxml/main_menu.fxml", 600, 420);
    }
}