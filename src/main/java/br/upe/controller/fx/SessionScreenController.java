package br.upe.controller.fx;

import br.upe.facade.FacadeInterface;
import br.upe.persistence.Persistence;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SessionScreenController extends BaseController implements FxController {
    private FacadeInterface facade;

    @FXML
    private Label userEmail;
    @FXML
    private VBox sessionVBox;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private AnchorPane sessionPane;

    public void setFacade(FacadeInterface facade) throws IOException {
        this.facade = facade;
        initial();
    }

    private void initial() throws IOException {
        userEmail.setText(facade.getUserData("email"));
        loadUserSessions();
    }

    public void handleUser() throws IOException {
        genericButton("/fxml/userScreen.fxml", sessionPane, facade, null);
    }

    public void handleSubEvent() throws IOException {
        genericButton("/fxml/subEventScreen.fxml", sessionPane, facade, null);
    }

    public void handleSubmit() throws IOException {
        genericButton("/fxml/submitScreen.fxml", sessionPane, facade, null);
    }

    public void handleEvent() throws IOException {
        genericButton("/fxml/mainScreen.fxml", sessionPane, facade, null);
    }

    public void handleAddSession() throws IOException {
        genericButton("/fxml/createSessionScreen.fxml", sessionPane, facade, null);
    }
    public void handleInscriptionSession() throws IOException {
        genericButton("/fxml/enterSessionScreen.fxml", sessionPane, facade, null);
    }

    private void loadUserSessions() throws IOException {
        sessionVBox.getChildren().clear();

        // Carregar as sessões do usuário
        facade.listSessions(facade.getUserData("id"), "");

        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setStyle("-fx-padding: 20px;");
        sessionVBox.setAlignment(Pos.CENTER);

        Map<UUID, Persistence> parentMap = new HashMap<>(facade.getEventHashMap());
        parentMap.putAll(facade.getSubEventHashMap());

        // Iterar sobre cada sessão
        for (Map.Entry<UUID, Persistence> entry : facade.getSessionHashMap().entrySet()) {
            Persistence persistence = entry.getValue();

            // Verifica se a sessão pertence ao usuário logado
            if (persistence.getData("ownerId").equals(facade.getUserData("id"))) {

                VBox sessionContainer = new VBox();
                sessionContainer.setStyle("-fx-background-color: #d3d3d3; -fx-padding: 10px; -fx-spacing: 5px; -fx-border-radius: 10px; -fx-background-radius: 10px;");

                // Exibe o nome da sessão
                Label sessionLabel = new Label((String) persistence.getData("name"));
                sessionLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #000000;");

                // Label para exibir o nome do evento ou subevento
                Label controller;

                // Verifica se a sessão está associada a um evento ou subevento
                UUID eventId = (UUID) persistence.getData("eventId");

                if (parentMap.containsKey(eventId)) {
                    // Se for um evento ou subevento, exibe o nome
                    String name = (String) parentMap.get(eventId).getData("name");
                    Label eventLabel = new Label(name);
                    controller = eventLabel;
                    eventLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #555555;");
                } else {
                    // Se o evento ou subevento não for encontrado, não exibe nada ou uma mensagem padrão
                    controller = new Label("Evento/Subevento não encontrado");
                    controller.setStyle("-fx-font-size: 18px; -fx-text-fill: #ff0000;");
                }

                // Botões de ação (Editar, Excluir, Detalhes)
                Button editButton = new Button("Editar");
                editButton.setStyle("-fx-background-color: #6fa3ef; -fx-text-fill: white; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(128, 128, 128, 1), 3.88, 0, -1, 5);");

                Button deleteButton = new Button("Excluir");
                deleteButton.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(128, 128, 128, 1), 3.88, 0, -1, 5);");

                Button detailsButton = new Button("Detalhes");
                detailsButton.setStyle("-fx-background-color: #ff914d; -fx-text-fill: white; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(128, 128, 128, 1), 3.88, 0, -1, 5);");

                detailsButton.setOnAction(e -> handleDetailSession(parentMap, (UUID) persistence.getData("id")));

                editButton.setOnAction(e -> {
                    try {
                        handleEditSession((String) persistence.getData("name"));
                    } catch (IOException ex) {
                        throw new IllegalArgumentException(ex);
                    }
                });

                deleteButton.setOnAction(e -> {
                    try {
                        handleDeleteSession((String) persistence.getData("id"), facade.getUserData("id"));
                    } catch (IOException ex) {
                        throw new IllegalArgumentException(ex);
                    }
                });

                // Layout dos botões de ação
                HBox actionButtons = new HBox(10);
                actionButtons.setAlignment(Pos.CENTER_RIGHT);
                actionButtons.getChildren().addAll(detailsButton, editButton, deleteButton);

                // Adiciona os componentes à VBox
                sessionContainer.getChildren().addAll(sessionLabel, actionButtons, controller);

                // Adiciona a sessão à lista
                sessionVBox.getChildren().add(sessionContainer);
            }
        }
    }


    private void handleEditSession(String eventName) throws IOException {
        genericButton("/fxml/updateSessionScreen.fxml", sessionPane, facade, eventName);
    }

    private void handleDetailSession(Map<UUID, Persistence> parentMap, UUID id) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalhes do Evento");
        alert.setHeaderText("Detalhes do Evento");

        Persistence session = facade.getSessionHashMap().get(id);
        Persistence owner = facade.getUserHashMap().get(session.getData("ownerId"));

        String parentName = parentMap.containsKey((UUID) session.getData("eventId"))
                ? (String) parentMap.get((UUID) session.getData("eventId")).getData("name")
                : "Evento/Subevento não encontrado";

        String content = "Nome: " + session.getData("name") + "\n" +
                "Data: " + session.getData("date") + "\n" +
                "Descrição: " + session.getData("description") + "\n" +
                "Início: " + session.getData("startTime") + "\n" +
                "Término: " + session.getData("endTime") + "\n" +
                "Local: " + session.getData("location") + "\n" +
                "Evento/Subevento: " + parentName + "\n" +
                "Administrador: " + owner.getData("email") + "\n";

        alert.setContentText(content);
        alert.showAndWait();
    }

    private void handleDeleteSession(String eventId, String userId) throws IOException {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmação de Exclusão");
        confirmationAlert.setHeaderText("Deseja realmente excluir este sessão?");
        confirmationAlert.setContentText("Esta ação não pode ser desfeita.");

        ButtonType buttonSim = new ButtonType("Sim");
        ButtonType buttonNao = new ButtonType("Não", ButtonBar.ButtonData.CANCEL_CLOSE);

        confirmationAlert.getButtonTypes().setAll(buttonSim, buttonNao);

        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == buttonSim) {
            facade.deleteSession(eventId, userId);
            loadUserSessions();
        }
    }

    public void logout() throws IOException {
        genericButton("/fxml/loginScreen.fxml", sessionPane, facade, null);
    }
}
