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

public class LoginScreenController extends BaseController implements FxController {

    @FXML
    private TextField emailTextField;

    @FXML
    private TextField textField;

    @FXML
    private PasswordField passTextField;

    @FXML
    private ToggleButton toggleShowPassword;

    @FXML
    private AnchorPane loginAnchorPane;

    @FXML
    private Text passPlaceholder;

    @FXML
    private Text emailPlaceholder;

    @FXML
    private Label errorLabel;

    @FXML
    private ImageView imageView1;

    private AccessMediator accessMediator;

    @FXML
    public void initialize() throws IOException {
        UserController userController = new UserController();
        FacadeInterface facade = new Facade(userController);

        this.accessMediator = new AccessMediator(null, facade, loginAnchorPane, errorLabel, this);
        accessMediator.registerComponents();

        setupPlaceholders();

        accessMediator.setComponents(null, null, emailTextField, passTextField, textField, toggleShowPassword, imageView1);

        Platform.runLater(() -> loginAnchorPane.requestFocus());

        toggleShowPassword.setOnAction(event -> {
            try {
                handleLoginTogglePassword();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void setupPlaceholders() {
        PlaceholderUtils.setupPlaceholder(emailTextField, emailPlaceholder);
        PlaceholderUtils.setupPlaceholder(passTextField, passPlaceholder);
    }

    public void handleLogin() {
        try {
            accessMediator.notify("handleAccessButton");
        } catch (IOException e) {
            throw new CustomRuntimeException("Algo deu errado", e);
        }
    }

    public void handleLoginTogglePassword() throws IOException {
        accessMediator.notify("togglePassword");
    }

    public TextField getPassTextField() {
        return passTextField;
    }

    public TextField getEmailTextField() {
        return emailTextField;
    }

    public Label getErrorLabel() {
        return errorLabel;
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
