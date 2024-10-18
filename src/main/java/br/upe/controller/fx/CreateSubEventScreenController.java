package br.upe.controller.fx;
import br.upe.controller.EventController;
import br.upe.controller.SubEventController;
import br.upe.controller.UserController;
import br.upe.persistence.Persistence;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static br.upe.ui.Validation.isValidDate;

public class CreateSubEventScreenController extends BaseController implements FxController {
    private UserController userController;
    private SubEventController subEventController;
    private EventController eventController;

    @FXML
    private AnchorPane newSubEventPane;
    @FXML
    private Label userEmail;
    @FXML
    private TextField nameTextField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField locationTextField;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private ComboBox<String> eventComboBox;
    @FXML
    private Label errorUpdtLabel;

    public void setUserController(UserController userController) {
        this.userController = userController;
        this.subEventController = new SubEventController();
        this.eventController = new EventController();
        initial();
    }

    private void initial() {
        userEmail.setText(userController.getData("email"));
        loadUserEvents();
    }

    public void handleEvent() throws IOException {
        genericButton("/fxml/mainScreen.fxml", newSubEventPane, userController, null);
    }

    public void handleSubEvent() throws IOException {
        genericButton("/fxml/subEventScreen.fxml", newSubEventPane, userController, null);
    }

    public void handleSubmitEvent() throws IOException {
        genericButton("/fxml/submitScreen.fxml", newSubEventPane, userController, null);
    }

    public void handleSession() throws IOException {
        genericButton("/fxml/sessionScreen.fxml", newSubEventPane, userController, null);
    }

    public void logout() throws IOException {
        genericButton("/fxml/loginScreen.fxml", newSubEventPane, userController, null);
    }

    public void handleUser() throws IOException {
        genericButton("/fxml/userScreen.fxml", newSubEventPane, userController, null);
    }

    private void loadUserEvents() {
        List<String> userEvents = eventController.list(userController.getData("id"), "fx");
        eventComboBox.getItems().addAll(userEvents);
    }


    public void createSubEvent() throws IOException {
        String subEventName = nameTextField.getText();
        String subEventLocation = locationTextField.getText();
        String subEventDescription = descriptionTextField.getText();
        String subEventDate = datePicker.getValue() != null ? datePicker.getValue().toString() : "";
        String selectedEventName = eventComboBox.getSelectionModel().getSelectedItem();

        Map<String, Persistence> subEventMap = subEventController.getSubEventHashMap();
        if (!validateEventDate(subEventDate, selectedEventName)) {
            errorUpdtLabel.setText("Data do subEvento não pode ser anterior a data do evento.");
        } else if (!isValidDate(subEventDate) || subEventLocation.isEmpty() || subEventDescription.isEmpty() || isValidName(subEventName, subEventMap)) {
            errorUpdtLabel.setText("Erro no preenchimento das informações.");
        }else {
            subEventController.create(selectedEventName, subEventName, subEventDate, subEventDescription, subEventLocation, userController.getData("id"));
            subEventController.read();
            handleSubEvent();
        }
    }
}
