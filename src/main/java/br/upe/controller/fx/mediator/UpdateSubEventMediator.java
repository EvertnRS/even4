package br.upe.controller.fx.mediator;

import br.upe.controller.fx.UpdateSubEventScreenController;
import br.upe.facade.FacadeInterface;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class UpdateSubEventMediator extends Mediator{
    private final UpdateSubEventScreenController updateScreenSubEventController;

    public UpdateSubEventMediator(UpdateSubEventScreenController updateScreenSubEventController, FacadeInterface facade, AnchorPane screenPane, Label errorUpdtLabel) {
        super(facade, screenPane, errorUpdtLabel, updateScreenSubEventController);
        this.updateScreenSubEventController = updateScreenSubEventController;
    }

    @Override
    public void registerComponents() {
        if (screenPane != null) {
            setupButtonAction("#updateButton", "handleSubEventUpdate");
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
        if (updateScreenSubEventController != null) {
            switch (event) {
                case "handleSubEventUpdate":
                    handleSubEventUpdate();
                    break;
                case "handleUser"
                , "handleSubEvent"
                , "handleBack"
                , "handleSession"
                , "handleEvent"
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

    private void handleSubEventUpdate() throws IOException {
        if (validateInputs()) {
            updateScreenSubEventController.updateSubEvent();
        }
    }

    private void loadScreenForEvent(String event) throws IOException {
        String fxmlFile = getFxmlPathForEvent(event);

        loadScreenWithTask(() -> {
            try {
                updateScreenSubEventController.genericButton(fxmlFile, screenPane, facade, null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private String getFxmlPathForEvent(String event) {
        return switch (event) {
            case "handleSubEventUpdate" -> "/fxml/updateSubEventScreen.fxml";
            case "handleUser" -> "/fxml/userScreen.fxml";
            case "handleSubEvent", "handleBack" -> "/fxml/subEventScreen.fxml";
            case "handleSession" -> "/fxml/sessionScreen.fxml";
            case "handleEvent" -> "/fxml/mainScreen.fxml";
            case "handleSubmit" -> "/fxml/submitScreen.fxml";
            case "loginScreen" -> "/fxml/loginScreen.fxml";
            default -> throw new IllegalArgumentException("Unknown event: " + event);
        };
    }

    private void logout() throws IOException {
        facade = null;
        loadScreenForEvent("loginScreen");
    }

    private void loadScreenWithTask(Runnable task) {
        assert screenPane != null;
        updateScreenSubEventController.loadScreen("Carregando", () -> {
            try {
                task.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, screenPane);
    }

}
