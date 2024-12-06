package br.upe.controller.fx.mediator;

import br.upe.controller.fx.LoginScreenController;
import br.upe.controller.fx.SignUpScreenController;
import br.upe.facade.FacadeInterface;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

import static br.upe.ui.Validation.isValidCPF;
import static br.upe.ui.Validation.isValidEmail;

public class AccessMediator extends Mediator {
    private final SignUpScreenController userSignUpController;
    private final LoginScreenController userLoginController;

    public AccessMediator(SignUpScreenController userSignUpController, FacadeInterface facade, AnchorPane screenPane, Label errorUpdtLabel, LoginScreenController userLoginController) {
        super(facade, screenPane, errorUpdtLabel, userSignUpController);
        this.userSignUpController = userSignUpController;
        this.userLoginController = userLoginController;
    }

    @Override
    public void registerComponents() {
        if (screenPane != null) {
            setupButtonAction("#handleRegisterButton", "handleRegister");
            setupButtonAction("#handleLoginButton", "handleLogin");
            setupButtonAction("#handleMoveToSignUp", "handleSignUp");
            setupButtonAction("#handleReturnButton", "returnToLogin");
        }
    }

    @Override
    public Object notify(String event) throws IOException {
        if (userSignUpController != null || userLoginController != null) {
            switch (event) {
                case "handleRegister":
                    String signUpEmail = userSignUpController.getEmailTextField().getText();
                    String cpf = userSignUpController.getCpfTextField().getText();
                    Label signUpErrorLabel = userSignUpController.getErrorLabel();
                    if (isValidEmail(signUpEmail) && isValidCPF(cpf)) {
                        userSignUpController.handleRegister();
                    } else {
                        Platform.runLater(() -> signUpErrorLabel.setText("Cadastro falhou! Insira informações válidas."));
                        signUpErrorLabel.setAlignment(Pos.CENTER);
                    }
                    break;
                case "handleLogin":
                    String pass = userLoginController.getPassTextField().getText();
                    String loginInEmail = userLoginController.getEmailTextField().getText();
                    Label logInErrorLabel = userLoginController.getErrorLabel();
                    if (facade.loginValidate(loginInEmail, pass)) {
                        try {
                            userLoginController.handleLogin();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        Platform.runLater(() -> logInErrorLabel.setText("Login falhou! Verifique suas credenciais."));
                        logInErrorLabel.setAlignment(Pos.CENTER);
                    }
                    break;
                case "handleAccessButton":
                    userLoginController.genericButton("/fxml/mainScreen.fxml", screenPane, facade, null);
                    break;
                case "returnToLogin":
                    userSignUpController.genericButton("/fxml/loginScreen.fxml", screenPane, null, null);
                    break;
                case "handleSignUp":
                    userLoginController.genericButton("/fxml/signUpScreen.fxml", screenPane, null, null);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid event: " + event);
            }
        }
        return null;
    }
}
