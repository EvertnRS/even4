package br.upe.controller.fx;

import br.upe.controller.fx.fxutils.PlaceholderUtils;
import br.upe.controller.fx.mediator.CreateSessionMediator;
import br.upe.facade.FacadeInterface;
import br.upe.persistence.Event;
import br.upe.persistence.SubEvent;
import br.upe.persistence.repository.Persistence;
import br.upe.utils.JPAUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.sql.Date;
import java.util.*;

import static br.upe.ui.Validation.areValidTimes;

public class CreateSessionScreenController extends BaseController implements FxController {
    private FacadeInterface facade;
    private final ObservableList<String> eventList = FXCollections.observableArrayList();
    private CreateSessionMediator mediator;

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
    private TextField searchField;

    @FXML
    private Text namePlaceholder;
    @FXML
    private Text datePlaceholder;
    @FXML
    private Text locationPlaceholder;
    @FXML
    private Text descriptionPlaceholder;
    @FXML
    private Text startTimePlaceholder;
    @FXML
    private Text endTimePlaceholder;
    @FXML
    private Text searchFieldPlaceholder;

    @FXML
    private ListView<String> suggestionsListView;
    @FXML
    private Label errorUpdtLabel;

    public void setFacade(FacadeInterface facade) throws IOException {
        this.facade = facade;
        initial();
    }

    private void initial() throws IOException {
        userEmail.setText(facade.getUserData("email"));
        setupPlaceholders();
        loadUserEvents();

        this.mediator = new CreateSessionMediator(this, facade, newSessionPane, errorUpdtLabel);
        mediator.registerComponents();
        mediator.setComponents(nameTextField, datePicker, locationTextField, descriptionTextField, startTimeTextField, endTimeTextField, searchField);
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

    private String[] verifyType(String name) {
        String[] type = new String[2]; // Define um array com dois elementos
        EntityManager entityManager = null;

        try {
            // Obtém o EntityManager a partir do JPAUtils
            entityManager = JPAUtils.getEntityManagerFactory();

            // Cria uma consulta para buscar o ID pelo nome
            TypedQuery<UUID> query = entityManager.createQuery(
                    "SELECT e.id FROM Event e WHERE e.name = :name", UUID.class);
            query.setParameter("name", name);

            // Atribui os valores ao array
            type[0] = query.getSingleResult().toString(); // ID
            type[1] = "evento"; // Tipo

            return type; // Retorna o array preenchido
        } catch (NoResultException e) {
            // Obtém o EntityManager a partir do JPAUtils
            entityManager = JPAUtils.getEntityManagerFactory();

            // Cria uma consulta para buscar o ID pelo nome
            TypedQuery<UUID> query = entityManager.createQuery(
                    "SELECT e.id FROM SubEvent e WHERE e.name = :name", UUID.class);
            query.setParameter("name", name);

            // Atribui os valores ao array
            type[0] = query.getSingleResult().toString(); // ID
            type[1] = "subEvento"; // Tipo

            return type;
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    private void loadUserEvents() throws IOException {
        List<Event> userEvents = facade.listEvents(facade.getUserData("id"));
        List<SubEvent> userSubEvents = facade.listSubEvents(facade.getUserData("id"));

        eventList.clear();
        userEvents.forEach(event -> eventList.add(event.getName()));
        userSubEvents.forEach(subEvent -> eventList.add(subEvent.getName()));

        FilteredList<String> filteredItems = new FilteredList<>(eventList, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredItems.setPredicate(event -> {
                if (newValue == null || newValue.isEmpty()) return false;
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
        java.sql.Date sessionDate = Date.valueOf( datePicker.getValue() != null ? datePicker.getValue().toString() : "");
        String startTime = startTimeTextField.getText();
        String endTime = endTimeTextField.getText();
        String selectedEventName = searchField.getText();
        String[] type = verifyType(selectedEventName);

        if (!validateEventDate(sessionDate.toString(), type[0], type[1])) {
            errorUpdtLabel.setText("Data da sessão não pode ser anterior a data do evento.");
            errorUpdtLabel.setAlignment(Pos.CENTER);
        } else {
            facade.createSession(selectedEventName, sessionName, sessionDate, sessionDescription, sessionLocation, startTime, endTime, facade.getUserData("id"), type);
            mediator.notify("handleSession");
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
