package br.upe.controller.fx;

import br.upe.controller.SubmitArticleController;
import br.upe.controller.EventController;
import br.upe.controller.UserController;
import br.upe.facade.Facade;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.List;

public class UpdateSubmitScreenController extends BaseController implements FxController {
    private SubmitArticleController SubmitArticleController;
    private EventController eventController;
    private Facade facade;

    @FXML
    private AnchorPane submitPane;
    @FXML
    private Label userEmail;
    @FXML
    private TextField editDescriptionTextField;
    @FXML
    private Text descriptionPlaceholder;
    @FXML
    private Label errorUpdtLabel;
    @FXML
    private Label errorDelLabel;
    @FXML
    private ComboBox<String> eventComboBox;
    @FXML
    private ComboBox<String> eventComboBox1;


    private String SessionName;

    public void setFacade(Facade facade) throws IOException {
        this.facade = facade;
        this.SubmitArticleController = new SubmitArticleController();
        this.eventController = new EventController();
        initial();
    }

    public void setEventName(String eventName) {
        this.SessionName = eventName;
    }


    private void initial() throws IOException {
        userEmail.setText(facade.getUserData("email"));
        loadUserEvents();
        setupPlaceholders();
    }

    private void setupPlaceholders() {
        PlaceholderUtils.setupPlaceholder(editDescriptionTextField, descriptionPlaceholder);
    }
    private void loadUserEvents() throws IOException {
        List<String> userEvents = eventController.list(facade.getUserData("id"), "fx");
        eventComboBox.getItems().addAll(userEvents);
        eventComboBox1.getItems().addAll(userEvents);

    }

    public void handleEvent() throws IOException {
        genericButton("/fxml/mainScreen.fxml", submitPane, facade, null);
    }

    public void handleSubEvent() throws IOException {
        genericButton("/fxml/subEventScreen.fxml", submitPane, facade, null);
    }

    public void handleSubmitEvent() throws IOException {
        genericButton("/fxml/submitScreen.fxml", submitPane, facade, null);
    }


    public void logout() throws IOException {
        genericButton("/fxml/loginScreen.fxml", submitPane, facade, null);
    }

    public void handleUser() throws IOException {
        genericButton("/fxml/userScreen.fxml", submitPane, facade, null);
    }

    public void handleSession() throws IOException {
        genericButton("/fxml/sessionScreen.fxml", submitPane, facade, null);
    }

    public void handleSubmit() throws IOException {
        genericButton("/fxml/submitScreen.fxml", submitPane, facade, null);
    }

    public void updateArticle() throws IOException {
        String novoEventName = eventComboBox.getSelectionModel().getSelectedItem();
        String oldEventName = eventComboBox1.getSelectionModel().getSelectedItem();
        String nameArticle = editDescriptionTextField.getText();

        SubmitArticleController.update(novoEventName, oldEventName, nameArticle);
        handleSubmit();
    }

}

