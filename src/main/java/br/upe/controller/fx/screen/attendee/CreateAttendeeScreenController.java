package br.upe.controller.fx.screen.attendee;

import br.upe.controller.fx.mediator.attendee.CreateAttendeeMediator;
import br.upe.controller.fx.screen.BaseController;
import br.upe.controller.fx.screen.FxController;
import br.upe.facade.FacadeInterface;
import br.upe.persistence.Model;
import br.upe.persistence.repository.SessionRepository;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.List;

public class CreateAttendeeScreenController extends BaseController implements FxController {
    private FacadeInterface facade;
    private CreateAttendeeMediator mediator;

    @FXML
    private ComboBox<String> eventComboBox;
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
        loadSessions();

        mediator = new CreateAttendeeMediator(this, facade, newAttendeePane, errorUpdtLabel);
        mediator.registerComponents();
    }

    private void loadSessions() {
        List<Model> sessions = facade.getAllSession();
        eventComboBox.getItems().clear();

        SessionRepository sessionRepository = SessionRepository.getInstance();

        for (Model session : sessions) {
            String sessionName = (String) sessionRepository.getData(session.getId(), "name");
            java.sql.Timestamp sessionTimestamp = (java.sql.Timestamp) sessionRepository.getData(session.getId(), "date");

            if (sessionName != null && sessionTimestamp != null && isAfterToday(sessionTimestamp.toLocalDateTime().toLocalDate())) {
                eventComboBox.getItems().add(sessionName);
            }
        }
    }

    public void createAttendee() throws IOException {
        String selectedSessionName = eventComboBox.getSelectionModel().getSelectedItem();

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


