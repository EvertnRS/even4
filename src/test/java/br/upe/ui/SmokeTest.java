package br.upe.ui;

import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

public class SmokeTest extends ApplicationTest {
    private Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        // Inicia a sua aplicação JavaFX
        this.stage = stage;
        new FxInterface().start(stage); // Substitua MyJavaFxApp pela sua classe principal
    }

    @Test
    public void testAppStarts() {
        // Verifica se a janela principal está visível
        assertNotNull(stage);

        // Verifica se o título da janela está correto
        assertEquals("Even4", stage.getTitle()); // Substitua pelo título real da sua aplicação
    }
}
