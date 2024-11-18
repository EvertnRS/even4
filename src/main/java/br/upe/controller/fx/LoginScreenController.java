package br.upe.controller.fx;
import br.upe.controller.UserController;
import br.upe.facade.Facade;
import br.upe.facade.FacadeInterface;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.IOException;

public class LoginScreenController extends BaseController implements FxController {

    @FXML
    private TextField emailTextField;

    @FXML
    private TextField passTextField;

    @FXML
    private AnchorPane loginAnchorPane;

    @FXML
    private Text passPlaceholder;

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
                passTextField.requestFocus();
            }
        });

        passTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.UP) {
                emailTextField.requestFocus();
            }
        });

        setupPlaceholders();
    }

    private void setupPlaceholders() {
        PlaceholderUtils.setupPlaceholder(emailTextField, emailPlaceholder);
        PlaceholderUtils.setupPlaceholder(passTextField, passPlaceholder);
    }

    public void handleLogin() throws IOException {
        String email = emailTextField.getText();
        String pass = passTextField.getText();

        UserController userController = UserController.getInstance();
        FacadeInterface facade = new Facade(userController);

        loadScreen("Carregando", () -> {
            if (userController.loginValidate(email, pass)) {
                Platform.runLater(() -> {
                    try {
                        genericButton("/fxml/mainScreen.fxml", loginAnchorPane, facade, null);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } else {
                Platform.runLater(() -> errorLabel.setText("Login falhou! Verifique suas credenciais."));
                errorLabel.setAlignment(Pos.CENTER);
            }
        }, loginAnchorPane);
    }

    public void moveToSignUp() throws IOException {
        genericButton("/fxml/signUpScreen.fxml", loginAnchorPane, null, null);
    }


    @Override
    public void setFacade(FacadeInterface facade) {
        // Método não implementado
    }


}
