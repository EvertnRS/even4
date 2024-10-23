package br.upe.controller.fx;

import br.upe.controller.UserController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;

public class SubmitEventScreenController extends BaseController implements FxController{
    UserController userController;

    @FXML
    private Label userEmail;
    @FXML
    private AnchorPane submitPane;

    public void setUserController(UserController userController) {
        this.userController = userController;
        initial();
    }

    private void initial() {
        userEmail.setText(userController.getData("email"));
    }

    public void handleUser() throws IOException {
        genericButton("/fxml/userScreen.fxml", submitPane, userController, null);
    }

    public void handleSubEvent() throws IOException {
        genericButton("/fxml/subEventScreen.fxml", submitPane, userController, null);
    }

    public void handleEvent() throws IOException {
        genericButton("/fxml/mainScreen.fxml", submitPane, userController, null);
    }

    public void handleSession() throws IOException {
        genericButton("/fxml/sessionScreen.fxml", submitPane, userController, null);
    }

    public void logout() throws IOException {
        genericButton("/fxml/loginScreen.fxml", submitPane, userController, null);
    }
}
