package br.upe.controller.fx.screen.user;


import br.upe.controller.fx.fxutils.PlaceholderUtils;
import br.upe.controller.fx.mediator.user.UserMediator;
import br.upe.controller.fx.screen.BaseController;
import br.upe.controller.fx.screen.FxController;
import br.upe.facade.FacadeInterface;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.Optional;

public class UserScreenController extends BaseController implements FxController {
    private FacadeInterface facade;
    private UserMediator userMediator;

    @FXML
    private AnchorPane userPane;
    @FXML
    private Label userName;
    @FXML
    private TextField emailTextField;
    @FXML
    private Text emailPlaceholder;
    @FXML
    private TextField cpfTextField;
    @FXML
    private Text cpfPlaceholder;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField passTextField;
    @FXML
    private Text namePlaceholder;
    @FXML
    private Text passPlaceholder;
    @FXML
    private Label errorUpdtLabel;
    @FXML
    private Label errorDelLabel;

    public TextField getEmailTextField() {
        return emailTextField;
    }

    public TextField getCpfTextField() {
        return cpfTextField;
    }

    public TextField getPassTextField() {
        return passTextField;
    }

    public Label getErrorUpdtLabel() {
        return errorUpdtLabel;
    }

    public Label getErrorDelLabel() {
        return errorDelLabel;
    }

    public void setFacade(FacadeInterface facade) {
        this.facade = facade;
        initial();
    }

    private void initial() {
        userName.setText(facade.getUserData("name"));
        loadUserDetails();


        this.userMediator = new UserMediator(this, facade, userPane, errorUpdtLabel);
        userMediator.registerComponents();

        userMediator.setComponents(nameTextField, cpfTextField, emailTextField, passTextField);

    }

    private void setupPlaceholders() {
        PlaceholderUtils.setupPlaceholder(emailTextField, emailPlaceholder);
        PlaceholderUtils.setupPlaceholder(cpfTextField, cpfPlaceholder);
        PlaceholderUtils.setupPlaceholder(nameTextField, namePlaceholder);
        PlaceholderUtils.setupPlaceholder(passTextField, passPlaceholder);
    }

    public void updateUser() throws IOException {

        String name = nameTextField.getText().trim();
        String cpf = cpfTextField.getText().trim();
        String email = emailTextField.getText().trim();
        String newPassword = passTextField.getText().trim();
        String password = showPasswordPrompt().trim();


        facade.updateUser(name, cpf, email, newPassword, password);
        userMediator.notify("handleUser");
    }

    public void deleteUser() {
        String password = showPasswordPrompt().trim();
        try {
            facade.deleteUser(password);
            userMediator.notify("logout");
        } catch (Exception e) {
            errorDelLabel.setText("Senha incorreta. Tente novamente.");
        }
    }

    private String showPasswordPrompt() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Validar Senha");
        alert.setHeaderText("Digite sua senha para continuar:");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Senha");

        GridPane grid = new GridPane();
        grid.add(new Label("Senha:"), 0, 0);
        grid.add(passwordField, 1, 0);
        GridPane.setHgrow(passwordField, Priority.ALWAYS);
        alert.getDialogPane().setContent(grid);

        alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            return passwordField.getText();
        }
        return "";
    }

    private void loadUserDetails() {

        String email = facade.getUserData("email");
        String cpf = facade.getUserData("cpf");
        nameTextField.setText(userName.getText());
        emailTextField.setText(email);
        cpfTextField.setText(cpf);

        setupPlaceholders();
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
