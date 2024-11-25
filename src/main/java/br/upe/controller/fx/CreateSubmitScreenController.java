package br.upe.controller.fx;
import br.upe.facade.FacadeInterface;
import br.upe.persistence.Event;
import br.upe.persistence.repository.EventRepository;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class CreateSubmitScreenController extends BaseController implements FxController {

    private FacadeInterface facade;

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

    public void setFacade(FacadeInterface facade) throws IOException {
        this.facade = facade;
        initial();
    }

    private void initial() throws IOException {
        userEmail.setText(facade.getUserData("email"));
        loadArticles();
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
    private void loadArticles() throws IOException {
        List<Event> allEvents = facade.getAllEvent();
        eventComboBox.getItems().clear();

        EventRepository eventRepository = EventRepository.getInstance();

        for (Event event : allEvents) {
            String eventName = (String) eventRepository.getData(event.getId(), "name");

            if (eventName != null) {
                eventComboBox.getItems().add(eventName);
            }
        }
    }




    @FXML
    private void createArticle()throws IOException {
        String novoEventName = eventComboBox.getSelectionModel().getSelectedItem();
        String nameArticle = namesTextField.getText();
        facade.createArticle(novoEventName, nameArticle, facade.getUserData("id"));
        handleSubmitEvent();

    }

    @FXML
    private void openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecione um Artigo");


        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );


        Stage stage = (Stage) submitPane.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            namesTextField.setText(selectedFile.getAbsolutePath());
        } else {
            errorUpdtLabel.setText("Nenhum arquivo selecionado.");
        }
    }

}