package br.upe.controller.fx;

import br.upe.controller.*;
import br.upe.persistence.Persistence;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class MainScreenController extends BaseController implements FxController {
    UserController userController;
    EventController eventController;

    @FXML
    private VBox eventVBox;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Label userEmail;
    @FXML
    private AnchorPane mainPane;

    @Override
    public void setUserController(UserController userController) {
        this.userController = userController;
        this.eventController = new EventController();
        initial();
    }

    private void initial() {
        userEmail.setText(userController.getData("email"));
        loadUserEvents();
    }

    public void handleSubmit() throws IOException {
        genericButton("/fxml/submitScreen.fxml", mainPane, userController);
    }

    public void handleUser() throws IOException {
        genericButton("/fxml/userScreen.fxml", mainPane, userController);
    }

    public void handleSubEvent() throws IOException {
        genericButton("/fxml/subEventScreen.fxml", mainPane, userController);
    }

    public void handleSession() throws IOException {
        genericButton("/fxml/sessionScreen.fxml", mainPane, userController);
    }

    public void logout() throws IOException {
        genericButton("/fxml/loginScreen.fxml", mainPane, userController);
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

