package br.upe.fx;

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

public class LoginScreenController {

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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mainScreen.fxml"));
                AnchorPane mainScreen = loader.load();

                MainScreenController mainScreenController = loader.getController();
                mainScreenController.setUserController(userController);

                Scene mainScene = new Scene(mainScreen);
                Stage stage = (Stage) loginAnchorPane.getScene().getWindow();

                stage.setScene(mainScene);
                stage.setTitle("Even4");

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            errorLabel.setText("Login falhou! Verifique suas credenciais.");
        }
    }

    public void moveToSignUp(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SignUpScreen.fxml"));
            AnchorPane signUpScreen = loader.load();

            Scene mainScene = new Scene(signUpScreen);
            Stage stage = (Stage) loginAnchorPane.getScene().getWindow();

            stage.setScene(mainScene);
            stage.setTitle("Even4");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
