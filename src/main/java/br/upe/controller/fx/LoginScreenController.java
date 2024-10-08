package br.upe.controller.fx;

import br.upe.controller.UserController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class LoginScreenController extends BaseController implements FxController{

    @FXML
    private TextField emailTextField;

    @FXML
    private TextField cpfTextField;

    @FXML
    private AnchorPane loginAnchorPane;

    @FXML
    private Label errorLabel;

    @FXML
    public void initialize() {

        loginAnchorPane.sceneProperty().addListener((observableScene, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.ENTER) {
                        handleLogin();
                    }
                });
            }
        });

        emailTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DOWN) {
                cpfTextField.requestFocus();
            }
        });

        cpfTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.UP) {
                emailTextField.requestFocus();
            }
        });
    }

    public void handleLogin() {
        String email = emailTextField.getText();
        String cpf = cpfTextField.getText();

        UserController userController = new UserController();
        if (userController.loginValidate(email, cpf)) {
            try {
                genericButton("/fxml/mainScreen.fxml", loginAnchorPane, userController);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            errorLabel.setText("Login falhou! Verifique suas credenciais.");
        }
    }

    public void moveToSignUp() throws IOException {
        genericButton("/fxml/signUpScreen.fxml", loginAnchorPane, null);
    }

    @Override
    public void setUserController(UserController userController) {
        // Método não implementado
    }
}
