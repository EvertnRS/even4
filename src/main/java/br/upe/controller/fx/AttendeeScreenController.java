package br.upe.controller.fx;

import br.upe.controller.fx.mediator.AttendeeMediator;
import br.upe.controller.fx.mediator.EventMediator;
import br.upe.facade.FacadeInterface;
import br.upe.persistence.Attendee;
import br.upe.persistence.Event;
import br.upe.persistence.SubEvent;
import br.upe.persistence.repository.AttendeeRepository;
import br.upe.persistence.repository.EventRepository;
import br.upe.persistence.repository.SessionRepository;
import br.upe.persistence.repository.SubEventRepository;
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
    @FXML
    private TextField searchTextField;
    @FXML
    private Text searchPlaceholder;
    @FXML
    private ImageView logoView6;

    public void setFacade(FacadeInterface facade) throws IOException {
        this.facade = facade;
        initial();
    }

    private void initial() throws IOException {
        userEmail.setText(facade.getUserData("email"));
        loadSessions();

        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            setupPlaceholders();
            try {
                loadSessions();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

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
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-top: 2px; -border-color: #cccccc");

        attendeeVBox.setAlignment(Pos.CENTER);

        for (Attendee attendee : userAttendees) {
            if (attendee.getUserId().getId().equals(UUID.fromString(facade.getUserData("id")))) {
                for (UUID sessionId : attendee.getSessionIds()) {
                    VBox sessionContainer = new VBox();
                    sessionContainer.setStyle("-fx-background-color: #d3d3d3; " +
                            "-fx-padding: 10px 20px 10px 20px; " +
                            "-fx-margin: 0 40px 0 40px; " +
                            "-fx-spacing: 5px; " +
                            "-fx-border-radius: 10px; " +
                            "-fx-background-radius: 10px;");

                    VBox.setMargin(sessionContainer, new Insets(5, 20, 5, 20));

                    Label sessionLabel;
                    if (searchTextField.getText().isEmpty() || String.valueOf(sessionRepository.getData(sessionId,"name")).contains(searchTextField.getText())) {
                        sessionLabel = new Label((String) sessionRepository.getData(sessionId, "name"));
                        sessionLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #000000;");

                        Button deleteButton = new Button("Excluir");
                        ImageView deleteIcon = new ImageView(new Image("images/icons/buttons/deleteIcon.png"));
                        deleteIcon.setFitWidth(16);
                        deleteIcon.setFitHeight(16);
                        deleteButton.setGraphic(deleteIcon);
                        deleteButton.setStyle("-fx-background-color: #ffffff; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(128, 128, 128, 1), 3.88, 0, -1, 5);");

                        Button certificateButton = new Button("Certificado");
                        ImageView certificateIcon = new ImageView(new Image("images/icons/buttons/certificateIcon.png"));
                        certificateIcon.setFitWidth(16);
                        certificateIcon.setFitHeight(16);
                        certificateButton.setGraphic(certificateIcon);
                        certificateButton.setStyle("-fx-background-color: #ffffff; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(128, 128, 128, 1), 3.88, 0, -1, 5);");


                        java.sql.Timestamp sessionTimestamp = (java.sql.Timestamp) sessionRepository.getData(sessionId, "date");
                        String sessionDate = sessionTimestamp.toString();
                        verifyCertification(certificateButton, sessionDate);

                        deleteButton.setOnAction(e -> {
                            try {
                                handleDeleteAttendee((UUID) attendeeRepository.getData(attendee.getId(), "id"), sessionId);
                            } catch (IOException ex) {
                                throw new IllegalArgumentException(ex);
                            }
                        });

                        certificateButton.setOnAction(e -> {
                            try {
                                handleCertificate(String.valueOf(attendeeRepository.getData(attendee.getId(), "id")));
                            } catch (IOException ex) {
                                throw new IllegalArgumentException(ex);
                            }
                        });

                        HBox actionButtons = new HBox(10);
                        actionButtons.setAlignment(Pos.CENTER_RIGHT);
                        actionButtons.getChildren().addAll(certificateButton, deleteButton);

                        Event event = (Event) sessionRepository.getData(sessionId, "eventId");
                        SubEvent subEvent = ((SubEvent) sessionRepository.getData(sessionId, "subEvent_id"));
                        Label eventLabel = null;
                        if (event == null && subEvent != null) {
                            eventLabel = createSubEventLabel(subEvent.getId());
                        } else if (event != null && subEvent == null){
                            eventLabel = createEventLabel(event.getId());
                        }

                        sessionContainer.getChildren().addAll(sessionLabel, actionButtons, eventLabel);

                        attendeeVBox.getChildren().add(sessionContainer);
                    }
                }
            }
        }
    }

    private void handleCertificate(String attendeeId) throws IOException {
        mediator.setAttendeeId(attendeeId);
        mediator.notify("handleCertificate");
    }

    private void verifyCertification(Button certificateButton, String sessionDateStr) {
        String datePart = sessionDateStr.split(" ")[0];
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate sessionDate = LocalDate.parse(datePart, formatter);
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
