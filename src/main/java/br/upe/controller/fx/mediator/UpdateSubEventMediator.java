package br.upe.controller.fx.mediator;

import br.upe.controller.fx.UpdateSubEventScreenController;
import br.upe.facade.FacadeInterface;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class UpdateSubEventMediator extends Mediator{
    private final UpdateSubEventScreenController updateSubEventController;

    public UpdateSubEventMediator(UpdateSubEventScreenController updateSubEventController, FacadeInterface facade, AnchorPane screenPane, Label errorUpdtLabel) {
        super(facade, screenPane, errorUpdtLabel, updateSubEventController);
        this.updateSubEventController = updateSubEventController;
    }

    @Override
    public void registerComponents() {
        if (screenPane != null) {
            setupButtonAction("#updateButton", "handleSubEventUpdate");
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
        if (updateSubEventController != null) {
            switch (event) {
                case "handleSubEventUpdate":
                    if(validateInputs()){
                        updateSubEventController.updateSubEvent();
                    }
                    break;
                case "handleUser":
                    updateSubEventController.genericButton("/fxml/userScreen.fxml", screenPane, facade, null);
                    break;
                case "handleSubEvent", "handleBack":
                    updateSubEventController.genericButton("/fxml/subEventScreen.fxml", screenPane, facade, null);
                    break;
                case "handleSession":
                    updateSubEventController.genericButton("/fxml/sessionScreen.fxml", screenPane, facade, null);
                    break;
                case "handleEvent":
                    updateSubEventController.genericButton("/fxml/mainScreen.fxml", screenPane, facade, null);
                    break;
                case "handleSubmit":
                    updateSubEventController.genericButton("/fxml/submitScreen.fxml", screenPane, facade, null);
                    break;
                case "logout":
                    updateSubEventController.genericButton("/fxml/loginScreen.fxml", screenPane, facade, null);
                    break;
                default:
                    break;
            }
        }
        return null;
    }
}
