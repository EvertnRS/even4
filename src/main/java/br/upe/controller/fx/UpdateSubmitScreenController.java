package br.upe.controller.fx;

import br.upe.facade.FacadeInterface;
import br.upe.persistence.repository.EventRepository;
import br.upe.persistence.repository.Persistence;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class UpdateSubmitScreenController extends BaseController implements FxController {
    private FacadeInterface facade;
    private String eventName;
    private String nameArticle;

    @FXML
    private AnchorPane submitPane;
    @FXML
    private Label userEmail;
    @FXML
    private ComboBox<String> eventComboBox;
    @FXML


    public void setFacade(FacadeInterface facade) throws IOException {
        this.facade = facade;
        initial();
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
    public void setNameArticle(String nameArticle) {
        this.nameArticle = nameArticle;
    }


    private void initial() throws IOException {
        userEmail.setText(facade.getUserData("email"));
        loadArticles();
    }

    private void loadArticles() throws IOException {
        EventRepository eventRepository = new EventRepository();

        HashMap<UUID, Persistence> allEvents = eventRepository.read();

        eventComboBox.getItems().clear();

        for (Persistence event : allEvents.values()) {
            String eventName = (String) event.getData("name");
            eventComboBox.getItems().add(eventName);
        }
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

        facade.updateArticle(novoEventName, eventName, nameArticle);
        handleSubmit();
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