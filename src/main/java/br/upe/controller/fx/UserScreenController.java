package br.upe.controller.fx;

import br.upe.controller.UserController;
import br.upe.facade.Facade;
import br.upe.facade.FacadeInterface;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.IOException;
import static br.upe.ui.Validation.isValidEmail;

public class UserScreenController extends BaseController implements FxController {
    private FacadeInterface facade;

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
    private Label errorDelLabel;

    public void setFacade(FacadeInterface facade) {
        this.facade = facade;
        initial();
    }

    private void initial() {
        userEmail.setText(facade.getUserData("email"));
        setupPlaceholders();
    }

    private void setupPlaceholders() {
        PlaceholderUtils.setupPlaceholder(emailTextField, emailPlaceholder);
        PlaceholderUtils.setupPlaceholder(cpfTextField, cpfPlaceholder);
    }

    public void handleEvent() throws IOException {
        genericButton("/fxml/mainScreen.fxml", userPane, facade, null);
    }

    public void handleSubEvent() throws IOException {
        genericButton("/fxml/subEventScreen.fxml", userPane, facade, null);
    }

    public void handleSubmitEvent() throws IOException {
        genericButton("/fxml/submitScreen.fxml", userPane, facade, null);
    }

    public void handleSession() throws IOException {
        genericButton("/fxml/sessionScreen.fxml", userPane, facade, null);
    }

    public void logout() throws IOException {
        genericButton("/fxml/loginScreen.fxml", userPane, facade, null);
    }

    public void updateUser() throws IOException {

        String email = emailTextField.getText();

        if (isValidEmail(email)) {
            facade.updateUser(email, facade.getUserData("cpf"));
            logout();
        } else {
            errorUpdtLabel.setText("E-mail invalido!");
        }
    }

    public void deleteUser() throws IOException {
        String cpf = cpfTextField != null ? cpfTextField.getText() : "";

        if (cpf == null || cpf.isEmpty()) {
            errorDelLabel.setText("CPF não informado.");
            return;
        }

        if (facade == null) {
            System.out.println("facade está nulo");
            return;
        }

        String cpfData = facade.getUserData("cpf");
        String idData = facade.getUserData("id");

        if (cpfData != null && cpf.equals(cpfData)) {
            facade.deleteUser(idData, "id");
            logout();
        } else {
            errorDelLabel.setText("Erro ao ler cpf! Verifique suas credenciais.");
        }
    }


}
