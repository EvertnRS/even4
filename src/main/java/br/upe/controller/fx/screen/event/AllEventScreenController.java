package br.upe.controller.fx.screen.event;

import br.upe.controller.fx.mediator.event.AllEventMediator;
import br.upe.controller.fx.screen.BaseController;
import br.upe.controller.fx.screen.FxController;
import br.upe.facade.FacadeInterface;
import br.upe.persistence.Model;
import br.upe.persistence.repository.EventRepository;
import br.upe.persistence.repository.UserRepository;
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

public class AllEventScreenController extends BaseController implements FxController {
    private FacadeInterface facade;
    private AllEventMediator mediator;
    private static final String BG_COLOR = "-fx-background-color: #ffffff; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(128, 128, 128, 1), 3.88, 0, -1, 5);";

    @FXML
    private VBox eventVBox;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Label userEmail;
    @FXML
    private AnchorPane mainPane;
    @FXML
    private TextField searchTextField;
    @FXML
    private Text searchPlaceholder;
    @FXML
    private ImageView logoView6;

    @Override
    public void setFacade(FacadeInterface facade) throws IOException {
        this.facade = facade;
        initial();
    }

    private void initial() throws IOException {
        userEmail.setText(facade.getUserData("email"));
        loadUserEvents();

        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            setupPlaceholders();
            try {
                loadUserEvents();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        this.mediator = new AllEventMediator(this, facade, mainPane, null);
        mediator.registerComponents();
    }

    private void loadUserEvents() throws IOException {
        eventVBox.getChildren().clear();

        List<Model> events = facade.getAllEvent();
        EventRepository eventRepository = EventRepository.getInstance();

        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);

        scrollPane.setStyle("-fx-background-color: transparent; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-top: 2px; -border-color: #cccccc");

        eventVBox.setAlignment(Pos.CENTER);

        for (Model event : events) {

                VBox eventContainer = new VBox();
                eventContainer.setStyle("-fx-background-color: #d3d3d3; " +
                        "-fx-padding: 10px 20px 10px 20px; " +
                        "-fx-margin: 0 40px 0 40px; " +
                        "-fx-spacing: 5px; " +
                        "-fx-border-radius: 10px; " +
                        "-fx-background-radius: 10px;");

                VBox.setMargin(eventContainer, new Insets(5, 20, 5, 20));
                Label eventLabel;

                if (searchTextField.getText().isEmpty() || String.valueOf(eventRepository.getData(event.getId(), "name")).contains(searchTextField.getText())) {
                    eventLabel = new Label((String) eventRepository.getData(event.getId(), "name"));
                    eventLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #000000;");

                    Button detailsButton = new Button("Detalhes");
                    ImageView detailsIcon = new ImageView(new Image("images/icons/buttons/detailsIcon.png"));
                    detailsIcon.setFitWidth(16);
                    detailsIcon.setFitHeight(16);
                    detailsButton.setGraphic(detailsIcon);
                    detailsButton.setStyle(BG_COLOR);


                    detailsButton.setOnAction(e ->
                            handleDetailEvent((UUID) eventRepository.getData(event.getId(), "id")));


                    HBox actionButtons = new HBox(10);
                    actionButtons.setAlignment(Pos.CENTER_RIGHT);
                    actionButtons.getChildren().addAll(detailsButton);

                    eventContainer.getChildren().addAll(eventLabel, actionButtons);

                    eventVBox.getChildren().add(eventContainer);
            }
        }
    }

    private void handleDetailEvent(UUID id) {
        loadScreen("Carregando", () -> {
            EventRepository eventRepository = EventRepository.getInstance();
            UserRepository userRepository = UserRepository.getInstance();

            String content = "Nome: " + eventRepository.getData(id, "name") + "\n" +
                    "Data: " + eventRepository.getData(id, "date") + "\n" +
                    "Descrição: " + eventRepository.getData(id, "description") + "\n" +
                    "Local: " + eventRepository.getData(id, "location") + "\n" +
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
        }, mainPane);
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

