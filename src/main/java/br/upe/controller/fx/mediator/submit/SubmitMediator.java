package br.upe.controller.fx.mediator.submit;

import br.upe.controller.fx.mediator.Mediator;
import br.upe.controller.fx.screen.submit.SubmitScreenController;
import br.upe.facade.FacadeInterface;
import br.upe.utils.CustomRuntimeException;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.UUID;

public class SubmitMediator extends Mediator {
    private final SubmitScreenController submitScreenController;
    private static final String HANDLE_SUB_EVENT = "handleSubEvent";
    private static final String HANDLE_SESSION = "handleSession";
    private static final String HANDLE_EVENT = "handleEvent";
    private static final String HANDLE_SUBMIT = "handleSubmit";
    private static final String HANDLE_USER = "handleUser";
    private static final String HANDLE_INSCRIPTION = "handleInscription";
    private static final String HANDLE_CREATEARTICLE = "handleCreateArticle";
    private static final String HANDLE_UPDATEARTICLE = "handleUpdateArticle";

    private UUID articleId;

    public SubmitMediator(SubmitScreenController submitScreenController, FacadeInterface facade, AnchorPane screenPane, Label errorUpdtLabel) {
        super(facade, screenPane, errorUpdtLabel, submitScreenController);
        this.submitScreenController = submitScreenController;
    }

    public void setArticleId(UUID eventId) {
        this.articleId = eventId;
    }

    @Override
    public void registerComponents() {
        if (screenPane != null) {
            setupButtonAction("#handleAddButton", HANDLE_CREATEARTICLE);
            setupButtonAction("#handleSubEventButton", HANDLE_SUB_EVENT);
            setupButtonAction("#handleSubmitButton", HANDLE_SUBMIT);
            setupButtonAction("#handleSessionButton", HANDLE_SESSION);
            setupButtonAction("#handleInscriptionButton", HANDLE_INSCRIPTION);
            setupButtonAction("#handleEventButton", HANDLE_EVENT);
            setupButtonAction("#handleUserButton", HANDLE_USER);
            setupButtonAction("#logoutButton", "logout");
        }
    }

    @Override
    public Object notify(String event) throws IOException {
        if (submitScreenController != null) {
            switch (event) {
                case HANDLE_CREATEARTICLE
                , HANDLE_UPDATEARTICLE
                , HANDLE_USER
                , HANDLE_SUBMIT
                , HANDLE_SESSION
                , HANDLE_INSCRIPTION
                , HANDLE_SUB_EVENT
                , HANDLE_EVENT:
                    loadScreenForEvent(event);
                    break;

                case "handleDeleteArticle":
                    return deleteButtonAlert();

                case "logout":
                    facade = null;
                    loadScreenForEvent("loginScreen");
                    break;

                default:
                    throw new IllegalArgumentException("Ação não reconhecida: " + event);
            }
        }
        return null;
    }

    private void loadScreenForEvent(String event) {
        String fxmlFile = getFxmlPathForEvent(event);

        if (!event.equals(HANDLE_UPDATEARTICLE)) {
            this.articleId = null;
        }

        loadScreenWithTask(() -> {
            try {
                submitScreenController.genericButton(fxmlFile, screenPane, facade, String.valueOf(articleId));
            } catch (IOException e) {
                throw new CustomRuntimeException("Algo deu errado", e);
            }
        });
    }

    private String getFxmlPathForEvent(String event) {
        return switch (event) {
            case HANDLE_CREATEARTICLE -> "/fxml/createSubmitScreen.fxml";
            case HANDLE_UPDATEARTICLE -> "/fxml/updateSubmitScreen.fxml";
            case HANDLE_USER -> "/fxml/userScreen.fxml";
            case HANDLE_INSCRIPTION -> "/fxml/attendeeScreen.fxml";
            case HANDLE_SUBMIT -> "/fxml/submitScreen.fxml";
            case HANDLE_SESSION -> "/fxml/allSessionsScreen.fxml";
            case HANDLE_SUB_EVENT -> "/fxml/allSubEventsScreen.fxml";
            case HANDLE_EVENT -> "/fxml/allEventsScreen.fxml";
            case "loginScreen" -> "/fxml/loginScreen.fxml";
            default -> throw new IllegalArgumentException("Unknown event: " + event);
        };
    }

    private void loadScreenWithTask(Runnable task) {
        assert screenPane != null;
        submitScreenController.loadScreen("Carregando", () -> {
            try {
                task.run();
            } catch (Exception e) {
                throw new CustomRuntimeException("Algo deu errado", e);
            }
        }, screenPane);
    }

}

