package br.upe.controller.fx.mediator;

import br.upe.controller.fx.SubEventScreenController;
import br.upe.facade.FacadeInterface;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class SubEventMediator extends Mediator {
    private final SubEventScreenController subEventController;
    private String subEventId;

    public SubEventMediator(SubEventScreenController subEventController, FacadeInterface facade, AnchorPane screenPane, Label errorUpdtLabel) {
        super(facade, screenPane, errorUpdtLabel, subEventController);
        this.subEventController = subEventController;
    }

    public void setSubEventId(String subEventId) {
        this.subEventId = subEventId;
    }

    @Override
    public void registerComponents() {
        if (screenPane != null) {
            setupButtonAction("#handleAddButton", "handleCreateSubEvent");
            setupButtonAction("#handleEventButton", "handleEvent");
            setupButtonAction("#handleSessionButton", "handleSession");
            setupButtonAction("#handleSubmitButton", "handleSubmit");
            setupButtonAction("#handleUserButton", "handleUser");
            setupButtonAction("#logoutButton", "logout");
        }
    }

    @Override
    public Object notify(String event) throws IOException {
        if (subEventController != null) {
            switch (event) {
                case "handleCreateSubEvent":
                    subEventController.genericButton("/fxml/createSubEventScreen.fxml", screenPane, facade, null);
                    break;
                case "handleUpdateSubEvent":
                    subEventController.genericButton("/fxml/updateEventScreen.fxml", screenPane, facade, subEventId);
                    break;
                case "handleDeleteSubEvent":
                    return deleteButtonAlert();
                case "handleUser":
                    subEventController.genericButton("/fxml/userScreen.fxml", screenPane, facade, null);
                    break;
                case "handleEvent":
                    subEventController.genericButton("/fxml/mainScreen.fxml", screenPane, facade, null);
                    break;
                case "handleSession":
                    subEventController.genericButton("/fxml/sessionScreen.fxml", screenPane, facade, null);
                    break;
                case "handleSubEvent":
                    subEventController.genericButton("/fxml/subEventScreen.fxml", screenPane, facade, null);
                    break;
                case "handleSubmit":
                    subEventController.genericButton("/fxml/submitScreen.fxml", screenPane, facade, null);
                    break;
                case "logout":
                    subEventController.genericButton("/fxml/loginScreen.fxml", screenPane, facade, null);
                    break;
                default:
                    break;
            }
        }
        return null;
    }
}
