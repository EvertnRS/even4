package br.upe.controller.fx;

import br.upe.controller.SubmitArticleController;
import br.upe.controller.UserController;
import br.upe.persistence.Persistence;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class SubmitScreenController extends BaseController implements FxController {
    private UserController userController;
    private final SubmitArticleController submitArticleController = new SubmitArticleController(); // Instância do controlador de artigos

    @FXML
    private Label userEmail;
    @FXML
    private VBox articleVBox;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private AnchorPane submitPane;

    public void setUserController(UserController userController) throws IOException {
        this.userController = userController;
        initial();
    }

    private void initial() throws IOException {
        userEmail.setText(userController.getData("email"));  // Exibe o e-mail do usuário
        loadUserArticles();  // Carrega os artigos do usuário
    }


    public void handleEvent() throws IOException {
        genericButton("/fxml/mainScreen.fxml", submitPane, userController, null);
    }

    public void handleSubEvent() throws IOException {
        genericButton("/fxml/subEventScreen.fxml", submitPane, userController, null);
    }

    public void handleSession() throws IOException {
        genericButton("/fxml/sessionScreen.fxml", submitPane, userController, null);
    }

    public void handleUser() throws IOException {
        genericButton("/fxml/userScreen.fxml", submitPane, userController, null);
    }

    public void handleAddArticle() throws IOException {
        genericButton("/fxml/createSubmitScreen.fxml", submitPane, userController, null);
    }

    public void logout() throws IOException {
        genericButton("/fxml/loginScreen.fxml", submitPane, userController, null);
    }


    private void loadUserArticles() throws IOException {
        articleVBox.getChildren().clear();  // Limpa a interface


        submitArticleController.read(userController.getData("id"));

        Map<String, Persistence> articles = submitArticleController.getArticleHashMap();
        System.out.println(articles);
        if (articles.isEmpty()) {
            Label noArticlesLabel = new Label("Nenhum artigo encontrado.");
            noArticlesLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #999999;");
            articleVBox.getChildren().add(noArticlesLabel);
            return;
        }

        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setStyle("-fx-padding: 20px;");
        articleVBox.setAlignment(Pos.CENTER);


        for (Map.Entry<String, Persistence> entry : articles.entrySet()) {
            Persistence article = entry.getValue();




            createArticleContainer(article, articleVBox);
        }
    }



    private void createArticleContainer(Persistence article, VBox articleVBox) {
        VBox articleContainer = new VBox();
        articleContainer.setStyle("-fx-background-color: #d3d3d3; -fx-padding: 10px; -fx-spacing: 5px; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        // Nome do artigo (estilo e posição principais)
        Label articleLabel = new Label(new File(article.getData("name")).getName());
        articleLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #000000;");

        // Nome do evento (estilo e posição secundários)
        Label eventLabel = new Label(new File(article.getData("event")).getName());
        eventLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #555555;");

        // Botões de ação, alinhados à direita
        HBox actionButtons = createActionButtons(article);

        // Ordem de adição dos elementos: artigo, evento, botões
        articleContainer.getChildren().addAll(articleLabel, actionButtons, eventLabel);
        articleVBox.getChildren().add(articleContainer);
    }

    private HBox createActionButtons(Persistence article) {
        Button editButton = new Button("Editar");
        editButton.setStyle("-fx-background-color: #6fa3ef; -fx-text-fill: white; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(128, 128, 128, 1), 3.88, 0, -1, 5);");
        editButton.setOnAction(e -> {
            try {
                handleEditArticle(article.getData("name"), article.getData("event"));
            } catch (IOException ex) {
                throw new IllegalArgumentException(ex);
            }
        });

        Button deleteButton = new Button("Excluir");
        deleteButton.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(128, 128, 128, 1), 3.88, 0, -1, 5);");
        deleteButton.setOnAction(e -> {
            try {
                handleDeleteArticle(article.getData("name"));
            } catch (IOException ex) {
                throw new IllegalArgumentException(ex);
            }
        });

        HBox actionButtons = new HBox(10);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);
        actionButtons.getChildren().addAll(editButton, deleteButton);
        return actionButtons;
    }


    private void handleEditArticle(String articleName, String eventName) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/updateSubmitScreen.fxml"));
        AnchorPane pane = loader.load();
        UpdateSubmitScreenController controller = loader.getController();
        controller.setUserController(userController);
        controller.setEventName(eventName); // Passa o eventName atual para o controller
        controller.setNameArticle(articleName); // Passa o nome do artigo
        submitPane.getChildren().setAll(pane);  // Exibe a nova tela
    }



    private void handleDeleteArticle(String articleName) throws IOException {

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmação de Exclusão");
        confirmationAlert.setHeaderText("Deseja realmente excluir este artigo?");
        confirmationAlert.setContentText("Esta ação não pode ser desfeita.");

        ButtonType buttonSim = new ButtonType("Sim");
        ButtonType buttonNao = new ButtonType("Não", ButtonBar.ButtonData.CANCEL_CLOSE);

        confirmationAlert.getButtonTypes().setAll(buttonSim, buttonNao);

        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == buttonSim) {
            submitArticleController.delete(articleName);
            loadUserArticles();
        }
    }


}

