package br.upe.controller.fx.mediator;

import br.upe.controller.fx.AttendeeScreenController;
import br.upe.facade.FacadeInterface;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class AttendeeMediator extends Mediator{
    private final AttendeeScreenController attendeeScreenController;
    private String attendeeId;

    public AttendeeMediator(AttendeeScreenController attendeeController, FacadeInterface facade, AnchorPane screenPane, Label errorUpdtLabel) {
        super(facade, screenPane, errorUpdtLabel, attendeeController);
        this.attendeeScreenController = attendeeController;
    }

    public void setAttendeeId(String attendeeId) {
        this.attendeeId = attendeeId;
    }

    @Override
    public void registerComponents() {
        if (screenPane != null) {
            setupButtonAction("#handleAddButton", "handleCreateAttendee");
            setupButtonAction("#handleEventButton", "handleEvent");
            setupButtonAction("#handleSubEventButton", "handleSubEvent");
            setupButtonAction("#handleSessionButton", "handleSession");
            setupButtonAction("#handleSubmitButton", "handleSubmit");
            setupButtonAction("#handleUserButton", "handleUser");
            setupButtonAction("#handleInscriptionButton", "handleInscription");
            setupButtonAction("#logoutButton", "logout");
        }
    }

    @Override
    public Object notify(String event) throws IOException {
        if (attendeeScreenController != null) {
            switch (event) {
                case "handleCreateAttendee"
                , "handleEvent"
                , "handleInscription"
                , "handleUser"
                , "handleSession"
                , "handleSubEvent"
                , "handleCertificate"
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

        if (!event.equals("handleCertificate")) {
            this.attendeeId = null;
        }

        loadScreenWithTask(() -> {
            try {
                attendeeScreenController.genericButton(fxmlFile, screenPane, facade, attendeeId);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private String getFxmlPathForEvent(String event) {
        return switch (event) {
            case "handleCreateAttendee" -> "/fxml/createAttendeeScreen.fxml";
            case "handleInscription" -> "/fxml/attendeeScreen.fxml";
            case "handleEvent" -> "/fxml/eventScreen.fxml";
            case "handleSubEvent" -> "/fxml/subEventScreen.fxml";
            case "handleUser" -> "/fxml/userScreen.fxml";
            case "handleSession" -> "/fxml/sessionScreen.fxml";
            case "handleSubmit" -> "/fxml/submitScreen.fxml";
            case "loginScreen" -> "/fxml/loginScreen.fxml";
            case "handleCertificate" -> "/fxml/certificateScreen.fxml";
            default -> throw new IllegalArgumentException("Unknown event: " + event);
        };
    }

    private void loadScreenWithTask(Runnable task) {
        assert screenPane != null;
        attendeeScreenController.loadScreen("Carregando", () -> {
            try {
                task.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, screenPane);
    }
}
