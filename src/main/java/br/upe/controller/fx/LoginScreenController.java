package br.upe.controller.fx;
import br.upe.controller.UserController;
import br.upe.controller.fx.fxutils.PlaceholderUtils;
import br.upe.controller.fx.mediator.AccessMediator;
import br.upe.facade.Facade;
import br.upe.facade.FacadeInterface;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
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

    private AccessMediator accessMediator;

    public TextField getPassTextField() {
        return passTextField;
    }

    public TextField getEmailTextField() {
        return emailTextField;
    }

    public Label getErrorLabel() {
        return errorLabel;
    }

    @FXML
    public void initialize() throws IOException {
        UserController userController = UserController.getInstance();
        FacadeInterface facade = new Facade(userController);

        this.accessMediator = new AccessMediator(null, facade, loginAnchorPane, errorLabel, this);
        accessMediator.registerComponents();

        setupPlaceholders();

        accessMediator.setComponents(null, null, emailTextField, passTextField);

        Platform.runLater(() -> loginAnchorPane.requestFocus());
    }

    private void setupPlaceholders() {
        PlaceholderUtils.setupPlaceholder(emailTextField, emailPlaceholder);
        PlaceholderUtils.setupPlaceholder(passTextField, passPlaceholder);
    }

    public void handleLogin() {
        try {
            accessMediator.notify("handleAccessButton");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
