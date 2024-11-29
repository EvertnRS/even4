package br.upe.ui;

import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

public class JavaFXSmokeTest extends ApplicationTest {
    private Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        new FxInterface().start(stage); // Substitua pela sua classe principal
    }

    @Test
    public void testAppStarts() {
        assertNotNull(stage, "A janela principal deve ser inicializada.");
        assertEquals("Even4", stage.getTitle(), "O título da janela não está correto."); // Substitua pelo título real
    }
}
