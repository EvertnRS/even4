package br.upe.controller.fx;

import br.upe.controller.UserController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.IOException;
import static br.upe.ui.Validation.isValidEmail;

public class UserScreenController extends BaseController implements FxController {
    private UserController userController;

    @FXML
    private AnchorPane userPane;
    @FXML
    private Label userEmail;
    @FXML
    private TextField emailTextField;
    @FXML
    private Text emailPlaceholder;
    @FXML
    private TextField cpfTextField;
    @FXML
    private Text cpfPlaceholder;
    @FXML
    private Label errorUpdtLabel;
    @FXML
    private Label errorUpdateLabel;
    @FXML
    private Label errorDelLabel;

    public void setUserController(UserController userController) {
        this.userController = userController;
        initial();
    }

    private void initial() {
        userEmail.setText(userController.getData("email"));
        setupPlaceholders();
    }

    private void setupPlaceholders() {
        PlaceholderUtils.setupPlaceholder(emailTextField, emailPlaceholder);
        PlaceholderUtils.setupPlaceholder(cpfTextField, cpfPlaceholder);
    }

    public void handleEvent() throws IOException {
        genericButton("/fxml/mainScreen.fxml", userPane, userController, null);
    }

    public void handleSubEvent() throws IOException {
        genericButton("/fxml/subEventScreen.fxml", userPane, userController, null);
    }

    public void handleSubmitEvent() throws IOException {
        genericButton("/fxml/submitScreen.fxml", userPane, userController, null);
    }

    public void handleSession() throws IOException {
        genericButton("/fxml/sessionScreen.fxml", userPane, userController, null);
    }

    public void logout() throws IOException {
        genericButton("/fxml/loginScreen.fxml", userPane, userController, null);
    }

    public void updateUser() throws IOException {

        String email = emailTextField.getText();

        if (isValidEmail(email)) {
            userController.update(email, userController.getData("cpf"));
            logout();
        } else {
            errorUpdateLabel.setText("E-mail invalido!");
        }
    }

    public void deleteUser() throws IOException {
        String cpf = cpfTextField != null ? cpfTextField.getText() : "";

        if (cpf == null || cpf.isEmpty()) {
            errorDelLabel.setText("CPF não informado.");
            return;
        }

        if (userController == null) {
            System.out.println("userController está nulo");
            return;
        }

        String cpfData = userController.getData("cpf");
        String idData = userController.getData("id");

        if (cpfData != null && cpf.equals(cpfData)) {
            userController.delete(idData, "id");
            logout();
        } else {
            errorDelLabel.setText("Erro ao ler cpf! Verifique suas credenciais.");
        }
    }


}
