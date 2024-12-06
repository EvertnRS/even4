package br.upe.controller.fx;

import br.upe.controller.fx.mediator.EventMediator;
import br.upe.facade.FacadeInterface;
import br.upe.persistence.Event;
import br.upe.persistence.repository.EventRepository;
import br.upe.persistence.repository.UserRepository;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EventScreenController extends BaseController implements FxController {
    private FacadeInterface facade;
    private EventMediator mediator;

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
        userEmail.setText(facade.getUserData("email"));
        loadUserEvents();

        this.mediator = new EventMediator(this, facade, mainPane, null);
        System.out.println("teste");
        mediator.registerComponents();
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
        loadScreen("Carregando", () -> {
            EventRepository eventRepository = EventRepository.getInstance();
            UserRepository userRepository = UserRepository.getInstance();

            String content = "Nome: " + eventRepository.getData(id, "name") + "\n" +
                    "Data: " + eventRepository.getData(id,"date") + "\n" +
                    "Descrição: " + eventRepository.getData(id,"description") + "\n" +
                    "Local: " + eventRepository.getData(id,"location") + "\n" +
                    "Administrador: " + userRepository.getData((UUID) eventRepository.getData(id,"ownerId"), "email") + "\n";

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
        }, mainPane);
    }

    private void handleDeleteEvent(UUID eventId, String userId) throws IOException {
        mediator.setEventId(String.valueOf(eventId));
        Optional<ButtonType> result = (Optional<ButtonType>) mediator.notify("handleDeleteEvent");

        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
            facade.deleteEvent(eventId, userId);
            loadUserEvents();
        }
    }

    private void handleEditEvent(UUID eventId) throws IOException {
        mediator.setEventId(String.valueOf(eventId));
        mediator.notify("handleUpdateEvent");
    }

    @Override
    public TextField getNameTextField() {
        return null;
    }

    @Override
    public TextField getLocationTextField() {
        return null;
    }

    @Override
    public TextField getDescriptionTextField() {
        return null;
    }

    @Override
    public DatePicker getDatePicker() {
        return null;
    }

}

