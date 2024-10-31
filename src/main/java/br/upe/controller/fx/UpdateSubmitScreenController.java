package br.upe.controller.fx;

import br.upe.controller.SubmitArticleController;
import br.upe.controller.EventController;
import br.upe.controller.UserController;
import br.upe.persistence.Event;
import br.upe.persistence.Persistence;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;
import java.util.HashMap;

public class UpdateSubmitScreenController extends BaseController implements FxController {
    private UserController userController;
    private SubmitArticleController SubmitArticleController;
    private EventController eventController;
    private String eventName;
    private String nameArticle;



    @FXML
    private AnchorPane submitPane;
    @FXML
    private Label userEmail;
    @FXML
    private Label errorUpdtLabel;
    @FXML
    private Label errorDelLabel;
    @FXML
    private ComboBox<String> eventComboBox;
    @FXML


    public void setUserController(UserController userController) throws IOException {
        this.userController = userController;
        this.SubmitArticleController = new SubmitArticleController();
        this.eventController = new EventController();
        initial();
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
    public void setNameArticle(String nameArticle) {
        this.nameArticle = nameArticle;
    }


    private void initial() throws IOException {
        userEmail.setText(userController.getData("email"));
        loadArticles();
    }
    private void loadArticles() throws IOException {
        Event eventController = new Event();

        HashMap<String, Persistence> allEvents = eventController.read();

        eventComboBox.getItems().clear();

        for (Persistence event : allEvents.values()) {
            eventComboBox.getItems().add(event.getData("name"));
        }
    }

    public void handleEvent() throws IOException {
        genericButton("/fxml/mainScreen.fxml", submitPane, userController, null);
    }

    public void handleSubEvent() throws IOException {
        genericButton("/fxml/subEventScreen.fxml", submitPane, userController, null);
    }

    public void handleSubmitEvent() throws IOException {
        genericButton("/fxml/submitScreen.fxml", submitPane, userController, null);
    }


    public void logout() throws IOException {
        genericButton("/fxml/loginScreen.fxml", submitPane, userController, null);
    }

    public void handleUser() throws IOException {
        genericButton("/fxml/userScreen.fxml", submitPane, userController, null);
    }

    public void handleSession() throws IOException {
        genericButton("/fxml/sessionScreen.fxml", submitPane, userController, null);
    }

    public void handleSubmit() throws IOException {
        genericButton("/fxml/submitScreen.fxml", submitPane, userController, null);
    }

    public void updateArticle() throws IOException {
        String novoEventName = eventComboBox.getSelectionModel().getSelectedItem();

        SubmitArticleController.update(novoEventName, eventName, nameArticle);
        handleSubmit();
    }

}