package br.upe.controller.fx.mediator.subevent;

import br.upe.controller.fx.mediator.Mediator;
import br.upe.controller.fx.screen.subevent.SubEventScreenController;
import br.upe.facade.FacadeInterface;
import br.upe.utils.CustomRuntimeException;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class SubEventMediator extends Mediator {
    private final SubEventScreenController subEventScreenController;
    private static final String HANDLE_SUB_EVENT = "handleSubEvent";
    private static final String HANDLE_SESSION = "handleSession";
    private static final String HANDLE_EVENT = "handleEvent";
    private static final String HANDLE_SUBMIT = "handleSubmit";
    private static final String HANDLE_USER = "handleUser";
    private static final String HANDLE_BACK = "handleBack";
    private static final String HANDLE_INSCRIPTION = "handleInscription";
    private static final String HANDLE_CREATESUBEVENT = "handleCreateSubEvent";
    private static final String HANDLE_UPDATESUBEVENT = "handleUpdateSubEvent";


    private String subEventId;

    public SubEventMediator(SubEventScreenController subEventController, FacadeInterface facade, AnchorPane screenPane, Label errorUpdtLabel) {
        super(facade, screenPane, errorUpdtLabel, subEventController);
        this.subEventScreenController = subEventController;
    }

    public void setSubEventId(String subEventId) {
        this.subEventId = subEventId;
    }

    @Override
    public void registerComponents() {
        if (screenPane != null) {
            setupButtonAction("#handleAddButton", HANDLE_CREATESUBEVENT);
            setupButtonAction("#handleEventButton", HANDLE_EVENT);
            setupButtonAction("#handleSubEventButton", HANDLE_SUB_EVENT);
            setupButtonAction("#handleSessionButton", HANDLE_SESSION);
            setupButtonAction("#handleSubmitButton", HANDLE_SUBMIT);
            setupButtonAction("#handleInscriptionButton", HANDLE_INSCRIPTION);
            setupButtonAction("#handleUserButton", HANDLE_USER);
            setupButtonAction("#handleBackButton", HANDLE_BACK);
            setupButtonAction("#logoutButton", "logout");
        }
    }

    @Override
    public Object notify(String event) throws IOException {
        if (subEventScreenController != null) {
            switch (event) {
                case HANDLE_CREATESUBEVENT
                , HANDLE_BACK
                , HANDLE_UPDATESUBEVENT
                , HANDLE_USER
                , HANDLE_EVENT
                , HANDLE_SUB_EVENT
                , HANDLE_SESSION
                , HANDLE_INSCRIPTION
                , HANDLE_SUBMIT:
                    loadScreenForEvent(event);
                    break;

                case "handleDeleteSubEvent":
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

        if (!event.equals(HANDLE_UPDATESUBEVENT)) {
            this.subEventId = null;
        }

        loadScreenWithTask(() -> {
            try {
                subEventScreenController.genericButton(fxmlFile, screenPane, facade, subEventId);
            } catch (IOException e) {
                throw new CustomRuntimeException("Algo deu errado", e);
            }
        });
    }

    private String getFxmlPathForEvent(String event) {
        return switch (event) {
            case HANDLE_SUB_EVENT, HANDLE_BACK -> "/fxml/allSubEventsScreen.fxml";
            case HANDLE_CREATESUBEVENT -> "/fxml/createSubEventScreen.fxml";
            case HANDLE_INSCRIPTION -> "/fxml/attendeeScreen.fxml";
            case HANDLE_UPDATESUBEVENT -> "/fxml/updateSubEventScreen.fxml";
            case HANDLE_EVENT -> "/fxml/allEventsScreen.fxml";
            case HANDLE_SESSION -> "/fxml/allSessionsScreen.fxml";
            case HANDLE_SUBMIT -> "/fxml/submitScreen.fxml";
            case "loginScreen" -> "/fxml/loginScreen.fxml";
            case HANDLE_USER -> "/fxml/userScreen.fxml";
            default -> throw new IllegalArgumentException("Unknown event: " + event);
        };
    }

    private void loadScreenWithTask(Runnable task) {
        assert screenPane != null;
        subEventScreenController.loadScreen("Carregando", () -> {
            try {
                task.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, screenPane);
    }

}
