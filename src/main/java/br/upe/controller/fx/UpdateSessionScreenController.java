package br.upe.controller.fx;

import br.upe.controller.SessionController;
import br.upe.controller.UserController;
import br.upe.persistence.Persistence;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.Map;

import static br.upe.ui.Validation.areValidTimes;
import static br.upe.ui.Validation.isValidDate;

public class UpdateSessionScreenController extends BaseController implements FxController {
    private UserController userController;
    private SessionController sessionController;
    private String sessionName;

    @FXML
    private AnchorPane editSessionPane;
    @FXML
    private Label userEmail;
    @FXML
    private TextField editNameTextField;
    @FXML
    private Text namePlaceholder;
    @FXML
    private DatePicker editDatePicker;
    @FXML
    private Text datePlaceholder;
    @FXML
    private TextField editLocationTextField;
    @FXML
    private Text locationPlaceholder;
    @FXML
    private TextField editDescriptionTextField;
    @FXML
    private Text descriptionPlaceholder;
    @FXML
    private TextField editStartTimeTextField;
    @FXML
    private Text startTimePlaceholder;
    @FXML
    private TextField editEndTimeTextField;
    @FXML
    private Text endTimePlaceholder;
    @FXML
    private Label errorUpdtLabel;
    @FXML
    private Label errorDelLabel;

    public void setUserController(UserController userController) throws IOException {
        this.userController = userController;
        this.sessionController = new SessionController();
        initial();
    }

    public void setEventName(String eventName) {
        this.sessionName = eventName;
    }


    private void initial() {
        userEmail.setText(userController.getData("email"));
        setupPlaceholders();
    }
    private void setupPlaceholders() {
        PlaceholderUtils.setupPlaceholder(editNameTextField, namePlaceholder);
        PlaceholderUtils.setupPlaceholder(editDatePicker, datePlaceholder);
        PlaceholderUtils.setupPlaceholder(editLocationTextField, locationPlaceholder);
        PlaceholderUtils.setupPlaceholder(editDescriptionTextField, descriptionPlaceholder);
        PlaceholderUtils.setupPlaceholder(editStartTimeTextField, startTimePlaceholder);
        PlaceholderUtils.setupPlaceholder(editEndTimeTextField, endTimePlaceholder);
    }

    public void handleEvent() throws IOException {
        genericButton("/fxml/mainScreen.fxml", editSessionPane, userController, null);
    }

    public void handleSubEvent() throws IOException {
        genericButton("/fxml/subEventScreen.fxml", editSessionPane, userController, null);
    }

    public void handleSubmitEvent() throws IOException {
        genericButton("/fxml/submitScreen.fxml", editSessionPane, userController, null);
    }

    public void handleSession() throws IOException {
        genericButton("/fxml/sessionScreen.fxml", editSessionPane, userController, null);
    }

    public void logout() throws IOException {
        genericButton("/fxml/loginScreen.fxml", editSessionPane, userController, null);
    }

    public void handleUser() throws IOException {
        genericButton("/fxml/userScreen.fxml", editSessionPane, userController, null);
    }

    public void updateSession() throws IOException {
        String newSubName = editNameTextField.getText();
        String newLocation = editLocationTextField.getText();
        String newDescription = editDescriptionTextField.getText();
        String newDate = editDatePicker.getValue() != null ? editDatePicker.getValue().toString() : "";
        String newStartTime = editStartTimeTextField.getText();
        String newEndTime = editEndTimeTextField.getText();
        Map<String, Persistence> sessionMap = sessionController.getSessionHashMap();
        if (!validateEventDate(newDate, newSubName)) {
            errorUpdtLabel.setText("Data da sessão não pode ser anterior a data do evento.");
        } else if (!isValidDate(newDate) || !areValidTimes(newStartTime, newEndTime)) {
            errorUpdtLabel.setText("Data ou horário inválido.");
        }else if (newLocation.isEmpty() || newDescription.isEmpty() || isValidName(sessionName, sessionMap)){
            errorUpdtLabel.setText("Erro no preenchimento das informações.");
        }else {
            sessionController.update(sessionName, newSubName, newDate, newDescription, newLocation,  userController.getData("id"), newStartTime, newEndTime);
            sessionController.read();
            handleSession();}
    }

}
