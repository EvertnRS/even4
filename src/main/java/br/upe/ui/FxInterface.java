package br.upe.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FxInterface extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/logo.fxml"));
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root);
        stage.setTitle("Even4");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.getIcons().clear();
        stage.getIcons().add(new javafx.scene.image.Image("/images/logo/Logo.png"));

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
