package br.upe.controller.fx.mediator.attendee;

import br.upe.controller.fx.mediator.Mediator;
import br.upe.controller.fx.screen.attendee.CreateAttendeeScreenController;
import br.upe.facade.FacadeInterface;
import br.upe.utils.CustomRuntimeException;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class CreateAttendeeMediator extends Mediator {
    private final CreateAttendeeScreenController createAttendeeScreenController;
    private static final String HANDLE_ATTENDEE_CREATE = "handleAttendeeCreate";
    private static final String HANDLE_EVENT = "handleEvent";
    private static final String HANDLE_SUB_EVENT = "handleSubEvent";
    private static final String HANDLE_SESSION = "handleSession";
    private static final String HANDLE_SUBMIT = "handleSubmit";
    private static final String HANDLE_USER = "handleUser";
    private static final String HANDLE_BACK = "handleBack";
    private static final String HANDLE_INSCRIPTION = "handleInscription";


    public CreateAttendeeMediator(CreateAttendeeScreenController createAttendeeScreenController, FacadeInterface facade, AnchorPane screenPane, Label errorUpdtLabel) {
        super(facade, screenPane, errorUpdtLabel, createAttendeeScreenController);
        this.createAttendeeScreenController = createAttendeeScreenController;
    }

    @Override
    public void registerComponents() {
        if (screenPane != null) {
            setupButtonAction("#createButton", HANDLE_ATTENDEE_CREATE);
            setupButtonAction("#handleEventButton", HANDLE_EVENT);
            setupButtonAction("#handleSubEventButton", HANDLE_SUB_EVENT);
            setupButtonAction("#handleSessionButton", HANDLE_SESSION);
            setupButtonAction("#handleSubmitButton", HANDLE_SUBMIT);
            setupButtonAction("#handleUserButton", HANDLE_USER);
            setupButtonAction("#handleBackButton", HANDLE_BACK);
            setupButtonAction("#handleInscriptionButton", HANDLE_INSCRIPTION);
            setupButtonAction("#logoutButton", "logout");
        }
        setupListeners();
    }

    @Override
    public Object notify(String event) throws IOException {
        if (createAttendeeScreenController != null) {
            switch (event) {
                case HANDLE_ATTENDEE_CREATE:
                    createAttendeeScreenController.createAttendee();
                    break;
                case HANDLE_USER
                , HANDLE_SUB_EVENT
                , HANDLE_BACK
                , HANDLE_SESSION
                , HANDLE_INSCRIPTION
                , HANDLE_EVENT
                , HANDLE_SUBMIT:
                    loadScreenForEvent(event);
                    break;
                case "logout":
                    logout();
                    break;
                default:
                    throw new IllegalArgumentException("Ação não reconhecida: " + event);
            }
        }
        return null;
    }

    private void loadScreenForEvent(String event) {
        String fxmlFile = getFxmlPathForEvent(event);

        loadScreenWithTask(() -> {
            try {
                createAttendeeScreenController.genericButton(fxmlFile, screenPane, facade, null);
            } catch (IOException e) {
                throw new CustomRuntimeException("Algo deu errado", e);
            }
        });
    }

    private String getFxmlPathForEvent(String event) {
        return switch (event) {
            case HANDLE_USER -> "/fxml/userScreen.fxml";
            case HANDLE_INSCRIPTION, HANDLE_BACK -> "/fxml/attendeeScreen.fxml";
            case HANDLE_SUB_EVENT -> "/fxml/allSubEventsScreen.fxml";
            case HANDLE_SESSION -> "/fxml/allSessionsScreen.fxml";
            case HANDLE_EVENT -> "/fxml/allEventsScreen.fxml";
            case HANDLE_SUBMIT -> "/fxml/submitScreen.fxml";
            case "loginScreen" -> "/fxml/loginScreen.fxml";
            default -> throw new IllegalArgumentException("Unknown event: " + event);
        };
    }

    private void logout() {
        facade = null;
        loadScreenForEvent("loginScreen");
    }

    private void loadScreenWithTask(Runnable task) {
        assert screenPane != null;
        createAttendeeScreenController.loadScreen("Carregando", () -> {
            try {
                task.run();
            } catch (Exception e) {
                throw new CustomRuntimeException("Algo deu errado", e);
            }
        }, screenPane);
    }

    private void setupListeners() {
        screenPane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    notify(HANDLE_ATTENDEE_CREATE);
                } catch (IOException e) {
                    throw new CustomRuntimeException("Listener desconfigurado", e);
                }
            }
        });
    }
}
