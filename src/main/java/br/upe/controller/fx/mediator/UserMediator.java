package br.upe.controller.fx.mediator;

import br.upe.controller.fx.UserScreenController;
import br.upe.facade.FacadeInterface;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

import static br.upe.ui.Validation.isValidCPF;
import static br.upe.ui.Validation.isValidEmail;

public class UserMediator extends Mediator {
    private static final String HANDLE_SUB_EVENT = "handleSubEvent";
    private static final String HANDLE_SESSION = "handleSession";
    private static final String HANDLE_EVENT = "handleEvent";
    private static final String HANDLE_SUBMIT = "handleSubmit";
    private static final String HANDLE_USER = "handleUser";
    private static final String HANDLE_INSCRIPTION = "handleInscription";
    private static final String HANDLE_UPDATEUSER = "handleUpdateUser";
    private static final String HANDLE_DELETEUSER = "handleDeleteUser";




    private final UserScreenController userScreenController;
    private TextField nameTextField;
    private TextField cpfTextField;
    private TextField emailTextField;
    private TextField passTextField;

    public UserMediator(UserScreenController userScreenController, FacadeInterface facade, AnchorPane screenPane, Label errorUpdtLabel) {
        super(facade, screenPane, errorUpdtLabel, userScreenController);
        this.userScreenController = userScreenController;
    }

    public void setComponents(TextField nameTextField, TextField cpfTextField, TextField emailTextField, TextField passTextField) {
        this.nameTextField = nameTextField;
        this.cpfTextField = cpfTextField;
        this.emailTextField = emailTextField;
        this.passTextField = passTextField;

        if (userScreenController != null) {
            setupListeners();
        }
    }

    @Override
    public void registerComponents() {
        if (screenPane != null) {
            setupButtonAction("#handleUpdateButton", HANDLE_UPDATEUSER);
            setupButtonAction("#handleDeleteButton", HANDLE_DELETEUSER);
            setupButtonAction("#handleSubEventButton", HANDLE_SUB_EVENT);
            setupButtonAction("#handleSessionButton", HANDLE_SESSION);
            setupButtonAction("#handleSubmitButton", HANDLE_SUBMIT);
            setupButtonAction("#handleEventButton", HANDLE_EVENT);
            setupButtonAction("#handleUserButton", HANDLE_USER);
            setupButtonAction("#handleInscriptionButton", HANDLE_INSCRIPTION);
            setupButtonAction("#logoutButton", "logout");
        }
    }

    @Override
    public Object notify(String event) throws IOException {
        if (userScreenController != null) {
            switch (event) {
                case HANDLE_UPDATEUSER:
                    handleUpdateUser();
                    break;
                case HANDLE_DELETEUSER:
                    userScreenController.deleteUser();
                    break;

                case HANDLE_USER
                , HANDLE_EVENT
                , HANDLE_SESSION
                , HANDLE_SUB_EVENT
                , HANDLE_INSCRIPTION
                , HANDLE_SUBMIT:
                    loadScreenForEvent(event);
                    break;

                case "handleDeleteEvent":
                    return deleteButtonAlert();

                case "logout":
                    facade = null;
                    userScreenController.genericButton("/fxml/loginScreen.fxml", screenPane, null, null);
                    break;

                default:
                    throw new IllegalArgumentException("Ação não reconhecida: " + event);
            }
        }
        return null;
    }

    private void handleUpdateUser() throws IOException {
        String cpf = userScreenController.getCpfTextField().getText();
        String email = userScreenController.getEmailTextField().getText();

        if (isValidEmail(email) && isValidCPF(cpf)) {
            userScreenController.updateUser();
        } else {
            errorUpdtLabel.setText("E-mail inválido!");
        }
    }

    private void loadScreenForEvent(String event) {
        String fxmlFile = getFxmlPathForEvent(event);

        loadScreenWithTask(() -> {
            try {
                userScreenController.genericButton(fxmlFile, screenPane, facade, null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private String getFxmlPathForEvent(String event) {
        return switch (event) {
            case HANDLE_USER -> "/fxml/userScreen.fxml";
            case HANDLE_INSCRIPTION -> "/fxml/attendeeScreen.fxml";
            case HANDLE_EVENT -> "/fxml/eventScreen.fxml";
            case HANDLE_SESSION -> "/fxml/sessionScreen.fxml";
            case HANDLE_SUB_EVENT -> "/fxml/subEventScreen.fxml";
            case HANDLE_SUBMIT -> "/fxml/submitScreen.fxml";
            default -> throw new IllegalArgumentException("Unknown event: " + event);
        };
    }

    private void loadScreenWithTask(Runnable task) {
        assert screenPane != null;
        userScreenController.loadScreen("Carregando", () -> {
            try {
                task.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, screenPane);
    }

    private void setupListeners() {

        configureNavigation(nameTextField, passTextField, cpfTextField);
        configureNavigation(cpfTextField, nameTextField, emailTextField);
        configureNavigation(emailTextField, cpfTextField, passTextField);
        configureNavigation(passTextField, emailTextField, nameTextField);

    }

    private void configureNavigation(Node currentField, Node previousField, Node nextField) {
        currentField.setOnKeyPressed(event -> handleKeyNavigation(event, previousField, nextField));
    }

    private void handleKeyNavigation(KeyEvent event, Node previousField, Node nextField) {
        if (event.getCode() == KeyCode.DOWN || event.getCode() == KeyCode.TAB) {
            nextField.requestFocus();
            event.consume();
        }
        if (event.getCode() == KeyCode.UP || (event.getCode() == KeyCode.TAB && event.isShiftDown())) {
            previousField.requestFocus();
            event.consume();
        }
    }
}
