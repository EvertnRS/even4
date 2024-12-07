package br.upe.controller.fx.mediator;

import br.upe.controller.fx.UserScreenController;
import br.upe.facade.FacadeInterface;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

import static br.upe.ui.Validation.isValidCPF;
import static br.upe.ui.Validation.isValidEmail;

public class UserMediator extends Mediator {
    private final UserScreenController userScreenController;

    public UserMediator(UserScreenController userScreenController, FacadeInterface facade, AnchorPane screenPane, Label errorUpdtLabel) {
        super(facade, screenPane, errorUpdtLabel, userScreenController);
        this.userScreenController = userScreenController;
    }

    @Override
    public void registerComponents() {
        if (screenPane != null) {
            setupButtonAction("#handleUpdateButton", "handleUpdateUser");
            setupButtonAction("#handleDeleteButton", "handleDeleteUser");
            setupButtonAction("#handleSubEventButton", "handleSubEvent");
            setupButtonAction("#handleSessionButton", "handleSession");
            setupButtonAction("#handleSubmitButton", "handleSubmit");
            setupButtonAction("#handleEventButton", "handleEvent");
            setupButtonAction("#logoutButton", "logout");
        }
    }

    @Override
    public Object notify(String event) throws IOException {
        if (userScreenController != null) {
            switch (event) {
                case "handleUpdateUser":
                    handleUpdateUser();
                    break;

                case "handleDeleteUser":
                    userScreenController.deleteUser();
                    break;

                case "handleUser"
                , "handleEvent"
                , "handleSession"
                , "handleSubEvent"
                , "handleSubmit":
                    loadScreenForEvent(event);
                    break;

                case "handleDeleteEvent":
                    return deleteButtonAlert();

                case "logout":
                    userScreenController.genericButton("/fxml/loginScreen.fxml", screenPane, facade, null);
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

    private void loadScreenForEvent(String event) throws IOException {
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
            case "handleUser" -> "/fxml/userScreen.fxml";
            case "handleEvent" -> "/fxml/mainScreen.fxml";
            case "handleSession" -> "/fxml/sessionScreen.fxml";
            case "handleSubEvent" -> "/fxml/subEventScreen.fxml";
            case "handleSubmit" -> "/fxml/submitScreen.fxml";
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
}
