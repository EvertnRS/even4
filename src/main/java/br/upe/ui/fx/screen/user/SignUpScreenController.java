package br.upe.ui.fx.screen.user;

import br.upe.controller.UserController;
import br.upe.ui.fx.fxutils.PlaceholderUtils;
import br.upe.ui.fx.mediator.user.AccessMediator;
import br.upe.ui.fx.screen.BaseController;
import br.upe.ui.fx.screen.FxController;
import br.upe.facade.Facade;
import br.upe.facade.FacadeInterface;
import br.upe.utils.CustomRuntimeException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.IOException;

public class SignUpScreenController extends BaseController implements FxController {
    private AccessMediator accessMediator;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField emailTextField;

    @FXML
    private TextField cpfTextField;

    @FXML
    private PasswordField passTextField;

    @FXML
    private TextField showPassText;

    @FXML
    private ToggleButton toggleShowPassword;

    @FXML
    private AnchorPane registerAnchorPane;

    @FXML
    private Text cpfPlaceholder;

    @FXML
    private Text emailPlaceholder;

    @FXML
    private Text passPlaceholder;

    @FXML
    private Text namePlaceholder;

    @FXML
    private Label errorLabel;

    @FXML
    private ImageView imageView1;

    public void setNameTextField(TextField nameTextField) {
        this.nameTextField = nameTextField;
    }

    public TextField getEmailTextField() {
        return emailTextField;
    }

    public Label getErrorLabel() {
        return errorLabel;
    }

    public void setEmailTextField(TextField emailTextField) {
        this.emailTextField = emailTextField;
    }

    public TextField getCpfTextField() {
        return cpfTextField;
    }

    public void setCpfTextField(TextField cpfTextField) {
        this.cpfTextField = cpfTextField;
    }

    @FXML
    public void initialize() {
        this.accessMediator = new AccessMediator(this, null, registerAnchorPane, errorLabel, null);
        accessMediator.registerComponents();

        setupPlaceholders();

        accessMediator.setComponents(nameTextField, cpfTextField, emailTextField, passTextField, showPassText, toggleShowPassword, imageView1);

        Platform.runLater(() -> registerAnchorPane.requestFocus());

        toggleShowPassword.setOnAction(event -> {
            try {
                handleSignupTogglePassword();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void setupPlaceholders() {
        PlaceholderUtils.setupPlaceholder(emailTextField, emailPlaceholder);
        PlaceholderUtils.setupPlaceholder(cpfTextField, cpfPlaceholder);
        PlaceholderUtils.setupPlaceholder(passTextField, passPlaceholder);
        PlaceholderUtils.setupPlaceholder(nameTextField, namePlaceholder);
    }

    public void handleRegister() throws IOException {
        String email = emailTextField.getText().trim();
        String cpf = cpfTextField.getText().trim();
        String name = nameTextField.getText().trim();
        String password = passTextField.getText().trim();
        UserController userController = new UserController();
        FacadeInterface facade = new Facade(userController);

        loadScreen("Carregando", () -> {
            try {
                facade.createUser(name, cpf, email, password);
            } catch (IOException e) {
                throw new CustomRuntimeException("Algo deu errado", e);
            }
            Platform.runLater(() -> {
                try {
                    accessMediator.notify("returnToLogin");
                } catch (IOException e) {
                    throw new CustomRuntimeException("Algo deu errado", e);
                }
            });
        }, registerAnchorPane);
    }

    public void handleSignupTogglePassword() throws IOException {
        accessMediator.notify("togglePassword");
    }

    @Override
    public void setFacade(FacadeInterface facade) {
        // Método não implementado
    }

    @Override
    public TextField getNameTextField() {
        return null;
    }

    @Override
    public TextField getLocationTextField() {
        return null;
    }

    @Override
    public TextField getDescriptionTextField() {
        return null;
    }

    @Override
    public DatePicker getDatePicker() {
        return null;
    }

}
