package br.upe.controller.fx;
import br.upe.controller.*;
import br.upe.facade.Facade;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;
import java.util.List;

public class CreateSubmitScreenController extends BaseController implements FxController {

    private Facade facade;
    private SubmitArticleController submitArticleController;
    private EventController eventController;

    @FXML
    private AnchorPane submitPane;
    @FXML
    private Label userEmail;
    @FXML
    private TextField namesTextField;
    @FXML
    private ComboBox<String> eventComboBox;
    @FXML
    private Label errorUpdtLabel;

    public void setFacade(Facade facade) throws IOException {
        this.facade = facade;
        this.eventController = new EventController();
        this.submitArticleController = new SubmitArticleController();
        initial();
    }

    private void initial() throws IOException {
        userEmail.setText(facade.getUserData("email"));
        loadUserEvents();
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

    public void handleSession() throws IOException {
        genericButton("/fxml/sessionScreen.fxml", submitPane, facade, null);
    }

    public void logout() throws IOException {
        genericButton("/fxml/loginScreen.fxml", submitPane, facade, null);
    }

    public void handleUser() throws IOException {
        genericButton("/fxml/userScreen.fxml", submitPane, facade, null);
    }
    private void loadUserEvents() throws IOException {
        List<String> userEvents = eventController.list(facade.getUserData("id"), "submit");
        eventComboBox.getItems().addAll(userEvents);
    }
    @FXML
    private void createArticle()throws IOException {
        String novoEventName = eventComboBox.getSelectionModel().getSelectedItem();
        String nameArticle = namesTextField.getText();
        submitArticleController.create(novoEventName, nameArticle, facade.getUserData("id"));
        handleSubmitEvent();

    }

}
