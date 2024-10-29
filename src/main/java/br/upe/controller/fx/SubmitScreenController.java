package br.upe.controller.fx;

import br.upe.controller.SubmitArticleController;
import br.upe.controller.UserController;
import br.upe.facade.Facade;
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
    private Facade facade;
    private final SubmitArticleController submitArticleController = new SubmitArticleController(); // Instância do controlador de artigos

    @FXML
    private Label userEmail;
    @FXML
    private VBox articleVBox;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private AnchorPane submitPane;

    // Configura o UserController e inicializa a tela de artigos
    public void setFacade(Facade facade) throws IOException {
        this.facade = facade;
        initial();
    }

    // Método de inicialização da interface
    private void initial() throws IOException {
        userEmail.setText(facade.getUserData("email"));  // Exibe o e-mail do usuário
        loadUserArticles();  // Carrega os artigos do usuário
    }


    // Botões para navegação entre telas
    public void handleEvent() throws IOException {
        genericButton("/fxml/mainScreen.fxml", submitPane, facade, null);
    }

    public void handleSubEvent() throws IOException {
        genericButton("/fxml/subEventScreen.fxml", submitPane, facade, null);
    }

    public void handleSession() throws IOException {
        genericButton("/fxml/sessionScreen.fxml", submitPane, facade, null);
    }

    public void handleUser() throws IOException {
        genericButton("/fxml/userScreen.fxml", submitPane, facade, null);
    }

    public void handleAddArticle() throws IOException {
        genericButton("/fxml/createSubmitScreen.fxml", submitPane, facade, null);
    }

    public void logout() throws IOException {
        genericButton("/fxml/loginScreen.fxml", submitPane, facade, null);
    }

    // Carrega os artigos do usuário logado
    private void loadUserArticles() throws IOException {
        articleVBox.getChildren().clear();  // Limpa a interface

        // Chama o método read para obter os artigos do usuário
        submitArticleController.read(facade.getUserData("id"));

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

        // Cria contêineres para cada artigo
        for (Map.Entry<String, Persistence> entry : articles.entrySet()) {
            Persistence article = entry.getValue();

            // Aqui, não há mais verificação de ID
            // Obtém o nome do artigo antes do "="
            String articleName = new File(article.getData("name")).getName().split("=")[0].trim();

            createArticleContainer(article, articleVBox); // Adiciona o artigo diretamente
        }
    }


    // Cria contêiner para exibir cada artigo individualmente
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
        genericButton("/fxml/updateSubmitScreen.fxml", submitPane, facade, articleName);
    }


    // Lógica de exclusão do artigo
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
            loadUserArticles();  // Recarrega os artigos para atualizar a tela
        }
    }


}
