package br.upe.controller.fx;

import br.upe.controller.fx.mediator.UpdateSubmitMediator;
import br.upe.facade.FacadeInterface;
import br.upe.persistence.Event;
import br.upe.persistence.Model;
import br.upe.persistence.repository.EventRepository;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class UpdateSubmitScreenController extends BaseController implements FxController {
    private FacadeInterface facade;
    private String eventName;
    private String nameArticle;
    private UUID ArticleId;
    private UpdateSubmitMediator mediator;

    @FXML
    private AnchorPane submitPane;
    @FXML
    private Label userEmail;
    @FXML
    private ComboBox<String> eventComboBox;

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
    public void setNameArticle(String nameArticle) {
        this.nameArticle = nameArticle;
    }
    public void setArticleId(UUID articleId) {this.ArticleId = articleId;}

    public void setFacade(FacadeInterface facade) throws IOException {
        this.facade = facade;
        initial();
    }

    private void initial() throws IOException {
        userEmail.setText(facade.getUserData("email"));
        loadArticles();

        this.mediator = new UpdateSubmitMediator(this, facade, submitPane, null);
        mediator.registerComponents();
    }

    private void loadArticles() throws IOException {
        List<Model> allEvents = facade.getAllEvent();
        eventComboBox.getItems().clear();

        EventRepository eventRepository = EventRepository.getInstance();

        for (Model event : allEvents) {
            String eventName = (String) eventRepository.getData(event.getId(), "name");

            if (eventName != null) {
                eventComboBox.getItems().add(eventName);
            }
        }
    }

    public void updateArticle() throws IOException {
        String novoEventName = eventComboBox.getSelectionModel().getSelectedItem();

        List<Model> allEvents = facade.getAllEvent();

        UUID eventId = null;

        for (Model event : allEvents) {
            if (event.getName().equals(novoEventName)) {
                eventId = event.getId();
                break;
            }
        }

        if (eventId != null) {
            facade.updateArticle(eventId, ArticleId);
            mediator.notify("handleBack");
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