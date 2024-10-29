package br.upe.controller.fx;

import br.upe.controller.UserController;
import br.upe.facade.Facade;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;

public class SubmitEventScreenController extends BaseController implements FxController{
    private Facade facade;

    @FXML
    private Label userEmail;
    @FXML
    private AnchorPane submitPane;

    public void setFacade(Facade facade) {
        this.facade = facade;
        initial();
    }

    private void initial() {
        userEmail.setText(facade.getUserData("email"));
    }

    public void handleUser() throws IOException {
        genericButton("/fxml/userScreen.fxml", submitPane, facade, null);
    }

    public void handleSubEvent() throws IOException {
        genericButton("/fxml/subEventScreen.fxml", submitPane, facade, null);
    }

    public void handleEvent() throws IOException {
        genericButton("/fxml/mainScreen.fxml", submitPane, facade, null);
    }

    public void handleSession() throws IOException {
        genericButton("/fxml/sessionScreen.fxml", submitPane, facade, null);
    }

    public void logout() throws IOException {
        genericButton("/fxml/loginScreen.fxml", submitPane, facade, null);
    }
}
