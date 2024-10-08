package br.upe.controller.fx;

import br.upe.controller.*;
import br.upe.persistence.Persistence;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Map;

public class MainScreenController extends BaseController implements FxController {
    UserController userController;
    EventController eventController;

    @FXML
    private VBox eventVBox;
    @FXML
    private Label userEmail;
    @FXML
    private AnchorPane mainPane;

    @Override
    public void setUserController(UserController userController) {
        this.userController = userController;
        this.eventController = new EventController();
        initial();
    }

    private void initial() {
        userEmail.setText(userController.getData("email"));
        loadUserEvents();
    }

    public void handleSubmit() throws IOException {
        genericButton("/fxml/submitScreen.fxml", mainPane, userController);
    }

    public void handleUser() throws IOException {
        genericButton("/fxml/userScreen.fxml", mainPane, userController);
    }

    public void handleSubEvent() throws IOException {
        genericButton("/fxml/subEventScreen.fxml", mainPane, userController);
    }

    public void handleSession() throws IOException {
        genericButton("/fxml/sessionScreen.fxml", mainPane, userController);
    }

    public void logout() throws IOException {
        genericButton("/fxml/loginScreen.fxml", mainPane, userController);
    }

    private void loadUserEvents() {
        eventVBox.getChildren().clear();

        eventController.list(userController.getData("id"), "");

        for (Map.Entry<String, Persistence> entry : eventController.getEventHashMap().entrySet()) {
            Persistence persistence = entry.getValue();
            if (persistence.getData("ownerId").equals(userController.getData("id"))) {
                Label eventLabel = new Label(persistence.getData("name"));

                eventVBox.getChildren().add(eventLabel);
            }
        }
    }
}

