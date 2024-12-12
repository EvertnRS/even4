package br.upe.controller.fx.mediator;

import br.upe.controller.fx.CreateSessionScreenController;
import br.upe.facade.FacadeInterface;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

import static br.upe.ui.Validation.areValidTimes;

public class CreateSessionMediator extends Mediator{
    private final CreateSessionScreenController createSessionScreenController;
    private TextField nameTextField;
    private DatePicker datePicker;
    private TextField locationTextField;
    private TextField descriptionTextField;
    private TextField startTimeTextField;
    private TextField endTimeTextField;
    private TextField searchField;

    public CreateSessionMediator(CreateSessionScreenController createSessionScreenController, FacadeInterface facade, AnchorPane screenPane, Label errorUpdtLabel) {
        super(facade, screenPane, errorUpdtLabel, createSessionScreenController);
        this.createSessionScreenController = createSessionScreenController;
    }

    public void setComponents(TextField nameTextField, DatePicker datePicker, TextField locationTextField, TextField descriptionTextField, TextField startTimeTextField, TextField endTimeTextField, TextField searchField) {
        this.nameTextField = nameTextField;
        this.datePicker = datePicker;
        this.locationTextField = locationTextField;
        this.descriptionTextField = descriptionTextField;
        this.startTimeTextField = startTimeTextField;
        this.endTimeTextField = endTimeTextField;
        this.searchField = searchField;

        if (createSessionScreenController != null) {
            setupListeners();
        }
    }

    @Override
    public void registerComponents() {
        if (screenPane != null) {
            setupButtonAction("#createButton", "handleSessionCreate");
            setupButtonAction("#handleEventButton", "handleEvent");
            setupButtonAction("#handleSubEventButton", "handleSubEvent");
            setupButtonAction("#handleSessionButton", "handleSession");
            setupButtonAction("#handleSubmitButton", "handleSubmit");
            setupButtonAction("#handleUserButton", "handleUser");
            setupButtonAction("#handleBackButton", "handleBack");
            setupButtonAction("#handleInscriptionButton", "handleInscription");
            setupButtonAction("#logoutButton", "logout");
        }
    }

    @Override
    public Object notify(String event) throws IOException {
        if (createSessionScreenController != null) {
            switch (event) {
                case "handleSessionCreate":
                    handleSessionCreate();
                    break;
                case "handleUser"
                , "handleSubEvent"
                , "handleInscription"
                , "handleBack"
                , "handleSession"
                , "handleEvent"
                , "handleSubmit":
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

    private void handleSessionCreate() throws IOException {
        String startTime = startTimeTextField.getText();
        String endTime = endTimeTextField.getText();

        if (validateInputs() && areValidTimes(startTime, endTime)) {
            createSessionScreenController.createSession();
        } else{
            errorUpdtLabel.setText("Erro no preenchimento das informações.");
            errorUpdtLabel.setAlignment(Pos.CENTER);
        }
    }

    private void loadScreenForEvent(String event) {
        String fxmlFile = getFxmlPathForEvent(event);

        loadScreenWithTask(() -> {
            try {
                createSessionScreenController.genericButton(fxmlFile, screenPane, facade, null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private String getFxmlPathForEvent(String event) {
        return switch (event) {
            case "handleUser" -> "/fxml/userScreen.fxml";
            case "handleSession", "handleBack" -> "/fxml/sessionScreen.fxml";
            case "handleSubEvent" -> "/fxml/subEventScreen.fxml";
            case "handleEvent" -> "/fxml/eventScreen.fxml";
            case "handleSubmit" -> "/fxml/submitScreen.fxml";
            case "loginScreen" -> "/fxml/loginScreen.fxml";
            case "handleInscription" -> "/fxml/attendeeScreen.fxml";
            default -> throw new IllegalArgumentException("Unknown event: " + event);
        };
    }

    private void logout() {
        facade = null;
        loadScreenForEvent("loginScreen");
    }

    private void loadScreenWithTask(Runnable task) {
        assert screenPane != null;
        createSessionScreenController.loadScreen("Carregando", () -> {
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
                    notify("handleSessionCreate");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        configureNavigation(nameTextField, locationTextField, searchField);
        configureNavigation(searchField, nameTextField, datePicker);
        datePicker.addEventFilter(KeyEvent.KEY_PRESSED, event -> handleKeyNavigation(event, searchField, startTimeTextField));
        configureNavigation(startTimeTextField, datePicker, endTimeTextField);
        configureNavigation(endTimeTextField, startTimeTextField, descriptionTextField);
        configureNavigation(descriptionTextField, endTimeTextField, locationTextField);
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
