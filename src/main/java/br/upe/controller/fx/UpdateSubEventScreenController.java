package br.upe.controller.fx;

import br.upe.controller.SubEventController;
import br.upe.controller.UserController;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;

public class UpdateSubEventScreenController extends BaseController implements FxController {
    private UserController userController;
    private SubEventController subEventController;
    private String subeventName;

    @FXML
    private AnchorPane editSubEventPane;
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
    private Label errorUpdtLabel;
    @FXML
    private Label errorDelLabel;

    public void setUserController(UserController userController) throws IOException {
        this.userController = userController;
        this.subEventController = new SubEventController();
        initial();
    }

    public void setEventName(String eventName) {
        this.subeventName = eventName;
    }


    private void initial() {
        userEmail.setText(userController.getData("email"));
    }

    public void handleEvent() throws IOException {
        genericButton("/fxml/mainScreen.fxml", editSubEventPane, userController, null);
    }

    public void handleSubEvent() throws IOException {
        genericButton("/fxml/subEventScreen.fxml", editSubEventPane, userController, null);
    }

    public void handleSubmitEvent() throws IOException {
        genericButton("/fxml/submitScreen.fxml", editSubEventPane, userController, null);
    }

    public void handleSession() throws IOException {
        genericButton("/fxml/sessionScreen.fxml", editSubEventPane, userController, null);
    }

    public void logout() throws IOException {
        genericButton("/fxml/loginScreen.fxml", editSubEventPane, userController, null);
    }

    public void handleUser() throws IOException {
        genericButton("/fxml/userScreen.fxml", editSubEventPane, userController, null);
    }

    public void updateSubEvent() throws IOException {
        String newSubName = editNameTextField.getText();
        String newLocation = editLocationTextField.getText();
        String newDescription = editDescriptionTextField.getText();
        String newDate = editDatePicker.getValue().toString();

        subEventController.update(subeventName, newSubName, newDate, newDescription, newLocation, userController.getData("id"));
        handleEvent();
    }

}
