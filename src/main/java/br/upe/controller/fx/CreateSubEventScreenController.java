package br.upe.controller.fx;

import br.upe.controller.fx.fxutils.PlaceholderUtils;
import br.upe.controller.fx.mediator.CreateSubEventMediator;
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
    private CreateSubEventMediator mediator;

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

        mediator = new CreateSubEventMediator(this, facade, newSubEventPane, errorUpdtLabel);
        mediator.registerComponents();

        setupPlaceholders();
    }

    private void setupPlaceholders() {
        PlaceholderUtils.setupPlaceholder(nameTextField, namePlaceholder);
        PlaceholderUtils.setupPlaceholder(searchField, searchFieldPlaceholder);
        PlaceholderUtils.setupPlaceholder(datePicker, datePlaceholder);
        PlaceholderUtils.setupPlaceholder(locationTextField, locationPlaceholder);
        PlaceholderUtils.setupPlaceholder(descriptionTextField, descriptionPlaceholder);
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
        String subEventName = nameTextField.getText();
        String subEventLocation = locationTextField.getText();
        String subEventDescription = descriptionTextField.getText();
        Date subEventDate = Date.valueOf(datePicker.getValue() != null ? datePicker.getValue().toString() : "");
        String selectedEventName = searchField.getText();

        facade.createSubEvent(selectedEventName,subEventName, subEventDate, subEventDescription, subEventLocation, facade.getUserData("id"));
        mediator.notify("handleSubEvent");
    }

    @Override
    public TextField getNameTextField() {
        return nameTextField;
    }

    @Override
    public TextField getLocationTextField() {
        return locationTextField;
    }

    @Override
    public TextField getDescriptionTextField() {
        return descriptionTextField;
    }

    @Override
    public DatePicker getDatePicker() {
        return datePicker;
    }
}

