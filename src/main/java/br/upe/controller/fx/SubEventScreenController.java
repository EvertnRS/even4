package br.upe.controller.fx;

import br.upe.controller.EventController;
import br.upe.controller.SubEventController;
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

public class SubEventScreenController extends BaseController implements FxController{
    private Facade facade;
    SubEventController subEventController;
    EventController eventController;

    @FXML
    private Label userEmail;
    @FXML
    private VBox subEventVBox;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private AnchorPane subEventPane;

    public void setFacade(Facade facade) throws IOException {
        this.facade = facade;
        this.subEventController = new SubEventController();
        this.eventController = new EventController();
        initial();
    }


    private void initial() throws IOException {
        userEmail.setText(facade.getUserData("email"));
        loadUserSubEvents();
    }

    public void handleUser() throws IOException {
        genericButton("/fxml/userScreen.fxml", subEventPane, facade, null);
    }

    public void handleEvent() throws IOException {
        genericButton("/fxml/mainScreen.fxml", subEventPane, facade, null);
    }

    public void handleSubmit() throws IOException {
        genericButton("/fxml/submitScreen.fxml", subEventPane, facade, null);
    }

    public void handleSession() throws IOException {
        genericButton("/fxml/sessionScreen.fxml", subEventPane, facade, null);
    }

    public void handleAddSubEvent() throws IOException {
        genericButton("/fxml/createSubEventScreen.fxml", subEventPane, facade, null);
    }

    public void logout() throws IOException {
        genericButton("/fxml/loginScreen.fxml", subEventPane, facade, null);
    }

    private void loadUserSubEvents() throws IOException {
        subEventVBox.getChildren().clear();

        subEventController.list(facade.getUserData("id"), "");

        configureScrollPane();
        subEventVBox.setAlignment(Pos.CENTER);

        Map<String, Persistence> eventHashMap = eventController.getEventHashMap();

        subEventController.getSubEventHashMap().entrySet().stream()
                .filter(entry -> isUserOwner(entry.getValue()))
                .forEach(entry -> {
                    VBox subeventContainer = createSubEventContainer(entry.getValue(), eventHashMap);
                    subEventVBox.getChildren().add(subeventContainer);
                });
    }

    private void configureScrollPane() {
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setStyle("-fx-padding: 20px;");
    }

    private boolean isUserOwner(Persistence persistence) {
        return persistence.getData("ownerId").equals(facade.getUserData("id"));
    }

    private VBox createSubEventContainer(Persistence persistence, Map<String, Persistence> eventHashMap) {
        VBox subEventContainer = new VBox();
        subEventContainer.setStyle("-fx-background-color: #d3d3d3; -fx-padding: 10px; -fx-spacing: 5px; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        Label subEventLabel = new Label(persistence.getData("name"));
        subEventLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #000000;");

        Label eventLabel = createEventLabel(persistence.getData("eventId"), eventHashMap);

        HBox actionButtons = createActionButtons(persistence);

        subEventContainer.getChildren().addAll(subEventLabel, actionButtons, eventLabel);
        return subEventContainer;
    }

    private Label createEventLabel(String eventId, Map<String, Persistence> eventHashMap) {
        Label eventLabel = new Label();
        String nameEvent = (eventHashMap != null && eventHashMap.containsKey(eventId))
                ? eventHashMap.get(eventId).getData("name")
                : "Evento não encontrado";
        eventLabel.setText(nameEvent);
        eventLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #555555;");
        return eventLabel;
    }

    private HBox createActionButtons(Persistence persistence) {
        Button editButton = new Button("Editar");
        editButton.setStyle("-fx-background-color: #6fa3ef; -fx-text-fill: white; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(128, 128, 128, 1), 3.88, 0, -1, 5);");
        editButton.setOnAction(e -> handleEditSubEventSafely(persistence.getData("name")));

        Button deleteButton = new Button("Excluir");
        deleteButton.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(128, 128, 128, 1), 3.88, 0, -1, 5);");
        deleteButton.setOnAction(e -> handleDeleteSubEventSafely(persistence.getData("id")));

        HBox actionButtons = new HBox(10);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);
        actionButtons.getChildren().addAll(editButton, deleteButton);
        return actionButtons;
    }

    private void handleEditSubEventSafely(String subEventName) {
        try {
            handleEditSubEvent(subEventName);
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    private void handleDeleteSubEventSafely(String subEventId) {
        try {
            handleDeleteSubEvent(subEventId, facade.getUserData("id"));
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
    }


    private void handleEditSubEvent(String eventName) throws IOException {
        genericButton("/fxml/updateSubEventScreen.fxml", subEventPane, facade, eventName);
    }

    private void handleDeleteSubEvent(String eventId, String userId) throws IOException {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmação de Exclusão");
        confirmationAlert.setHeaderText("Deseja realmente excluir este subEvento?");
        confirmationAlert.setContentText("Esta ação não pode ser desfeita.");

        ButtonType buttonSim = new ButtonType("Sim");
        ButtonType buttonNao = new ButtonType("Não", ButtonBar.ButtonData.CANCEL_CLOSE);

        confirmationAlert.getButtonTypes().setAll(buttonSim, buttonNao);

        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == buttonSim) {
            subEventController.delete(eventId, userId);
            loadUserSubEvents();
        }
    }


}
