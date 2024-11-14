package br.upe.controller.fx;

import br.upe.controller.EventController;
import br.upe.controller.SubEventController;
import br.upe.facade.FacadeInterface;
import br.upe.persistence.Model;
import br.upe.persistence.repository.Persistence;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class BaseController {

    public void genericButton(String path, AnchorPane pane, FacadeInterface facade, String eventId) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
        AnchorPane screen = loader.load();

        if (facade != null){
            FxController screenController = loader.getController();
            screenController.setFacade(facade);

            if (screenController instanceof UpdateEventScreenController) {
                ((UpdateEventScreenController) screenController).setEventId(UUID.fromString(eventId));
            }
            if (screenController instanceof UpdateSubEventScreenController) {
                ((UpdateSubEventScreenController) screenController).setEventName(eventId);
            }
            if (screenController instanceof UpdateSessionScreenController) {
                ((UpdateSessionScreenController) screenController).setEventName(eventId);
            }
            if (screenController instanceof UpdateSubmitScreenController) {
                ((UpdateSubmitScreenController) screenController).setEventName(eventId);
            }
            if (screenController instanceof CertificateScreenController) {
                ((CertificateScreenController) screenController).setEventName(eventId);
            }
        }


        Scene scene = new Scene(screen);
        Stage stage = (Stage) pane.getScene().getWindow();
        stage.getIcons().clear();
        stage.getIcons().add(new javafx.scene.image.Image("/images/Logo.png"));

        stage.setScene(scene);
        stage.setTitle("Even4");

    }

    public <T> boolean isValidName(String name, List<T> items) {
        for (T item : items) {
            if (item instanceof Model && ((Model) item).getName().equals(name) || name.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public Pane createLoadPane(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 14pt; -fx-text-fill: white;");
        label.setAlignment(Pos.CENTER);

        StackPane loadStack = new StackPane();
        loadStack.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");

        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setProgress(-1); // Indeterminate state for spinner
        progressIndicator.setStyle("-fx-progress-color: white;");

        VBox vbox = new VBox(20, label, progressIndicator);
        vbox.setAlignment(Pos.CENTER);

        loadStack.getChildren().add(vbox);

        return loadStack;
    }

    public void loadScreen(String text, Runnable taskBackend, Pane root) {
        Pane loadPane = createLoadPane(text);

        loadPane.prefWidthProperty().bind(root.widthProperty());
        loadPane.prefHeightProperty().bind(root.heightProperty());

        Platform.runLater(() -> root.getChildren().add(loadPane));

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                taskBackend.run();
                return null;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> root.getChildren().remove(loadPane));
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> root.getChildren().remove(loadPane));
            }
        };

        new Thread(task).start();
    }

    public boolean validateEventDate(String date, String searchId) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        EventController eventController = new EventController();
        SubEventController subEventController = new SubEventController();
        Map<UUID, Persistence> parentMap = new HashMap<>(eventController.getHashMap());
        parentMap.putAll(subEventController.getHashMap());


        String parentDateString = "";
        for (Map.Entry<UUID, Persistence> entry : parentMap.entrySet()) {
            Persistence listIndex = entry.getValue();
            if (listIndex.getData("name").equals(searchId)) {
                parentDateString = (String) listIndex.getData("date");
                break;
            }
        }
            LocalDate eventDate = LocalDate.parse(parentDateString, formatter);
            LocalDate inputDate = LocalDate.parse(date, formatter);

            return !inputDate.isBefore(eventDate);
    }

    public abstract void setFacade(FacadeInterface facade) throws IOException;
}

