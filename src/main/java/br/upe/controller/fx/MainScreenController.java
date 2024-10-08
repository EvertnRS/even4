package br.upe.controller.fx;

import br.upe.controller.*;
import br.upe.persistence.Persistence;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Map;

public class MainScreenController {
    UserController userController;
    EventController eventController;

    @FXML
    private VBox eventVBox;
    @FXML
    private Label userEmail;
    @FXML
    private AnchorPane MainPane;

    public void setUserController(UserController userController) {
        this.userController = userController;
        this.eventController = new EventController();
        initial();
    }

    private void initial() {
        userEmail.setText(userController.getData("email"));
        loadUserEvents();
    }

    public void handleUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/userScreen.fxml"));
            AnchorPane userScreen = loader.load();

            UserScreenController userScreenController = loader.getController();
            userScreenController.setUserController(userController);

            Scene userScene = new Scene(userScreen);
            Stage stage = (Stage) MainPane.getScene().getWindow();

            stage.setScene(userScene);
            stage.setTitle("Even4");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleSubEvent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/subEventScreen.fxml"));
            AnchorPane subEventScreen = loader.load();

            SubEventScreenController subEventController = loader.getController();
            subEventController.setUserController(userController);

            Scene subEventScene = new Scene(subEventScreen);
            Stage stage = (Stage) MainPane.getScene().getWindow();

            stage.setScene(subEventScene);
            stage.setTitle("Even4");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleSession() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sessionScreen.fxml"));
            AnchorPane subEventScreen = loader.load();

            SubEventScreenController subEventController = loader.getController();
            subEventController.setUserController(userController);

            Scene subEventScene = new Scene(subEventScreen);
            Stage stage = (Stage) MainPane.getScene().getWindow();

            stage.setScene(subEventScene);
            stage.setTitle("Even4");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/loginScreen.fxml"));
            AnchorPane loginScreen = loader.load();

            Scene loginScene = new Scene(loginScreen);
            Stage stage = (Stage) MainPane.getScene().getWindow();

            stage.setScene(loginScene);
            stage.setTitle("Even4");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadUserEvents() {
        eventVBox.getChildren().clear();

        eventController.list(userController.getData("id"), "");

        for (Map.Entry<String, Persistence> entry : eventController.getEventHashMap().entrySet()) {
            Persistence persistence = entry.getValue();
            if (persistence.getData("ownerId").equals(userController.getData("id"))) {
                Label eventLabel = new Label(persistence.getData("name"));

                eventVBox.getChildren().add(eventLabel);
            }
        }
    }
}

