package br.upe.controller.fx.mediator.event;

import br.upe.controller.fx.mediator.Mediator;
import br.upe.controller.fx.screen.event.CreateEventScreenController;
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

public class CreateEventMediator extends Mediator {
    private final CreateEventScreenController createEventScreenController;
    private TextField nameTextField;
    private DatePicker datePicker;
    private TextField locationTextField;
    private TextField descriptionTextField;
    private static final String HANDLE_EVENT_CREATE = "handleEventCreate";
    private static final String HANDLE_EVENT = "handleEvent";
    private static final String HANDLE_SUB_EVENT = "handleSubEvent";
    private static final String HANDLE_INSCRIPTION = "handleInscription";
    private static final String HANDLE_SESSION = "handleSession";
    private static final String HANDLE_SUBMIT = "handleSubmit";
    private static final String HANDLE_USER = "handleUser";
    private static final String HANDLE_BACK = "handleBack";


    public CreateEventMediator(CreateEventScreenController createEventController, FacadeInterface facade, AnchorPane screenPane, Label errorUpdtLabel) {
        super(facade, screenPane, errorUpdtLabel, createEventController);
        this.createEventScreenController = createEventController;
    }

    public void setComponents(TextField nameTextField, DatePicker datePicker, TextField locationTextField, TextField descriptionTextField) {
        this.nameTextField = nameTextField;
        this.datePicker = datePicker;
        this.locationTextField = locationTextField;
        this.descriptionTextField = descriptionTextField;

        if (createEventScreenController != null) {
            setupListeners();
        }
    }

    @Override
    public void registerComponents() {
        if (screenPane != null) {
            setupButtonAction("#createButton", HANDLE_EVENT_CREATE);
            setupButtonAction("#handleEventButton", HANDLE_EVENT);
            setupButtonAction("#handleSubEventButton", HANDLE_SUB_EVENT);
            setupButtonAction("#handleInscriptionButton", HANDLE_INSCRIPTION);
            setupButtonAction("#handleSessionButton", HANDLE_SESSION);
            setupButtonAction("#handleSubmitButton", HANDLE_SUBMIT);
            setupButtonAction("#handleUserButton", HANDLE_USER);
            setupButtonAction("#handleBackButton", HANDLE_BACK);
            setupButtonAction("#logoutButton", "logout");
        }
    }

    @Override
    public Object notify(String event) throws IOException {
        if (createEventScreenController != null) {
            switch (event) {
                case HANDLE_EVENT_CREATE:
                    handleEventCreate();
                    break;
                case HANDLE_USER
                , HANDLE_EVENT
                , HANDLE_BACK
                , HANDLE_INSCRIPTION
                , HANDLE_SESSION
                , HANDLE_SUB_EVENT
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

    private void handleEventCreate() throws IOException {
        if (validateInputs(null, facade.getAllEvent())) {
            createEventScreenController.createEvent();
        }
    }

    private void loadScreenForEvent(String event){
        String fxmlFile = getFxmlPathForEvent(event);

        loadScreenWithTask(() -> {
            try {
                createEventScreenController.genericButton(fxmlFile, screenPane, facade, null);
            } catch (IOException e) {
                throw new CustomRuntimeException("Tela não carregada", e);
            }
        });
    }

    private String getFxmlPathForEvent(String event) {
        return switch (event) {
            case HANDLE_EVENT_CREATE -> "/fxml/createEventScreen.fxml";
            case HANDLE_USER -> "/fxml/userScreen.fxml";
            case HANDLE_EVENT -> "/fxml/allEventsScreen.fxml";
            case HANDLE_BACK -> "/fxml/eventScreen.fxml";
            case HANDLE_SESSION -> "/fxml/allSessionsScreen.fxml";
            case HANDLE_SUB_EVENT -> "/fxml/allSubEventsScreen.fxml";
            case HANDLE_INSCRIPTION -> "/fxml/attendeeScreen.fxml";
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
        createEventScreenController.loadScreen("Carregando", () -> {
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
                    notify(HANDLE_EVENT_CREATE);
                } catch (IOException e) {
                    throw new CustomRuntimeException("Algo deu errado", e);
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
