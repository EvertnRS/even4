package br.upe.controller.fx.screen.subevent;

import br.upe.controller.fx.mediator.subevent.AllSubEventMediator;
import br.upe.controller.fx.screen.BaseController;
import br.upe.controller.fx.screen.FxController;
import br.upe.facade.FacadeInterface;
import br.upe.persistence.Model;
import br.upe.persistence.repository.EventRepository;
import br.upe.persistence.repository.SubEventRepository;
import br.upe.persistence.repository.UserRepository;
import br.upe.utils.CustomRuntimeException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class AllSubEventScreenController extends BaseController implements FxController {
    private FacadeInterface facade;
    private static final String BG_COLOR = "-fx-background-color: #ffffff; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(128, 128, 128, 1), 3.88, 0, -1, 5);";


    @FXML
    private Label userEmail;
    @FXML
    private VBox subEventVBox;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private AnchorPane subEventPane;
    @FXML
    private TextField searchTextField;
    @FXML
    private Text searchPlaceholder;
    @FXML
    private ImageView logoView6;

    public void setFacade(FacadeInterface facade) throws IOException {
        this.facade = facade;
        initial();
    }

    private void initial() {
        userEmail.setText(facade.getUserData("email"));
        loadAllSubEvents();

        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            setupPlaceholders();
            loadAllSubEvents();
        });

        AllSubEventMediator mediator = new AllSubEventMediator(this, facade, subEventPane, null);
        mediator.registerComponents();
    }


    private void loadAllSubEvents() {
        subEventVBox.getChildren().clear();

        List<Model> subEvents = facade.getAllSubEvent();
        SubEventRepository subeventRepository = SubEventRepository.getInstance();

        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);

        scrollPane.setStyle("-fx-background-color: transparent; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-top: 2px; -border-color: #cccccc");

        subEventVBox.setAlignment(Pos.CENTER);

        for (Model subevent : subEvents) {
                VBox eventContainer = new VBox();
                eventContainer.setStyle("-fx-background-color: #d3d3d3; " +
                        "-fx-padding: 10px 20px 10px 20px; " +
                        "-fx-margin: 0 40px 0 40px; " +
                        "-fx-spacing: 5px; " +
                        "-fx-border-radius: 10px; " +
                        "-fx-background-radius: 10px;");

                VBox.setMargin(eventContainer, new Insets(5, 20, 5, 20));
                Label subEventLabel;
                if (searchTextField.getText().isEmpty() || String.valueOf(subeventRepository.getData(subevent.getId(), "name")).contains(searchTextField.getText())) {
                    subEventLabel = new Label((String) subeventRepository.getData(subevent.getId(), "name"));
                    subEventLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #000000;");


                    Button detailsButton = new Button("Detalhes");
                    ImageView detailsIcon = new ImageView(new Image("images/icons/buttons/detailsIcon.png"));
                    detailsIcon.setFitWidth(16);
                    detailsIcon.setFitHeight(16);
                    detailsButton.setGraphic(detailsIcon);
                    detailsButton.setStyle(BG_COLOR);

                    detailsButton.setOnAction(e ->
                            handleDetailSubEvent((UUID) subeventRepository.getData(subevent.getId(), "id")));


                    HBox actionButtons = new HBox(10);
                    actionButtons.setAlignment(Pos.CENTER_RIGHT);
                    actionButtons.getChildren().addAll(detailsButton);
                    Label eventLabel = createEventLabel((UUID) subeventRepository.getData(subevent.getId(), "eventId"));
                    eventContainer.getChildren().addAll(subEventLabel, actionButtons, eventLabel);

                    subEventVBox.getChildren().add(eventContainer);
                }
        }
    }


    private Label createEventLabel(UUID eventId) {
        Label eventLabel = new Label();
        EventRepository eventRepository = EventRepository.getInstance();
        String nameEvent = (String) eventRepository.getData(eventId, "name");
        eventLabel.setText(nameEvent);
        eventLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #555555;");
        return eventLabel;
    }


    private void handleDetailSubEvent(UUID id) {
        loadScreen("Carregando", () -> {
            EventRepository eventRepository = EventRepository.getInstance();
            SubEventRepository subeventRepository = SubEventRepository.getInstance();
            UserRepository userRepository = UserRepository.getInstance();

            String content = "Nome: " + subeventRepository.getData(id, "name") + "\n" +
                    "Data: " + subeventRepository.getData(id, "date") + "\n" +
                    "Descrição: " + subeventRepository.getData(id, "description") + "\n" +
                    "Local: " + subeventRepository.getData(id, "location") + "\n" +
                    "Evento: " + eventRepository.getData((UUID) subeventRepository.getData(id, "eventId"), "name") + "\n" +
                    "Administrador: " + userRepository.getData("email") + "\n";

            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Detalhes do Evento");
                alert.setTitle(" ");

                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().clear();
                stage.getIcons().add(new Image("/images/logo/Logo.png"));

                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.setStyle("-fx-background-color: #f0f0f0; -fx-font-size: 14px; -fx-text-fill: #333333;");
                dialogPane.lookup(".header-panel").setStyle("-fx-background-color: #ff914d; -fx-text-fill: #ffffff; -fx-font-weight: bold;");
                dialogPane.lookup(".content").setStyle("-fx-font-size: 14px; -fx-text-fill: rgb(51,51,51);");

                alert.setContentText(content);
                alert.showAndWait();
            });
        }, subEventPane);
    }

    public void setupPlaceholders() {
        if (!searchTextField.getText().isEmpty()) {
            searchPlaceholder.setVisible(false);
            logoView6.setVisible(false);
        } else {
            searchPlaceholder.setVisible(true);
            logoView6.setVisible(true);
        }
    }

    @Override
    public TextField getNameTextField() {
        return null;
    }

    @Override
    public TextField getLocationTextField() {
        return null;
    }

    @Override
    public TextField getDescriptionTextField() {
        return null;
    }

    @Override
    public DatePicker getDatePicker() {
        return null;
    }

}