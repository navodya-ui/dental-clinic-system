package lk.icbt.dentalclinic.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX entry point. Loads the login screen first; every subsequent
 * screen is swapped into the same Stage via SceneManager so we keep
 * a single top-level window throughout the session.
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        SceneManager.setStage(primaryStage);
        primaryStage.setTitle("Sunrise Dental Clinic - Appointment Management System");

        Parent root = FXMLLoader.load(MainApp.class.getResource("/fxml/login.fxml"));
        primaryStage.setScene(new Scene(root, 480, 360));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}