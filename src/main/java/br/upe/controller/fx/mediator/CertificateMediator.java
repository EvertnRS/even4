package br.upe.controller.fx.mediator;

import br.upe.controller.fx.CertificateScreenController;
import br.upe.facade.FacadeInterface;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CertificateMediator extends Mediator{
    private final CertificateScreenController certificateScreenController;

    public CertificateMediator(CertificateScreenController certificateScreenController, FacadeInterface facade, AnchorPane screenPane, Label errorUpdtLabel) {
        super(facade, screenPane, errorUpdtLabel, certificateScreenController);
        this.certificateScreenController = certificateScreenController;
    }

    @Override
    public void registerComponents() {
        if (screenPane != null) {
            setupButtonAction("#openDirectoryChooser", "fileChooser");
            setupButtonAction("#createButton", "handleCertificateCreate");
            setupButtonAction("#handleEventButton", "handleEvent");
            setupButtonAction("#handleSubEventButton", "handleSubEvent");
            setupButtonAction("#handleSessionButton", "handleSession");
            setupButtonAction("#handleSubmitButton", "handleSubmit");
            setupButtonAction("#handleUserButton", "handleUser");
            setupButtonAction("#handleBackButton", "handleBack");
            setupButtonAction("#logoutButton", "logout");
        }
        setupListeners();
    }

    @Override
    public Object notify(String event) throws IOException {
        if (certificateScreenController != null) {
            switch (event) {
                case "handleCertificateCreate":
                    if (validateAddress()){
                        certificateScreenController.createCertificate();
                    }
                    break;
                case "openDirectoryChooser":
                    certificateScreenController.openDirectoryChooser();
                    break;
                case "handleUser"
                , "handleSubEvent"
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
            case "handleUser" -> "/fxml/userScreen.fxml";
            case "handleSubEvent" -> "/fxml/subEventScreen.fxml";
            case "handleBack" -> "/fxml/attendeeScreen.fxml";
            case "handleSession" -> "/fxml/sessionScreen.fxml";
            case "handleEvent" -> "/fxml/eventScreen.fxml";
            case "handleSubmit" -> "/fxml/submitScreen.fxml";
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
                    notify("handleCertificateCreate");
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
