package br.upe.controller.fx;

import br.upe.controller.fx.mediator.SubmitMediator;
import br.upe.facade.FacadeInterface;
import br.upe.persistence.SubmitArticle;
import br.upe.persistence.repository.SubmitArticlesRepository;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

public class SubmitScreenController extends BaseController implements FxController {
    private FacadeInterface facade;
    private SubmitMediator mediator;


    @FXML
    private Label userEmail;
    @FXML
    private VBox articleVBox;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private AnchorPane submitPane;

    public void setFacade(FacadeInterface facade) throws IOException {
        this.facade = facade;
        initial();
    }

    private void initial() throws IOException {
        userEmail.setText(facade.getUserData("email"));

        mediator = new SubmitMediator(this, facade, submitPane, null);
        mediator.registerComponents();

        loadUserArticles();
    }

    private void loadUserArticles() throws IOException {
        articleVBox.getChildren().clear();

        List<SubmitArticle> userArticles = facade.listSubmitArticles(facade.getUserData("id"));
        SubmitArticlesRepository submitArticlesRepository = SubmitArticlesRepository.getInstance();

        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setStyle("-fx-padding: 20px;");
        articleVBox.setAlignment(Pos.CENTER);

        for (SubmitArticle article : userArticles) {
            if (article.getOwnerId().getId().equals(UUID.fromString(facade.getUserData("id")))) {
                createArticleContainer(article.getId(), submitArticlesRepository);
            }
        }
    }

    private void createArticleContainer(UUID articleId, SubmitArticlesRepository submitArticlesRepository) {
        VBox articleContainer = new VBox();
        articleContainer.setStyle("-fx-background-color: #d3d3d3; -fx-padding: 10px; -fx-spacing: 5px; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        Label articleLabel = new Label((String) submitArticlesRepository.getData(articleId, "name"));
        articleLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #000000;");

        HBox actionButtons = createActionButtons(articleId);

        articleContainer.getChildren().addAll(articleLabel, actionButtons);
        articleVBox.getChildren().add(articleContainer);
    }

    private HBox createActionButtons(UUID articleId) {
        Button editButton = createStyledButton("Editar", "#6fa3ef");
        editButton.setOnAction(e -> {
            try {
                handleEditArticle(articleId);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        Button deleteButton = createStyledButton("Excluir", "#ff6b6b");
        deleteButton.setOnAction(e -> {
            try {
                handleDeleteArticle(articleId);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        Button detailsButton = createStyledButton("Detalhes", "#ff914d");
        detailsButton.setOnAction(e -> handleDetailArticle(articleId));

        Button downloadButton = createStyledButton("Download", "#4CAF50");
        downloadButton.setOnAction(e -> {
            try {
                handleDownloadArticle(articleId);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        HBox actionButtons = new HBox(10);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);
        actionButtons.getChildren().addAll(downloadButton, detailsButton, editButton, deleteButton);
        return actionButtons;
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: white; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(128, 128, 128, 1), 3.88, 0, -1, 5);", color));
        return button;
    }

    private void handleDetailArticle(UUID articleId) {
        loadScreen("Carregando", () -> {
        SubmitArticlesRepository submitArticlesRepository = SubmitArticlesRepository.getInstance();

        String content = "Nome: " + submitArticlesRepository.getData(articleId, "name") + "\n" +
                "Evento: " + submitArticlesRepository.getData(articleId, "event") + "\n";

        Platform.runLater(() -> {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalhes do Artigo");
        alert.setHeaderText("Detalhes do Artigo");
        alert.setContentText(content);
        alert.showAndWait();
            });
        }, submitPane);
    }

    private void handleDownloadArticle(UUID articleId) throws IOException {
        SubmitArticlesRepository submitArticlesRepository = SubmitArticlesRepository.getInstance();

        byte[] pdfBytes = (byte[]) submitArticlesRepository.getData(articleId, "article");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar Arquivo PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showSaveDialog(submitPane.getScene().getWindow());

            if (file != null) {
                try (OutputStream os = new FileOutputStream(file)) {
                    os.write(pdfBytes);  // Grava os bytes do PDF no arquivo escolhido
                    os.flush();
                    Alert  alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Download Completo");
                    alert.setHeaderText(null);
                    alert.setContentText("Arquivo PDF salvo com sucesso em " + file.getAbsolutePath());
                    alert.showAndWait();
                } catch (IOException e) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Erro no Download");
                    errorAlert.setHeaderText("Não foi possível salvar o arquivo");
                    errorAlert.setContentText("Erro: " + e.getMessage());
                    errorAlert.showAndWait();
                }
            }
    }


    private void handleEditArticle(UUID articleId) throws IOException {
        mediator.setArticleId(articleId);
        mediator.notify("handleUpdateArticle");
    }

    private void handleDeleteArticle(UUID articleId) throws IOException {
        mediator.setArticleId(articleId);

        Optional<ButtonType> result = (Optional<ButtonType>) mediator.notify("handleDeleteEvent");

        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
            facade.deleteArticle(articleId);
            loadUserArticles();
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