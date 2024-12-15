package br.upe.controller.fx.mediator.submit;

import br.upe.controller.fx.mediator.Mediator;
import br.upe.controller.fx.screen.submit.CreateSubmitScreenController;
import br.upe.facade.FacadeInterface;
import br.upe.utils.CustomRuntimeException;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CreateSubmitMediator extends Mediator {
    private final CreateSubmitScreenController createSubmitScreenController;
    private static final String HANDLE_SUB_EVENT = "handleSubEvent";
    private static final String HANDLE_SESSION = "handleSession";
    private static final String HANDLE_EVENT = "handleEvent";
    private static final String HANDLE_SUBMIT = "handleSubmit";
    private static final String HANDLE_USER = "handleUser";
    private static final String HANDLE_INSCRIPTION = "handleInscription";
    private static final String HANDLE_CREATEARTICLE = "handleArticleCreate";
    private static final String HANDLE_BACK = "handleBack";



    public CreateSubmitMediator(CreateSubmitScreenController createSubmitScreenController, FacadeInterface facade, AnchorPane screenPane, Label errorUpdtLabel) {
        super(facade, screenPane, errorUpdtLabel, createSubmitScreenController);
        this.createSubmitScreenController = createSubmitScreenController;
    }

    @Override
    public void registerComponents() {
        if (screenPane != null) {
            setupButtonAction("#createButton", HANDLE_CREATEARTICLE);
            setupButtonAction("#handleEventButton", HANDLE_EVENT);
            setupButtonAction("#handleSubEventButton", HANDLE_SUB_EVENT);
            setupButtonAction("#handleSessionButton", HANDLE_SESSION);
            setupButtonAction("#handleSubmitButton", HANDLE_SUBMIT);
            setupButtonAction("#handleUserButton", HANDLE_USER);
            setupButtonAction("#handleInscriptionButton", HANDLE_INSCRIPTION);
            setupButtonAction("#handleBackButton", HANDLE_BACK);
            setupButtonAction("#fileChooser", "openFileChooser");
            setupButtonAction("#logoutButton", "logout");
        }
    }

    @Override
    public Object notify(String event) throws IOException {
        if (createSubmitScreenController != null) {
            switch (event) {
                case HANDLE_CREATEARTICLE:
                    if (validateAddress()) {
                        handleSubmitCreate();
                    }
                    break;
                case "openFileChooser":
                    handleFileChooser();
                    break;
                case HANDLE_USER
                , HANDLE_INSCRIPTION
                , HANDLE_EVENT
                , HANDLE_BACK
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

    private void handleSubmitCreate() throws IOException {
        createSubmitScreenController.createArticle();
    }

    private void handleFileChooser() {
        createSubmitScreenController.openFileChooser();
    }

    private void loadScreenForEvent(String event) {
        String fxmlFile = getFxmlPathForEvent(event);

        loadScreenWithTask(() -> {
            try {
                createSubmitScreenController.genericButton(fxmlFile, screenPane, facade, null);
            } catch (IOException e) {
                throw new CustomRuntimeException("Algo deu errado", e);
            }
        });
    }

    private String getFxmlPathForEvent(String event) {
        return switch (event) {
            case HANDLE_EVENT -> "/fxml/allEventsScreen.fxml";
            case HANDLE_CREATEARTICLE -> "/fxml/createSubmitScreen.fxml";
            case HANDLE_INSCRIPTION -> "/fxml/attendeeScreen.fxml";
            case HANDLE_USER -> "/fxml/userScreen.fxml";
            case HANDLE_SESSION -> "/fxml/allSessionsScreen.fxml";
            case HANDLE_SUB_EVENT -> "/fxml/allSubEventsScreen.fxml";
            case HANDLE_SUBMIT, HANDLE_BACK -> "/fxml/submitScreen.fxml";
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
        createSubmitScreenController.loadScreen("Carregando", () -> {
            try {
                task.run();
            } catch (Exception e) {
                throw new CustomRuntimeException("Algo deu errado", e);
            }
        }, screenPane);
    }

    public boolean validateAddress() {
        Path path = Paths.get(createSubmitScreenController.getNamesTextField().getText());

        if (Files.exists(path)) {
            if (Files.isRegularFile(path)) {
                return true;
            } else {
                errorUpdtLabel.setText("Nenhum arquivo.");
                errorUpdtLabel.setAlignment(Pos.CENTER);
            }
            errorUpdtLabel.setText("Nenhum arquivo selecionado.");
            errorUpdtLabel.setAlignment(Pos.CENTER);
            return false;
        }
        errorUpdtLabel.setText("Nenhum arquivo selecionado.");
        errorUpdtLabel.setAlignment(Pos.CENTER);
        return false;
    }

}
