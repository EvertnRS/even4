package br.upe.controller.fx.mediator;

import br.upe.controller.fx.CreateAttendeeScreenController;
import br.upe.facade.FacadeInterface;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class CreateAttendeeMediator extends Mediator{
    private final CreateAttendeeScreenController createAttendeeScreenController;

    public CreateAttendeeMediator(CreateAttendeeScreenController createAttendeeScreenController, FacadeInterface facade, AnchorPane screenPane, Label errorUpdtLabel) {
        super(facade, screenPane, errorUpdtLabel, createAttendeeScreenController);
        this.createAttendeeScreenController = createAttendeeScreenController;
    }

    @Override
    public void registerComponents() {
        if (screenPane != null) {
            setupButtonAction("#createButton", "handleAttendeeCreate");
            setupButtonAction("#handleEventButton", "handleEvent");
            setupButtonAction("#handleSubEventButton", "handleSubEvent");
            setupButtonAction("#handleSessionButton", "handleSession");
            setupButtonAction("#handleSubmitButton", "handleSubmit");
            setupButtonAction("#handleUserButton", "handleUser");
            setupButtonAction("#handleBackButton", "handleBack");
            setupButtonAction("#handleInscriptionButton", "handleInscription");
            setupButtonAction("#logoutButton", "logout");
        }
        setupListeners();
    }

    @Override
    public Object notify(String event) throws IOException {
        if (createAttendeeScreenController != null) {
            switch (event) {
                case "handleAttendeeCreate":
                    createAttendeeScreenController.createAttendee();
                    break;
                case "handleUser"
                , "handleSubEvent"
                , "handleBack"
                , "handleSession"
                , "handleInscription"
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

    private void loadScreenForEvent(String event) {
        String fxmlFile = getFxmlPathForEvent(event);

        loadScreenWithTask(() -> {
            try {
                createAttendeeScreenController.genericButton(fxmlFile, screenPane, facade, null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private String getFxmlPathForEvent(String event) {
        return switch (event) {
            case "handleUser" -> "/fxml/userScreen.fxml";
            case "handleInscription" -> "/fxml/attendeeScreen.fxml";
            case "handleSubEvent" -> "/fxml/subEventScreen.fxml";
            case "handleBack" -> "/fxml/attendeeScreen.fxml";
            case "handleSession" -> "/fxml/sessionScreen.fxml";
            case "handleEvent" -> "/fxml/eventScreen.fxml";
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
        createAttendeeScreenController.loadScreen("Carregando", () -> {
            try {
                task.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, screenPane);
    }

    private void setupListeners() {
        screenPane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    notify("handleAttendeeCreate");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
