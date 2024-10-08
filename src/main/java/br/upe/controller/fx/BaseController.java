package br.upe.controller.fx;

import br.upe.controller.UserController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class BaseController {

    public void genericButton(String path, AnchorPane pane, UserController userController) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
        AnchorPane screen = loader.load();

        if (userController != null){
        FxController screenController = loader.getController();
        screenController.setUserController(userController);
        }

        Scene scene = new Scene(screen);
        Stage stage = (Stage) pane.getScene().getWindow();

        stage.setScene(scene);
        stage.setTitle("Even4");

    }

    public abstract void setUserController(UserController userController);
}

