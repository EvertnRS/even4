package br.upe.controller.fx;

import br.upe.controller.fx.fxutils.PlaceholderUtils;
import br.upe.controller.fx.mediator.CreateSubEventMediator;
import br.upe.facade.FacadeInterface;
import br.upe.persistence.Event;
import br.upe.utils.JPAUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.UUID;


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

        mediator.setComponents(nameTextField, datePicker, locationTextField, descriptionTextField, searchField);
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
        UUID selectedEvent = getEventIdByName(searchField.getText());

        if (!validateEventDate(subEventDate.toString(), String.valueOf(selectedEvent), "evento")) {
            errorUpdtLabel.setText("Data do subEvento não pode ser anterior a data do Evento.");
            errorUpdtLabel.setAlignment(Pos.CENTER);
        } else {
            facade.createSubEvent(searchField.getText(), subEventName, subEventDate, subEventDescription, subEventLocation, facade.getUserData("id"));
            mediator.notify("handleSubEvent");
        }

    }

    public UUID getEventIdByName(String eventName) {
        try {
            EntityManager entityManager = JPAUtils.getEntityManagerFactory();
            TypedQuery<UUID> query = entityManager.createQuery(
                    "SELECT e.id FROM Event e WHERE LOWER(TRIM(e.name)) = LOWER(TRIM(:name))", UUID.class);
            query.setParameter("name", eventName.trim());
            return query.getSingleResult();
        } catch (NoResultException e) {
            throw new IllegalArgumentException("Event not found: " + eventName, e);
        }
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

