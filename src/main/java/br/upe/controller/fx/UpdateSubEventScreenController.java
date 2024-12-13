package br.upe.controller.fx;

import br.upe.controller.fx.fxutils.PlaceholderUtils;
import br.upe.controller.fx.mediator.UpdateSubEventMediator;
import br.upe.facade.FacadeInterface;
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
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.UUID;

public class UpdateSubEventScreenController extends BaseController implements FxController {
    private FacadeInterface facade;
    private UUID subEventId;
    private UpdateSubEventMediator mediator;

    @FXML
    private AnchorPane editSubEventPane;
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
    private Label errorUpdtLabel;

    public void setFacade(FacadeInterface facade) {
        this.facade = facade;
        initial();
    }

    public void setEventId(UUID eventId) {
        this.subEventId = eventId;
        loadSubEventDetails();
    }

    private void initial() {
        userEmail.setText(facade.getUserData("email"));
        setupPlaceholders();

        mediator = new UpdateSubEventMediator(this, facade, editSubEventPane, errorUpdtLabel);
        mediator.registerComponents();

        mediator.setComponents(editNameTextField, editDatePicker, editLocationTextField, editDescriptionTextField);
    }

    private void setupPlaceholders() {
        PlaceholderUtils.setupPlaceholder(editNameTextField, namePlaceholder);
        PlaceholderUtils.setupPlaceholder(editDatePicker, datePlaceholder);
        PlaceholderUtils.setupPlaceholder(editLocationTextField, locationPlaceholder);
        PlaceholderUtils.setupPlaceholder(editDescriptionTextField, descriptionPlaceholder);
    }


    private void loadSubEventDetails() {
        SubEventRepository subeventRepository = SubEventRepository.getInstance();
        if (subeventRepository != null) {
            String eventName = (String) subeventRepository.getData(subEventId, "name");
            String eventLocation = (String) subeventRepository.getData(subEventId, "location");
            String eventDescription = (String) subeventRepository.getData(subEventId, "description");
            editNameTextField.setText(eventName);
            editLocationTextField.setText(eventLocation);
            editDescriptionTextField.setText(eventDescription);

            Object dateObject = subeventRepository.getData(subEventId, "date");
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


    public void updateSubEvent() throws IOException {
        String newName = editNameTextField.getText();
        String newLocation = editLocationTextField.getText();
        String newDescription = editDescriptionTextField.getText();
        Date newDate = Date.valueOf(editDatePicker.getValue() != null ? editDatePicker.getValue().toString() : "");

        if (!validateEventDate(newDate.toString(), String.valueOf(getEventIdBySubEventId(subEventId)), "evento")) {
            errorUpdtLabel.setText("Data do SubEvento não pode ser anterior a data do Evento.");
            errorUpdtLabel.setAlignment(Pos.CENTER);
        } else {
            facade.updateSubEvent(subEventId, newName, newDate, newDescription, newLocation);
            mediator.notify("handleSubEvent");
        }
    }

    public UUID getEventIdBySubEventId(UUID subEventId) {
        try {
            EntityManager entityManager = JPAUtils.getEntityManagerFactory();
            TypedQuery<UUID> query = entityManager.createQuery(
                    "SELECT s.eventId FROM SubEvent s WHERE s.id = :subEventId", UUID.class);
            query.setParameter("subEventId", subEventId);
            return query.getSingleResult();
        } catch (NoResultException e) {
            throw new IllegalArgumentException("Event not found for sub-event ID: " + subEventId, e);
        }
    }

    public UUID getId() {
        return subEventId;
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