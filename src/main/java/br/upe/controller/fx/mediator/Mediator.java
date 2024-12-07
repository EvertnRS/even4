package br.upe.controller.fx.mediator;
import br.upe.controller.fx.FxController;
import br.upe.facade.FacadeInterface;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import br.upe.persistence.Model;

import javafx.scene.layout.AnchorPane;
import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static br.upe.ui.Validation.isValidDate;

public abstract class Mediator implements MediatorInterface {
    private final FxController fxController;
    protected FacadeInterface facade;
    protected final AnchorPane screenPane;
    protected final Label errorUpdtLabel;

    protected Mediator(FacadeInterface facade, AnchorPane screenPane, Label errorUpdtLabel, FxController fxController) {
        this.facade = facade;
        this.screenPane = screenPane;
        this.errorUpdtLabel = errorUpdtLabel;
        this.fxController = fxController;
    }

    protected void setupButtonAction(String buttonId, String action) {
        Button button = (Button) screenPane.lookup(buttonId);
        if (button != null) {
            button.setOnAction(event -> {
                try {
                    notify(action);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public boolean validateInputs() {
        String newName = fxController.getNameTextField().getText();
        String newLocation = fxController.getLocationTextField().getText();
        String newDescription = fxController.getDescriptionTextField().getText();

        if (fxController.getDatePicker().getValue() == null) {
            errorUpdtLabel.setText("Data inválida.");
            errorUpdtLabel.setAlignment(Pos.CENTER);
            return false;
        }

        Date newDate = Date.valueOf(fxController.getDatePicker().getValue());

        List<Model> eventList = facade.getAllEvent();

        if (!isValidDate(String.valueOf(newDate))) {
            errorUpdtLabel.setText("Data inválida.");
            errorUpdtLabel.setAlignment(Pos.CENTER);
            return false;
        }

        if (newLocation.isEmpty() || newDescription.isEmpty() || isValidName(newName, eventList)) {
            errorUpdtLabel.setText("Erro no preenchimento das informações.");
            errorUpdtLabel.setAlignment(Pos.CENTER);
            return false;
        }
        return true;
    }

    protected <T> boolean isValidName(String name, List<T> items) {
        for (T item : items) {
            if (item instanceof Model && ((Model) item).getName().equals(name) || name.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public Optional<ButtonType> deleteButtonAlert(){
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmação de Exclusão");
        confirmationAlert.setHeaderText("Deseja realmente excluir este evento?");
        confirmationAlert.setContentText("Esta ação não pode ser desfeita.");

        ButtonType buttonSim = new ButtonType("Sim", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonNao = new ButtonType("Não", ButtonBar.ButtonData.CANCEL_CLOSE);

        confirmationAlert.getButtonTypes().setAll(buttonSim, buttonNao);

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        return result;
    }

}
