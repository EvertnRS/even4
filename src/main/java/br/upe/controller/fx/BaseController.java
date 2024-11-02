package br.upe.controller.fx;

import br.upe.controller.EventController;
import br.upe.controller.SubEventController;
import br.upe.facade.Facade;
import br.upe.facade.FacadeInterface;
import br.upe.persistence.Persistence;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseController {

    public void genericButton(String path, AnchorPane pane, FacadeInterface facade, String eventId) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
        AnchorPane screen = loader.load();

        if (facade != null){
            FxController screenController = loader.getController();
            screenController.setFacade(facade);

            if (screenController instanceof UpdateEventScreenController) {
                ((UpdateEventScreenController) screenController).setEventId(eventId);
            }
            if (screenController instanceof UpdateSubEventScreenController) {
                ((UpdateSubEventScreenController) screenController).setEventName(eventId);
            }
            if (screenController instanceof UpdateSessionScreenController) {
                ((UpdateSessionScreenController) screenController).setEventName(eventId);
            }
            if (screenController instanceof UpdateSubmitScreenController) {
                ((UpdateSubmitScreenController) screenController).setEventName(eventId);
            }
            if (screenController instanceof CertificateScreenController) {
                ((CertificateScreenController) screenController).setEventName(eventId);
            }
        }


        Scene scene = new Scene(screen);
        Stage stage = (Stage) pane.getScene().getWindow();

        stage.setScene(scene);
        stage.setTitle("Even4");

    }

    public boolean isValidName(String name, Map<String, Persistence> eventHashMap) {
        for (Map.Entry<String, Persistence> entry : eventHashMap.entrySet()) {
            Persistence event = entry.getValue();
            if (event.getData("name").equals(name) || name.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public boolean validateEventDate(String date, String searchId) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        EventController eventController = new EventController();
        SubEventController subEventController = new SubEventController();
        Map<String, Persistence> parentMap = new HashMap<>(eventController.getHashMap());
        parentMap.putAll(subEventController.getHashMap());


        String parentDateString = "";
        for (Map.Entry<String, Persistence> entry : parentMap.entrySet()) {
            Persistence listIndex = entry.getValue();
            if (listIndex.getData("name").equals(searchId)) {
                parentDateString = listIndex.getData("date");
                break;
            }
        }
            LocalDate eventDate = LocalDate.parse(parentDateString, formatter);
            LocalDate inputDate = LocalDate.parse(date, formatter);

            return !inputDate.isBefore(eventDate);
    }

    public abstract void setFacade(FacadeInterface facade) throws IOException;
}

