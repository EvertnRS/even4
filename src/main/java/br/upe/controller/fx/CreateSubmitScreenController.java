package br.upe.controller.fx;
import br.upe.controller.*;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CreateSubmitScreenController extends BaseController implements FxController {

    private UserController userController;
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

    public void setUserController(UserController userController) throws IOException {
        this.userController = userController;
        this.eventController = new EventController();
        this.submitArticleController = new SubmitArticleController();
        initial();
    }

    private void initial() throws IOException {
        userEmail.setText(userController.getData("email"));
        loadUserEvents();
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

    public void handleSession() throws IOException {
        genericButton("/fxml/sessionScreen.fxml", submitPane, userController, null);
    }

    public void logout() throws IOException {
        genericButton("/fxml/loginScreen.fxml", submitPane, userController, null);
    }

    public void handleUser() throws IOException {
        genericButton("/fxml/userScreen.fxml", submitPane, userController, null);
    }
    private void loadUserEvents() throws IOException {
        List<String> userEvents = eventController.list(userController.getData("id"), "submit");
        eventComboBox.getItems().addAll(userEvents);
    }

    @FXML
    private void createArticle()throws IOException {
        String novoEventName = eventComboBox.getSelectionModel().getSelectedItem();
        String nameArticle = namesTextField.getText();
        submitArticleController.create(novoEventName, nameArticle, userController.getData("id"));
        handleSubmitEvent();

    }

    @FXML
    private void openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecione um Artigo");


        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
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
