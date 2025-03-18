package br.upe.ui.fx.mediator.user;

import br.upe.ui.fx.mediator.Mediator;
import br.upe.ui.fx.screen.user.LoginScreenController;
import br.upe.ui.fx.screen.user.SignUpScreenController;
import br.upe.facade.FacadeInterface;
import br.upe.persistence.repository.UserRepository;
import br.upe.utils.CustomRuntimeException;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.io.IOException;

import static br.upe.utils.Validation.isValidCPF;
import static br.upe.utils.Validation.isValidEmail;

public class AccessMediator extends Mediator {
    private final SignUpScreenController userSignUpController;
    private final LoginScreenController userLoginController;
    private static final  String HANDLE_LOGIN = "handleLogin";
    private static final  String HANDLE_REGISTER = "handleRegister";

    private TextField nameTextField;
    private TextField cpfTextField;
    private TextField emailTextField;
    private TextField passTextField;
    private TextField showPassText;
    private ToggleButton toggleShowPassword;
    private ImageView passView;

    public AccessMediator(SignUpScreenController userSignUpController, FacadeInterface facade, AnchorPane screenPane, Label errorUpdtLabel, LoginScreenController userLoginController) {
        super(facade, screenPane, errorUpdtLabel, userSignUpController);
        this.userSignUpController = userSignUpController;
        this.userLoginController = userLoginController;
    }

    public void setComponents(TextField nameTextField, TextField cpfTextField, TextField emailTextField, PasswordField passTextField, TextField showPassText, ToggleButton toggleShowPassword, ImageView passView) {
        this.nameTextField = nameTextField;
        this.cpfTextField = cpfTextField;
        this.emailTextField = emailTextField;
        this.passTextField = passTextField;
        this.showPassText = showPassText;
        this.toggleShowPassword = toggleShowPassword;
        this.passView = passView;

        if (userSignUpController != null) {
            setupRegisterListeners();
        } else if (userLoginController != null) {
            setupLoginListeners();
        }
    }

    @Override
    public void registerComponents() {
        if (screenPane != null) {
            setupButtonAction("#handleRegisterButton", HANDLE_REGISTER);
            setupButtonAction("#handleLoginButton", HANDLE_LOGIN);
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
            case HANDLE_REGISTER:
                handleRegisterEvent();
                break;
            case HANDLE_LOGIN:
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
            case "togglePassword":
                togglePassword();
                break;
            default:
                throw new IllegalArgumentException("Invalid event: " + event);
        }
        return null;
    }

    private void togglePassword() {
        showPassText.textProperty().bindBidirectional(passTextField.textProperty());

        if (toggleShowPassword.isSelected()) {
            showPassText.setVisible(true);
            passTextField.setVisible(false);
            Image newImage = new Image("/images/icons/buttons/hideIcon.png");
            passView.setImage(newImage);
        } else {
            showPassText.setVisible(false);
            passTextField.setVisible(true);
            Image newImage = new Image("/images/icons/buttons/showIcon.png");
            passView.setImage(newImage);
        }

        toggleShowPassword.setDisable(true);

        PauseTransition pause = new PauseTransition(Duration.millis(500));
        pause.setOnFinished(event -> toggleShowPassword.setDisable(false));
        pause.play();
    }

    private void handleRegisterEvent() {
        assert userSignUpController != null;
        UserRepository userRepository = UserRepository.getInstance();
        String signUpEmail = userSignUpController.getEmailTextField().getText();
        String cpf = userSignUpController.getCpfTextField().getText();
        Label signUpErrorLabel = userSignUpController.getErrorLabel();

        if (userRepository.userExists(signUpEmail, Long.valueOf(cpf))) {
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
                        throw new CustomRuntimeException("Algo deu errado", e);
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
            try {
                if (facade.loginValidate(loginInEmail, pass)) {
                    Platform.runLater(userLoginController::handleLogin);
                } else {
                    Platform.runLater(() -> {
                        logInErrorLabel.setText("Login falhou! Verifique suas credenciais.");
                        logInErrorLabel.setAlignment(Pos.CENTER);
                    });
                }
            } catch (IOException e) {
                throw new CustomRuntimeException("Ocorreu um erro no acesso.", e);
            }
        }, screenPane);
    }

    private void setupLoginListeners() {
        screenPane.sceneProperty().addListener((observableScene, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.ENTER) {
                        try {
                            notify(HANDLE_LOGIN);
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
                            notify(HANDLE_REGISTER);
                        } catch (IOException e) {
                            throw new IllegalArgumentException(e);
                        }
                    }
                });
            }
        });

        cpfTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                cpfTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (cpfTextField.getText().length() > 11) {
                cpfTextField.setText(cpfTextField.getText().substring(0, 11));
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
