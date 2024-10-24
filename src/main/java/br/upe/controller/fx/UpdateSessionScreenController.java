package br.upe.controller.fx;

import br.upe.controller.SessionController;
import br.upe.controller.UserController;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;

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
    private DatePicker editDatePicker;
    @FXML
    private TextField editLocationTextField;
    @FXML
    private TextField editDescriptionTextField;
    @FXML
    private TextField editStartTimeTextField;
    @FXML
    private TextField editEndTimeTextField;
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
        String newDate = editDatePicker.getValue().toString();
        String newStartTime = editStartTimeTextField.getText();
        String newEndTime = editEndTimeTextField.getText();

        sessionController.update(sessionName, newSubName, newDate, newDescription, newLocation,  userController.getData("id"), newStartTime, newEndTime);
        handleEvent();
    }

}
