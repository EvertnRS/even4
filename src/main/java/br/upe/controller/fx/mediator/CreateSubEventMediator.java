package br.upe.controller.fx.mediator;

import br.upe.controller.fx.CreateSubEventScreenController;
import br.upe.facade.FacadeInterface;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class CreateSubEventMediator extends Mediator{
    private final CreateSubEventScreenController createSubEventScreenController;

    public CreateSubEventMediator(CreateSubEventScreenController createSubEventScreenController, FacadeInterface facade, AnchorPane screenPane, Label errorUpdtLabel) {
        super(facade, screenPane, errorUpdtLabel, createSubEventScreenController);
        this.createSubEventScreenController = createSubEventScreenController;
    }

    @Override
    public void registerComponents() {
        if (screenPane != null) {
            setupButtonAction("#createButton", "handleSubEventCreate");
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
        if (createSubEventScreenController != null) {
            switch (event) {
                case "handleSubEventCreate":
                    handleSubEventCreate();
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

    private void handleSubEventCreate() throws IOException {
        if (validateInputs()) {
            createSubEventScreenController.createSubEvent();
        }
    }

    private void loadScreenForEvent(String event) {
        String fxmlFile = getFxmlPathForEvent(event);

        loadScreenWithTask(() -> {
            try {
                createSubEventScreenController.genericButton(fxmlFile, screenPane, facade, null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private String getFxmlPathForEvent(String event) {
        return switch (event) {
            case "handleSubEventCreate" -> "/fxml/createSubEventScreen.fxml";
            case "handleUser" -> "/fxml/userScreen.fxml";
            case "handleSubEvent", "handleBack" -> "/fxml/subEventScreen.fxml";
            case "handleSession" -> "/fxml/sessionScreen.fxml";
            case "handleEvent" -> "/fxml/mainScreen.fxml";
            case "handleSubmit" -> "/fxml/submitScreen.fxml";
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
        createSubEventScreenController.loadScreen("Carregando", () -> {
            try {
                task.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, screenPane);
    }

}
