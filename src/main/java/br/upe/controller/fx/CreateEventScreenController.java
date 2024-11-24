package br.upe.controller.fx;
import br.upe.facade.FacadeInterface;
import br.upe.persistence.Event;
import br.upe.persistence.Model;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import java.io.IOException;
import java.sql.Date;
import java.util.List;

import static br.upe.ui.Validation.isValidDate;

public class CreateEventScreenController extends BaseController implements FxController {
    private FacadeInterface facade;

    @FXML
    private AnchorPane newEventPane;
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
    private Label errorUpdtLabel;

    public void setFacade(FacadeInterface facade) throws IOException {
        this.facade = facade;
        initial();
    }

    private void initial() {
        userEmail.setText(facade.getUserData("email"));
        setupPlaceholders();
    }

    private void setupPlaceholders() {
        PlaceholderUtils.setupPlaceholder(nameTextField, namePlaceholder);
        PlaceholderUtils.setupPlaceholder(datePicker, datePlaceholder);
        PlaceholderUtils.setupPlaceholder(locationTextField, locationPlaceholder);
        PlaceholderUtils.setupPlaceholder(descriptionTextField, descriptionPlaceholder);
    }

    public void handleEvent() throws IOException {
        genericButton("/fxml/mainScreen.fxml", newEventPane, facade, null);
    }

    public void handleSubEvent() throws IOException {
        genericButton("/fxml/subEventScreen.fxml", newEventPane, facade, null);
    }

    public void handleSubmitEvent() throws IOException {
        genericButton("/fxml/submitScreen.fxml", newEventPane, facade, null);
    }

    public void handleSession() throws IOException {
        genericButton("/fxml/sessionScreen.fxml", newEventPane, facade, null);
    }

    public void logout() throws IOException {
        genericButton("/fxml/loginScreen.fxml", newEventPane, facade, null);
    }

    public void handleUser() throws IOException {
        genericButton("/fxml/userScreen.fxml", newEventPane, facade, null);
    }

    public void createEvent() throws IOException {
        String eventName = nameTextField.getText();
        String eventLocation = locationTextField.getText();
        String eventDescription = descriptionTextField.getText();
        Date eventDate = Date.valueOf(datePicker.getValue() != null ? datePicker.getValue().toString() : "");

        List<Model> eventList = facade.getAllEvent();
        if (!isValidDate(String.valueOf(eventDate)) || eventLocation.isEmpty() || eventDescription.isEmpty() || isValidName(eventName, eventList)) {
            errorUpdtLabel.setText("Erro no preenchimento das informações.");
            errorUpdtLabel.setAlignment(Pos.CENTER);
        }else {
            facade.createEvent(eventName, eventDate, eventDescription, eventLocation, facade.getUserData("id"));
            facade.readEvent();
            handleEvent();
        }
    }

}


