package br.upe.controller.fx;

import br.upe.controller.UserController;
import br.upe.controller.fx.fxutils.PlaceholderUtils;
import br.upe.controller.fx.mediator.AccessMediator;
import br.upe.facade.Facade;
import br.upe.facade.FacadeInterface;
import br.upe.utils.CustomRuntimeException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
    private TextField passTextField;

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

        accessMediator.setComponents(nameTextField, cpfTextField, emailTextField, passTextField);

        Platform.runLater(() -> registerAnchorPane.requestFocus());
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
        UserController userController = UserController.getInstance();
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
