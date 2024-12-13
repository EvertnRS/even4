package br.upe.controller.fx.mediator;

import br.upe.controller.fx.UpdateSubEventScreenController;
import br.upe.facade.FacadeInterface;
import br.upe.utils.CustomRuntimeException;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.UUID;

public class UpdateSubEventMediator extends Mediator {
    private final UpdateSubEventScreenController updateScreenSubEventController;
    private UUID currentId;
    private static final String HANDLE_SUB_EVENT = "handleSubEvent";
    private static final String HANDLE_SESSION = "handleSession";
    private static final String HANDLE_EVENT = "handleEvent";
    private static final String HANDLE_SUBMIT = "handleSubmit";
    private static final String HANDLE_USER = "handleUser";
    private static final String HANDLE_INSCRIPTION = "handleInscription";
    private static final String HANDLE_BACK = "handleBack";
    private static final String HANDLE_SUB_EVENT_UPDATE = "handleSubEventUpdate";


    private TextField nameTextField;
    private DatePicker datePicker;
    private TextField locationTextField;
    private TextField descriptionTextField;

    public UpdateSubEventMediator(UpdateSubEventScreenController updateScreenSubEventController, FacadeInterface facade, AnchorPane screenPane, Label errorUpdtLabel) {
        super(facade, screenPane, errorUpdtLabel, updateScreenSubEventController);
        this.updateScreenSubEventController = updateScreenSubEventController;

    }

    public void setComponents(TextField nameTextField, DatePicker datePicker, TextField locationTextField, TextField descriptionTextField) {
        this.nameTextField = nameTextField;
        this.datePicker = datePicker;
        this.locationTextField = locationTextField;
        this.descriptionTextField = descriptionTextField;

        if (updateScreenSubEventController != null) {
            setupListeners();
        }
    }

    @Override
    public void registerComponents() {
        if (screenPane != null) {
            setupButtonAction("#updateButton", HANDLE_SUB_EVENT_UPDATE);
            setupButtonAction("#handleEventButton", HANDLE_EVENT);
            setupButtonAction("#handleSubEventButton", HANDLE_SUB_EVENT);
            setupButtonAction("#handleSessionButton", HANDLE_SESSION);
            setupButtonAction("#handleSubmitButton", HANDLE_SUBMIT);
            setupButtonAction("#handleUserButton", HANDLE_USER);
            setupButtonAction("#handleBackButton", HANDLE_BACK);
            setupButtonAction("#handleInscriptionButton", HANDLE_INSCRIPTION);
            setupButtonAction("#logoutButton", "logout");
        }
    }

    @Override
    public Object notify(String event) throws IOException {
        if (updateScreenSubEventController != null) {
            switch (event) {
                case HANDLE_SUB_EVENT_UPDATE:
                    handleSubEventUpdate();
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

    private void handleSubEventUpdate() throws IOException {
        if (validateInputs(currentId, facade.getAllSubEvent())) {
            this.currentId = updateScreenSubEventController.getId();
            updateScreenSubEventController.updateSubEvent();
        }
    }

    private void loadScreenForEvent(String event) {
        String fxmlFile = getFxmlPathForEvent(event);

        loadScreenWithTask(() -> {
            try {
                updateScreenSubEventController.genericButton(fxmlFile, screenPane, facade, null);
            } catch (IOException e) {
                throw new CustomRuntimeException("Algo deu errado", e);
            }
        });
    }

    private String getFxmlPathForEvent(String event) {
        return switch (event) {
            case HANDLE_SUB_EVENT_UPDATE -> "/fxml/updateSubEventScreen.fxml";

            case HANDLE_USER -> "/fxml/userScreen.fxml";
            case HANDLE_SUB_EVENT, HANDLE_BACK -> "/fxml/subEventScreen.fxml";
            case HANDLE_SESSION -> "/fxml/sessionScreen.fxml";
            case HANDLE_EVENT -> "/fxml/eventScreen.fxml";
            case HANDLE_SUBMIT -> "/fxml/submitScreen.fxml";
            case HANDLE_INSCRIPTION -> "/fxml/attendeeScreen.fxml";
            case "loginScreen" -> "/fxml/loginScreen.fxml";
            default -> throw new IllegalArgumentException("Unknown event: " + event);
        };
    }

    private void logout() throws IOException {
        facade = null;
        loadScreenForEvent("loginScreen");
    }

    private void loadScreenWithTask(Runnable task) {
        assert screenPane != null;
        updateScreenSubEventController.loadScreen("Carregando", () -> {
            try {
                task.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, screenPane);
    }

    private void setupListeners() {
        screenPane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    notify(HANDLE_SUB_EVENT_UPDATE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        configureNavigation(nameTextField, locationTextField, datePicker);
        configureNavigation(descriptionTextField, datePicker, locationTextField);
        configureNavigation(locationTextField, descriptionTextField, nameTextField);

        datePicker.addEventFilter(KeyEvent.KEY_PRESSED, event -> handleKeyNavigation(event, nameTextField, descriptionTextField));
    }

    private void configureNavigation(Node currentField, Node previousField, Node nextField) {
        currentField.setOnKeyPressed(event -> handleKeyNavigation(event, previousField, nextField));
    }

    private void handleKeyNavigation(KeyEvent event, Node previousField, Node nextField) {
        if (event.getCode() == KeyCode.DOWN || event.getCode() == KeyCode.TAB) {
            nextField.requestFocus();
            event.consume();
        }
        if (event.getCode() == KeyCode.UP || (event.getCode() == KeyCode.TAB && event.isShiftDown())) {
            previousField.requestFocus();
            event.consume();
        }
    }

}
