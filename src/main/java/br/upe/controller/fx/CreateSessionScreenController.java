package br.upe.controller.fx;

import br.upe.controller.EventController;
import br.upe.controller.SessionController;
import br.upe.controller.SubEventController;
import br.upe.controller.UserController;
import br.upe.persistence.Persistence;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import static br.upe.ui.Validation.*;

public class CreateSessionScreenController extends BaseController implements FxController {
    private UserController userController;
    private SubEventController subEventController;
    private EventController eventController;
    private SessionController sessionController;
    private final ObservableList<String> eventList = FXCollections.observableArrayList();

    @FXML
    private AnchorPane newSessionPane;
    @FXML
    private Label userEmail;
    @FXML
    private TextField nameTextField;
    @FXML
    private Text namePlaceholder;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Text datePlaceholder;
    @FXML
    private TextField locationTextField;
    @FXML
    private Text locationPlaceholder;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private Text descriptionPlaceholder;
    @FXML
    private TextField startTimeTextField;
    @FXML
    private Text startTimePlaceholder;
    @FXML
    private TextField endTimeTextField;
    @FXML
    private Text endTimePlaceholder;
    @FXML
    private TextField searchField;
    @FXML
    private Text searchFieldPlaceholder;
    @FXML
    private ListView<String> suggestionsListView;
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
        setupPlaceholders();
        loadUserEvents();
    }

    private void setupPlaceholders() {
        PlaceholderUtils.setupPlaceholder(nameTextField, namePlaceholder);
        PlaceholderUtils.setupPlaceholder(searchField, searchFieldPlaceholder);
        PlaceholderUtils.setupPlaceholder(datePicker, datePlaceholder);
        PlaceholderUtils.setupPlaceholder(locationTextField, locationPlaceholder);
        PlaceholderUtils.setupPlaceholder(descriptionTextField, descriptionPlaceholder);
        PlaceholderUtils.setupPlaceholder(startTimeTextField, startTimePlaceholder);
        PlaceholderUtils.setupPlaceholder(endTimeTextField, endTimePlaceholder);
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
        eventList.addAll(userEvents);
        eventList.addAll(userSubEvents);

        FilteredList<String> filteredItems = new FilteredList<>(eventList, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredItems.setPredicate(event -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return event.toLowerCase().contains(lowerCaseFilter);
            });
            suggestionsListView.setItems(filteredItems);
            suggestionsListView.setVisible(!filteredItems.isEmpty());
        });

        suggestionsListView.setOnMouseClicked(event -> {
            String selectedEvent = suggestionsListView.getSelectionModel().getSelectedItem();
            searchField.setText(selectedEvent);
            suggestionsListView.setVisible(false);
        });
    }


    public void createSession() throws IOException {
        String sessionName = nameTextField.getText();
        String sessionLocation = locationTextField.getText();
        String sessionDescription = descriptionTextField.getText();
        String sessionDate = datePicker.getValue() != null ? datePicker.getValue().toString() : "";
        String startTime = startTimeTextField.getText();
        String endTime = endTimeTextField.getText();
        String selectedEventName = searchField.getText();
        String type = verifyType(selectedEventName);

        Map<String, Persistence> sessionMap = sessionController.getSessionHashMap();
        if (!isValidDate(sessionDate))
        {
            errorUpdtLabel.setText("Erro no preenchimento das informações.");
        }
        else if (!validateEventDate(sessionDate, selectedEventName)) {
            errorUpdtLabel.setText("Data da sessão não pode ser anterior a data do evento.");
        } else if (!areValidTimes(startTime, endTime)) {
            errorUpdtLabel.setText("Data ou horário inválido.");
        }else if (sessionLocation.isEmpty() || sessionDescription.isEmpty() || isValidName(sessionName, sessionMap)){
            errorUpdtLabel.setText("Erro no preenchimento das informações.");
        }else {
            sessionController.create(selectedEventName, sessionName, sessionDate, sessionDescription, sessionLocation, startTime, endTime, userController.getData("id"), type);
            sessionController.read();
            handleSession();
        }
    }

}
