package br.upe.controller.fx.mediator.event;

import br.upe.controller.fx.mediator.Mediator;
import br.upe.controller.fx.screen.event.EventScreenController;
import br.upe.facade.FacadeInterface;
import br.upe.utils.CustomRuntimeException;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class EventMediator extends Mediator {
    private final EventScreenController eventScreenController;
    private static final String HANDLE_BACK = "handleBack";
    private static final String HANDLE_SUB_EVENT = "handleSubEvent";
    private static final String HANDLE_SESSION = "handleSession";
    private static final String HANDLE_EVENT = "handleEvent";
    private static final String HANDLE_SUBMIT = "handleSubmit";
    private static final String HANDLE_USER = "handleUser";
    private static final String HANDLE_INSCRIPTION = "handleInscription";
    private static final String HANDLE_CREATEEVENT = "handleCreateEvent";
    private static final String HANDLE_UPDATEEVENT = "handleUpdateEvent";
    private static final String HANDLE_EVENTARTICLE = "handleEventArticles";

    private String eventId;

    public EventMediator(EventScreenController eventController, FacadeInterface facade, AnchorPane screenPane, Label errorUpdtLabel) {
        super(facade, screenPane, errorUpdtLabel, eventController);
        this.eventScreenController = eventController;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    @Override
    public void registerComponents() {
        if (screenPane != null) {
            setupButtonAction("#handleAddButton", HANDLE_CREATEEVENT);
            setupButtonAction("#handleInscriptionButton", HANDLE_INSCRIPTION);
            setupButtonAction("#handleEventButton", HANDLE_EVENT);
            setupButtonAction("#handleSubEventButton", HANDLE_SUB_EVENT);
            setupButtonAction("#handleSessionButton", HANDLE_SESSION);
            setupButtonAction("#handleSubmitButton", HANDLE_SUBMIT);
            setupButtonAction("#handleBackButton", HANDLE_BACK);
            setupButtonAction("#handleUserButton", HANDLE_USER);
            setupButtonAction("#logoutButton", "logout");
        }
    }

    @Override
    public Object notify(String event) throws IOException {
        if (eventScreenController != null) {
            switch (event) {
                case HANDLE_CREATEEVENT
                , HANDLE_UPDATEEVENT
                , HANDLE_EVENT
                , HANDLE_EVENTARTICLE
                , HANDLE_USER
                , HANDLE_BACK
                , HANDLE_SESSION
                , HANDLE_INSCRIPTION
                , HANDLE_SUB_EVENT
                , HANDLE_SUBMIT:
                    loadScreenForEvent(event);
                    break;

                case "handleDeleteEvent":
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

        if (!event.equals(HANDLE_UPDATEEVENT) && !event.equals(HANDLE_EVENTARTICLE)) {
            this.eventId = null;
        }

        loadScreenWithTask(() -> {
            try {
                eventScreenController.genericButton(fxmlFile, screenPane, facade, eventId);
            } catch (IOException e) {
                throw new CustomRuntimeException("Algo deu errado", e);
            }
        });
    }

    private String getFxmlPathForEvent(String event) {
        return switch (event) {
            case HANDLE_BACK, HANDLE_EVENT -> "/fxml/allEventsScreen.fxml";
            case HANDLE_CREATEEVENT -> "/fxml/createEventScreen.fxml";
            case HANDLE_UPDATEEVENT -> "/fxml/updateEventScreen.fxml";
            case HANDLE_EVENTARTICLE -> "/fxml/eventArticleScreen.fxml";
            case HANDLE_USER -> "/fxml/userScreen.fxml";
            case HANDLE_SESSION -> "/fxml/allSessionsScreen.fxml";
            case HANDLE_INSCRIPTION -> "/fxml/attendeeScreen.fxml";
            case HANDLE_SUB_EVENT -> "/fxml/allSubEventsScreen.fxml";
            case HANDLE_SUBMIT -> "/fxml/submitScreen.fxml";
            case "loginScreen" -> "/fxml/loginScreen.fxml";
            default -> throw new IllegalArgumentException("Unknown event: " + event);
        };
    }


    private void loadScreenWithTask(Runnable task) {
        assert screenPane != null;
        eventScreenController.loadScreen("Carregando", () -> {
            try {
                task.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, screenPane);
    }

}
