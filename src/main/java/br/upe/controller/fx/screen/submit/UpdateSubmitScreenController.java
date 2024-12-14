package br.upe.controller.fx.screen.submit;

import br.upe.controller.fx.fxutils.PlaceholderUtils;
import br.upe.controller.fx.mediator.submit.UpdateSubmitMediator;
import br.upe.controller.fx.screen.BaseController;
import br.upe.controller.fx.screen.FxController;
import br.upe.facade.FacadeInterface;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class UpdateSubmitScreenController extends BaseController implements FxController {
    private FacadeInterface facade;
    private UUID articleId;
    private UpdateSubmitMediator mediator;

    @FXML
    private AnchorPane submitPane;
    @FXML
    private Label userEmail;
    @FXML
    private Text newArticlePlaceholder;
    @FXML
    private TextField newArticleTextField;
    @FXML
    private Label errorUpdtLabel;

    public void setEventName(String articleId) {
        this.articleId = UUID.fromString(articleId);
    }

    public void setFacade(FacadeInterface facade) throws IOException {
        this.facade = facade;
        initial();
    }

    private void initial() throws IOException {
        userEmail.setText(facade.getUserData("email"));

        setupPlaceholders();

        this.mediator = new UpdateSubmitMediator(this, facade, submitPane, errorUpdtLabel);
        mediator.registerComponents();
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
            newArticleTextField.setText(selectedFile.getAbsolutePath());
        } else {
            errorUpdtLabel.setText("Nenhum arquivo selecionado.");
            errorUpdtLabel.setAlignment(Pos.CENTER);
        }
    }

    public void updateArticle() throws IOException {
        String newArticle = newArticleTextField.getText();

        facade.updateArticle(newArticle, articleId);
        mediator.notify("handleBack");
    }

    public TextField getNewArticleTextField() {
        return newArticleTextField;
    }

    private void setupPlaceholders() {
        PlaceholderUtils.setupPlaceholder(newArticleTextField, newArticlePlaceholder);
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