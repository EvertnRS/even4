package br.upe.controller.fx;

import br.upe.controller.fx.mediator.AttendeeMediator;
import br.upe.controller.fx.mediator.EventMediator;
import br.upe.facade.FacadeInterface;
import br.upe.persistence.Attendee;
import br.upe.persistence.repository.AttendeeRepository;
import br.upe.persistence.repository.EventRepository;
import br.upe.persistence.repository.SessionRepository;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AttendeeScreenController extends BaseController implements FxController{
    private FacadeInterface facade;
    private AttendeeMediator mediator;

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

        this.mediator = new AttendeeMediator(this, facade, attendeePane, null);
        mediator.registerComponents();
    }

    private void loadSessions() throws IOException {
        attendeeVBox.getChildren().clear();

        List<Attendee> userAttendees = facade.listAttendees(facade.getUserData("id"));
        AttendeeRepository attendeeRepository = AttendeeRepository.getInstance();
        SessionRepository sessionRepository = SessionRepository.getInstance();

        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setStyle("-fx-padding: 20px;");
        attendeeVBox.setAlignment(Pos.CENTER);

        for (Attendee attendee : userAttendees) {
            if (attendee.getUserId().getId().equals(UUID.fromString(facade.getUserData("id")))) {
                for (UUID sessionId : attendee.getSessionIds()) {
                    VBox sessionContainer = new VBox();
                    sessionContainer.setStyle("-fx-background-color: #d3d3d3; -fx-padding: 10px; -fx-spacing: 5px; -fx-border-radius: 10px; -fx-background-radius: 10px;");

                    Label sessionLabel = new Label((String) sessionRepository.getData(sessionId, "name"));
                    sessionLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #000000;");

                    Button deleteButton = new Button("Excluir");
                    deleteButton.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(128, 128, 128, 1), 3.88, 0, -1, 5);");

                    Button certificateButton = new Button("Detalhes");
                    certificateButton.setStyle("-fx-background-color: #ff914d; -fx-text-fill: white; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(128, 128, 128, 1), 3.88, 0, -1, 5);");

                    String sessionDate = (String) sessionRepository.getData(sessionId, "date");
                    verifyCertification(certificateButton, sessionDate);

                    deleteButton.setOnAction(e -> {
                        try {
                            handleDeleteAttendee((UUID) attendeeRepository.getData(attendee.getId(),"id"), UUID.fromString(facade.getUserData("id")));
                        } catch (IOException ex) {
                            throw new IllegalArgumentException(ex);
                        }
                    });

                    certificateButton.setOnAction(e -> {
                        try {
                            handleCertificate(String.valueOf(attendeeRepository.getData(attendee.getId(),"id")));
                        } catch (IOException ex) {
                            throw new IllegalArgumentException(ex);
                        }
                    });

                    HBox actionButtons = new HBox(10);
                    actionButtons.setAlignment(Pos.CENTER_RIGHT);
                    actionButtons.getChildren().addAll(certificateButton, deleteButton);

                    Label eventLabel = createEventLabel((UUID) sessionRepository.getData(sessionId, "eventId"));

                    sessionContainer.getChildren().addAll(sessionLabel, actionButtons, eventLabel);

                    attendeeVBox.getChildren().add(sessionContainer);
                }
            }
        }
    }

    private void handleCertificate(String attendeeId) throws IOException {
        mediator.setAttendeeId(attendeeId);
        mediator.notify("handleCertificate");
    }

    private void verifyCertification(Button certificateButton, String sessionDateStr) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate sessionDate = LocalDate.parse(sessionDateStr, formatter);
            LocalDate currentDate = LocalDate.now();

            certificateButton.setVisible(currentDate.isAfter(sessionDate));
    }

    private Label createEventLabel(UUID eventId) {
        Label eventLabel = new Label();
        EventRepository eventRepository = EventRepository.getInstance();
        String nameEvent = (String) eventRepository.getData(eventId, "name");
        eventLabel.setText(nameEvent);

        eventLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #555555;");
        return eventLabel;
    }

    private void handleDeleteAttendee(UUID id, UUID userId) throws IOException {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmação de Exclusão");
        confirmationAlert.setHeaderText("Deseja realmente excluir este participante?");
        confirmationAlert.setContentText("Esta ação não pode ser desfeita.");

        ButtonType buttonSim = new ButtonType("Sim");
        ButtonType buttonNao = new ButtonType("Não", ButtonBar.ButtonData.CANCEL_CLOSE);

        confirmationAlert.getButtonTypes().setAll(buttonSim, buttonNao);

        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == buttonSim) {
            facade.deleteAttendee(id, userId);
            loadSessions();
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
