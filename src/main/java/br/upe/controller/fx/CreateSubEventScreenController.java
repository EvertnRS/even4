package br.upe.controller.fx;
import br.upe.controller.EventController;
import br.upe.controller.SubEventController;
import br.upe.controller.UserController;
import br.upe.persistence.Persistence;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javafx.scene.text.Text;

import static br.upe.ui.Validation.isValidDate;

public class CreateSubEventScreenController extends BaseController implements FxController {
    private UserController userController;
    private SubEventController subEventController;
    private EventController eventController;
    private final ObservableList<String> eventList = FXCollections.observableArrayList();


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
    private ListView<String> suggestionsListView;
    @FXML
    private TextField searchField;
    @FXML
    private Text namePlaceholder;
    @FXML
    private Text searchFieldPlaceholder;
    @FXML
    private Text locationPlaceholder;
    @FXML
    private Text descriptionPlaceholder;
    @FXML
    private Text datePlaceholder;
    @FXML
    private Label errorUpdtLabel;



    public void setUserController(UserController userController) throws IOException {
        this.userController = userController;
        this.subEventController = new SubEventController();
        this.eventController = new EventController();
        initial();
    }


    private void initial() throws IOException {
        userEmail.setText(userController.getData("email"));
        loadUserEvents();
        setupPlaceholders();
    }

    private void setupPlaceholders() {
        PlaceholderUtils.setupPlaceholder(nameTextField, namePlaceholder);
        PlaceholderUtils.setupPlaceholder(searchField, searchFieldPlaceholder);
        PlaceholderUtils.setupPlaceholder(datePicker, datePlaceholder);
        PlaceholderUtils.setupPlaceholder(locationTextField, locationPlaceholder);
        PlaceholderUtils.setupPlaceholder(descriptionTextField, descriptionPlaceholder);
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

    private void loadUserEvents() throws IOException {
        List<String> userEvents = eventController.list(userController.getData("id"), "fx");
        eventList.setAll(userEvents);

        FilteredList<String> filteredItems = new FilteredList<>(eventList, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredItems.setPredicate(event -> {
                if (newValue == null || newValue.isEmpty()) {
                    return false;
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


    public void createSubEvent() throws IOException {
        String subEventName = nameTextField.getText();
        String subEventLocation = locationTextField.getText();
        String subEventDescription = descriptionTextField.getText();
        String subEventDate = datePicker.getValue() != null ? datePicker.getValue().toString() : "";
        String selectedEventName = searchField.getText();

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
