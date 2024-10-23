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
    private SubmitArticleController submitarticleController;
    private SubmitArticleController submitArticleController = new SubmitArticleController(); // Instância adicional

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
        this.submitarticleController = new SubmitArticleController();
        initial();
    }

    private void initial() throws IOException {
        userEmail.setText(userController.getData("email"));
        loadUserArticles();
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
        articleVBox.getChildren().clear(); // Limpa os artigos na interface

        // Carrega os artigos do controlador usando apenas o ID do usuário
        String userId = userController.getData("id");
        boolean hasArticles = submitArticleController.list(userId);
        System.out.println("User ID: " + userId);

        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setStyle("-fx-padding: 20px;");
        articleVBox.setAlignment(Pos.CENTER);

        if (!hasArticles) {
            Label noArticlesLabel = new Label("Nenhum artigo encontrado.");
            noArticlesLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #999999;");
            articleVBox.getChildren().add(noArticlesLabel);
            return;
        }

        // Mapa para agrupar artigos por evento
        Map<String, VBox> eventMap = new HashMap<>();

        // Itera sobre os artigos carregados no controlador
        for (Map.Entry<String, Persistence> entry : submitArticleController.getArticleHashMap().entrySet()) {
            Persistence article = entry.getValue();

            // Verifica se o artigo pertence ao usuário atual
            if (article.getData("ownerId").equals(userId)) {
                String eventName = article.getData("eventName"); // Obtém o nome do evento do artigo

                // Se o evento não estiver no mapa, cria um novo VBox para ele
                eventMap.computeIfAbsent(eventName, key -> {
                    VBox eventVBox = new VBox();
                    eventVBox.setStyle("-fx-padding: 10px; -fx-spacing: 10px;");
                    Label eventLabel = new Label(key);
                    eventLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #333333;");
                    eventVBox.getChildren().add(eventLabel);
                    return eventVBox;
                });

                // Adiciona o artigo ao VBox do evento correspondente
                createArticleContainer(article, eventMap.get(eventName));
            }
        }

        // Adiciona todos os VBoxes de eventos à VBox principal
        articleVBox.getChildren().addAll(eventMap.values());
    }



    private void createArticleContainer(Persistence persistence, VBox eventVBox) {
        VBox articleContainer = new VBox();
        articleContainer.setStyle("-fx-background-color: #d3d3d3; -fx-padding: 10px; -fx-spacing: 5px; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        // Usa apenas o nome simples do arquivo
        String articleName = new File(persistence.getData("name")).getName();
        Label articleLabel = new Label(articleName);
        articleLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #333333;");

        Button editButton = new Button("Editar");
        editButton.setStyle("-fx-background-color: #6fa3ef; -fx-text-fill: white; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(128, 128, 128, 1), 3.88, 0, -1, 5);");

        Button deleteButton = new Button("Excluir");
        deleteButton.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(128, 128, 128, 1), 3.88, 0, -1, 5);");

        editButton.setOnAction(e -> {
            try {
                handleEditArticle(articleName); // Passa o nome simples
            } catch (IOException ex) {
                throw new IllegalArgumentException(ex);
            }
        });

        deleteButton.setOnAction(e -> {
            try {
                handleDeleteArticle(articleName, userController.getData("id")); // Passa o nome simples
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



    private void handleEditArticle(String name) throws IOException {
        // Lógica para editar o artigo
    }

    private void handleDeleteArticle(String articleName, String userId) throws IOException {
        // Verifica se o artigo existe no HashMap
        Persistence article = submitArticleController.getArticleHashMap().get(articleName);

        // Se o artigo foi encontrado, confirma a exclusão
        if (article != null) {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirmação de Exclusão");
            confirmationAlert.setHeaderText("Deseja realmente excluir este artigo?");
            confirmationAlert.setContentText("Esta ação não pode ser desfeita.");

            ButtonType buttonSim = new ButtonType("Sim");
            ButtonType buttonNao = new ButtonType("Não", ButtonBar.ButtonData.CANCEL_CLOSE);

            confirmationAlert.getButtonTypes().setAll(buttonSim, buttonNao);

            Optional<ButtonType> result = confirmationAlert.showAndWait();

            if (result.isPresent() && result.get() == buttonSim) {
                // Chama o método de exclusão no controlador de artigos usando apenas o nome do artigo
                submitArticleController.delete(articleName); // Você não precisa do userId aqui
                loadUserArticles(); // Recarrega os artigos para atualizar a tela
            }
        } else {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Erro ao excluir artigo");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Artigo não encontrado.");
            errorAlert.showAndWait();
        }
    }




}
