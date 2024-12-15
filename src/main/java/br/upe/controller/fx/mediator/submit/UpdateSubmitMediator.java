package br.upe.controller.fx.mediator.submit;

import br.upe.controller.fx.mediator.Mediator;
import br.upe.controller.fx.screen.submit.UpdateSubmitScreenController;
import br.upe.facade.FacadeInterface;
import br.upe.utils.CustomRuntimeException;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UpdateSubmitMediator extends Mediator {
    private final UpdateSubmitScreenController updateSubmitScreenController;
    private static final String HANDLE_SUB_EVENT = "handleSubEvent";
    private static final String HANDLE_SESSION = "handleSession";
    private static final String HANDLE_EVENT = "handleEvent";
    private static final String HANDLE_SUBMIT = "handleSubmit";
    private static final String HANDLE_USER = "handleUser";
    private static final String HANDLE_INSCRIPTION = "handleInscription";
    private static final String HANDLE_BACK = "handleBack";

    public UpdateSubmitMediator(UpdateSubmitScreenController updateSubmitScreenController, FacadeInterface facade, AnchorPane screenPane, Label errorUpdtLabel) {
        super(facade, screenPane, errorUpdtLabel, updateSubmitScreenController);
        this.updateSubmitScreenController = updateSubmitScreenController;
    }

    @Override
    public void registerComponents() {
        if (screenPane != null) {
            setupButtonAction("#updateButton", "handleArticleUpdate");
            setupButtonAction("#fileChooser", "openFileChooser");
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
        if (updateSubmitScreenController != null) {
            switch (event) {
                case "handleArticleUpdate":
                    if (validateAddress()) {
                        handleArticleUpdate();
                    }
                    break;
                case "openFileChooser":
                    updateSubmitScreenController.openFileChooser();
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

    private void handleArticleUpdate() throws IOException {
        updateSubmitScreenController.updateArticle();
    }

    private void loadScreenForEvent(String event) {
        String fxmlFile = getFxmlPathForEvent(event);

        loadScreenWithTask(() -> {
            try {
                updateSubmitScreenController.genericButton(fxmlFile, screenPane, facade, null);
            } catch (IOException e) {
                throw new CustomRuntimeException("Algo deu errado", e);
            }
        });
    }

    private String getFxmlPathForEvent(String event) {
        return switch (event) {
            case HANDLE_USER -> "/fxml/userScreen.fxml";
            case HANDLE_INSCRIPTION -> "/fxml/attendeeScreen.fxml";
            case HANDLE_EVENT -> "/fxml/allEventsScreen.fxml";
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
        updateSubmitScreenController.loadScreen("Carregando", () -> {
            try {
                task.run();
            } catch (Exception e) {
                throw new CustomRuntimeException("Algo deu errado", e);
            }
        }, screenPane);
    }

    public boolean validateAddress() {
        Path path = Paths.get(updateSubmitScreenController.getNewArticleTextField().getText());

        if (Files.exists(path) && Files.isRegularFile(path)) {
            return true;
        } else {
            errorUpdtLabel.setText("Nenhum arquivo selecionado.");
            errorUpdtLabel.setAlignment(Pos.CENTER);
            return false;
        }
    }
}
