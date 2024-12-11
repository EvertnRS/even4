package br.upe.controller.fx;
import br.upe.controller.fx.mediator.CreateSubmitMediator;
import br.upe.facade.FacadeInterface;
import br.upe.persistence.Model;
import br.upe.persistence.repository.EventRepository;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
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

    private FacadeInterface facade;
    private CreateSubmitMediator mediator;

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

    private void initial() {
        userEmail.setText(facade.getUserData("email"));
        loadArticles();

        this.mediator = new CreateSubmitMediator(this, facade, submitPane, errorUpdtLabel);
        mediator.registerComponents();
    }

    private void loadArticles() {
        List<Model> allEvents = facade.getAllEvent();
        eventComboBox.getItems().clear();

        EventRepository eventRepository = EventRepository.getInstance();

        for (Model event : allEvents) {
            String eventName = (String) eventRepository.getData(event.getId(), "name");

            if (eventName != null) {
                eventComboBox.getItems().add(eventName);
            }
        }
    }

    @FXML
    public void createArticle()throws IOException {
        String eventName = eventComboBox.getSelectionModel().getSelectedItem();
        String nameArticle = namesTextField.getText();
        facade.createArticle(eventName, nameArticle, facade.getUserData("id"));
        mediator.notify("handleBack");

    }

    public void openFileChooser() {
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
            errorUpdtLabel.setAlignment(Pos.CENTER);
        }
    }

    public TextField getNamesTextField() {
        return namesTextField;
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