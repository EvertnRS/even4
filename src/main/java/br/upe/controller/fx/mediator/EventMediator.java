package br.upe.controller.fx.mediator;

import br.upe.controller.fx.EventScreenController;
import br.upe.facade.FacadeInterface;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class EventMediator extends Mediator {
    private final EventScreenController eventScreenController;
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
            setupButtonAction("#handleAddButton", "handleCreateEvent");
            setupButtonAction("#handleInscriptionButton", "handleInscription");
            setupButtonAction("#handleEventButton", "handleEvent");
            setupButtonAction("#handleSubEventButton", "handleSubEvent");
            setupButtonAction("#handleSessionButton", "handleSession");
            setupButtonAction("#handleSubmitButton", "handleSubmit");
            setupButtonAction("#handleUserButton", "handleUser");
            setupButtonAction("#logoutButton", "logout");
        }
    }

    @Override
    public Object notify(String event) throws IOException {
        if (eventScreenController != null) {
            switch (event) {
                case "handleCreateEvent"
                , "handleUpdateEvent"
                , "handleEvent"
                , "handleEventArticles"
                , "handleUser"
                , "handleSession"
                , "handleInscription"
                , "handleSubEvent"
                , "handleSubmit":
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

    private void loadScreenForEvent(String event){
        String fxmlFile = getFxmlPathForEvent(event);

        if (!event.equals("handleUpdateEvent") && !event.equals("handleEventArticles")) {
            this.eventId = null;
        }

        loadScreenWithTask(() -> {
            try {
                eventScreenController.genericButton(fxmlFile, screenPane, facade, eventId);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private String getFxmlPathForEvent(String event) {
        return switch (event) {
            case "handleCreateEvent" -> "/fxml/createEventScreen.fxml";
            case "handleUpdateEvent" -> "/fxml/updateEventScreen.fxml";
            case "handleEventArticles" -> "/fxml/eventArticleScreen.fxml";
            case "handleEvent" -> "/fxml/eventScreen.fxml";
            case "handleUser" -> "/fxml/userScreen.fxml";
            case "handleSession" -> "/fxml/sessionScreen.fxml";
            case "handleInscription" -> "/fxml/attendeeScreen.fxml";
            case "handleSubEvent" -> "/fxml/subEventScreen.fxml";
            case "handleSubmit" -> "/fxml/submitScreen.fxml";
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
