package br.upe.controller.fx;

import br.upe.facade.FacadeInterface;
import br.upe.persistence.Event;
import br.upe.persistence.Persistence;
import br.upe.persistence.Repository.EventRepository;
import br.upe.persistence.Repository.UserRepository;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MainScreenController extends BaseController implements FxController {
    private FacadeInterface facade;

    @FXML
    private VBox eventVBox;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Label userEmail;
    @FXML
    private AnchorPane mainPane;

    @Override
    public void setFacade(FacadeInterface facade) throws IOException {
        this.facade = facade;
        initial();
    }

    private void initial() throws IOException {
        userEmail.setText(facade.getUserData("name"));
        loadUserEvents();
    }

    public void handleSubmit() throws IOException {
        genericButton("/fxml/submitScreen.fxml", mainPane, facade, null);
    }

    public void handleUser() throws IOException {
        genericButton("/fxml/userScreen.fxml", mainPane, facade, null);
    }

    public void handleSubEvent() throws IOException {
        genericButton("/fxml/subEventScreen.fxml", mainPane, facade, null);
    }

    public void handleSession() throws IOException {
        genericButton("/fxml/sessionScreen.fxml", mainPane, facade, null);
    }

    public void logout() throws IOException {
        genericButton("/fxml/loginScreen.fxml", mainPane, facade, null);
    }

    private void loadUserEvents() throws IOException {
        eventVBox.getChildren().clear();

        List<Event> userEvents = facade.listEvents(facade.getUserData("id"));
        EventRepository eventRepository = EventRepository.getInstance();

        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);

        scrollPane.setStyle("-fx-padding: 20px;");

        eventVBox.setAlignment(Pos.CENTER);

        for (Event event : userEvents) {
            if (event.getIdOwner().getId().equals(UUID.fromString(facade.getUserData("id")))) {

                VBox eventContainer = new VBox();
                eventContainer.setStyle("-fx-background-color: #d3d3d3; -fx-padding: 10px; -fx-spacing: 5px; -fx-border-radius: 10px; -fx-background-radius: 10px;");

                Label eventLabel = new Label((String) eventRepository.getData(event.getId(),"name"));
                eventLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #000000;");

                Button editButton = new Button("Editar");
                editButton.setStyle("-fx-background-color: #6fa3ef; -fx-text-fill: white; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(128, 128, 128, 1), 3.88, 0, -1, 5);");

                Button deleteButton = new Button("Excluir");
                deleteButton.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(128, 128, 128, 1), 3.88, 0, -1, 5);");

                Button detailsButton = new Button("Detalhes");
                detailsButton.setStyle("-fx-background-color: #ff914d; -fx-text-fill: white; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(128, 128, 128, 1), 3.88, 0, -1, 5);");

                detailsButton.setOnAction(e ->
                    handleDetailEvent((UUID) eventRepository.getData(event.getId(),"id")));

                editButton.setOnAction(e -> {
                    try {
                        handleEditEvent((UUID) eventRepository.getData(event.getId(),"id"));
                    } catch (IOException ex) {
                        throw new IllegalArgumentException(ex);
                    }
                });

                deleteButton.setOnAction(e -> {
                    try {
                        handleDeleteEvent((UUID) eventRepository.getData(event.getId(),"id"), facade.getUserData("id"));
                    } catch (IOException ex) {
                        throw new IllegalArgumentException(ex);
                    }
                });

                HBox actionButtons = new HBox(10);
                actionButtons.setAlignment(Pos.CENTER_RIGHT);
                actionButtons.getChildren().addAll(detailsButton,editButton, deleteButton);

                eventContainer.getChildren().addAll(eventLabel, actionButtons);

                eventVBox.getChildren().add(eventContainer);
            }
        }
    }

    private void handleDetailEvent(UUID id) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalhes do Evento");
        alert.setHeaderText("Detalhes do Evento");

        EventRepository eventRepository = EventRepository.getInstance();
        UserRepository userRepository = UserRepository.getInstance();

        String content = "Nome: " + eventRepository.getData(id, "name") + "\n" +
                "Data: " + eventRepository.getData(id,"date") + "\n" +
                "Descrição: " + eventRepository.getData(id,"description") + "\n" +
                "Local: " + eventRepository.getData(id,"location") + "\n" +
                "Administrador: " + (String) userRepository.getData((UUID) eventRepository.getData(id,"ownerId"), "email") + "\n";

        alert.setContentText(content);
        alert.showAndWait();
    }

    private void handleDeleteEvent(UUID eventId, String userId) throws IOException {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmação de Exclusão");
        confirmationAlert.setHeaderText("Deseja realmente excluir este evento?");
        confirmationAlert.setContentText("Esta ação não pode ser desfeita.");

        ButtonType buttonSim = new ButtonType("Sim");
        ButtonType buttonNao = new ButtonType("Não", ButtonBar.ButtonData.CANCEL_CLOSE);

        confirmationAlert.getButtonTypes().setAll(buttonSim, buttonNao);

        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == buttonSim) {
            facade.deleteEvent(eventId, userId);
            loadUserEvents();
        }
    }

    private void handleEditEvent(UUID eventId) throws IOException {
        genericButton("/fxml/updateEventScreen.fxml", mainPane, facade, String.valueOf(eventId));
    }

    public void handleAddEvent() throws IOException {
        genericButton("/fxml/createEventScreen.fxml", mainPane, facade, null);
    }

}

