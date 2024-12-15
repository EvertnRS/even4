package br.upe.controller.fx.mediator.session;

import br.upe.controller.fx.mediator.Mediator;
import br.upe.controller.fx.screen.session.AllSessionScreenController;
import br.upe.facade.FacadeInterface;
import br.upe.utils.CustomRuntimeException;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class AllSessionMediator extends Mediator {
    private final AllSessionScreenController sessionScreenController;
    private static final String HANDLE_SUB_EVENT = "handleSubEvent";
    private static final String HANDLE_SESSION = "handleSession";
    private static final String HANDLE_EVENT = "handleEvent";
    private static final String HANDLE_SUBMIT = "handleSubmit";
    private static final String HANDLE_USER = "handleUser";
    private static final String HANDLE_OWNER = "handleOwner";
    private static final String HANDLE_INSCRIPTION = "handleInscription";
    private static final String HANDLE_CREATESESSION = "handleCreateSession";
    private static final String HANDLE_UPDATESESSION = "handleUpdateSession";
    private String sessionId;

    public AllSessionMediator(AllSessionScreenController sessionController, FacadeInterface facade, AnchorPane screenPane, Label errorUpdtLabel) {
        super(facade, screenPane, errorUpdtLabel, sessionController);
        this.sessionScreenController = sessionController;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public void registerComponents() {
        if (screenPane != null) {
            setupButtonAction("#handleAddButton", HANDLE_CREATESESSION);
            setupButtonAction("#handleEventButton", HANDLE_EVENT);
            setupButtonAction("#handleSubEventButton", HANDLE_SUB_EVENT);
            setupButtonAction("#handleSessionButton", HANDLE_SESSION);
            setupButtonAction("#handleOwnerButton", HANDLE_OWNER);
            setupButtonAction("#handleSubmitButton", HANDLE_SUBMIT);
            setupButtonAction("#handleInscriptionButton", HANDLE_INSCRIPTION);
            setupButtonAction("#handleUserButton", HANDLE_USER);
            setupButtonAction("#logoutButton", "logout");
        }
    }

    @Override
    public Object notify(String event) throws IOException {
        if (sessionScreenController != null) {
            switch (event) {
                case HANDLE_USER
                , HANDLE_EVENT
                , HANDLE_SUB_EVENT
                , HANDLE_SESSION
                , HANDLE_OWNER
                , HANDLE_INSCRIPTION
                , HANDLE_SUBMIT:
                    loadScreenForEvent(event);
                    break;

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

        if (!event.equals(HANDLE_UPDATESESSION)) {
            this.sessionId = null;
        }

        loadScreenWithTask(() -> {
            try {
                sessionScreenController.genericButton(fxmlFile, screenPane, facade, sessionId);
            } catch (IOException e) {
                throw new CustomRuntimeException("Algo deu errado", e);
            }
        });
    }

    private String getFxmlPathForEvent(String event) {
        return switch (event) {
            case HANDLE_INSCRIPTION -> "/fxml/attendeeScreen.fxml";
            case HANDLE_USER -> "/fxml/userScreen.fxml";
            case HANDLE_SUB_EVENT -> "/fxml/allSubEventsScreen.fxml";
            case HANDLE_EVENT -> "/fxml/allEventsScreen.fxml";
            case HANDLE_SESSION -> "/fxml/allSessionsScreen.fxml";
            case HANDLE_OWNER -> "/fxml/sessionScreen.fxml";
            case HANDLE_SUBMIT -> "/fxml/submitScreen.fxml";
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
                throw new CustomRuntimeException("Algo deu errado", e);
            }
        }, screenPane);
    }

}
