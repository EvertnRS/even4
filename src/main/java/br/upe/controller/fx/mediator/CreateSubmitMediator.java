package br.upe.controller.fx.mediator;

import br.upe.controller.fx.CreateSubmitScreenController;
import br.upe.facade.FacadeInterface;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class CreateSubmitMediator extends Mediator{
    private final CreateSubmitScreenController createSubmitScreenController;

    public CreateSubmitMediator(CreateSubmitScreenController createSubmitScreenController, FacadeInterface facade, AnchorPane screenPane, Label errorUpdtLabel) {
        super(facade, screenPane, errorUpdtLabel, createSubmitScreenController);
        this.createSubmitScreenController = createSubmitScreenController;
    }

    @Override
    public void registerComponents() {
        if (screenPane != null) {
            setupButtonAction("#createButton", "handleArticleCreate");
            setupButtonAction("#handleEventButton", "handleEvent");
            setupButtonAction("#handleSubEventButton", "handleSubEvent");
            setupButtonAction("#handleSessionButton", "handleSession");
            setupButtonAction("#handleSubmitButton", "handleSubmit");
            setupButtonAction("#handleUserButton", "handleUser");
            setupButtonAction("#handleBackButton", "handleBack");
            setupButtonAction("#fileChooser", "openFileChooser");
            setupButtonAction("#logoutButton", "logout");
        }
    }

    @Override
    public Object notify(String event) throws IOException {
        if (createSubmitScreenController != null) {
            switch (event) {
                case "handleArticleCreate":
                    handleSubmitCreate();
                    break;
                case "openFileChooser":
                    handleFileChooser();
                    break;
                case "handleUser"
                , "handleEvent"
                , "handleBack"
                , "handleSession"
                , "handleSubEvent"
                , "handleSubmit":
                    loadScreenForEvent(event);
                    break;
                case "logout":
                    logout();
                    break;
                default:
                    throw new IllegalArgumentException("Ação não reconhecida: " + event);
            }
        }
        return null;
    }

    private void handleSubmitCreate() throws IOException {
        createSubmitScreenController.createArticle();
    }

    private void handleFileChooser() {
        createSubmitScreenController.openFileChooser();
    }

    private void loadScreenForEvent(String event){
        String fxmlFile = getFxmlPathForEvent(event);

        loadScreenWithTask(() -> {
            try {
                createSubmitScreenController.genericButton(fxmlFile, screenPane, facade, null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private String getFxmlPathForEvent(String event) {
        return switch (event) {
            case "handleArticleCreate" -> "/fxml/createSubmitScreen.fxml";
            case "handleUser" -> "/fxml/userScreen.fxml";
            case "handleEvent" -> "/fxml/eventScreen.fxml";
            case "handleSession" -> "/fxml/sessionScreen.fxml";
            case "handleSubEvent" -> "/fxml/subEventScreen.fxml";
            case "handleSubmit", "handleBack" -> "/fxml/submitScreen.fxml";
            case "loginScreen" -> "/fxml/loginScreen.fxml";
            default -> throw new IllegalArgumentException("Unknown event: " + event);
        };
    }

    private void logout() {
        facade = null;
        loadScreenForEvent("loginScreen");
    }

    private void loadScreenWithTask(Runnable task) {
        assert screenPane != null;
        createSubmitScreenController.loadScreen("Carregando", () -> {
            try {
                task.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, screenPane);
    }

}
