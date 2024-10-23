package br.upe.controller.fx;

import br.upe.controller.SessionController;
import br.upe.controller.SubmitArticleController;
import br.upe.controller.UserController;
import br.upe.persistence.Persistence;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class SubmitScreenController extends BaseController implements FxController {
    private UserController userController;
    private SubmitArticleController submitarticleController;

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

    }






}

