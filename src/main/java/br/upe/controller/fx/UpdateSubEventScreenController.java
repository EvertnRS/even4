package br.upe.controller.fx;

import br.upe.controller.fx.fxutils.PlaceholderUtils;
import br.upe.facade.FacadeInterface;
import br.upe.persistence.Model;
import br.upe.persistence.repository.SubEventRepository;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static br.upe.ui.Validation.isValidDate;

public class UpdateSubEventScreenController extends BaseController implements FxController {
    private FacadeInterface facade;
    private UUID subEventName;

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

    public void setEventName(UUID eventName) {
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

    private void loadSubEventDetails() {
        SubEventRepository subeventRepository = SubEventRepository.getInstance();
        if (subeventRepository != null) {
            String eventName = (String) subeventRepository.getData(subEventName,"name");
            String eventLocation = (String) subeventRepository.getData(subEventName,"location");
            String eventDescription = (String) subeventRepository.getData(subEventName,"description");
            editNameTextField.setText(eventName);
            editLocationTextField.setText(eventLocation);
            editDescriptionTextField.setText(eventDescription);

            Object dateObject = subeventRepository.getData(subEventName, "date");
            java.sql.Date sqlDate;

            if (dateObject instanceof java.sql.Timestamp) {
                sqlDate = new java.sql.Date(((java.sql.Timestamp) dateObject).getTime());
            } else if (dateObject instanceof java.sql.Date) {
                sqlDate = (java.sql.Date) dateObject;
            } else {
                throw new IllegalArgumentException("Tipo inesperado: " + dateObject.getClass().getName());
            }

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String subeventDate = formatter.format(sqlDate);
            if (!subeventDate.isEmpty()) {
                editDatePicker.setValue(LocalDate.parse(subeventDate));
            }

            setupPlaceholders();
        } else {
            errorUpdtLabel.setText("SubEvento não encontrado.");
            errorUpdtLabel.setAlignment(Pos.CENTER);
        }
    }


    public void updateSubEvent() throws IOException {
        String newName = editNameTextField.getText();
        String newLocation = editLocationTextField.getText();
        String newDescription = editDescriptionTextField.getText();
        Date newDate = Date.valueOf(editDatePicker.getValue() != null ? editDatePicker.getValue().toString() : "");
        List<Model> subeventList = facade.getAllSubEvent();
        if (!isValidDate(String.valueOf(newDate))) {
            errorUpdtLabel.setText("Data inválida.");
            errorUpdtLabel.setAlignment(Pos.CENTER);
        } else if (newLocation.isEmpty() || newDescription.isEmpty() || isValidName(String.valueOf(subEventName), subeventList)) {
            errorUpdtLabel.setText("Erro no preenchimento das informações.");
            errorUpdtLabel.setAlignment(Pos.CENTER);
        } else {
            facade.updateSubEvent(subEventName, newName, newDate, newDescription, newLocation);
            handleSubEvent();
        }
    }

    @Override
    public TextField getNameTextField() {
        return null;
    }

    @Override
    public TextField getLocationTextField() {
        return null;
    }

    @Override
    public TextField getDescriptionTextField() {
        return null;
    }

    @Override
    public DatePicker getDatePicker() {
        return null;
    }

}