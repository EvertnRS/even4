package br.upe.controller.fx;

import br.upe.facade.FacadeInterface;
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
    private FacadeInterface facade;


    @FXML
    private Label userEmail;
    @FXML
    private VBox articleVBox;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private AnchorPane submitPane;

    // Configura o UserController e inicializa a tela de artigos
    public void setFacade(FacadeInterface facade) throws IOException {
        this.facade = facade;
        initial();
    }

    private void initial() throws IOException {
        userEmail.setText(facade.getUserData("email"));  // Exibe o e-mail do usuário
        loadUserArticles();  // Carrega os artigos do usuário
    }


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


    private void loadUserArticles() throws IOException {
        articleVBox.getChildren().clear();  // Limpa a interface

        // Chama o método read para obter os artigos do usuário
        facade.readArticle(facade.getUserData("id"));

        Map<String, Persistence> articles = facade.getArticleHashMap();
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



            createArticleContainer(article, articleVBox, articles); // Adiciona o artigo diretamente
        }
    }



    private void createArticleContainer(Persistence article, VBox articleVBox, Map<String, Persistence> articles) {
        VBox articleContainer = new VBox();
        articleContainer.setStyle("-fx-background-color: #d3d3d3; -fx-padding: 10px; -fx-spacing: 5px; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        // Nome do artigo (estilo e posição principais)
        Label articleLabel = new Label(new File(article.getData("name")).getName());
        articleLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #000000;");

        // Nome do evento (estilo e posição secundários)
        Label eventLabel = new Label(new File(article.getData("event")).getName());
        eventLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #555555;");

        // Botões de ação, alinhados à direita
        HBox actionButtons = createActionButtons(article, articles);

        // Ordem de adição dos elementos: artigo, evento, botões
        articleContainer.getChildren().addAll(articleLabel, actionButtons, eventLabel);
        articleVBox.getChildren().add(articleContainer);
    }

    private HBox createActionButtons(Persistence article, Map<String, Persistence> articles) {
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

        Button detailsButton = new Button("Detalhes");
        detailsButton.setStyle("-fx-background-color: #ff914d; -fx-text-fill: white; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(128, 128, 128, 1), 3.88, 0, -1, 5);");

        detailsButton.setOnAction(e ->
                handleDetailArticle(articles, article.getData("id")));

        HBox actionButtons = new HBox(10);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);
        actionButtons.getChildren().addAll(detailsButton, editButton, deleteButton);
        return actionButtons;
    }

    private void handleDetailArticle(Map<String, Persistence> articles, String id) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        Persistence article = articles.get(id);
        alert.setTitle("Detalhes do Artigo");
        alert.setHeaderText("Detalhes do Artigo");
        String content = "Nome: " + article.getData("name") + "\n" +
                "Caminho: " + article.getData("path") + "\n" +
                "Evento: " + article.getData("event") + "\n";

        alert.setContentText(content);
        alert.showAndWait();
    }


    private void handleEditArticle(String articleName, String eventName) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/updateSubmitScreen.fxml"));
        AnchorPane pane = loader.load();
        UpdateSubmitScreenController controller = loader.getController();
        controller.setFacade(facade);
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
            facade.deleteArticle(articleName);
            loadUserArticles();  // Recarrega os artigos para atualizar a tela
        }
    }


}