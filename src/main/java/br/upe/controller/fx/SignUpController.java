package br.upe.controller.fx;

import br.upe.controller.UserController;
import br.upe.facade.FacadeInterface;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.IOException;
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
    private Text cpfPlaceholder;

    @FXML
    private Text emailPlaceholder;

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

    public void handleRegister() throws IOException {
        String email = emailTextField.getText().trim();
        String cpf = cpfTextField.getText().trim();

        //trocar para facade
        UserController userController = new UserController();
        if (isValidEmail(email) && isValidCPF(cpf)) {
            userController.create(email.trim(), cpf.trim());
            returnToLogin();
        } else {
            errorLabel.setText("Cadastro falhou! Insira informações válidas.");
        }
    }

    public void returnToLogin() throws IOException {
        genericButton("/fxml/loginScreen.fxml", registerAnchorPane, null, null);
    }

    @Override
    public void setFacade(FacadeInterface facade) {
        // Método não implementado
    }

}
