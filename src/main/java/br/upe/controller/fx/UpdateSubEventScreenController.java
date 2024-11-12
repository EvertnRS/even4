package br.upe.controller.fx;

import br.upe.facade.Facade;
import br.upe.facade.FacadeInterface;
import br.upe.persistence.Persistence;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import static br.upe.ui.Validation.isValidDate;

public class UpdateSubEventScreenController extends BaseController implements FxController {
    private FacadeInterface facade;
    private String subEventName;

    @FXML
    private AnchorPane editSubEventPane;
    @FXML
    private Label userEmail;
    @FXML
    private TextField editNameTextField;
    @FXML
    private Text namePlaceholder;
    @FXML
    private DatePicker editDatePicker;
    @FXML
    private Text datePlaceholder;
    @FXML
    private TextField editLocationTextField;
    @FXML
    private Text locationPlaceholder;
    @FXML
    private TextField editDescriptionTextField;
    @FXML
    private Text descriptionPlaceholder;
    @FXML
    private Label errorUpdtLabel;
    @FXML
    private Label errorDelLabel;

    public void setFacade(FacadeInterface facade) {
        this.facade = facade;
        initial();
    }

    public void setEventName(String eventName) {
        this.subEventName = eventName;
    }


    private void initial() {
        userEmail.setText(facade.getUserData("email"));
        setupPlaceholders();
    }

    private void setupPlaceholders() {
        PlaceholderUtils.setupPlaceholder(editNameTextField, namePlaceholder);
        PlaceholderUtils.setupPlaceholder(editDatePicker, datePlaceholder);
        PlaceholderUtils.setupPlaceholder(editLocationTextField, locationPlaceholder);
        PlaceholderUtils.setupPlaceholder(editDescriptionTextField, descriptionPlaceholder);
    }

    public void handleEvent() throws IOException {
        genericButton("/fxml/mainScreen.fxml", editSubEventPane, facade, null);
    }

    public void handleSubEvent() throws IOException {
        genericButton("/fxml/subEventScreen.fxml", editSubEventPane, facade, null);
    }

    public void handleSubmitEvent() throws IOException {
        genericButton("/fxml/submitScreen.fxml", editSubEventPane, facade, null);
    }

    public void handleSession() throws IOException {
        genericButton("/fxml/sessionScreen.fxml", editSubEventPane, facade, null);
    }

    public void logout() throws IOException {
        genericButton("/fxml/loginScreen.fxml", editSubEventPane, facade, null);
    }

    public void handleUser() throws IOException {
        genericButton("/fxml/userScreen.fxml", editSubEventPane, facade, null);
    }

    public void updateSubEvent() throws IOException {
        /*String newSubName = editNameTextField.getText();
        String newLocation = editLocationTextField.getText();
        String newDescription = editDescriptionTextField.getText();
        String newDate = editDatePicker.getValue().toString();

        Map<UUID, Persistence> subEventMap = facade.getSubEventHashMap();
        if (!isValidDate(newDate) || newLocation.isEmpty() || newDescription.isEmpty() || isValidName(newSubName, subEventMap)) {
            errorUpdtLabel.setText("Erro no preenchimento das informações.");
        }else {
            facade.updateSubEvent(subEventName, newSubName, newDate, newDescription, newLocation, facade.getUserData("id"));
            handleSubEvent();
        }*/
    }

}
