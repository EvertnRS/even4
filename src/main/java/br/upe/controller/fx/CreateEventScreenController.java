package br.upe.controller.fx;

import br.upe.controller.EventController;
import br.upe.controller.UserController;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

import static br.upe.ui.Validation.areValidTimes;
import static br.upe.ui.Validation.isValidDate;

public class CreateEventScreenController extends BaseController implements FxController {
    private UserController userController;
    private EventController eventController;

    @FXML
    private AnchorPane newEventPane;
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
    private Label errorUpdtLabel;

    public void setUserController(UserController userController) {
        this.userController = userController;
        this.eventController = new EventController();
        initial();
    }

    private void initial() {
        userEmail.setText(userController.getData("email"));
    }

    public void handleEvent() throws IOException {
        genericButton("/fxml/mainScreen.fxml", newEventPane, userController, null);
    }

    public void handleSubEvent() throws IOException {
        genericButton("/fxml/subEventScreen.fxml", newEventPane, userController, null);
    }

    public void handleSubmitEvent() throws IOException {
        genericButton("/fxml/submitScreen.fxml", newEventPane, userController, null);
    }

    public void handleSession() throws IOException {
        genericButton("/fxml/sessionScreen.fxml", newEventPane, userController, null);
    }

    public void logout() throws IOException {
        genericButton("/fxml/loginScreen.fxml", newEventPane, userController, null);
    }

    public void handleUser() throws IOException {
        genericButton("/fxml/userScreen.fxml", newEventPane, userController, null);
    }

    public void createEvent() throws IOException {
        String eventName = nameTextField.getText();
        String eventLocation = locationTextField.getText();
        String eventDescription = descriptionTextField.getText();
        String eventDate = datePicker.getValue().toString();

        if (!isValidDate(eventDate)) {
            errorUpdtLabel.setText("Data inválida.");
        }else {
            eventController.create(eventName, eventLocation, eventDescription, eventDate, userController.getData("id"));
            eventController.read();
            handleEvent();
        }
    }

}
