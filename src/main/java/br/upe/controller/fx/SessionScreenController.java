package br.upe.controller.fx;

import br.upe.controller.fx.mediator.SessionMediator;
import br.upe.facade.FacadeInterface;
import br.upe.persistence.Event;
import br.upe.persistence.Session;
import br.upe.persistence.SubEvent;
import br.upe.persistence.repository.*;
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
import java.util.*;

public class SessionScreenController extends BaseController implements FxController {
    private FacadeInterface facade;
    private SessionMediator mediator;

    @FXML
    private Label userEmail;
    @FXML
    private VBox sessionVBox;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private AnchorPane sessionPane;
    @FXML
    private TextField searchTextField;
    @FXML
    private Text searchPlaceholder;
    @FXML
    private ImageView logoView6;

    public void setFacade(FacadeInterface facade) throws IOException {
        this.facade = facade;
        initialize();
    }

    private void initialize() throws IOException {
        userEmail.setText(facade.getUserData("email"));
        loadUserSessions();

        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            setupPlaceholders();
            try {
                loadUserSessions();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        this.mediator = new SessionMediator(this, facade, sessionPane, null);
        mediator.registerComponents();
    }

    private void loadUserSessions() throws IOException {
        SessionRepository sessionRepository = SessionRepository.getInstance();
        sessionVBox.getChildren().clear();

        // Configurações do ScrollPane
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-top: 2px; -border-color: #cccccc");
        sessionVBox.setAlignment(Pos.CENTER);

        List<Session> userSessions = facade.listSessions(facade.getUserData("id")); // Método para buscar as sessões
        for (Session session : userSessions) {
            if (searchTextField.getText().isEmpty() || String.valueOf(sessionRepository.getData(session.getId(),"name")).contains(searchTextField.getText())) {
                VBox sessionContainer = createSessionContainer(session);
                sessionVBox.getChildren().add(sessionContainer);
            }
        }
    }

    private VBox createSessionContainer(Session session) {
        SessionRepository sessionRepository = SessionRepository.getInstance();

        VBox sessionContainer = new VBox();
        sessionContainer.setStyle("-fx-background-color: #d3d3d3; " +
                "-fx-padding: 10px 20px 10px 20px; " +
                "-fx-margin: 0 40px 0 40px; " +
                "-fx-spacing: 5px; " +
                "-fx-border-radius: 10px; " +
                "-fx-background-radius: 10px;");

        VBox.setMargin(sessionContainer, new Insets(5, 20, 5, 20));

        Label sessionLabel = new Label(session.getName());
        sessionLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #000000;");

        // Botões de ação
        HBox actionButtons = createActionButtons(session);

        // Informações adicionais
        Event event = (Event) sessionRepository.getData(session.getId(), "eventId");
        SubEvent subEvent = ((SubEvent) sessionRepository.getData(session.getId(), "subEvent_id"));
        Label eventLabel = null;
        if (event == null && subEvent != null) {
            eventLabel = createSubEventLabel(subEvent.getId());
        } else if (event != null && subEvent == null){
            eventLabel = createEventLabel(event.getId());
        }

        sessionContainer.getChildren().addAll(sessionLabel, actionButtons, eventLabel);

        return sessionContainer;
    }


    private Label createEventLabel(UUID eventId) {
        Label eventLabel = new Label();
        if(eventId != null){

            EventRepository eventRepository = EventRepository.getInstance();
            String eventName = (String) eventRepository.getData(eventId, "name");
            eventLabel.setText(eventName);
            eventLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #555555;");

        }
        return eventLabel;
    }

    private Label createSubEventLabel(UUID subEventId) {
        Label subEventLabel = new Label();
        if (subEventId != null) {

            SubEventRepository subEventRepository = SubEventRepository.getInstance();
            String subEventName = (String) subEventRepository.getData(subEventId, "name");
            subEventLabel.setText(subEventName);
            subEventLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #777777;");

        }
        return subEventLabel;
    }

    private HBox createActionButtons(Session session) {
        SessionRepository sessionRepository = SessionRepository.getInstance();
        HBox actionButtons = new HBox(10);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);

        Button detailsButton = new Button("Detalhes");
        ImageView detailsIcon = new ImageView(new Image("images/icons/buttons/detailsIcon.png"));
        detailsIcon.setFitWidth(16);
        detailsIcon.setFitHeight(16);
        detailsButton.setGraphic(detailsIcon);
        detailsButton.setStyle("-fx-background-color: #ffffff; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(128, 128, 128, 1), 3.88, 0, -1, 5);");
        detailsButton.setOnAction(e -> handleDetailSession((UUID) sessionRepository.getData(session.getId(),"id")));

        Button editButton = new Button("Editar");
        ImageView editIcon = new ImageView(new Image("images/icons/buttons/editIcon.png"));
        editIcon.setFitWidth(16);
        editIcon.setFitHeight(16);
        editButton.setGraphic(editIcon);
        editButton.setStyle("-fx-background-color: #ffffff; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(128, 128, 128, 1), 3.88, 0, -1, 5);");

        editButton.setOnAction(e -> {
            try {
                handleEditSession((String) sessionRepository.getData(session.getId(), "name"));
            } catch (IOException ex) {
                throw new IllegalArgumentException(ex);
            }
        });

        Button deleteButton = new Button("Excluir");
        ImageView deleteIcon = new ImageView(new Image("images/icons/buttons/deleteIcon.png"));
        deleteIcon.setFitWidth(16);
        deleteIcon.setFitHeight(16);
        deleteButton.setGraphic(deleteIcon);
        deleteButton.setStyle("-fx-background-color: #ffffff; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(128, 128, 128, 1), 3.88, 0, -1, 5);");

        deleteButton.setOnAction(e -> {
            try {
                handleDeleteSession((UUID) sessionRepository.getData(session.getId(),"id"), facade.getUserData("id"));
            } catch (IOException ex) {
                throw new IllegalArgumentException(ex);
            }
        });

        actionButtons.getChildren().addAll(detailsButton, editButton, deleteButton);
        return actionButtons;
    }

    private void handleDetailSession(UUID sessionId) {
        loadScreen("Carregando", () -> {
            SessionRepository sessionRepository = SessionRepository.getInstance();
            UserRepository userRepository = UserRepository.getInstance();

            String content = "Nome: " + sessionRepository.getData(sessionId, "name") + "\n" +
                    "Data: " + sessionRepository.getData(sessionId, "date") + "\n" +
                    "Descrição: " + sessionRepository.getData(sessionId, "description") + "\n" +
                    "Início: " + sessionRepository.getData(sessionId, "startTime") + "\n" +
                    "Término: " + sessionRepository.getData(sessionId, "endTime") + "\n" +
                    "Local: " + sessionRepository.getData(sessionId, "location") + "\n" +
                    "Administrador: " + userRepository.getData("email") + "\n";

            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Detalhes da Sessão");
                alert.setTitle(" ");

                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().clear();
                stage.getIcons().add(new Image("/images/logo/Logo.png"));

                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.setStyle("-fx-background-color: #f0f0f0; -fx-font-size: 14px; -fx-text-fill: #333333;");
                dialogPane.lookup(".header-panel").setStyle("-fx-background-color: #ff914d; -fx-text-fill: #ffffff; -fx-font-weight: bold;");
                dialogPane.lookup(".content").setStyle("-fx-font-size: 14px; -fx-text-fill: rgb(51,51,51);");

                alert.setContentText(content);
                alert.showAndWait();
            });
        }, sessionPane);
    }

    private void handleEditSession(String sessionName) throws IOException {
        System.out.println("Editando sessão: " + sessionName);
        mediator.setSessionId(sessionName);
        mediator.notify("handleUpdateSession");
    }

    private void handleDeleteSession(UUID sessionId, String userId) throws IOException {
        mediator.setSessionId(String.valueOf(sessionId));
        Optional<ButtonType> result = (Optional<ButtonType>) mediator.notify("handleDeleteSession");

        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
            facade.deleteSession(sessionId, userId);
            loadUserSessions();
        }
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
