package br.upe.controller.fx.mediator;

import br.upe.controller.fx.UpdateEventScreenController;
import br.upe.facade.FacadeInterface;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class UpdateEventMediator extends Mediator {
    private final UpdateEventScreenController updateEventController;

    public UpdateEventMediator(UpdateEventScreenController updateEventController, FacadeInterface facade, AnchorPane screenPane, Label errorUpdtLabel) {
        super(facade, screenPane, errorUpdtLabel, updateEventController);
        this.updateEventController = updateEventController;
    }

    @Override
    public void registerComponents() {
        if (screenPane != null) {
            setupButtonAction("#updateButton", "handleUpdate");
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
        if (updateEventController != null) {
            switch (event) {
                case "handleUpdate":
                    if(validateInputs()){
                        updateEventController.updateEvent();
                    }
                    break;
                case "handleUser":
                    updateEventController.genericButton("/fxml/userScreen.fxml", screenPane, facade, null);
                    break;
                case "handleEvent", "handleBack":
                    updateEventController.genericButton("/fxml/mainScreen.fxml", screenPane, facade, null);
                    break;
                case "handleSession":
                    updateEventController.genericButton("/fxml/sessionScreen.fxml", screenPane, facade, null);
                    break;
                case "handleSubEvent":
                    updateEventController.genericButton("/fxml/subEventScreen.fxml", screenPane, facade, null);
                    break;
                case "handleSubmit":
                    updateEventController.genericButton("/fxml/submitScreen.fxml", screenPane, facade, null);
                    break;
                case "logout":
                    updateEventController.genericButton("/fxml/loginScreen.fxml", screenPane, facade, null);
                    break;
                default:
                    break;
            }
        }
        return null;
    }
}
