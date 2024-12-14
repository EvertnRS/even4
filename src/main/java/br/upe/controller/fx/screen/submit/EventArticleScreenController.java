package br.upe.controller.fx.screen.submit;

import br.upe.controller.fx.mediator.submit.EventArticleMediator;
import br.upe.controller.fx.screen.BaseController;
import br.upe.controller.fx.screen.FxController;
import br.upe.facade.FacadeInterface;
import br.upe.persistence.SubmitArticle;
import br.upe.persistence.repository.SubmitArticlesRepository;
import br.upe.utils.CustomRuntimeException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EventArticleScreenController extends BaseController implements FxController {
    private FacadeInterface facade;
    private UUID eventId;
    private EventArticleMediator mediator;
    private static final String BG_COLOR = "-fx-background-color: #ffffff; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(128, 128, 128, 1), 3.88, 0, -1, 5);";

    @FXML
    private Label userEmail;
    @FXML
    private VBox articleVBox;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private AnchorPane submitPane;
    @FXML
    private TextField searchTextField;
    @FXML
    private Text searchPlaceholder;
    @FXML
    private ImageView logoView6;

    public void setFacade(FacadeInterface facade) throws IOException {
        this.facade = facade;
        initial();
    }

    public void setEventId(UUID eventId) throws IOException {
        this.eventId = eventId;
        loadEventArticles();
    }

    private void initial() throws IOException {
        userEmail.setText(facade.getUserData("email"));

        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            setupPlaceholders();
            try {
                loadEventArticles();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        mediator = new EventArticleMediator(this, facade, submitPane, null);
        mediator.registerComponents();

        loadEventArticles();
    }

    private void loadEventArticles() throws IOException {
        articleVBox.getChildren().clear();

        List<SubmitArticle> eventArticles = facade.getEventArticles(eventId);
        SubmitArticlesRepository submitArticlesRepository = SubmitArticlesRepository.getInstance();

        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-top: 2px; -border-color: #cccccc");
        articleVBox.setAlignment(Pos.CENTER);

        for (SubmitArticle article : eventArticles) {
            createArticleContainer(article.getId(), submitArticlesRepository);
        }
    }

    private void createArticleContainer(UUID articleId, SubmitArticlesRepository submitArticlesRepository) {
        VBox eventArticleContainer = new VBox();
        eventArticleContainer.setStyle("-fx-background-color: #d3d3d3; " +
                "-fx-padding: 10px 20px 10px 20px; " +
                "-fx-margin: 0 40px 0 40px; " +
                "-fx-spacing: 5px; " +
                "-fx-border-radius: 10px; " +
                "-fx-background-radius: 10px;");

        VBox.setMargin(eventArticleContainer, new Insets(5, 20, 5, 20));

        Label articleLabel;
        if (searchTextField.getText().isEmpty() || String.valueOf(submitArticlesRepository.getData(articleId, "name")).contains(searchTextField.getText())) {
            articleLabel = new Label((String) submitArticlesRepository.getData(articleId, "name"));
            articleLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #000000;");

            HBox actionButtons = createActionButtons(articleId);

            eventArticleContainer.getChildren().addAll(articleLabel, actionButtons);
            articleVBox.getChildren().add(eventArticleContainer);
        }
    }

    private HBox createActionButtons(UUID articleId) {
        Button deleteButton = new Button("Rejeitar");
        ImageView deleteIcon = new ImageView(new Image("images/icons/buttons/deleteIcon.png"));
        deleteIcon.setFitWidth(16);
        deleteIcon.setFitHeight(16);
        deleteButton.setGraphic(deleteIcon);
        deleteButton.setStyle(BG_COLOR);

        deleteButton.setOnAction(e -> {
            try {
                handleDeleteArticle(articleId);
            } catch (IOException ex) {
                throw new CustomRuntimeException("Algo deu errado", ex);
            }
        });

        Button detailsButton = new Button("Detalhes");
        ImageView detailsIcon = new ImageView(new Image("images/icons/buttons/detailsIcon.png"));
        detailsIcon.setFitWidth(16);
        detailsIcon.setFitHeight(16);
        detailsButton.setGraphic(detailsIcon);
        detailsButton.setStyle(BG_COLOR);

        detailsButton.setOnAction(e -> handleDetailArticle(articleId));

        Button downloadButton = new Button("Download");
        ImageView downloadIcon = new ImageView(new Image("images/icons/buttons/downloadIcon.png"));
        downloadIcon.setFitWidth(16);
        downloadIcon.setFitHeight(16);
        downloadButton.setGraphic(downloadIcon);
        downloadButton.setStyle(BG_COLOR);

        downloadButton.setOnAction(e -> {
            try {
                handleDownloadArticle(articleId);
            } catch (IOException ex) {
                throw new CustomRuntimeException("Algo deu errado", ex);
            }
        });

        HBox actionButtons = new HBox(10);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);
        actionButtons.getChildren().addAll(downloadButton, detailsButton, deleteButton);
        return actionButtons;
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
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
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

    private void handleDeleteArticle(UUID articleId) throws IOException {
        mediator.setArticleId(articleId);

        Optional<ButtonType> result = (Optional<ButtonType>) mediator.notify("handleDeleteEvent");

        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
            facade.deleteArticle(articleId);
            loadEventArticles();
        }
    }

    public void setupPlaceholders() {
        if (!searchTextField.getText().isEmpty()) {
            searchPlaceholder.setVisible(false);
            logoView6.setVisible(false);
        } else {
            searchPlaceholder.setVisible(true);
            logoView6.setVisible(true);
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