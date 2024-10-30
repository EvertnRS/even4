package br.upe.controller.fx;

import javafx.animation.TranslateTransition;
import javafx.scene.Cursor;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class PlaceholderUtils {
    private static void animatePlaceholder(Text placeholder, boolean focus) {
        TranslateTransition transition = new TranslateTransition(Duration.millis(200), placeholder);
        if (focus) {
            transition.setFromY(0);
            transition.setToY(-13);
            placeholder.setFill(Color.BLACK);
        } else {
            transition.setFromY(-13);
            transition.setToY(0);
            placeholder.setFill(Color.DARKGRAY);
        }
        transition.play();
    }

    public static void setupPlaceholder(Control control, Text placeholder) {
        if (!isControlEmpty(control)) {
            animatePlaceholder(placeholder, true);
        }

        control.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (Boolean.TRUE.equals(newValue)) {
                animatePlaceholder(placeholder, true);
            } else if (isControlEmpty(control)) {
                animatePlaceholder(placeholder, false);
            }
        });

        placeholder.setOnMouseEntered(event -> placeholder.setCursor(Cursor.TEXT));
        placeholder.setOnMouseExited(event -> placeholder.setCursor(Cursor.DEFAULT));

        placeholder.setOnMouseClicked(event -> {
            control.requestFocus();
        });
    }

    private static boolean isControlEmpty(Control control) {
        if (control instanceof TextField) {
            return ((TextField) control).getText().isEmpty();
        } else if (control instanceof DatePicker) {
            return ((DatePicker) control).getValue() == null;
        }
        return true;
    }
}
