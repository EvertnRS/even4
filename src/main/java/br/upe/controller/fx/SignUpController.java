package br.upe.controller.fx;

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

public class SignUpController {

    @FXML
    private ImageView imageView;

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
        Image logo = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/Even4.png")));
        imageView.setImage(logo);

        loginAnchorPane.sceneProperty().addListener((observableScene, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.ENTER) {
                        handleRegister();
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

    public void handleRegister() {
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

    public void returnToLogin(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/loginScreen.fxml"));
            AnchorPane loginScreen = loader.load();

            Scene mainScene = new Scene(loginScreen);
            Stage stage = (Stage) loginAnchorPane.getScene().getWindow();

            stage.setScene(mainScene);
            stage.setTitle("Even4");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
