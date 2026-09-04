package lk.icbt.dentalclinic.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Small helper so every controller can switch screens with one line,
 * e.g. SceneManager.switchTo("/fxml/main_menu.fxml", 600, 400);
 * without each controller needing to know about the primary Stage directly.
 */
public class SceneManager {

    private static Stage stage;

    public static void setStage(Stage primaryStage) {
        stage = primaryStage;
    }

    public static void switchTo(String fxmlPath, double width, double height) throws IOException {
        Parent root = FXMLLoader.load(SceneManager.class.getResource(fxmlPath));
        stage.setScene(new Scene(root, width, height));
    }
}