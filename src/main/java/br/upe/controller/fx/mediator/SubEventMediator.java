package br.upe.controller.fx.mediator;

import br.upe.controller.fx.SubEventScreenController;
import br.upe.facade.FacadeInterface;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class SubEventMediator extends Mediator {
    private final SubEventScreenController subEventScreenController;
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
            setupButtonAction("#handleAddButton", "handleCreateSubEvent");
            setupButtonAction("#handleEventButton", "handleEvent");
            setupButtonAction("#handleSubEventButton", "handleSubEvent");
            setupButtonAction("#handleSessionButton", "handleSession");
            setupButtonAction("#handleSubmitButton", "handleSubmit");
            setupButtonAction("#handleInscriptionButton", "handleInscription");
            setupButtonAction("#handleUserButton", "handleUser");
            setupButtonAction("#logoutButton", "logout");
        }
    }

    @Override
    public Object notify(String event) throws IOException {
        if (subEventScreenController != null) {
            switch (event) {
                case "handleCreateSubEvent"
                , "handleUpdateSubEvent"
                , "handleUser"
                , "handleEvent"
                , "handleSubEvent"
                , "handleSession"
                , "handleInscription"
                , "handleSubmit":
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

        if (!event.equals("handleUpdateSubEvent")) {
            this.subEventId = null;
        }

        loadScreenWithTask(() -> {
            try {
                subEventScreenController.genericButton(fxmlFile, screenPane, facade, subEventId);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private String getFxmlPathForEvent(String event) {
        return switch (event) {
            case "handleCreateSubEvent" -> "/fxml/createSubEventScreen.fxml";
            case "handleInscription" -> "/fxml/attendeeScreen.fxml";
            case "handleUpdateSubEvent" -> "/fxml/updateSubEventScreen.fxml";
            case "handleUser" -> "/fxml/userScreen.fxml";
            case "handleSubEvent" -> "/fxml/subEventScreen.fxml";
            case "handleEvent" -> "/fxml/eventScreen.fxml";
            case "handleSession" -> "/fxml/sessionScreen.fxml";
            case "handleSubmit" -> "/fxml/submitScreen.fxml";
            case "loginScreen" -> "/fxml/loginScreen.fxml";
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
