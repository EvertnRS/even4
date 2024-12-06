package br.upe.controller.fx;

import br.upe.controller.fx.fxutils.PlaceholderUtils;
import br.upe.facade.FacadeInterface;
import br.upe.persistence.Event;
import br.upe.persistence.Model;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;
import java.sql.Date;
import java.util.List;

import javafx.scene.text.Text;

import static br.upe.ui.Validation.isValidDate;

public class CreateSubEventScreenController extends BaseController implements FxController {
    private FacadeInterface facade;
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
        PlaceholderUtils.setupPlaceholder(searchField, searchFieldPlaceholder);
        PlaceholderUtils.setupPlaceholder(datePicker, datePlaceholder);
        PlaceholderUtils.setupPlaceholder(locationTextField, locationPlaceholder);
        PlaceholderUtils.setupPlaceholder(descriptionTextField, descriptionPlaceholder);
    }

    public void handleEvent() throws IOException {
        genericButton("/fxml/mainScreen.fxml", newSubEventPane, facade, null);
    }

    public void handleSubEvent() throws IOException {
        genericButton("/fxml/subEventScreen.fxml", newSubEventPane, facade, null);
    }

    public void handleSubmitEvent() throws IOException {
        genericButton("/fxml/submitScreen.fxml", newSubEventPane, facade, null);
    }

    public void handleSession() throws IOException {
        genericButton("/fxml/sessionScreen.fxml", newSubEventPane, facade, null);
    }

    public void logout() throws IOException {
        genericButton("/fxml/loginScreen.fxml", newSubEventPane, facade, null);
    }

    public void handleUser() throws IOException {
        genericButton("/fxml/userScreen.fxml", newSubEventPane, facade, null);
    }

    private void loadUserEvents() throws IOException {
        List<Event> userEvents = facade.listEvents(facade.getUserData("id"));
        eventList.clear();
        for (Event event : userEvents) {
            eventList.add(event.getName());
        }

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
        String subeventName = nameTextField.getText();
        String subeventLocation = locationTextField.getText();
        String subeventDescription = descriptionTextField.getText();
        Date subeventDate = Date.valueOf(datePicker.getValue() != null ? datePicker.getValue().toString() : "");
        String selectedEventName = searchField.getText();

        List<Model> subeventList = facade.getAllSubEvent();
        if (!isValidDate(String.valueOf(subeventDate)) || subeventLocation.isEmpty() || subeventDescription.isEmpty() || isValidName(subeventName, subeventList)) {
            errorUpdtLabel.setText("Erro no preenchimento das informações.");
            errorUpdtLabel.setAlignment(Pos.CENTER);
        }else {
            facade.createSubEvent(selectedEventName,subeventName, subeventDate, subeventDescription, subeventLocation, facade.getUserData("id"));
            handleSubEvent();
        }
    }

    @Override
    public TextField getNameTextField() {
        return null;
    }

    @Override
    public TextField getLocationTextField() {
        return null;
    }

    @Override
    public TextField getDescriptionTextField() {
        return null;
    }

    @Override
    public DatePicker getDatePicker() {
        return null;
    }

}
