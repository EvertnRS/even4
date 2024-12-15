package br.upe.controller.fx.screen.event;

import br.upe.controller.fx.mediator.event.EventMediator;
import br.upe.controller.fx.screen.BaseController;
import br.upe.controller.fx.screen.FxController;
import br.upe.facade.FacadeInterface;
import br.upe.persistence.Event;
import br.upe.persistence.repository.EventRepository;
import br.upe.persistence.repository.UserRepository;
import br.upe.utils.CustomRuntimeException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EventScreenController extends BaseController implements FxController {
    private FacadeInterface facade;
    private EventMediator mediator;
    private static final String BG_COLOR = "-fx-background-color: #ffffff; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(128, 128, 128, 1), 3.88, 0, -1, 5);";

    @FXML
    private VBox eventVBox;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Label userEmail;
    @FXML
    private AnchorPane mainPane;
    @FXML
    private TextField searchTextField;
    @FXML
    private Text searchPlaceholder;
    @FXML
    private ImageView logoView6;

    @Override
    public void setFacade(FacadeInterface facade) throws IOException {
        this.facade = facade;
        initial();
    }

    private void initial() throws IOException {
        userEmail.setText(facade.getUserData("email"));
        loadUserEvents();

        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            setupPlaceholders();
            try {
                loadUserEvents();
            } catch (IOException e) {
                throw new CustomRuntimeException("Algo  deu errado", e);
            }
        });

        this.mediator = new EventMediator(this, facade, mainPane, null);
        mediator.registerComponents();
    }

    private void loadUserEvents() throws IOException {
        eventVBox.getChildren().clear();

        List<Event> userEvents = facade.listEvents(facade.getUserData("id"));
        EventRepository eventRepository = EventRepository.getInstance();

        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);

        scrollPane.setStyle("-fx-background-color: transparent; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-top: 2px; -border-color: #cccccc");

        eventVBox.setAlignment(Pos.CENTER);

        for (Event event : userEvents) {
            if (event.getIdOwner().getId().equals(UUID.fromString(facade.getUserData("id")))) {

                VBox eventContainer = new VBox();
                eventContainer.setStyle("-fx-background-color: #d3d3d3; " +
                        "-fx-padding: 10px 20px 10px 20px; " +
                        "-fx-margin: 0 40px 0 40px; " +
                        "-fx-spacing: 5px; " +
                        "-fx-border-radius: 10px; " +
                        "-fx-background-radius: 10px;");

                VBox.setMargin(eventContainer, new Insets(5, 20, 5, 20));
                Label eventLabel;

                if (searchTextField.getText().isEmpty() || String.valueOf(eventRepository.getData(event.getId(), "name")).contains(searchTextField.getText())) {
                    eventLabel = new Label((String) eventRepository.getData(event.getId(), "name"));
                    eventLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #000000;");

                    Button editButton = new Button("Editar");
                    ImageView editIcon = new ImageView(new Image("images/icons/buttons/editIcon.png"));
                    editIcon.setFitWidth(16);
                    editIcon.setFitHeight(16);
                    editButton.setGraphic(editIcon);
                    editButton.setStyle(BG_COLOR);

                    Button deleteButton = new Button("Excluir");
                    ImageView deleteIcon = new ImageView(new Image("images/icons/buttons/deleteIcon.png"));
                    deleteIcon.setFitWidth(16);
                    deleteIcon.setFitHeight(16);
                    deleteButton.setGraphic(deleteIcon);
                    deleteButton.setStyle(BG_COLOR);

                    Button detailsButton = new Button("Detalhes");
                    ImageView detailsIcon = new ImageView(new Image("images/icons/buttons/detailsIcon.png"));
                    detailsIcon.setFitWidth(16);
                    detailsIcon.setFitHeight(16);
                    detailsButton.setGraphic(detailsIcon);
                    detailsButton.setStyle(BG_COLOR);

                    Button articleButton = new Button("Artigos");
                    ImageView articleIcon = new ImageView(new Image("images/icons/buttons/articleIcon.png"));
                    articleIcon.setFitWidth(16);
                    articleIcon.setFitHeight(16);
                    articleButton.setGraphic(articleIcon);
                    articleButton.setStyle(BG_COLOR);

                    detailsButton.setOnAction(e ->
                            handleDetailEvent((UUID) eventRepository.getData(event.getId(), "id")));

                    articleButton.setOnAction(e ->
                    {
                        try {
                            handleEventArticles((UUID) eventRepository.getData(event.getId(), "id"));
                        } catch (IOException ex) {
                            throw new CustomRuntimeException("Algo deu errado", ex);
                        }
                    });

                    editButton.setOnAction(e -> {
                        try {
                            handleEditEvent((UUID) eventRepository.getData(event.getId(), "id"));
                        } catch (IOException ex) {
                            throw new IllegalArgumentException(ex);
                        }
                    });

                    deleteButton.setOnAction(e -> {
                        try {
                            handleDeleteEvent((UUID) eventRepository.getData(event.getId(), "id"), facade.getUserData("id"));
                        } catch (IOException ex) {
                            throw new IllegalArgumentException(ex);
                        }
                    });

                    HBox actionButtons = new HBox(10);
                    actionButtons.setAlignment(Pos.CENTER_RIGHT);
                    actionButtons.getChildren().addAll(articleButton, detailsButton, editButton, deleteButton);

                    eventContainer.getChildren().addAll(eventLabel, actionButtons);

                    eventVBox.getChildren().add(eventContainer);
                }
            }
        }
    }

    private void handleEventArticles(UUID id) throws IOException {
        mediator.setEventId(String.valueOf(id));
        mediator.notify("handleEventArticles");
    }

    private void handleDetailEvent(UUID id) {
        loadScreen("Carregando", () -> {
            EventRepository eventRepository = EventRepository.getInstance();
            UserRepository userRepository = UserRepository.getInstance();

            String content = "Nome: " + eventRepository.getData(id, "name") + "\n" +
                    "Data: " + eventRepository.getData(id, "date") + "\n" +
                    "Descrição: " + eventRepository.getData(id, "description") + "\n" +
                    "Local: " + eventRepository.getData(id, "location") + "\n" +
                    "Administrador: " + userRepository.getData("email") + "\n";

            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Detalhes do Evento");
                alert.setTitle(" ");

                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().clear();
                stage.getIcons().add(new javafx.scene.image.Image("/images/logo/Logo.png"));

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

    public void setupPlaceholders() {
        if (!searchTextField.getText().isEmpty()) {
            searchPlaceholder.setVisible(false);
            logoView6.setVisible(false);
        } else {
            searchPlaceholder.setVisible(true);
            logoView6.setVisible(true);
        }
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

