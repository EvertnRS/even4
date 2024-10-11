package br.upe.controller.fx;

import br.upe.controller.EventController;
import br.upe.controller.SubEventController;
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

public class SubEventScreenController extends BaseController implements FxController{
    UserController userController;
    SubEventController subEventController;

    @FXML
    private Label userEmail;
    @FXML
    private VBox subEventVBox;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private AnchorPane subEventPane;

    public void setUserController(UserController userController) {
        this.userController = userController;
        this.subEventController = new SubEventController();
        initial();
    }


    private void initial() {
        userEmail.setText(userController.getData("email"));
        loadUserSubEvents();
    }

    public void handleUser() throws IOException {
        genericButton("/fxml/userScreen.fxml", subEventPane, userController, null);
    }

    public void handleEvent() throws IOException {
        genericButton("/fxml/mainScreen.fxml", subEventPane, userController, null);
    }

    public void handleSubmit() throws IOException {
        genericButton("/fxml/submitScreen.fxml", subEventPane, userController, null);
    }

    public void handleSession() throws IOException {
        genericButton("/fxml/sessionScreen.fxml", subEventPane, userController, null);
    }

    public void handleAddSubEvent() throws IOException {
        genericButton("/fxml/createSubEventScreen.fxml", subEventPane, userController, null);
    }

    public void logout() throws IOException {
        genericButton("/fxml/loginScreen.fxml", subEventPane, userController, null);
    }

    private void loadUserSubEvents() {
        subEventVBox.getChildren().clear();

        subEventController.list(userController.getData("id"), "");

        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);

        scrollPane.setStyle("-fx-padding: 20px;");

        subEventVBox.setAlignment(Pos.CENTER);

        for (Map.Entry<String, Persistence> entry : subEventController.getSubEventHashMap().entrySet()) {
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
                        handleEditEvent(persistence.getData("name"));
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });

                deleteButton.setOnAction(e -> handleDeleteEvent(persistence.getData("id"), userController.getData("id")));

                HBox actionButtons = new HBox(10);
                actionButtons.setAlignment(Pos.CENTER_RIGHT);
                actionButtons.getChildren().addAll(editButton, deleteButton);

                eventContainer.getChildren().addAll(eventLabel, actionButtons);

                subEventVBox.getChildren().add(eventContainer);
            }
        }
    }

    private void handleEditEvent(String eventName) throws IOException {
        genericButton("/fxml/updateEventScreen.fxml", subEventPane, userController, eventName);
    }

    private void handleDeleteEvent(String eventId, String userId) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmação de Exclusão");
        confirmationAlert.setHeaderText("Deseja realmente excluir este subEvento?");
        confirmationAlert.setContentText("Esta ação não pode ser desfeita.");

        ButtonType buttonSim = new ButtonType("Sim");
        ButtonType buttonNao = new ButtonType("Não", ButtonBar.ButtonData.CANCEL_CLOSE);

        confirmationAlert.getButtonTypes().setAll(buttonSim, buttonNao);

        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == buttonSim) {
            System.out.println(userId);
            subEventController.delete(eventId, userId);
            loadUserSubEvents();
        }
    }


}
