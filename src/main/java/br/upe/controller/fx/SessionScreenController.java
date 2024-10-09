package br.upe.controller.fx;

import br.upe.controller.EventController;
import br.upe.controller.UserController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class SessionScreenController extends BaseController implements FxController {
    UserController userController;

    @FXML
    private Label userEmail;
    @FXML
    private AnchorPane sessionPane;

    public void setUserController(UserController userController) {
        this.userController = userController;
        initial();
    }

    private void initial() {
        userEmail.setText(userController.getData("email"));
    }

    public void handleUser() throws IOException {
        genericButton("/fxml/userScreen.fxml", sessionPane, userController, null, null);
    }

    public void handleSubEvent() throws IOException {
        genericButton("/fxml/sessionScreen.fxml", sessionPane, userController, null, null);
    }

    public void handleSubmit() throws IOException {
        genericButton("/fxml/submitScreen.fxml", sessionPane, userController, null, null);
    }

    public void handleEvent() throws IOException {
        genericButton("/fxml/mainScreen.fxml", sessionPane, userController, null, null);
    }

    public void logout() throws IOException {
        genericButton("/fxml/loginScreen.fxml", sessionPane, userController, null, null);
    }
}
