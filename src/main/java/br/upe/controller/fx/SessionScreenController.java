package br.upe.controller.fx;

import br.upe.facade.FacadeInterface;
import br.upe.persistence.Session;
import br.upe.persistence.SubEvent;
import br.upe.persistence.repository.EventRepository;
import br.upe.persistence.repository.Persistence;
import br.upe.persistence.repository.SessionRepository;
import br.upe.persistence.repository.SubEventRepository;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.*;

public class SessionScreenController extends BaseController implements FxController {
    private FacadeInterface facade;

    @FXML
    private Label userEmail;
    @FXML
    private VBox sessionVBox;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private AnchorPane sessionPane;

    public void setFacade(FacadeInterface facade) throws IOException {
        this.facade = facade;
        initialize();
    }

    private void initialize() throws IOException {
        userEmail.setText(facade.getUserData("email"));
        loadUserSessions();
    }

    public void handleUser() throws IOException {
        genericButton("/fxml/userScreen.fxml", sessionPane, facade, null);
    }

    public void handleSubEvent() throws IOException {
        genericButton("/fxml/subEventScreen.fxml", sessionPane, facade, null);
    }

    public void handleSubmit() throws IOException {
        genericButton("/fxml/submitScreen.fxml", sessionPane, facade, null);
    }

    public void handleEvent() throws IOException {
        genericButton("/fxml/mainScreen.fxml", sessionPane, facade, null);
    }

    public void handleAddSession() throws IOException {
        genericButton("/fxml/createSessionScreen.fxml", sessionPane, facade, null);
    }

    public void handleInscriptionSession() throws IOException {
        genericButton("/fxml/enterSessionScreen.fxml", sessionPane, facade, null);
    }

    public void logout() throws IOException {
        genericButton("/fxml/loginScreen.fxml", sessionPane, facade, null);
    }

    private void loadUserSessions() throws IOException {
        sessionVBox.getChildren().clear();

        // Configurações do ScrollPane
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setStyle("-fx-padding: 20px;");
        sessionVBox.setAlignment(Pos.CENTER);

        // Itera pelas sessões do usuário
        List<Session> userSessions = facade.listSessions(facade.getUserData("id")); // Método para buscar as sessões
        for (Session session : userSessions) {
            if (session.getOwnerId().getId().equals(UUID.fromString(facade.getUserData("id")))) {
                VBox sessionContainer = createSessionContainer(session);
                sessionVBox.getChildren().add(sessionContainer);
            }
        }
    }

    private VBox createSessionContainer(Session session) throws IOException {
        SessionRepository sessionRepository = SessionRepository.getInstance();

        VBox sessionContainer = new VBox();
        sessionContainer.setStyle("-fx-background-color: #d3d3d3; -fx-padding: 10px; -fx-spacing: 5px; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        // Nome da sessão
        Label sessionLabel = new Label(session.getName());
        sessionLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #000000;");

        // Botões de ação
        HBox actionButtons = createActionButtons(session);

        // Informações adicionais
        Label eventLabel = createEventLabel((UUID) sessionRepository.getData(session.getId(), "eventId"));
        Label subEventLabel = createSubEventLabel(((SubEvent) sessionRepository.getData(session.getId(), "subEvent_id")).getId());


        sessionContainer.getChildren().addAll(sessionLabel, actionButtons, eventLabel, subEventLabel);

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

    private Label getParentLabel(Persistence session, Map<UUID, Persistence> parentMap) {
        UUID parentId = (UUID) session.getData("eventId");
        String parentName = parentMap.containsKey(parentId)
                ? (String) parentMap.get(parentId).getData("name")
                : "Evento/Subevento não encontrado";

        Label parentLabel = new Label(parentName);
        parentLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #555555;");
        return parentLabel;
    }

    private HBox createActionButtons(Session session) {
        SessionRepository sessionRepository = SessionRepository.getInstance();
        HBox actionButtons = new HBox(10);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);

        Button detailsButton = createButton("Detalhes", "#ff914d");
        detailsButton.setOnAction(e -> handleDetailSession((UUID) sessionRepository.getData(session.getId(),"id")));

        Button editButton = createButton("Editar", "#6fa3ef");
        editButton.setOnAction(e -> {
            try {
                handleEditSession((String) sessionRepository.getData("name"));
            } catch (IOException ex) {
                throw new IllegalArgumentException(ex);
            }
        });

        Button deleteButton = createButton("Excluir", "#ff6b6b");
        deleteButton.setOnAction(e -> {
            try {
                handleDeleteSession((UUID) sessionRepository.getData("id"));
            } catch (IOException ex) {
                throw new IllegalArgumentException(ex);
            }
        });

        actionButtons.getChildren().addAll(detailsButton, editButton, deleteButton);
        return actionButtons;
    }

    private Button createButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: white; -fx-cursor: hand;", color));
        return button;
    }

    private void handleDetailSession(UUID sessionId) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalhes da Sessão");
        alert.setHeaderText("Informações da Sessão");

        Persistence session = facade.getSessionHashMap().get(sessionId);
        Persistence owner = facade.getUserHashMap().get(session.getData("ownerId"));

        String content = String.format("Nome: %s\nData: %s\nDescrição: %s\nInício: %s\nTérmino: %s\nLocal: %s\nAdministrador: %s",
                session.getData("name"),
                session.getData("date"),
                session.getData("description"),
                session.getData("startTime"),
                session.getData("endTime"),
                session.getData("location"),
                owner.getData("email"));

        alert.setContentText(content);
        alert.showAndWait();
    }

    private void handleEditSession(String sessionName) throws IOException {
        genericButton("/fxml/updateSessionScreen.fxml", sessionPane, facade, sessionName);
    }

    private void handleDeleteSession(UUID sessionId) throws IOException {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmação de Exclusão");
        confirmationAlert.setHeaderText("Deseja realmente excluir esta sessão?");
        confirmationAlert.setContentText("Esta ação não pode ser desfeita.");

        ButtonType buttonYes = new ButtonType("Sim");
        ButtonType buttonNo = new ButtonType("Não", ButtonBar.ButtonData.CANCEL_CLOSE);

        confirmationAlert.getButtonTypes().setAll(buttonYes, buttonNo);

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == buttonYes) {
            facade.deleteSession(sessionId, facade.getUserData("id"));
            loadUserSessions();
        }
    }
}
