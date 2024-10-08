package br.upe.controller.fx;

import br.upe.controller.*;
import br.upe.persistence.Persistence;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class MainScreenController {
    UserController userController;
    EventController eventController;

    @FXML
    private VBox eventVBox;
    @FXML
    private ScrollPane scrollPane;
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

        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);

        scrollPane.setStyle("-fx-padding: 20px;");

        eventVBox.setAlignment(Pos.CENTER);

        for (Map.Entry<String, Persistence> entry : eventController.getEventHashMap().entrySet()) {
            Persistence persistence = entry.getValue();
            if (persistence.getData("ownerId").equals(userController.getData("id"))) {

                VBox eventContainer = new VBox();
                eventContainer.setStyle("-fx-background-color: #d3d3d3; -fx-padding: 10px; -fx-spacing: 5px; -fx-border-radius: 10px; -fx-background-radius: 10px;");

                Label eventLabel = new Label(persistence.getData("name"));
                eventLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #333333;");

                Button editButton = new Button("Editar");
                editButton.setStyle("-fx-background-color: #6fa3ef; -fx-text-fill: white;");

                Button deleteButton = new Button("Excluir");
                deleteButton.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white;");


                editButton.setOnAction(e -> {
                    editEvent(persistence.getData("id"));
                });

                deleteButton.setOnAction(e -> {
                    deleteEvent(persistence.getData("id"), userController.getData("id"));
                });

                HBox actionButtons = new HBox(10);
                actionButtons.getChildren().addAll(editButton, deleteButton);

                eventContainer.getChildren().add(actionButtons);

                eventContainer.getChildren().add(eventLabel);

                eventVBox.getChildren().add(eventContainer);
            }
        }
    }

    private void deleteEvent(String eventId, String userId) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmação de Exclusão");
        confirmationAlert.setHeaderText("Deseja realmente excluir este evento?");
        confirmationAlert.setContentText("Esta ação não pode ser desfeita.");

        ButtonType buttonSim = new ButtonType("Sim");
        ButtonType buttonNao = new ButtonType("Não", ButtonBar.ButtonData.CANCEL_CLOSE);

        confirmationAlert.getButtonTypes().setAll(buttonSim, buttonNao);

        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == buttonSim) {
            System.out.println(userId);
            eventController.delete(eventId, userId);
            loadUserEvents();
        }
    }

    private void editEvent(String eventId) {
    }


}

