package br.upe.controller.fx;

import br.upe.controller.fx.fxutils.PlaceholderUtils;
import br.upe.controller.fx.mediator.UpdateEventMediator;
import br.upe.facade.FacadeInterface;
import br.upe.persistence.repository.EventRepository;
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

public class UpdateEventScreenController extends BaseController implements FxController {
    private FacadeInterface facade;
    private UUID eventId;
    private UpdateEventMediator mediator;

    @FXML
    private AnchorPane editEventPane;
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

    public void setFacade(FacadeInterface facade) throws IOException {
        this.facade = facade;
        initial();
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
        loadEventDetails();
    }

    private void setupPlaceholders() {
        PlaceholderUtils.setupPlaceholder(editNameTextField, namePlaceholder);
        PlaceholderUtils.setupPlaceholder(editDatePicker, datePlaceholder);
        PlaceholderUtils.setupPlaceholder(editLocationTextField, locationPlaceholder);
        PlaceholderUtils.setupPlaceholder(editDescriptionTextField, descriptionPlaceholder);
    }

    private void initial() {
        userEmail.setText(facade.getUserData("email"));
        setupPlaceholders();

        this.mediator = new UpdateEventMediator(this, facade, editEventPane, errorUpdtLabel);
        mediator.registerComponents();

        mediator.setComponents(editNameTextField, editDatePicker, editLocationTextField, editDescriptionTextField);
    }

    private void loadEventDetails() {
        EventRepository eventRepository = EventRepository.getInstance();
        if (eventRepository != null) {
            String eventName = (String) eventRepository.getData(eventId, "name");
            String eventLocation = (String) eventRepository.getData(eventId, "location");
            String eventDescription = (String) eventRepository.getData(eventId, "description");
            editNameTextField.setText(eventName);
            editLocationTextField.setText(eventLocation);
            editDescriptionTextField.setText(eventDescription);

            Object dateObject = eventRepository.getData(eventId, "date");
            java.sql.Date sqlDate;

            if (dateObject instanceof java.sql.Timestamp) {
                sqlDate = new java.sql.Date(((java.sql.Timestamp) dateObject).getTime());
            } else if (dateObject instanceof java.sql.Date) {
                sqlDate = (java.sql.Date) dateObject;
            } else {
                throw new IllegalArgumentException("Tipo inesperado: " + dateObject.getClass().getName());
            }

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String eventDate = formatter.format(sqlDate);
            if (!eventDate.isEmpty()) {
                editDatePicker.setValue(LocalDate.parse(eventDate));
            }

            setupPlaceholders();
        } else {
            errorUpdtLabel.setText("Evento não encontrado.");
            errorUpdtLabel.setAlignment(Pos.CENTER);
        }
    }

    public void updateEvent() throws IOException {

        String newName = editNameTextField.getText();
        String newLocation = editLocationTextField.getText();
        String newDescription = editDescriptionTextField.getText();
        Date newDate = Date.valueOf(editDatePicker.getValue() != null ? editDatePicker.getValue().toString() : "");
        facade.updateEvent(eventId, newName, newDate, newDescription, newLocation);
        mediator.notify("handleEvent");
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
