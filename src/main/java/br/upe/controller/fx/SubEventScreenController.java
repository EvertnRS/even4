package br.upe.controller.fx;

import br.upe.controller.UserController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class SubEventScreenController extends BaseController implements FxController{
    UserController userController;

    @FXML
    private Label userEmail;
    @FXML
    private AnchorPane subEventPane;

    public void setUserController(UserController userController) {
        this.userController = userController;
        initial();
    }

    private void initial() {
        userEmail.setText(userController.getData("email"));
    }

    public void handleUser() throws IOException {
        genericButton("/fxml/userScreen.fxml", subEventPane, userController);
    }

    public void handleEvent() throws IOException {
        genericButton("/fxml/mainScreen.fxml", subEventPane, userController);
    }

    public void handleSubmit() throws IOException {
        genericButton("/fxml/submitScreen.fxml", subEventPane, userController);
    }

    public void handleSession() throws IOException {
        genericButton("/fxml/sessionScreen.fxml", subEventPane, userController);
    }

    public void logout() throws IOException {
        genericButton("/fxml/loginScreen.fxml", subEventPane, userController);
    }
}
