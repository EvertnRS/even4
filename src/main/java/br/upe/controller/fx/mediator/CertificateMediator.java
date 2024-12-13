package br.upe.controller.fx.mediator;

import br.upe.controller.fx.CertificateScreenController;
import br.upe.facade.FacadeInterface;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CertificateMediator extends Mediator {
    private final CertificateScreenController certificateScreenController;
    private static final String HANDLE_CERTIFICATE_CREATE = "handleCertificateCreate";
    private static final String HANDLE_EVENT = "handleEvent";
    private static final String HANDLE_SUB_EVENT = "handleSubEvent";
    private static final String HANDLE_SESSION = "handleSession";
    private static final String HANDLE_SUBMIT = "handleSubmit";
    private static final String HANDLE_USER = "handleUser";
    private static final String HANDLE_BACK = "handleBack";
    private static final String HANDLE_INSCRIPTION = "handleInscription";


    public CertificateMediator(CertificateScreenController certificateScreenController, FacadeInterface facade, AnchorPane screenPane, Label errorUpdtLabel) {
        super(facade, screenPane, errorUpdtLabel, certificateScreenController);
        this.certificateScreenController = certificateScreenController;
    }

    @Override
    public void registerComponents() {
        if (screenPane != null) {
            setupButtonAction("#openDirectoryChooser", "fileChooser");
            setupButtonAction("#createButton", HANDLE_CERTIFICATE_CREATE);
            setupButtonAction("#handleEventButton", HANDLE_EVENT);
            setupButtonAction("#handleSubEventButton", HANDLE_SUB_EVENT);
            setupButtonAction("#handleSessionButton", HANDLE_SESSION);
            setupButtonAction("#handleSubmitButton", HANDLE_SUBMIT);
            setupButtonAction("#handleUserButton", HANDLE_USER);
            setupButtonAction("#handleBackButton", HANDLE_BACK);
            setupButtonAction("#handleInscriptionButton", HANDLE_INSCRIPTION);
            setupButtonAction("#logoutButton", "logout");
        }
        setupListeners();
    }

    @Override
    public Object notify(String event) throws IOException {
        if (certificateScreenController != null) {
            switch (event) {
                case HANDLE_CERTIFICATE_CREATE:
                    if (validateAddress()) {
                        certificateScreenController.createCertificate();
                    }
                    break;
                case "fileChooser":
                    certificateScreenController.openDirectoryChooser();
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

    private void loadScreenForEvent(String event) {
        String fxmlFile = getFxmlPathForEvent(event);

        loadScreenWithTask(() -> {
            try {
                certificateScreenController.genericButton(fxmlFile, screenPane, facade, null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private String getFxmlPathForEvent(String event) {
        return switch (event) {
            case HANDLE_USER -> "/fxml/userScreen.fxml";
            case HANDLE_SUB_EVENT -> "/fxml/subEventScreen.fxml";
            case HANDLE_BACK -> "/fxml/attendeeScreen.fxml";
            case HANDLE_SESSION -> "/fxml/sessionScreen.fxml";
            case HANDLE_EVENT -> "/fxml/eventScreen.fxml";
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
        certificateScreenController.loadScreen("Carregando", () -> {
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
                    notify(HANDLE_CERTIFICATE_CREATE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public boolean validateAddress() {
        Path path = Paths.get(certificateScreenController.getAddresTextField().getText());

        if (Files.exists(path)) {
            if (Files.isDirectory(path)) {
                return true;
            } else {
                errorUpdtLabel.setText("Nenhuma pasta selecionada.");
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
