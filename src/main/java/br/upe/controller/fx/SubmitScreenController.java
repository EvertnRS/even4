package br.upe.controller.fx;

import br.upe.controller.SubmitArticleController;
import br.upe.controller.UserController;
import br.upe.persistence.Persistence;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
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


            String articleName = new File(article.getData("name")).getName().split("=")[0].trim();


            createArticleContainer(article, articleVBox);
        }
    }



    private void createArticleContainer(Persistence article, VBox eventVBox) {
        VBox articleContainer = new VBox();
        articleContainer.setStyle("-fx-background-color: #d3d3d3; -fx-padding: 10px; -fx-spacing: 5px; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        String articleName = new File(article.getData("name")).getName();

        Label articleLabel = new Label(articleName);
        articleLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #333333;");

        Button editButton = new Button("Editar");
        Button deleteButton = new Button("Excluir");

        editButton.setOnAction(e -> {
            try {
                handleEditArticle(articleName);
            } catch (IOException ex) {
                throw new IllegalArgumentException(ex);
            }
        });

        deleteButton.setOnAction(e -> {
            try {
                handleDeleteArticle(articleName);
            } catch (IOException ex) {
                throw new IllegalArgumentException(ex);
            }
        });

        HBox actionButtons = new HBox(10);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);
        actionButtons.getChildren().addAll(editButton, deleteButton);

        articleContainer.getChildren().addAll(articleLabel, actionButtons);
        eventVBox.getChildren().add(articleContainer);
    }


    private void handleEditArticle(String articleName) throws IOException {
        genericButton("/fxml/updateSubmitScreen.fxml", submitPane, userController, articleName);
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
