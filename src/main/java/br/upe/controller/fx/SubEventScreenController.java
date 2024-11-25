package br.upe.controller.fx;

import br.upe.facade.FacadeInterface;
import br.upe.persistence.Event;
import br.upe.persistence.SubEvent;
import br.upe.persistence.repository.EventRepository;
import br.upe.persistence.repository.Persistence;
import br.upe.persistence.repository.SubEventRepository;
import br.upe.persistence.repository.UserRepository;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SubEventScreenController extends BaseController implements FxController{
    private FacadeInterface facade;

    @FXML
    private Label userEmail;
    @FXML
    private VBox subEventVBox;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private AnchorPane subEventPane;

    public void setFacade(FacadeInterface facade) throws IOException {
        this.facade = facade;
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

        List<SubEvent> userSubEvents = facade.listSubEvents(facade.getUserData("id"));
        SubEventRepository subeventRepository = SubEventRepository.getInstance();

        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);

        scrollPane.setStyle("-fx-padding: 20px;");

        subEventVBox.setAlignment(Pos.CENTER);

        for (SubEvent subevent : userSubEvents) {
            if (subevent.getOwnerId().getId().equals(UUID.fromString(facade.getUserData("id")))) {

                VBox eventContainer = new VBox();
                eventContainer.setStyle("-fx-background-color: #d3d3d3; -fx-padding: 10px; -fx-spacing: 5px; -fx-border-radius: 10px; -fx-background-radius: 10px;");

                Label subEventLabel = new Label((String) subeventRepository.getData(subevent.getId(),"name"));
                subEventLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #000000;");

                Button editButton = new Button("Editar");
                editButton.setStyle("-fx-background-color: #6fa3ef; -fx-text-fill: white; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(128, 128, 128, 1), 3.88, 0, -1, 5);");

                Button deleteButton = new Button("Excluir");
                deleteButton.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(128, 128, 128, 1), 3.88, 0, -1, 5);");

                Button detailsButton = new Button("Detalhes");
                detailsButton.setStyle("-fx-background-color: #ff914d; -fx-text-fill: white; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(128, 128, 128, 1), 3.88, 0, -1, 5);");

                detailsButton.setOnAction(e ->
                        handleDetailSubEvent((UUID) subeventRepository.getData(subevent.getId(),"id")));

                editButton.setOnAction(e -> {
                    try {
                        handleEditSubEvent((UUID) subeventRepository.getData(subevent.getId(),"id"));
                    } catch (IOException ex) {
                        throw new IllegalArgumentException(ex);
                    }
                });

                deleteButton.setOnAction(e -> {
                    try {
                        handleDeleteSubEvent((UUID) subeventRepository.getData(subevent.getId(),"id"), facade.getUserData("id"));
                    } catch (IOException ex) {
                        throw new IllegalArgumentException(ex);
                    }
                });

                HBox actionButtons = new HBox(10);
                actionButtons.setAlignment(Pos.CENTER_RIGHT);
                actionButtons.getChildren().addAll(detailsButton,editButton, deleteButton);
                Label eventLabel = createEventLabel((UUID) subeventRepository.getData(subevent.getId(), "eventId"));
                eventContainer.getChildren().addAll(subEventLabel, actionButtons, eventLabel);

                subEventVBox.getChildren().add(eventContainer);
            }
        }
    }


    private Label createEventLabel(UUID eventId) {
        Label eventLabel = new Label();
        EventRepository eventRepository = EventRepository.getInstance();
        String nameEvent = (String) eventRepository.getData(eventId, "name");
        eventLabel.setText(nameEvent);
        eventLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #555555;");
        return eventLabel;
    }


    private void handleDetailSubEvent(UUID id) {
        loadScreen("Carregando", () -> {
            EventRepository eventRepository = EventRepository.getInstance();
            SubEventRepository subeventRepository = SubEventRepository.getInstance();
            UserRepository userRepository = UserRepository.getInstance();

            String content = "Nome: " + subeventRepository.getData(id, "name") + "\n" +
                    "Data: " + subeventRepository.getData(id, "date") + "\n" +
                    "Descrição: " + subeventRepository.getData(id, "description") + "\n" +
                    "Local: " + subeventRepository.getData(id, "location") + "\n" +
                    "Evento: " + eventRepository.getData((UUID) subeventRepository.getData(id, "eventId"), "name") + "\n" +
                    "Administrador: " + userRepository.getData((UUID) subeventRepository.getData(id, "ownerId"), "email") + "\n";

            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Detalhes do Evento");
                alert.setTitle(" ");

                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().clear();
                stage.getIcons().add(new javafx.scene.image.Image("/images/Logo.png"));

                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.setStyle("-fx-background-color: #f0f0f0; -fx-font-size: 14px; -fx-text-fill: #333333;");
                dialogPane.lookup(".header-panel").setStyle("-fx-background-color: #ff914d; -fx-text-fill: #ffffff; -fx-font-weight: bold;");
                dialogPane.lookup(".content").setStyle("-fx-font-size: 14px; -fx-text-fill: rgb(51,51,51);");

                alert.setContentText(content);
                alert.showAndWait();
            });
        }, subEventPane);
    }


    private void handleEditSubEvent(UUID eventName) throws IOException {
        SubEventRepository subeventRepository = SubEventRepository.getInstance();
        genericButton("/fxml/updateSubEventScreen.fxml", subEventPane, facade, String.valueOf(subeventRepository.getData(eventName, "id")));
    }

    private void handleDeleteSubEvent(UUID eventId, String userId) throws IOException {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmação de Exclusão");
        confirmationAlert.setHeaderText("Deseja realmente excluir este subEvento?");
        confirmationAlert.setContentText("Esta ação não pode ser desfeita.");

        ButtonType buttonSim = new ButtonType("Sim");
        ButtonType buttonNao = new ButtonType("Não", ButtonBar.ButtonData.CANCEL_CLOSE);

        confirmationAlert.getButtonTypes().setAll(buttonSim, buttonNao);

        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == buttonSim) {
            facade.deleteSubEvent(eventId, userId);
            loadUserSubEvents();
        }
    }
}