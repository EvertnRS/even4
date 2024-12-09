package br.upe.controller.fx;

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

    @FXML
    private AnchorPane newSessionPane;
    @FXML
    private Label userEmail;
    @FXML
    private TextField nameTextField, locationTextField, descriptionTextField, startTimeTextField, endTimeTextField, searchField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Text namePlaceholder, datePlaceholder, locationPlaceholder, descriptionPlaceholder, startTimePlaceholder, endTimePlaceholder, searchFieldPlaceholder;
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
        genericButton("/fxml/mainScreen.fxml", newSessionPane, facade, null);
    }

    public void handleSubEvent() throws IOException {
        genericButton("/fxml/subEventScreen.fxml", newSessionPane, facade, null);
    }

    public void handleSubmitEvent() throws IOException {
        genericButton("/fxml/submitScreen.fxml", newSessionPane, facade, null);
    }

    public void handleSession() throws IOException {
        genericButton("/fxml/sessionScreen.fxml", newSessionPane, facade, null);
    }

    public void logout() throws IOException {
        genericButton("/fxml/loginScreen.fxml", newSessionPane, facade, null);
    }

    public void handleUser() throws IOException {
        genericButton("/fxml/userScreen.fxml", newSessionPane, facade, null);
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



        if (selectedEventName == null || selectedEventName.isEmpty()) {
            errorUpdtLabel.setText("Por favor, selecione um evento ou subevento válido.");
            return;
        }

        if (sessionDate == null || !areValidTimes(startTime, endTime) || sessionLocation.isEmpty() || sessionDescription.isEmpty()) {
            errorUpdtLabel.setText("Erro no preenchimento das informações.");
            errorUpdtLabel.setAlignment(Pos.CENTER);
        } else if (!validateEventDate(sessionDate.toString(), verifyType(selectedEventName))) {
            errorUpdtLabel.setText("Data da sessão não pode ser anterior a data do evento.");
        } else {
            facade.createSession(selectedEventName, sessionName, sessionDate, sessionDescription, sessionLocation, startTime, endTime, facade.getUserData("id"), type);
            handleSession();
        }
    }
}
