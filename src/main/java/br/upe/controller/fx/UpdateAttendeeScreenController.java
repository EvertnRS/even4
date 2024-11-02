package br.upe.controller.fx;

import br.upe.controller.AttendeeController;
import br.upe.controller.SessionController;
import br.upe.controller.UserController;
import br.upe.facade.Facade;
import br.upe.facade.FacadeInterface;
import br.upe.persistence.Persistence;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class UpdateAttendeeScreenController extends BaseController implements FxController {
    private FacadeInterface facade;
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

    public void setFacade(FacadeInterface facade) throws IOException {
        this.facade = facade;
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

    private void loadUserEvents(){
        Map<String, Persistence> sessionHashMap = facade.getSessionHashMap();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<String> filteredSessions = sessionHashMap.values().stream()
                .filter(session -> {
                    String sessionDateStr = session.getData("date");
                    LocalDate sessionDate = LocalDate.parse(sessionDateStr, formatter);
                    return sessionDate.isAfter(today);
                })
                .map(session -> session.getData("name"))
                .toList();

        eventComboBox.getItems().addAll(filteredSessions);
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

        Map<String, Persistence> sessionHashMap = facade.getSessionHashMap();
        for (Map.Entry<String, Persistence> entry : sessionHashMap.entrySet()) {
            Persistence persistence = entry.getValue();
            if (persistence.getData("name").equals(selectedSessionName)) {
                sessionId = persistence.getData("id");
            }
        }

        Map<String, Persistence> attendeeMap = facade.getAttendeeHashMap();
        if (isValidName(attendeeName, attendeeMap)) {
            errorUpdtLabel.setText("Erro no preenchimento das informações.");
        }else {
            facade.updateAttendee(attendeeName,sessionId);
            facade.readAttendee();
            handleInscription();
        }


    }




}


