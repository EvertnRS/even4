package br.upe.fx;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;

public class LogoController {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private ImageView imageView;

    @FXML
    public void initialize() {
        Image logo = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/Even4.png")));
        imageView.setImage(logo);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1.6), event -> {
            changeToLoginScreen();
        }));
        timeline.play();
    }

    private void changeToLoginScreen() {
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), anchorPane);
        fadeOut.setFromValue(0.5);
        fadeOut.setToValue(0.0);

        fadeOut.setOnFinished(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/loginScreen.fxml"));
                AnchorPane loginScreen = loader.load();

                loginScreen.setOpacity(0);

                Scene loginScene = new Scene(loginScreen);
                Stage stage = (Stage) anchorPane.getScene().getWindow();

                stage.setScene(loginScene);
                stage.setTitle("Even4");

                FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), loginScreen);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();

                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        fadeOut.play();
    }
}
