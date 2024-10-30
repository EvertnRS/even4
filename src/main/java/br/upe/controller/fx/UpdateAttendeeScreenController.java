package br.upe.controller.fx;

import br.upe.controller.AttendeeController;
import br.upe.controller.SessionController;
import br.upe.controller.UserController;
import br.upe.facade.Facade;
import br.upe.persistence.Persistence;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class UpdateAttendeeScreenController extends BaseController implements FxController {
    private Facade facade;
    private SessionController sessionController;
    private AttendeeController attendeeController;
    @FXML
    private ComboBox<String> eventComboBox;
    @FXML
    private AnchorPane newAttendeePane;
    @FXML
    private Label userEmail;
    @FXML
    private TextField nameTextField;
    @FXML
    private Text namePlaceholder;
    @FXML
    private Label errorUpdtLabel;

    public void setFacade(Facade facade) throws IOException {
        this.facade = facade;
        this.sessionController = new SessionController();
        this.attendeeController = new AttendeeController();
        initial();
    }

    private void initial() throws IOException {
        userEmail.setText(facade.getUserData("email"));
        loadUserEvents();
        setupPlaceholders();
    }

    private void setupPlaceholders() {
        PlaceholderUtils.setupPlaceholder(nameTextField, namePlaceholder);
    }

    private void loadUserEvents() throws IOException {
        List<String> userEvents = sessionController.list(facade.getUserData("id"), "fx");
        eventComboBox.getItems().addAll(userEvents);

    }

    public void handleEvent() throws IOException {
        genericButton("/fxml/mainScreen.fxml", newAttendeePane, facade, null);
    }

    public void handleSubEvent() throws IOException {
        genericButton("/fxml/subEventScreen.fxml", newAttendeePane, facade, null);
    }

    public void handleSubmitEvent() throws IOException {
        genericButton("/fxml/submitScreen.fxml", newAttendeePane, facade, null);
    }

    public void handleSession() throws IOException {
        genericButton("/fxml/sessionScreen.fxml", newAttendeePane, facade, null);
    }

    public void handleInscription() throws IOException {
        genericButton("/fxml/enterSessionScreen.fxml", newAttendeePane, facade, null);
    }

    public void logout() throws IOException {
        genericButton("/fxml/loginScreen.fxml", newAttendeePane, facade, null);
    }

    public void handleUser() throws IOException {
        genericButton("/fxml/userScreen.fxml", newAttendeePane, facade, null);
    }

    public void updateAttendee() throws IOException {
        String attendeeName = nameTextField.getText();
        String selectedSessionName = eventComboBox.getSelectionModel().getSelectedItem();
        String sessionId = "";

        Map<String, Persistence> sessionHashMap = sessionController.getSessionHashMap();
        for (Map.Entry<String, Persistence> entry : sessionHashMap.entrySet()) {
            Persistence persistence = entry.getValue();
            if (persistence.getData("name").equals(selectedSessionName)) {
                sessionId = persistence.getData("id");
            }
        }

        Map<String, Persistence> attendeeMap = attendeeController.getAttendeeHashMap();
        if (isValidName(attendeeName, attendeeMap)) {
            errorUpdtLabel.setText("Erro no preenchimento das informações.");
        }else {
            attendeeController.update(attendeeName,sessionId);
            attendeeController.read();
            handleInscription();
        }


    }




}


