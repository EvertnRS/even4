package br.upe.controller.fx;

import br.upe.controller.EventController;
import br.upe.controller.SessionController;
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
import java.util.List;
import java.util.Map;
import static br.upe.ui.Validation.*;

public class CreateSessionScreenController extends BaseController implements FxController {
    private UserController userController;
    private SubEventController subEventController;
    private EventController eventController;
    private SessionController sessionController;

    @FXML
    private AnchorPane newSessionPane;
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
    private TextField startTimeTextField;
    @FXML
    private TextField endTimeTextField;
    @FXML
    private ComboBox<String> eventComboBox;
    @FXML
    private Label errorUpdtLabel;

    public void setUserController(UserController userController) throws IOException {
        this.userController = userController;
        this.subEventController = new SubEventController();
        this.eventController = new EventController();
        this.sessionController = new SessionController();
        initial();
    }

    private void initial() throws IOException {
        userEmail.setText(userController.getData("email"));
        loadUserEvents();
    }

    public void handleEvent() throws IOException {
        genericButton("/fxml/mainScreen.fxml", newSessionPane, userController, null);
    }

    public void handleSubEvent() throws IOException {
        genericButton("/fxml/subEventScreen.fxml", newSessionPane, userController, null);
    }

    public void handleSubmitEvent() throws IOException {
        genericButton("/fxml/submitScreen.fxml", newSessionPane, userController, null);
    }

    public void handleSession() throws IOException {
        genericButton("/fxml/SessionScreen.fxml", newSessionPane, userController, null);
    }

    public void logout() throws IOException {
        genericButton("/fxml/loginScreen.fxml", newSessionPane, userController, null);
    }

    public void handleUser() throws IOException {
        genericButton("/fxml/userScreen.fxml", newSessionPane, userController, null);
    }

    public String verifyType(String name) {

        Map<String, Persistence> eventMap = eventController.getEventHashMap();
        Map<String, Persistence> subEventMap = subEventController.getSubEventHashMap();
        for (Map.Entry<String, Persistence> entry : eventMap.entrySet()) {
            Persistence persistence = entry.getValue();
            if (persistence.getData("name").equals(name)) {
                return "Event";
            }
        }

        for (Map.Entry<String, Persistence> entry : subEventMap.entrySet()) {
            Persistence persistence = entry.getValue();
            if (persistence.getData("name").equals(name)) {
                return "SubEvent";
            }
        }
        return "";
    }

    private void loadUserEvents() throws IOException {
        List<String> userEvents = eventController.list(userController.getData("id"), "fx");
        List<String> userSubEvents = subEventController.list(userController.getData("id"), "fx");
        eventComboBox.getItems().addAll(userEvents);
        eventComboBox.getItems().addAll(userSubEvents);
    }


    public void createSession() throws IOException {
        String sessionName = nameTextField.getText();
        String sessionLocation = locationTextField.getText();
        String sessionDescription = descriptionTextField.getText();
        String sessionDate = datePicker.getValue() != null ? datePicker.getValue().toString() : "";
        String startTime = startTimeTextField.getText();
        String endTime = endTimeTextField.getText();
        String selectedEventName = eventComboBox.getSelectionModel().getSelectedItem();
        String type = verifyType(selectedEventName);

        Map<String, Persistence> sessionMap = sessionController.getSessionHashMap();
        if (!validateEventDate(sessionDate, selectedEventName)) {
            errorUpdtLabel.setText("Data da sessão não pode ser anterior a data do evento.");
        } else if (!isValidDate(sessionDate) || !areValidTimes(startTime, endTime) || sessionLocation.isEmpty() || sessionDescription.isEmpty() || isValidName(sessionName, sessionMap)) {
            errorUpdtLabel.setText("Data ou horário inválido.");
        }else {
            sessionController.create(selectedEventName, sessionName, sessionDate, sessionDescription, sessionLocation, startTime, endTime, userController.getData("id"), type);
            sessionController.read();
            handleSession();
        }
    }

}
