package br.upe.controller.fx;

import br.upe.controller.EventController;
import br.upe.controller.UserController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

import static br.upe.ui.Validation.isValidCPF;
import static br.upe.ui.Validation.isValidEmail;

public class SignUpController extends BaseController implements FxController {

    @FXML
    private TextField emailTextField;

    @FXML
    private TextField cpfTextField;

    @FXML
    private AnchorPane registerAnchorPane;

    @FXML
    private Label errorLabel;

    @FXML
    public void initialize() {
        registerAnchorPane.sceneProperty().addListener((observableScene, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.ENTER) {
                        try {
                            handleRegister();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
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
    }

    public void handleRegister() throws IOException {
        String email = emailTextField.getText().trim();
        String cpf = cpfTextField.getText().trim();

        UserController userController = new UserController();
        if (isValidEmail(email) && isValidCPF(cpf)) {
            userController.create(email.trim(), cpf.trim());
            returnToLogin();
        } else {
            errorLabel.setText("Cadastro falhou! Insira informações válidas.");
        }
    }

    public void returnToLogin() throws IOException {
        genericButton("/fxml/loginScreen.fxml", registerAnchorPane, null, null, null);
    }

    @Override
    public void setUserController(UserController userController) {
        // Método não implementado
    }

}
