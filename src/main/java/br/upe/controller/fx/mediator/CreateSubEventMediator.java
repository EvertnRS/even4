package br.upe.controller.fx.mediator;

import br.upe.controller.fx.CreateSubEventScreenController;
import br.upe.facade.FacadeInterface;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class CreateSubEventMediator extends Mediator{
    private final CreateSubEventScreenController createSubEventController;

    public CreateSubEventMediator(CreateSubEventScreenController createSubEventController, FacadeInterface facade, AnchorPane screenPane, Label errorUpdtLabel) {
        super(facade, screenPane, errorUpdtLabel, createSubEventController);
        this.createSubEventController = createSubEventController;
    }

    @Override
    public void registerComponents() {
        if (screenPane != null) {
            setupButtonAction("#createButton", "handleSubEventCreate");
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
        if (createSubEventController != null) {
            switch (event) {
                case "handleSubEventCreate":
                    if(validateInputs()){
                        createSubEventController.createSubEvent();
                    }
                    break;
                case "handleUser":
                    createSubEventController.genericButton("/fxml/userScreen.fxml", screenPane, facade, null);
                    break;
                case "handleSubEvent", "handleBack":
                    createSubEventController.genericButton("/fxml/subEventScreen.fxml", screenPane, facade, null);
                    break;
                case "handleSession":
                    createSubEventController.genericButton("/fxml/sessionScreen.fxml", screenPane, facade, null);
                    break;
                case "handleEvent":
                    createSubEventController.genericButton("/fxml/mainScreen.fxml", screenPane, facade, null);
                    break;
                case "handleSubmit":
                    createSubEventController.genericButton("/fxml/submitScreen.fxml", screenPane, facade, null);
                    break;
                case "logout":
                    createSubEventController.genericButton("/fxml/loginScreen.fxml", screenPane, facade, null);
                    break;
                default:
                    break;
            }
        }
        return null;
    }
}
