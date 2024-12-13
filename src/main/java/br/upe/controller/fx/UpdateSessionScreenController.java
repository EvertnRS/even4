package br.upe.controller.fx;

import br.upe.controller.fx.fxutils.PlaceholderUtils;
import br.upe.controller.fx.mediator.UpdateSessionMediator;
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
            mediator.notify("handleSession");
        }
    }

    private String[] verifyType(String sessionName) {
        String[] type = new String[3];
        EntityManager entityManager = null;

        try {
            if (sessionName == null || sessionName.trim().isEmpty()) {
                throw new IllegalArgumentException("O nome da sessão não pode ser nulo ou vazio");
            }

            entityManager = JPAUtils.getEntityManagerFactory();

            // Consulta para buscar a sessão com nome específico
            TypedQuery<Session> sessionQuery = entityManager.createQuery(
                    "SELECT s FROM Session s WHERE LOWER(TRIM(s.name)) = LOWER(TRIM(:name))",
                    Session.class
            );
            sessionQuery.setParameter("name", sessionName.trim());
            LOGGER.info("Session Name: " + sessionName);

            List<Session> sessionResults = sessionQuery.getResultList();
            if (sessionResults.isEmpty()) {
                throw new IllegalArgumentException("Nenhum dado encontrado para o nome da sessão: " + sessionName);
            }

            // Exibe a sessão encontrada
            Session session = sessionResults.get(0);

            // Preencha os dados de retorno
            if (session.getSubEventId() != null && session.getSubEventId().getId() != null) {
                type[0] = session.getSubEventId().getId().toString();
                type[1] = "subEvento"; // Ou faça a consulta para obter o nome do subevento
                type[2] = session.getId().toString();
            } else if (session.getEventId() != null && session.getEventId().getId() != null) {
                type[0] = session.getEventId().getId().toString();
                type[1] = "evento"; // Ou faça a consulta para obter o nome do evento
                type[2] = session.getId().toString();
            }

        } catch (NoResultException e) {
            throw new IllegalArgumentException("Nenhum dado encontrado para o nome da sessão: " + sessionName, e);
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
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

            if (dateObject instanceof java.sql.Timestamp) {
                sqlDate = new java.sql.Date(((java.sql.Timestamp) dateObject).getTime());
            } else if (dateObject instanceof java.sql.Date) {
                sqlDate = (java.sql.Date) dateObject;
            } else {
                throw new IllegalArgumentException("Tipo inesperado: " + dateObject.getClass().getName());
            }

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
        if (timeObject instanceof java.sql.Time) {
            SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
            return timeFormatter.format((java.sql.Time) timeObject);
        } else {
            throw new IllegalArgumentException("Tipo inesperado: " + timeObject.getClass().getName());
        }
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
