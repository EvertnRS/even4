package br.upe.controller.fx;

import br.upe.controller.EventController;
import br.upe.controller.UserController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

import static br.upe.ui.Validation.isValidEmail;

public class UserScreenController extends BaseController implements FxController {
    UserController userController;

    @FXML
    private AnchorPane userPane;
    @FXML
    private Label userEmail;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField cpfTextField;
    @FXML
    private Label errorUpdtLabel;
    @FXML
    private Label errorDelLabel;

    public void setUserController(UserController userController) {
        this.userController = userController;
        initial();
    }

    private void initial() {
        userEmail.setText(userController.getData("email"));
    }

    public void handleEvent() throws IOException {
        genericButton("/fxml/mainScreen.fxml", userPane, userController, null, null);
    }

    public void handleSubEvent() throws IOException {
        genericButton("/fxml/subEventScreen.fxml", userPane, userController, null, null);
    }

    public void handleSubmitEvent() throws IOException {
        genericButton("/fxml/submitScreen.fxml", userPane, userController, null, null);
    }

    public void handleSession() throws IOException {
        genericButton("/fxml/sessionScreen.fxml", userPane, userController, null, null);
    }

    public void logout() throws IOException {
        genericButton("/fxml/loginScreen.fxml", userPane, userController, null, null);
    }

    public void updateUser(){
        String email = emailTextField.getText();

        if (isValidEmail(email)) {
            userController.update(email, userController.getData("cpf"));
            initial();
        }
        else{
            errorUpdtLabel.setText("Erro ao ler email! Verifique suas credenciais.");
        }
    }

    public void deleteUser() throws IOException {
        String cpf = cpfTextField.getText();

        if (cpf.equals(userController.getData("cpf"))) {
            userController.delete(userController.getData("id"), "id");
            logout();
        }
        else{
            errorDelLabel.setText("Erro ao ler cpf! Verifique suas credenciais.");
        }
    }
}
