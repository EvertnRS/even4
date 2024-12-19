package br.upe.controller.fx.screen.session;

import br.upe.controller.fx.fxutils.PlaceholderUtils;
import br.upe.controller.fx.mediator.session.UpdateSessionMediator;
import br.upe.controller.fx.screen.BaseController;
import br.upe.controller.fx.screen.FxController;
import br.upe.facade.FacadeInterface;
import br.upe.persistence.Session;
import br.upe.persistence.repository.SessionRepository;
import br.upe.utils.JPAUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class UpdateSessionScreenController extends BaseController implements FxController {
    private FacadeInterface facade;
    private String sessionName;
    private String sessionId;
    private UpdateSessionMediator mediator;
    private static final Logger LOGGER = Logger.getLogger(UpdateSessionScreenController.class.getName());


    @FXML
    private AnchorPane editSessionPane;
    @FXML
    private Label userEmail;
    @FXML
    private TextField editNameTextField;
    @FXML
    private Text namePlaceholder;
    @FXML
    private DatePicker editDatePicker;
    @FXML
    private Text datePlaceholder;
    @FXML
    private TextField editLocationTextField;
    @FXML
    private Text locationPlaceholder;
    @FXML
    private TextField editDescriptionTextField;
    @FXML
    private Text descriptionPlaceholder;
    @FXML
    private TextField editStartTimeTextField;
    @FXML
    private Text startTimePlaceholder;
    @FXML
    private TextField editEndTimeTextField;
    @FXML
    private Text endTimePlaceholder;
    @FXML
    private Label errorUpdtLabel;

    public void setFacade(FacadeInterface facade) throws IOException {
        this.facade = facade;
        initial();
    }

    public void setEventName(String eventName) {
        this.sessionName = eventName;
        String[] list = verifyType(eventName);
        this.sessionId = list[2];
        loadSessionDetails();
    }


    private void initial() {
        userEmail.setText(facade.getUserData("email"));
        setupPlaceholders();

        mediator = new UpdateSessionMediator(this, facade, editSessionPane, errorUpdtLabel);
        mediator.registerComponents();
        mediator.setComponents(editNameTextField, editDatePicker, editLocationTextField, editDescriptionTextField, editStartTimeTextField, editEndTimeTextField);
    }

    private void setupPlaceholders() {
        PlaceholderUtils.setupPlaceholder(editNameTextField, namePlaceholder);
        PlaceholderUtils.setupPlaceholder(editDatePicker, datePlaceholder);
        PlaceholderUtils.setupPlaceholder(editLocationTextField, locationPlaceholder);
        PlaceholderUtils.setupPlaceholder(editDescriptionTextField, descriptionPlaceholder);
        PlaceholderUtils.setupPlaceholder(editStartTimeTextField, startTimePlaceholder);
        PlaceholderUtils.setupPlaceholder(editEndTimeTextField, endTimePlaceholder);
    }

    public void updateSession() throws IOException {
        String[] type = verifyType(sessionName);
        String newSubName = editNameTextField.getText();
        String newLocation = editLocationTextField.getText();
        String newDescription = editDescriptionTextField.getText();
        String newDate = editDatePicker.getValue() != null ? editDatePicker.getValue().toString() : "";
        String newStartTime = editStartTimeTextField.getText();
        String newEndTime = editEndTimeTextField.getText();
        if (!validateEventDate(newDate, type[0], type[1])) {
            errorUpdtLabel.setText("Data da sessão não pode ser anterior à data do evento.");
            errorUpdtLabel.setAlignment(Pos.CENTER);
        } else {
            facade.updateSession(sessionName, newSubName, newDate, newDescription, newLocation, facade.getUserData("id"), newStartTime, newEndTime);
            mediator.notify("handleBack");
        }
    }

    private String[] verifyType(String sessionName) {
        String[] type = facade.verifyBySessionName(sessionName);
        if (type[2] == null) {
            throw new IllegalArgumentException("Nenhuma sessão associada encontrada para o nome: " + sessionName);
        }
        return type;
    }

    private void loadSessionDetails() {
        SessionRepository sessionRepository = SessionRepository.getInstance();
        if (sessionRepository != null) {
            String sessionNames = (String) sessionRepository.getData(UUID.fromString(sessionId), "name");
            String sessionLocation = (String) sessionRepository.getData(UUID.fromString(sessionId), "location");
            String sessionDescription = (String) sessionRepository.getData(UUID.fromString(sessionId), "description");

            // Format start time
            Object startTimeObject = sessionRepository.getData(UUID.fromString(sessionId), "startTime");
            String sessionStartTime = formatTime(startTimeObject);
            editStartTimeTextField.setText(sessionStartTime);

            // Format end time
            Object endTimeObject = sessionRepository.getData(UUID.fromString(sessionId), "endTime");
            String sessionEndTime = formatTime(endTimeObject);
            editEndTimeTextField.setText(sessionEndTime);

            editNameTextField.setText(sessionNames);
            editLocationTextField.setText(sessionLocation);
            editDescriptionTextField.setText(sessionDescription);

            Object dateObject = sessionRepository.getData(UUID.fromString(sessionId), "date");
            java.sql.Date sqlDate;

            sqlDate = switch (dateObject) {
                case java.sql.Timestamp timestamp -> new java.sql.Date(timestamp.getTime());
                case java.sql.Date date -> date;
                default -> throw new IllegalArgumentException("Unexpected type: " + dateObject.getClass().getName());
            };

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String subeventDate = formatter.format(sqlDate);
            if (!subeventDate.isEmpty()) {
                editDatePicker.setValue(LocalDate.parse(subeventDate));
            }

            setupPlaceholders();
        } else {
            errorUpdtLabel.setText("SubEvento não encontrado.");
            errorUpdtLabel.setAlignment(Pos.CENTER);
        }
    }

    private String formatTime(Object timeObject) {
        return switch (timeObject) {
            case java.sql.Time time -> {
                SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
                yield timeFormatter.format(time);
            }
            default -> throw new IllegalArgumentException("Unexpected type: " + timeObject.getClass().getName());
        };
    }

    public UUID getId() {
        return UUID.fromString(sessionId);
    }

    @Override
    public TextField getNameTextField() {
        return editNameTextField;
    }

    @Override
    public TextField getLocationTextField() {
        return editLocationTextField;
    }

    @Override
    public TextField getDescriptionTextField() {
        return editDescriptionTextField;
    }

    @Override
    public DatePicker getDatePicker() {
        return editDatePicker;
    }

}
