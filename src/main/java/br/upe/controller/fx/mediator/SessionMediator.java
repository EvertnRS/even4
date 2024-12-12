package br.upe.controller.fx.mediator;

import br.upe.controller.fx.SessionScreenController;
import br.upe.facade.FacadeInterface;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class SessionMediator extends Mediator {
    private final SessionScreenController sessionScreenController;
    private String sessionId;

    public SessionMediator(SessionScreenController sessionController, FacadeInterface facade, AnchorPane screenPane, Label errorUpdtLabel) {
        super(facade, screenPane, errorUpdtLabel, sessionController);
        this.sessionScreenController = sessionController;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public void registerComponents() {
        if (screenPane != null) {
            setupButtonAction("#handleAddButton", "handleCreateSession");
            setupButtonAction("#handleEventButton", "handleEvent");
            setupButtonAction("#handleSubEventButton", "handleSubEvent");
            setupButtonAction("#handleSessionButton", "handleSession");
            setupButtonAction("#handleSubmitButton", "handleSubmit");
            setupButtonAction("#handleInscriptionButton", "handleInscription");
            setupButtonAction("#handleUserButton", "handleUser");
            setupButtonAction("#logoutButton", "logout");
        }
    }

    @Override
    public Object notify(String event) throws IOException {
        if (sessionScreenController != null) {
            switch (event) {
                case "handleCreateSession"
                , "handleUpdateSession"
                , "handleUser"
                , "handleEvent"
                , "handleSubEvent"
                , "handleSession"
                , "handleInscription"
                , "handleSubmit":
                    loadScreenForEvent(event);
                    break;

                case "handleDeleteSession":
                    return deleteButtonAlert();

                case "logout":
                    facade = null;
                    loadScreenForEvent("loginScreen");
                    break;

                default:
                    throw new IllegalArgumentException("Ação não reconhecida: " + event);
            }
        }
        return null;
    }

    private void loadScreenForEvent(String event) {
        String fxmlFile = getFxmlPathForEvent(event);

        if (!event.equals("handleUpdateSession")) {
            this.sessionId = null;
        }

        loadScreenWithTask(() -> {
            try {
                sessionScreenController.genericButton(fxmlFile, screenPane, facade, sessionId);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private String getFxmlPathForEvent(String event) {
        return switch (event) {
            case "handleCreateSession" -> "/fxml/createSessionScreen.fxml";
            case "handleInscription" -> "/fxml/attendeeScreen.fxml";
            case "handleUpdateSession" -> "/fxml/updateSessionScreen.fxml";
            case "handleUser" -> "/fxml/userScreen.fxml";
            case "handleSubEvent" -> "/fxml/subEventScreen.fxml";
            case "handleEvent" -> "/fxml/eventScreen.fxml";
            case "handleSession" -> "/fxml/sessionScreen.fxml";
            case "handleSubmit" -> "/fxml/submitScreen.fxml";
            case "loginScreen" -> "/fxml/loginScreen.fxml";
            default -> throw new IllegalArgumentException("Unknown event: " + event);
        };
    }

    private void loadScreenWithTask(Runnable task) {
        assert screenPane != null;
        sessionScreenController.loadScreen("Carregando", () -> {
            try {
                task.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, screenPane);
    }

}
