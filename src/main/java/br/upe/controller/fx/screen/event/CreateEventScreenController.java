package br.upe.controller.fx.screen.event;
import br.upe.controller.fx.fxutils.PlaceholderUtils;
import br.upe.controller.fx.mediator.event.CreateEventMediator;
import br.upe.controller.fx.screen.BaseController;
import br.upe.controller.fx.screen.FxController;
import br.upe.facade.FacadeInterface;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import java.io.IOException;
import java.sql.Date;

public class CreateEventScreenController extends BaseController implements FxController {
    private FacadeInterface facade;
    private CreateEventMediator mediator;

    @FXML
    private AnchorPane newEventPane;
    @FXML
    private Label userEmail;
    @FXML
    private TextField nameTextField;
    @FXML
    private Text namePlaceholder;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Text datePlaceholder;
    @FXML
    private TextField locationTextField;
    @FXML
    private Text locationPlaceholder;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private Text descriptionPlaceholder;
    @FXML
    private Label errorUpdtLabel;

    public void setFacade(FacadeInterface facade) throws IOException {
        this.facade = facade;
        initial();
    }

    private void setupPlaceholders() {
        PlaceholderUtils.setupPlaceholder(nameTextField, namePlaceholder);
        PlaceholderUtils.setupPlaceholder(datePicker, datePlaceholder);
        PlaceholderUtils.setupPlaceholder(locationTextField, locationPlaceholder);
        PlaceholderUtils.setupPlaceholder(descriptionTextField, descriptionPlaceholder);
    }

    private void initial() {
        userEmail.setText(facade.getUserData("email"));
        setupPlaceholders();

        this.mediator = new CreateEventMediator(this, facade, newEventPane, errorUpdtLabel);
        mediator.registerComponents();

        mediator.setComponents(nameTextField, datePicker, locationTextField, descriptionTextField);

    }

    public void createEvent() throws IOException {
        String eventName = nameTextField.getText();
        String eventLocation = locationTextField.getText();
        String eventDescription = descriptionTextField.getText();
        Date eventDate = Date.valueOf(datePicker.getValue() != null ? datePicker.getValue().toString() : "");

        facade.createEvent(eventName, eventDate, eventDescription, eventLocation, facade.getUserData("id"));
        mediator.notify("handleEvent");
    }

    @Override
    public TextField getNameTextField() {
        return nameTextField;
    }

    @Override
    public TextField getLocationTextField() {
        return locationTextField;
    }

    @Override
    public TextField getDescriptionTextField() {
        return descriptionTextField;
    }

    @Override
    public DatePicker getDatePicker() {
        return datePicker;
    }
}


