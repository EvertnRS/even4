package br.upe.controller.fx;

import br.upe.controller.fx.fxutils.PlaceholderUtils;
import br.upe.facade.FacadeInterface;
import br.upe.persistence.Session;
import br.upe.persistence.repository.Persistence;
import br.upe.persistence.repository.SessionRepository;
import br.upe.persistence.repository.SubEventRepository;
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
import java.util.*;


import static br.upe.ui.Validation.areValidTimes;
import static br.upe.ui.Validation.isValidDate;

public class UpdateSessionScreenController extends BaseController implements FxController {
    private FacadeInterface facade;
    private String sessionName;
    private SessionRepository sessionRepository;

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
    @FXML
    private Label errorDelLabel;

    public void setFacade(FacadeInterface facade) throws IOException {
        this.facade = facade;
        initial();
    }

    public void setEventName(String eventName) {
        String [] list = verifyType(eventName);
        this.sessionName = list[2];
        loadSessionDetails();
    }


    private void initial() {
        userEmail.setText(facade.getUserData("email"));
        setupPlaceholders();
    }
    private void setupPlaceholders() {
        PlaceholderUtils.setupPlaceholder(editNameTextField, namePlaceholder);
        PlaceholderUtils.setupPlaceholder(editDatePicker, datePlaceholder);
        PlaceholderUtils.setupPlaceholder(editLocationTextField, locationPlaceholder);
        PlaceholderUtils.setupPlaceholder(editDescriptionTextField, descriptionPlaceholder);
        PlaceholderUtils.setupPlaceholder(editStartTimeTextField, startTimePlaceholder);
        PlaceholderUtils.setupPlaceholder(editEndTimeTextField, endTimePlaceholder);
    }

    public void handleEvent() throws IOException {
        genericButton("/fxml/eventScreen.fxml", editSessionPane, facade, null);
    }

    public void handleSubEvent() throws IOException {
        genericButton("/fxml/subEventScreen.fxml", editSessionPane, facade, null);
    }

    public void handleSubmitEvent() throws IOException {
        genericButton("/fxml/submitScreen.fxml", editSessionPane, facade, null);
    }

    public void handleSession() throws IOException {
        genericButton("/fxml/sessionScreen.fxml", editSessionPane, facade, null);
    }

    public void logout() throws IOException {
        genericButton("/fxml/loginScreen.fxml", editSessionPane, facade, null);
    }

    public void handleUser() throws IOException {
        genericButton("/fxml/userScreen.fxml", editSessionPane, facade, null);
    }

    public void updateSession() throws IOException {
        String[] type = verifyType(sessionName);
        String newSubName = editNameTextField.getText();
        String newLocation = editLocationTextField.getText();
        String newDescription = editDescriptionTextField.getText();
        String newDate = editDatePicker.getValue() != null ? editDatePicker.getValue().toString() : "";
        String newStartTime = editStartTimeTextField.getText();
        String newEndTime = editEndTimeTextField.getText();
        Map<UUID, Persistence> sessionMap = facade.getSessionHashMap();
        if (!validateEventDate(newDate, type)) {
            errorUpdtLabel.setText("Data da sessão não pode ser anterior à data do evento.");
        } else if (!isValidDate(newDate) || !areValidTimes(newStartTime, newEndTime)) {
            errorUpdtLabel.setText("Data ou horário inválido.");
        } else if (newLocation.isEmpty() || newDescription.isEmpty() || isValidName(newSubName, new ArrayList<>(sessionMap.values()))) {
            errorUpdtLabel.setText("Erro no preenchimento das informações.");
        } else {
            facade.updateSession(sessionName, newSubName, newDate, newDescription, newLocation, facade.getUserData("id"), newStartTime, newEndTime);
            facade.readSession();
            handleSession();
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
            String sessionNames = (String) sessionRepository.getData(UUID.fromString(sessionName),"name");
            String sessionLocation = (String) sessionRepository.getData(UUID.fromString(sessionName),"location");
            String sessionDescription = (String) sessionRepository.getData(UUID.fromString(sessionName),"description");
            String sessionStartTime =  (sessionRepository.getData(UUID.fromString(sessionName),"startTime")).toString();
            String sessionEndTime = (sessionRepository.getData(UUID.fromString(sessionName),"endTime")).toString();
            editNameTextField.setText(sessionNames);
            editLocationTextField.setText(sessionLocation);
            editDescriptionTextField.setText(sessionDescription);
            editStartTimeTextField.setText(sessionStartTime);
            editEndTimeTextField.setText(sessionEndTime);

            Object dateObject = sessionRepository.getData(UUID.fromString(sessionName), "date");
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
