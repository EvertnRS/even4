package br.upe.controller.fx.mediator;

import br.upe.controller.fx.EventScreenController;
import br.upe.facade.FacadeInterface;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class EventMediator extends Mediator {
    private final EventScreenController eventController;
    private String eventId;

    public EventMediator(EventScreenController eventController, FacadeInterface facade, AnchorPane screenPane, Label errorUpdtLabel) {
        super(facade, screenPane, errorUpdtLabel, eventController);
        this.eventController = eventController;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    @Override
    public void registerComponents() {
        if (screenPane != null) {
            setupButtonAction("#handleAddButton", "handleCreateEvent");
            setupButtonAction("#handleSubEventButton", "handleSubEvent");
            setupButtonAction("#handleSessionButton", "handleSession");
            setupButtonAction("#handleSubmitButton", "handleSubmit");
            setupButtonAction("#handleUserButton", "handleUser");
            setupButtonAction("#logoutButton", "logout");
        }
    }

    @Override
    public Object notify(String event) throws IOException {
        if (eventController != null) {
            switch (event) {
                case "handleCreateEvent":
                    eventController.genericButton("/fxml/createEventScreen.fxml", screenPane, facade, null);
                    break;
                case "handleUpdateEvent":
                    eventController.genericButton("/fxml/updateEventScreen.fxml", screenPane, facade, eventId);
                    break;
                case "handleDeleteEvent":
                    return deleteButtonAlert();
                case "handleUser":
                    eventController.genericButton("/fxml/userScreen.fxml", screenPane, facade, null);
                    break;
                case "handleEvent":
                    eventController.genericButton("/fxml/mainScreen.fxml", screenPane, facade, null);
                    break;
                case "handleSession":
                    eventController.genericButton("/fxml/sessionScreen.fxml", screenPane, facade, null);
                    break;
                case "handleSubEvent":
                    eventController.genericButton("/fxml/subEventScreen.fxml", screenPane, facade, null);
                    break;
                case "handleSubmit":
                    eventController.genericButton("/fxml/submitScreen.fxml", screenPane, facade, null);
                    break;
                case "logout":
                    eventController.genericButton("/fxml/loginScreen.fxml", screenPane, facade, null);
                    break;
                default:
                    break;
            }
        }
        return null;
    }
}
