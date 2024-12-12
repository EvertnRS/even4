package br.upe.controller.fx.mediator;

import br.upe.controller.fx.LoginScreenController;
import br.upe.controller.fx.SignUpScreenController;
import br.upe.facade.FacadeInterface;
import br.upe.persistence.repository.Persistence;
import br.upe.persistence.repository.UserRepository;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

import static br.upe.ui.Validation.isValidCPF;
import static br.upe.ui.Validation.isValidEmail;

public class AccessMediator extends Mediator {
    private final SignUpScreenController userSignUpController;
    private final LoginScreenController userLoginController;

    private TextField nameTextField;
    private TextField cpfTextField;
    private TextField emailTextField;
    private TextField passTextField;

    public AccessMediator(SignUpScreenController userSignUpController, FacadeInterface facade, AnchorPane screenPane, Label errorUpdtLabel, LoginScreenController userLoginController) {
        super(facade, screenPane, errorUpdtLabel, userSignUpController);
        this.userSignUpController = userSignUpController;
        this.userLoginController = userLoginController;
    }

    public void setComponents(TextField nameTextField, TextField cpfTextField, TextField emailTextField, TextField passTextField) {
        this.nameTextField = nameTextField;
        this.cpfTextField = cpfTextField;
        this.emailTextField = emailTextField;
        this.passTextField = passTextField;

        if (userSignUpController != null) {
            setupRegisterListeners();
        } else if (userLoginController != null) {
            setupLoginListeners();
        }
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
        if (userSignUpController == null && userLoginController == null) {
            return null;
        }

        switch (event) {
            case "handleRegister":
                handleRegisterEvent();
                break;
            case "handleLogin":
                handleLoginEvent();
                break;
            case "handleAccessButton":
                assert userLoginController != null;
                userLoginController.genericButton("/fxml/attendeeScreen.fxml", screenPane, facade, null);
                break;
            case "returnToLogin":
                assert userSignUpController != null;
                userSignUpController.genericButton("/fxml/loginScreen.fxml", screenPane, null, null);
                break;
            case "handleSignUp":
                assert userLoginController != null;
                userLoginController.genericButton("/fxml/signUpScreen.fxml", screenPane, null, null);
                break;
            default:
                throw new IllegalArgumentException("Invalid event: " + event);
        }

        return null;
    }

    private void handleRegisterEvent() {
        assert userSignUpController != null;
        Persistence userRepository = UserRepository.getInstance();
        String signUpEmail = userSignUpController.getEmailTextField().getText();
        String cpf = userSignUpController.getCpfTextField().getText();
        Label signUpErrorLabel = userSignUpController.getErrorLabel();

        if (((UserRepository) userRepository).userExists(signUpEmail, Long.valueOf(cpf))) {
            signUpErrorLabel.setText("Usuário com este email ou CPF já existe.");
            signUpErrorLabel.setAlignment(Pos.CENTER);
            return;

        }
        userSignUpController.loadScreen("Carregando", () -> {
            if (isValidEmail(signUpEmail) && isValidCPF(cpf)) {
                Platform.runLater(() -> {
                    try {
                        userSignUpController.handleRegister();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } else {
                Platform.runLater(() -> {
                    signUpErrorLabel.setText("Cadastro falhou! Insira informações válidas.");
                    signUpErrorLabel.setAlignment(Pos.CENTER);
                });
            }
        }, screenPane);
    }

    private void handleLoginEvent() {
        assert userLoginController != null;
        String pass = userLoginController.getPassTextField().getText();
        String loginInEmail = userLoginController.getEmailTextField().getText();
        Label logInErrorLabel = userLoginController.getErrorLabel();

        userLoginController.loadScreen("Carregando", () -> {
            if (facade.loginValidate(loginInEmail, pass)) {
                Platform.runLater(() -> {
                    userLoginController.handleLogin();
                });
            } else {
                Platform.runLater(() -> {
                    logInErrorLabel.setText("Login falhou! Verifique suas credenciais.");
                    logInErrorLabel.setAlignment(Pos.CENTER);
                });
            }
        }, screenPane);
    }

    private void setupLoginListeners() {
        screenPane.sceneProperty().addListener((observableScene, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.ENTER) {
                        try {
                            notify("handleLogin");
                        } catch (IOException e) {
                            throw new IllegalArgumentException(e);
                        }
                    }
                });
            }
        });

        emailTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DOWN || event.getCode() == KeyCode.TAB) {
                passTextField.requestFocus();
                event.consume();
            }
            if (event.getCode() == KeyCode.UP || (event.getCode() == KeyCode.TAB && event.isShiftDown())) {
                event.consume();
            }
        });

        passTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.UP || (event.getCode() == KeyCode.TAB && event.isShiftDown())) {
                emailTextField.requestFocus();
                event.consume();
            }
            if (event.getCode() == KeyCode.DOWN || event.getCode() == KeyCode.TAB) {
                event.consume();
            }
        });
    }

    private void setupRegisterListeners() {
        screenPane.sceneProperty().addListener((observableScene, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.ENTER) {
                        try {
                            notify("handleRegister");
                        } catch (IOException e) {
                            throw new IllegalArgumentException(e);
                        }
                    }
                });
            }
        });

        nameTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DOWN || event.getCode() == KeyCode.TAB) {
                cpfTextField.requestFocus();
                event.consume();
            }
            if (event.getCode() == KeyCode.UP || (event.getCode() == KeyCode.TAB && event.isShiftDown())) {
                passTextField.requestFocus();
                event.consume();
            }
        });

        cpfTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DOWN || event.getCode() == KeyCode.TAB) {
                emailTextField.requestFocus();
                event.consume();
            }
            if (event.getCode() == KeyCode.UP || (event.getCode() == KeyCode.TAB && event.isShiftDown())) {
                nameTextField.requestFocus();
                event.consume();
            }
        });

        emailTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DOWN || event.getCode() == KeyCode.TAB) {
                passTextField.requestFocus();
                event.consume();
            }
            if (event.getCode() == KeyCode.UP || (event.getCode() == KeyCode.TAB && event.isShiftDown())) {
                cpfTextField.requestFocus();
                event.consume();
            }
        });

        passTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DOWN || event.getCode() == KeyCode.TAB) {
                nameTextField.requestFocus();
                event.consume();
            }
            if (event.getCode() == KeyCode.UP || (event.getCode() == KeyCode.TAB && event.isShiftDown())) {
                emailTextField.requestFocus();
                event.consume();
            }
        });

    }

}
