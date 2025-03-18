package br.upe.ui.fx.screen.attendee;

import br.upe.persistence.Event;
import br.upe.persistence.SubEvent;
import br.upe.persistence.repository.EventRepository;
import br.upe.persistence.repository.SubEventRepository;
import br.upe.ui.fx.mediator.attendee.CreateAttendeeMediator;
import br.upe.ui.fx.screen.BaseController;
import br.upe.ui.fx.screen.FxController;
import br.upe.facade.FacadeInterface;
import br.upe.persistence.Model;
import br.upe.persistence.repository.SessionRepository;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class CreateAttendeeScreenController extends BaseController implements FxController {
    private FacadeInterface facade;
    private CreateAttendeeMediator mediator;

    @FXML
    private ComboBox<String> eventComboBox;
    @FXML
    private ComboBox<String> sessionComboBox;
    @FXML
    private AnchorPane newAttendeePane;
    @FXML
    private Label userEmail;
    @FXML
    private Label errorUpdtLabel;

    public void setFacade(FacadeInterface facade) throws IOException {
        this.facade = facade;
        initial();
    }

    private void initial() {
        userEmail.setText(facade.getUserData("email"));
        sessionComboBox.setVisible(false);
        loadEvents();

        mediator = new CreateAttendeeMediator(this, facade, newAttendeePane, errorUpdtLabel);
        mediator.registerComponents();
    }

    private void loadEvents() {
        Platform.runLater(() -> {
            try {
                List<Model> events = facade.getAllEvent();
                List<Model> subEvents = facade.getAllSubEvent();
                eventComboBox.getItems().clear();

                EventRepository eventRepository = EventRepository.getInstance();
                SubEventRepository subEventRepository = SubEventRepository.getInstance();

                for (Model event : events) {
                    String eventName = (String) eventRepository.getData(event.getId(), "name");
                    java.sql.Timestamp eventTimestamp = (java.sql.Timestamp) eventRepository.getData(event.getId(), "date");

                    if (eventName != null && eventTimestamp != null && isAfterToday(eventTimestamp.toLocalDateTime().toLocalDate())) {
                        eventComboBox.getItems().add(eventName);
                    }
                }

                for (Model subEvent : subEvents) {
                    String subEventName = (String) subEventRepository.getData(subEvent.getId(), "name");

                    if (subEventName != null ) {
                        eventComboBox.getItems().add(subEventName);
                    }
                }

                eventComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        String[] eventId = facade.verifyByEventName(newValue);
                        sessionComboBox.setVisible(true);
                        loadSessions(eventId[0]);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void loadSessions(String eventId) {
        List<Model> sessions = facade.getAllSession();
        sessionComboBox.getItems().clear();

        SessionRepository sessionRepository = SessionRepository.getInstance();

        for (Model session : sessions) {
            Event sessionEvent = (Event) sessionRepository.getData(session.getId(), "eventId");
            SubEvent subEvent = (SubEvent) sessionRepository.getData(session.getId(), "subEvent_id");

            if ((sessionEvent != null && sessionEvent.getId().toString().equals(eventId)) ||
                    (subEvent != null && subEvent.getId().toString().equals(eventId))) {
                String sessionName = (String) sessionRepository.getData(session.getId(), "name");
                java.sql.Timestamp sessionTimestamp = (java.sql.Timestamp) sessionRepository.getData(session.getId(), "date");

                if (sessionName != null && sessionTimestamp != null && isAfterToday(sessionTimestamp.toLocalDateTime().toLocalDate())) {
                    sessionComboBox.getItems().add(sessionName);
                }
            }
        }
    }

    public void createAttendee() throws IOException {
        String selectedSessionName = sessionComboBox.getSelectionModel().getSelectedItem();

        facade.createAttendee(selectedSessionName, facade.getUserData("id"));
        mediator.notify("handleBack");

    }

    public boolean isAfterToday(java.time.LocalDate date) {
        return date.isAfter(java.time.LocalDate.now());
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


