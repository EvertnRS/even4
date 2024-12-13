package br.upe.controller.fx.mediator;

import br.upe.controller.fx.CreateSubEventScreenController;
import br.upe.facade.FacadeInterface;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class CreateSubEventMediator extends Mediator {
    private final CreateSubEventScreenController createSubEventScreenController;
    private static final String HANDLE_SUB_EVENT = "handleSubEvent";
    private static final String HANDLE_SESSION = "handleSession";
    private static final String HANDLE_EVENT = "handleEvent";
    private static final String HANDLE_SUBMIT = "handleSubmit";
    private static final String HANDLE_USER = "handleUser";
    private static final String HANDLE_INSCRIPTION = "handleInscription";
    private static final String HANDLE_BACK = "handleBack";
    private static final String HANDLE_SUBEVENTCREATE = "handleSubEventCreate";



    private TextField nameTextField;
    private DatePicker datePicker;
    private TextField locationTextField;
    private TextField descriptionTextField;
    private TextField searchField;

    public CreateSubEventMediator(CreateSubEventScreenController createSubEventScreenController, FacadeInterface facade, AnchorPane screenPane, Label errorUpdtLabel) {
        super(facade, screenPane, errorUpdtLabel, createSubEventScreenController);
        this.createSubEventScreenController = createSubEventScreenController;
    }

    public void setComponents(TextField nameTextField, DatePicker datePicker, TextField locationTextField, TextField descriptionTextField, TextField searchField) {
        this.nameTextField = nameTextField;
        this.datePicker = datePicker;
        this.locationTextField = locationTextField;
        this.descriptionTextField = descriptionTextField;
        this.searchField = searchField;

        if (createSubEventScreenController != null) {
            setupListeners();
        }
    }

    @Override
    public void registerComponents() {
        if (screenPane != null) {
            setupButtonAction("#createButton", HANDLE_SUBEVENTCREATE);
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
        if (createSubEventScreenController != null) {
            switch (event) {
                case HANDLE_SUBEVENTCREATE:
                    handleSubEventCreate();
                    break;
                case HANDLE_USER
                , HANDLE_SUB_EVENT
                , HANDLE_INSCRIPTION
                , HANDLE_BACK
                , HANDLE_SESSION
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

    private void handleSubEventCreate() throws IOException {
        if (validateInputs()) {
            createSubEventScreenController.createSubEvent();
        }
    }

    private void loadScreenForEvent(String event) {
        String fxmlFile = getFxmlPathForEvent(event);

        loadScreenWithTask(() -> {
            try {
                createSubEventScreenController.genericButton(fxmlFile, screenPane, facade, null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private String getFxmlPathForEvent(String event) {
        return switch (event) {
            case HANDLE_USER -> "/fxml/userScreen.fxml";
            case HANDLE_SUB_EVENT, HANDLE_BACK -> "/fxml/subEventScreen.fxml";
            case HANDLE_SESSION -> "/fxml/sessionScreen.fxml";
            case HANDLE_EVENT -> "/fxml/eventScreen.fxml";
            case HANDLE_SUBMIT -> "/fxml/submitScreen.fxml";
            case "loginScreen" -> "/fxml/loginScreen.fxml";
            case HANDLE_INSCRIPTION -> "/fxml/attendeeScreen.fxml";
            default -> throw new IllegalArgumentException("Unknown event: " + event);
        };
    }

    private void logout() {
        facade = null;
        loadScreenForEvent("loginScreen");
    }

    private void loadScreenWithTask(Runnable task) {
        assert screenPane != null;
        createSubEventScreenController.loadScreen("Carregando", () -> {
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
                    notify(HANDLE_SUBEVENTCREATE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        configureNavigation(nameTextField, locationTextField, searchField);
        configureNavigation(searchField, nameTextField, datePicker);
        datePicker.addEventFilter(KeyEvent.KEY_PRESSED, event -> handleKeyNavigation(event, searchField, descriptionTextField));
        configureNavigation(descriptionTextField, datePicker, locationTextField);
        configureNavigation(locationTextField, descriptionTextField, nameTextField);


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
