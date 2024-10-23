package br.upe.controller.fx;

import br.upe.controller.EventController;
import br.upe.controller.SessionController;
import br.upe.controller.UserController;
import br.upe.persistence.Persistence;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

public class SessionScreenController extends BaseController implements FxController {
    private UserController userController;
    private EventController eventController;
    private SessionController sessionController;

    @FXML
    private Label userEmail;
    @FXML
    private VBox sessionVBox;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private AnchorPane sessionPane;

    public void setUserController(UserController userController) {
        this.userController = userController;
        this.sessionController = new SessionController();
        this.eventController = new EventController();
        initial();
    }

    private void initial() {
        userEmail.setText(userController.getData("email"));
        loadUserSessions();
    }

    public void handleUser() throws IOException {
        genericButton("/fxml/userScreen.fxml", sessionPane, userController, null);
    }

    public void handleSubEvent() throws IOException {
        genericButton("/fxml/subEventScreen.fxml", sessionPane, userController, null);
    }

    public void handleSubmit() throws IOException {
        genericButton("/fxml/submitScreen.fxml", sessionPane, userController, null);
    }

    public void handleEvent() throws IOException {
        genericButton("/fxml/mainScreen.fxml", sessionPane, userController, null);
    }

    public void handleAddSession() throws IOException {
        genericButton("/fxml/createSessionScreen.fxml", sessionPane, userController, null);
    }

    private void loadUserSessions() {
        sessionVBox.getChildren().clear();

        sessionController.list(userController.getData("id"), "");

        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);

        scrollPane.setStyle("-fx-padding: 20px;");

        sessionVBox.setAlignment(Pos.CENTER);

        for (Map.Entry<String, Persistence> entry : sessionController.getSessionHashMap().entrySet()) {
            Persistence persistence = entry.getValue();
            if (persistence.getData("ownerId").equals(userController.getData("id"))) {

                VBox eventContainer = new VBox();
                eventContainer.setStyle("-fx-background-color: #d3d3d3; -fx-padding: 10px; -fx-spacing: 5px; -fx-border-radius: 10px; -fx-background-radius: 10px;");

                Label eventLabel = new Label(persistence.getData("name"));
                eventLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #333333;");

                Button editButton = new Button("Editar");
                editButton.setStyle("-fx-background-color: #6fa3ef; -fx-text-fill: white; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(128, 128, 128, 1), 3.88, 0, -1, 5);");

                Button deleteButton = new Button("Excluir");
                deleteButton.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(128, 128, 128, 1), 3.88, 0, -1, 5);");


                editButton.setOnAction(e -> {
                    try {
                        handleEditSession(persistence.getData("name"));
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });

                deleteButton.setOnAction(e -> handleDeleteSession(persistence.getData("id"), userController.getData("id")));

                HBox actionButtons = new HBox(10);
                actionButtons.setAlignment(Pos.CENTER_RIGHT);
                actionButtons.getChildren().addAll(editButton, deleteButton);

                eventContainer.getChildren().addAll(eventLabel, actionButtons);

                sessionVBox.getChildren().add(eventContainer);
            }
        }
    }

    private void handleEditSession(String eventName) throws IOException {
        genericButton("/fxml/updateSessionScreen.fxml", sessionPane, userController, eventName);
    }

    private void handleDeleteSession(String eventId, String userId) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmação de Exclusão");
        confirmationAlert.setHeaderText("Deseja realmente excluir este sessão?");
        confirmationAlert.setContentText("Esta ação não pode ser desfeita.");

        ButtonType buttonSim = new ButtonType("Sim");
        ButtonType buttonNao = new ButtonType("Não", ButtonBar.ButtonData.CANCEL_CLOSE);

        confirmationAlert.getButtonTypes().setAll(buttonSim, buttonNao);

        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == buttonSim) {
            System.out.println(userId);
            sessionController.delete(eventId, userId);
            loadUserSessions();
        }
    }

    public void logout() throws IOException {
        genericButton("/fxml/loginScreen.fxml", sessionPane, userController, null);
    }
}
