package br.upe.controller.fx;
import br.upe.controller.UserController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.IOException;

public class LoginScreenController extends BaseController implements FxController {

    @FXML
    private TextField emailTextField;

    @FXML
    private TextField cpfTextField;

    @FXML
    private AnchorPane loginAnchorPane;

    @FXML
    private Text cpfPlaceholder;

    @FXML
    private Text emailPlaceholder;

    @FXML
    private Label errorLabel;

    @FXML
    public void initialize() {
        loginAnchorPane.sceneProperty().addListener((observableScene, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.ENTER) {
                        try {
                            handleLogin();
                        } catch (IOException e) {
                            throw new IllegalArgumentException(e);
                        }
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

        setupPlaceholders();
    }

    private void setupPlaceholders() {
        PlaceholderUtils.setupPlaceholder(emailTextField, emailPlaceholder);
        PlaceholderUtils.setupPlaceholder(cpfTextField, cpfPlaceholder);
    }

    public void handleLogin() throws IOException {
        String email = emailTextField.getText();
        String cpf = cpfTextField.getText();

        UserController userController = new UserController();
        if (userController.loginValidate(email, cpf)) {
            genericButton("/fxml/mainScreen.fxml", loginAnchorPane, userController, null);
        } else {
            errorLabel.setText("Login falhou! Verifique suas credenciais.");
        }
    }

    public void moveToSignUp() throws IOException {
        genericButton("/fxml/signUpScreen.fxml", loginAnchorPane, null, null);
    }


    @Override
    public void setUserController(UserController userController) throws IOException {
        //Método não implementado
    }
}
