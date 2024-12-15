package br.upe.controller.fx.mediator.attendee;

import br.upe.controller.fx.mediator.Mediator;
import br.upe.controller.fx.screen.attendee.AttendeeScreenController;
import br.upe.facade.FacadeInterface;
import br.upe.utils.CustomRuntimeException;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class AttendeeMediator extends Mediator {
    private final AttendeeScreenController attendeeScreenController;
    private String attendeeId;
    private static final String HANDLE_CREATE_ATTENDEE = "handleCreateAttendee";
    private static final String HANDLE_EVENT = "handleEvent";
    private static final String HANDLE_SUB_EVENT = "handleSubEvent";
    private static final String HANDLE_SESSION = "handleSession";
    private static final String HANDLE_SUBMIT = "handleSubmit";
    private static final String HANDLE_USER = "handleUser";
    private static final String HANDLE_INSCRIPTION = "handleInscription";
    private static final String HANDLE_CERTIFICATE = "handleCertificate";


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
            setupButtonAction("#handleAddButton", HANDLE_CREATE_ATTENDEE);
            setupButtonAction("#handleEventButton", HANDLE_EVENT);
            setupButtonAction("#handleSubEventButton", HANDLE_SUB_EVENT);
            setupButtonAction("#handleSessionButton", HANDLE_SESSION);
            setupButtonAction("#handleSubmitButton", HANDLE_SUBMIT);
            setupButtonAction("#handleUserButton", HANDLE_USER);
            setupButtonAction("#handleInscriptionButton", HANDLE_INSCRIPTION);
            setupButtonAction("#logoutButton", "logout");
        }
    }

    @Override
    public Object notify(String event) throws IOException {
        if (attendeeScreenController != null) {
            switch (event) {
                case HANDLE_CREATE_ATTENDEE
                , HANDLE_EVENT
                , HANDLE_INSCRIPTION
                , HANDLE_USER
                , HANDLE_SESSION
                , HANDLE_SUB_EVENT
                , HANDLE_CERTIFICATE
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

        if (!event.equals(HANDLE_CERTIFICATE)) {
            this.attendeeId = null;
        }

        loadScreenWithTask(() -> {
            try {
                attendeeScreenController.genericButton(fxmlFile, screenPane, facade, attendeeId);
            } catch (IOException e) {
                throw new CustomRuntimeException("Algo deu errado", e);
            }
        });
    }

    private String getFxmlPathForEvent(String event) {
        return switch (event) {
            case HANDLE_CREATE_ATTENDEE -> "/fxml/createAttendeeScreen.fxml";
            case HANDLE_INSCRIPTION -> "/fxml/attendeeScreen.fxml";
            case HANDLE_EVENT -> "/fxml/allEventsScreen.fxml";
            case HANDLE_SUB_EVENT -> "/fxml/allSubEventsScreen.fxml";
            case HANDLE_USER -> "/fxml/userScreen.fxml";
            case HANDLE_SESSION -> "/fxml/allSessionsScreen.fxml";
            case HANDLE_SUBMIT -> "/fxml/submitScreen.fxml";
            case "loginScreen" -> "/fxml/loginScreen.fxml";
            case HANDLE_CERTIFICATE -> "/fxml/certificateScreen.fxml";
            default -> throw new IllegalArgumentException("Unknown event: " + event);
        };
    }

    private void loadScreenWithTask(Runnable task) {
        assert screenPane != null;
        attendeeScreenController.loadScreen("Carregando", () -> {
            try {
                task.run();
            } catch (Exception e) {
                throw new CustomRuntimeException("Algo deu errado", e);
            }
        }, screenPane);
    }
}
