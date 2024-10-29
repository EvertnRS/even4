package br.upe.controller.fx;

import br.upe.controller.AttendeeController;
import br.upe.controller.SessionController;
import br.upe.controller.UserController;
import br.upe.facade.Facade;
import br.upe.persistence.Persistence;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class EnterSessionScreenController extends BaseController implements FxController{
    private Facade facade;
    AttendeeController attendeeController;

    @FXML
    private VBox attendeeVBox;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Label userEmail;
    @FXML
    private AnchorPane attendeePane;

    public void setFacade(Facade facade) throws IOException {
        this.facade = facade;
        this.attendeeController = new AttendeeController();
        initial();
    }

    private void initial() throws IOException {
        userEmail.setText(facade.getUserData("email"));
        loadSessions();
    }

    public void handleUser() throws IOException {
        genericButton("/fxml/userScreen.fxml", attendeePane, facade, null);
    }

    public void handleSubEvent() throws IOException {
        genericButton("/fxml/subEventScreen.fxml", attendeePane, facade, null);
    }

    public void handleEvent() throws IOException {
        genericButton("/fxml/mainScreen.fxml", attendeePane, facade, null);
    }

    public void handleSession() throws IOException {
        genericButton("/fxml/sessionScreen.fxml", attendeePane, facade, null);
    }
    public void handleAddSession() throws IOException{
        genericButton("/fxml/sessionScreen.fxml",attendeePane, facade,null);
    }


    public void logout() throws IOException {
        genericButton("/fxml/loginScreen.fxml", attendeePane, facade, null);
    }
    private void loadSessions() throws IOException {
        attendeeVBox.getChildren().clear();

        attendeeController.list(facade.getUserData("id"), "");

        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);

        scrollPane.setStyle("-fx-padding: 20px;");

        attendeeVBox.setAlignment(Pos.CENTER);

        for (Map.Entry<String, Persistence> entry : attendeeController.getAttendeeHashMap().entrySet()) {
            Persistence persistence = entry.getValue();
            if (persistence.getData("userId").equals(facade.getUserData("id"))) {

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
                        handleEditAttendee(persistence.getData("name"));
                    } catch (IOException ex) {
                        throw new IllegalArgumentException(ex);
                    }
                });

                deleteButton.setOnAction(e -> {
                    try {
                        handleDeleteAttendee(persistence.getData("id"), facade.getUserData("id"),persistence.getData("sessionId"));
                    } catch (IOException ex) {
                        throw new IllegalArgumentException(ex);
                    }
                });

                HBox actionButtons = new HBox(10);
                actionButtons.setAlignment(Pos.CENTER_RIGHT);
                actionButtons.getChildren().addAll(editButton, deleteButton);

                eventContainer.getChildren().addAll(eventLabel, actionButtons);

                attendeeVBox.getChildren().add(eventContainer);
            }
        }
    }
    private void handleEditAttendee(String eventName) throws IOException {
        genericButton("/fxml/updateAttendeeScreen.fxml", attendeePane, facade, eventName);
    }
    private void handleDeleteAttendee(String eventId, String userId, String sessionId) throws IOException {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmação de Exclusão");
        confirmationAlert.setHeaderText("Deseja realmente excluir este participante?");
        confirmationAlert.setContentText("Esta ação não pode ser desfeita.");

        ButtonType buttonSim = new ButtonType("Sim");
        ButtonType buttonNao = new ButtonType("Não", ButtonBar.ButtonData.CANCEL_CLOSE);

        confirmationAlert.getButtonTypes().setAll(buttonSim, buttonNao);

        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == buttonSim) {
            attendeeController.delete(userId, "id", sessionId);
            loadSessions();
        }
    }
    public void handleAddAttendee() throws IOException {
        genericButton("/fxml/createAttendeeScreen.fxml", attendeePane, facade, null);
    }
}
