package br.upe.controller.fx.mediator;

import br.upe.controller.fx.EventScreenController;
import br.upe.controller.fx.UserScreenController;
import br.upe.facade.FacadeInterface;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

import static br.upe.ui.Validation.isValidCPF;
import static br.upe.ui.Validation.isValidEmail;

public class UserMediator extends Mediator {
    private final UserScreenController userScreenController;
    private String eventId;

    public UserMediator(UserScreenController userScreenController, FacadeInterface facade, AnchorPane screenPane, Label errorUpdtLabel) {
        super(facade, screenPane, errorUpdtLabel, userScreenController);
        this.userScreenController = userScreenController;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
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
                    String cpf = userScreenController.getCpfTextField().getText();
                    String email = userScreenController.getEmailTextField().getText();

                    if (isValidEmail(email) && isValidCPF(cpf)) {
                        userScreenController.updateUser();
                    } else {
                        errorUpdtLabel.setText("E-mail invalido!");
                    }
                    break;
                case "handleDeleteUser":
                    userScreenController.deleteUser();
                    break;
                case "handleUpdateEvent":
                    userScreenController.genericButton("/fxml/updateEventScreen.fxml", screenPane, facade, eventId);
                    break;
                case "handleDeleteEvent":
                    return deleteButtonAlert();
                case "handleUser":
                    userScreenController.genericButton("/fxml/userScreen.fxml", screenPane, facade, null);
                    break;
                case "handleEvent":
                    userScreenController.genericButton("/fxml/mainScreen.fxml", screenPane, facade, null);
                    break;
                case "handleSession":
                    userScreenController.genericButton("/fxml/sessionScreen.fxml", screenPane, facade, null);
                    break;
                case "handleSubEvent":
                    userScreenController.genericButton("/fxml/subEventScreen.fxml", screenPane, facade, null);
                    break;
                case "handleSubmit":
                    userScreenController.genericButton("/fxml/submitScreen.fxml", screenPane, facade, null);
                    break;
                case "logout":
                    userScreenController.genericButton("/fxml/loginScreen.fxml", screenPane, facade, null);
                    break;
                default:
                    break;
            }
        }
        return null;
    }
}
