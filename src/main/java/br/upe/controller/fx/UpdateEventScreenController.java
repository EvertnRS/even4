package br.upe.controller.fx;

import br.upe.controller.EventController;
import br.upe.controller.UserController;
import br.upe.facade.Facade;
import br.upe.facade.FacadeInterface;
import br.upe.persistence.Persistence;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

import static br.upe.ui.Validation.isValidDate;

public class UpdateEventScreenController extends BaseController implements FxController {
    private FacadeInterface facade;
    private String eventId;

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
    @FXML
    private Label errorDelLabel;

    public void setFacade(FacadeInterface facade) throws IOException {
        this.facade = facade;
        initial();
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
        loadEventDetails();
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
    }

    public void handleEvent() throws IOException {
        genericButton("/fxml/mainScreen.fxml", editEventPane, facade, null);
    }

    public void handleSubEvent() throws IOException {
        genericButton("/fxml/subEventScreen.fxml", editEventPane, facade, null);
    }

    public void handleSubmitEvent() throws IOException {
        genericButton("/fxml/submitScreen.fxml", editEventPane, facade, null);
    }

    public void handleSession() throws IOException {
        genericButton("/fxml/sessionScreen.fxml", editEventPane, facade, null);
    }

    public void logout() throws IOException {
        genericButton("/fxml/loginScreen.fxml", editEventPane, facade, null);
    }

    public void handleUser() throws IOException {
        genericButton("/fxml/userScreen.fxml", editEventPane, facade, null);
    }

    private void loadEventDetails() {
        Map<String, Persistence> eventMap = facade.getEventHashMap();
        Persistence event = eventMap.get(eventId);

        if (event != null) {
            editNameTextField.setText(event.getData("name"));
            editLocationTextField.setText(event.getData("location"));
            editDescriptionTextField.setText(event.getData("description"));

            String eventDate = event.getData("date");
            if (eventDate != null && !eventDate.isEmpty()) {
                editDatePicker.setValue(LocalDate.parse(eventDate));
            }
        } else {
            errorUpdtLabel.setText("Evento não encontrado.");
        }
    }


    public void updateEvent() throws IOException {

        String newName = editNameTextField.getText();
        String newLocation = editLocationTextField.getText();
        String newDescription = editDescriptionTextField.getText();
        String newDate = editDatePicker.getValue() != null ? editDatePicker.getValue().toString() : "";
        Map<String, Persistence> eventMap = facade.getEventHashMap();
        if (!isValidDate(newDate) || newLocation.isEmpty() || newDescription.isEmpty() || isValidName(eventId, eventMap)) {
            errorUpdtLabel.setText("Erro no preenchimento das informações.");
        }else {
            facade.updateEvent(eventId, newName, newDate, newDescription, newLocation, facade.getUserData("id"));
            handleEvent();
        }
    }

}
