package br.upe.controller.fx;
import br.upe.controller.UserController;
import br.upe.facade.Facade;
import br.upe.persistence.Persistence;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import java.io.IOException;
import java.util.Map;
import static br.upe.ui.Validation.isValidDate;

public class CreateEventScreenController extends BaseController implements FxController {
    private UserController userController;
    private Facade facade;

    @FXML
    private AnchorPane newEventPane;
    @FXML
    private Label userEmail;
    @FXML
    private TextField nameTextField;
    @FXML
    private Text namePlaceholder;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Text datePlaceholder;
    @FXML
    private TextField locationTextField;
    @FXML
    private Text locationPlaceholder;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private Text descriptionPlaceholder;
    @FXML
    private Label errorUpdtLabel;

    public void setFacade(Facade facade) throws IOException {
        this.facade = facade;
        initial();
    }

    private void initial() {
        userEmail.setText(userController.getData("email"));
        setupPlaceholders();
    }

    private void setupPlaceholders() {
        PlaceholderUtils.setupPlaceholder(nameTextField, namePlaceholder);
        PlaceholderUtils.setupPlaceholder(datePicker, datePlaceholder);
        PlaceholderUtils.setupPlaceholder(locationTextField, locationPlaceholder);
        PlaceholderUtils.setupPlaceholder(descriptionTextField, descriptionPlaceholder);
    }

    public void handleEvent() throws IOException {
        genericButton("/fxml/mainScreen.fxml", newEventPane, facade, null);
    }

    public void handleSubEvent() throws IOException {
        genericButton("/fxml/subEventScreen.fxml", newEventPane, facade, null);
    }

    public void handleSubmitEvent() throws IOException {
        genericButton("/fxml/submitScreen.fxml", newEventPane, facade, null);
    }

    public void handleSession() throws IOException {
        genericButton("/fxml/sessionScreen.fxml", newEventPane, facade, null);
    }

    public void logout() throws IOException {
        genericButton("/fxml/loginScreen.fxml", newEventPane, facade, null);
    }

    public void handleUser() throws IOException {
        genericButton("/fxml/userScreen.fxml", newEventPane, facade, null);
    }

    public void createEvent() throws IOException {
        String eventName = nameTextField.getText();
        String eventLocation = locationTextField.getText();
        String eventDescription = descriptionTextField.getText();
        String eventDate = datePicker.getValue() != null ? datePicker.getValue().toString() : "";

        Map<String, Persistence> eventMap = facade.getEventHashMap();
        if (!isValidDate(eventDate) || eventLocation.isEmpty() || eventDescription.isEmpty() || isValidName(eventName, eventMap)) {
            errorUpdtLabel.setText("Erro no preenchimento das informações.");
        }else {
            facade.createEvent(eventName, eventDate, eventDescription, eventLocation, facade.getUserData("id"));
            facade.readEvent();
            handleEvent();
        }
    }

}


