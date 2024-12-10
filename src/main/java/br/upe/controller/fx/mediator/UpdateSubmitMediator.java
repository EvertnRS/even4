package br.upe.controller.fx.mediator;

import br.upe.controller.fx.UpdateSubmitScreenController;
import br.upe.facade.FacadeInterface;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class UpdateSubmitMediator extends Mediator{
    private final UpdateSubmitScreenController updateSubmitScreenController;

    public UpdateSubmitMediator(UpdateSubmitScreenController updateSubmitScreenController, FacadeInterface facade, AnchorPane screenPane, Label errorUpdtLabel) {
        super(facade, screenPane, errorUpdtLabel, updateSubmitScreenController);
        this.updateSubmitScreenController = updateSubmitScreenController;
    }

    @Override
    public void registerComponents() {
        if (screenPane != null) {
            setupButtonAction("#updateButton", "handleArticleUpdate");
            setupButtonAction("#handleEventButton", "handleEvent");
            setupButtonAction("#handleSubEventButton", "handleSubEvent");
            setupButtonAction("#handleSessionButton", "handleSession");
            setupButtonAction("#handleSubmitButton", "handleSubmit");
            setupButtonAction("#handleUserButton", "handleUser");
            setupButtonAction("#handleBackButton", "handleBack");
            setupButtonAction("#logoutButton", "logout");
        }
    }

    @Override
    public Object notify(String event) throws IOException {
        if (updateSubmitScreenController != null) {
            switch (event) {
                case "handleArticleUpdate":
                    handleArticleUpdate();
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

    private void handleArticleUpdate() throws IOException {
        updateSubmitScreenController.updateArticle();
    }

    private void loadScreenForEvent(String event) {
        String fxmlFile = getFxmlPathForEvent(event);

        loadScreenWithTask(() -> {
            try {
                updateSubmitScreenController.genericButton(fxmlFile, screenPane, facade, null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private String getFxmlPathForEvent(String event) {
        return switch (event) {
            case "handleUser" -> "/fxml/userScreen.fxml";
            case "handleEvent" -> "/fxml/eventScreen.fxml";
            case "handleSession" -> "/fxml/sessionScreen.fxml";
            case "handleSubEvent" -> "/fxml/subEventScreen.fxml";
            case "handleSubmit" , "handleBack"-> "/fxml/submitScreen.fxml";
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
        updateSubmitScreenController.loadScreen("Carregando", () -> {
            try {
                task.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, screenPane);
    }
}
