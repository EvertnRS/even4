package br.upe.controller.fx;

import br.upe.controller.EventController;
import br.upe.controller.UserController;
import br.upe.persistence.Persistence;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

import static br.upe.ui.Validation.isValidDate;

public class UpdateEventScreenController extends BaseController implements FxController {
    private UserController userController;
    private EventController eventController;
    private String eventName;
    private String eventId;

    @FXML
    private AnchorPane editEventPane;
    @FXML
    private Label userEmail;
    @FXML
    private TextField editNameTextField;
    @FXML
    private DatePicker editDatePicker;
    @FXML
    private TextField editLocationTextField;
    @FXML
    private TextField editDescriptionTextField;
    @FXML
    private Label errorUpdtLabel;
    @FXML
    private Label errorDelLabel;

    public void setUserController(UserController userController) throws IOException {
        this.userController = userController;
        this.eventController = new EventController();
        initial();
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
        loadEventDetails();
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }


    private void initial() {
        userEmail.setText(userController.getData("email"));
    }

    public void handleEvent() throws IOException {
        genericButton("/fxml/mainScreen.fxml", editEventPane, userController, null);
    }

    public void handleSubEvent() throws IOException {
        genericButton("/fxml/subEventScreen.fxml", editEventPane, userController, null);
    }

    public void handleSubmitEvent() throws IOException {
        genericButton("/fxml/submitScreen.fxml", editEventPane, userController, null);
    }

    public void handleSession() throws IOException {
        genericButton("/fxml/sessionScreen.fxml", editEventPane, userController, null);
    }

    public void logout() throws IOException {
        genericButton("/fxml/loginScreen.fxml", editEventPane, userController, null);
    }

    public void handleUser() throws IOException {
        genericButton("/fxml/userScreen.fxml", editEventPane, userController, null);
    }

    private void loadEventDetails() {
        Map<String, Persistence> eventMap = eventController.getEventHashMap();
        Persistence event = eventMap.get(eventName);

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


        System.out.println("Evento selecionado: " + eventId);

        String newName = editNameTextField.getText();
        String newLocation = editLocationTextField.getText();
        String newDescription = editDescriptionTextField.getText();
        String newDate = editDatePicker.getValue() != null ? editDatePicker.getValue().toString() : "";
        Map<String, Persistence> eventMap = eventController.getEventHashMap();
        if (!isValidDate(newDate) || newLocation.isEmpty() || newDescription.isEmpty() || isValidName(eventName, eventMap)) {
            errorUpdtLabel.setText("Erro no preenchimento das informações.");
        }else {
            eventController.update(eventName, newName, newDate, newDescription, newLocation, userController.getData("id"));
            handleEvent();
        }
    }

}
