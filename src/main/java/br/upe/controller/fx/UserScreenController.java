package br.upe.controller.fx;

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

public class UserScreenController {
    UserController userController;

    @FXML
    private AnchorPane UserPane;
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

    public void logout(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/loginScreen.fxml"));
            AnchorPane loginScreen = loader.load();

            Scene loginScene = new Scene(loginScreen);
            Stage stage = (Stage) UserPane.getScene().getWindow();

            stage.setScene(loginScene);
            stage.setTitle("Even4");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleEvent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mainScreen.fxml"));
            AnchorPane mainScreen = loader.load();

            MainScreenController mainScreenController = loader.getController();
            mainScreenController.setUserController(userController);

            Scene mainScene = new Scene(mainScreen);
            Stage stage = (Stage) UserPane.getScene().getWindow();

            stage.setScene(mainScene);
            stage.setTitle("Even4");

        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void handleSubmitEvent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/submitScreen.fxml"));
            AnchorPane submitScreen = loader.load();

            SubmitEventScreenController submitEventScreenController = loader.getController();
            submitEventScreenController.setUserController(userController);

            Scene submitScene = new Scene(submitScreen);
            Stage stage = (Stage) UserPane.getScene().getWindow();

            stage.setScene(submitScene);
            stage.setTitle("Even4");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleSession() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sessionScreen.fxml"));
            AnchorPane sessionScreen = loader.load();

            SessionScreenController sessionScreenController = loader.getController();
            sessionScreenController.setUserController(userController);

            Scene sessionScene = new Scene(sessionScreen);
            Stage stage = (Stage) UserPane.getScene().getWindow();

            stage.setScene(sessionScene);
            stage.setTitle("Even4");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteUser(){
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
