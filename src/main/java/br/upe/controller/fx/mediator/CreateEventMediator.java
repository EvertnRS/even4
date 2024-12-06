package br.upe.controller.fx.mediator;

import br.upe.controller.fx.CreateEventScreenController;
import br.upe.facade.FacadeInterface;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class CreateEventMediator extends Mediator {
    private final CreateEventScreenController createEventController;

    public CreateEventMediator(CreateEventScreenController createEventController, FacadeInterface facade, AnchorPane screenPane, Label errorUpdtLabel) {
        super(facade, screenPane, errorUpdtLabel, createEventController);
        this.createEventController = createEventController;
    }

    @Override
    public void registerComponents() {
        if (screenPane != null) {
            setupButtonAction("#createButton", "handleEventCreate");
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
        if (createEventController != null) {
            switch (event) {
                case "handleEventCreate":
                    if(validateInputs()){
                        createEventController.createEvent();
                    }
                    break;
                case "handleUser":
                    createEventController.genericButton("/fxml/userScreen.fxml", screenPane, facade, null);
                    break;
                case "handleEvent", "handleBack":
                    createEventController.genericButton("/fxml/mainScreen.fxml", screenPane, facade, null);
                    break;
                case "handleSession":
                    createEventController.genericButton("/fxml/sessionScreen.fxml", screenPane, facade, null);
                    break;
                case "handleSubEvent":
                    createEventController.genericButton("/fxml/subEventScreen.fxml", screenPane, facade, null);
                    break;
                case "handleSubmit":
                    createEventController.genericButton("/fxml/submitScreen.fxml", screenPane, facade, null);
                    break;
                case "logout":
                    createEventController.genericButton("/fxml/loginScreen.fxml", screenPane, facade, null);
                    break;
                default:
                    break;
            }
        }
        return null;
    }
}
