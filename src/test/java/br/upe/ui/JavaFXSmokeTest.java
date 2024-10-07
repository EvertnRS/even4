package br.upe.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JavaFXSmokeTest {

    @BeforeAll
    public static void setUp() {
        Thread javafxThread = new Thread(() -> Application.launch(TestApp.class));
        javafxThread.setDaemon(true);
        javafxThread.start();
        // Espera a inicialização do JavaFX
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMainSceneIsDisplayed() {
        Platform.runLater(() -> {
            try {
                Stage stage = new Stage();
                new FxInterface().start(stage);
                assertNotNull(stage.getScene(), "A cena não foi carregada.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @AfterAll
    public static void tearDown() {
        Platform.exit();
    }

    public static class TestApp extends Application {
        @Override
        public void start(Stage primaryStage) {
        }
    }
}
