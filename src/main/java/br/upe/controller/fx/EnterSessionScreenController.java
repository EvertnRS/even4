package br.upe.controller.fx;

import br.upe.facade.FacadeInterface;
import br.upe.persistence.repository.Persistence;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class EnterSessionScreenController extends BaseController implements FxController{
    private FacadeInterface facade;

    @FXML
    private VBox attendeeVBox;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Label userEmail;
    @FXML
    private AnchorPane attendeePane;

    public void setFacade(FacadeInterface facade) throws IOException {
        this.facade = facade;
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

        facade.listAttendees(facade.getUserData("id"), "");

        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);

        scrollPane.setStyle("-fx-padding: 20px;");

        attendeeVBox.setAlignment(Pos.CENTER);

        Map<UUID, Persistence> sessionHashMap = facade.getSessionHashMap();

        for (Map.Entry<UUID, Persistence> entry : facade.getAttendeeHashMap().entrySet()) {
            Persistence persistence = entry.getValue();
            if (persistence.getData("userId").equals(facade.getUserData("id"))) {

                VBox eventContainer = new VBox();
                eventContainer.setStyle("-fx-background-color: #d3d3d3; -fx-padding: 10px; -fx-spacing: 5px; -fx-border-radius: 10px; -fx-background-radius: 10px;");

                Label eventLabel = new Label((String) persistence.getData("name"));
                eventLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #333333;");

                Label sessionLabel = createSessionLabel((UUID) persistence.getData("sessionId"), sessionHashMap);

                Button certificateButton = new Button("Certificado");
                certificateButton.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #ff914d; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(128, 128, 128, 1), 3.88, 0, -1, 5);");
                verifyCertification(certificateButton, (String) sessionHashMap.get((UUID) persistence.getData("sessionId")).getData("date"));

                Button editButton = new Button("Editar");
                editButton.setStyle("-fx-background-color: #6fa3ef; -fx-text-fill: white; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(128, 128, 128, 1), 3.88, 0, -1, 5);");

                Button deleteButton = new Button("Excluir");
                deleteButton.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(128, 128, 128, 1), 3.88, 0, -1, 5);");

                Button detailsButton = new Button("Detalhes");
                detailsButton.setStyle("-fx-background-color: #ff914d; -fx-text-fill: white; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(128, 128, 128, 1), 3.88, 0, -1, 5);");

                detailsButton.setOnAction(e ->
                        handleDetailAttendee(sessionHashMap, (UUID) persistence.getData("id")));

                editButton.setOnAction(e -> {
                    try {
                        handleEditAttendee((String) persistence.getData("name"));
                    } catch (IOException ex) {
                        throw new IllegalArgumentException(ex);
                    }
                });

                deleteButton.setOnAction(e -> {
                    try {
                        handleDeleteAttendee(facade.getUserData("id"),(String) persistence.getData("sessionId"));
                    } catch (IOException ex) {
                        throw new IllegalArgumentException(ex);
                    }
                });

                certificateButton.setOnAction(e -> {
                    try {
                        handleCertificate((String) persistence.getData("id"));
                    } catch (IOException ex) {
                        throw new IllegalArgumentException(ex);
                    }
                });

                HBox actionButtons = new HBox(10);
                actionButtons.setAlignment(Pos.CENTER_RIGHT);
                actionButtons.getChildren().addAll(certificateButton, detailsButton, editButton, deleteButton);

                eventContainer.getChildren().addAll(eventLabel, actionButtons, sessionLabel);

                attendeeVBox.getChildren().add(eventContainer);
            }
        }
    }

    private void handleDetailAttendee(Map<UUID, Persistence> sessionHashMap, UUID id) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalhes do Participante");
        alert.setHeaderText("Detalhes do Participante");

        Persistence attendee = facade.getAttendeeHashMap().get(id);
        Persistence owner = facade.getUserHashMap().get((UUID) attendee.getData("userId"));

        String content = "Nome: " + attendee.getData("name") + "\n" +
                "Sessão: " + sessionHashMap.get((UUID) attendee.getData("sessionId")).getData("name") + "\n" +
                "Administrador: " + owner.getData("email") + "\n";

        alert.setContentText(content);
        alert.showAndWait();
    }

    private void handleCertificate(String attendeeId) throws IOException {
        genericButton("/fxml/certificateScreen.fxml", attendeePane, facade, attendeeId);
    }

    private void verifyCertification(Button certificateButton, String sessionDateStr) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate sessionDate = LocalDate.parse(sessionDateStr, formatter);
            LocalDate currentDate = LocalDate.now();

            certificateButton.setVisible(currentDate.isAfter(sessionDate));
    }

    private Label createSessionLabel(UUID eventId, Map<UUID, Persistence> sessionHashMap) {
        Label sessionLabel = new Label();
        String nameEvent = (sessionHashMap != null && sessionHashMap.containsKey(eventId))
                ? (String) sessionHashMap.get(eventId).getData("name")
                : "Evento não encontrado";
        sessionLabel.setText(nameEvent);
        sessionLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #555555;");
        return sessionLabel;
    }

    private void handleEditAttendee(String eventName) throws IOException {
        genericButton("/fxml/updateAttendeeScreen.fxml", attendeePane, facade, eventName);
    }
    private void handleDeleteAttendee(String userId, String sessionId) throws IOException {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmação de Exclusão");
        confirmationAlert.setHeaderText("Deseja realmente excluir este participante?");
        confirmationAlert.setContentText("Esta ação não pode ser desfeita.");

        ButtonType buttonSim = new ButtonType("Sim");
        ButtonType buttonNao = new ButtonType("Não", ButtonBar.ButtonData.CANCEL_CLOSE);

        confirmationAlert.getButtonTypes().setAll(buttonSim, buttonNao);

        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == buttonSim) {
            facade.deleteAttendee(userId, "id", sessionId);
            loadSessions();
        }
    }
    public void handleAddAttendee() throws IOException {
        genericButton("/fxml/createAttendeeScreen.fxml", attendeePane, facade, null);
    }
}
