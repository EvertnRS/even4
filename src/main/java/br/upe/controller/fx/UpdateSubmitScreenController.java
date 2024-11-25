package br.upe.controller.fx;

import br.upe.facade.FacadeInterface;
import br.upe.persistence.Event;
import br.upe.persistence.repository.EventRepository;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class UpdateSubmitScreenController extends BaseController implements FxController {
    private FacadeInterface facade;
    private String eventName;
    private String nameArticle;
    private UUID ArticleId;

    @FXML
    private AnchorPane submitPane;
    @FXML
    private Label userEmail;
    @FXML
    private ComboBox<String> eventComboBox;
    @FXML


    public void setFacade(FacadeInterface facade) throws IOException {
        this.facade = facade;
        initial();
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
    public void setNameArticle(String nameArticle) {
        this.nameArticle = nameArticle;
    }

    public void setArticleId(UUID articleId) {this.ArticleId = articleId;}


    private void initial() throws IOException {
        userEmail.setText(facade.getUserData("email"));
        loadArticles();
    }

    private void loadArticles() throws IOException {
        List<Event> allEvents = facade.getAllEvent();
        eventComboBox.getItems().clear();

        EventRepository eventRepository = EventRepository.getInstance();

        for (Event event : allEvents) {
            String eventName = (String) eventRepository.getData(event.getId(), "name");

            if (eventName != null) {
                eventComboBox.getItems().add(eventName);
            }
        }
    }

    public void handleEvent() throws IOException {
        genericButton("/fxml/mainScreen.fxml", submitPane, facade, null);
    }

    public void handleSubEvent() throws IOException {
        genericButton("/fxml/subEventScreen.fxml", submitPane, facade, null);
    }

    public void handleSubmitEvent() throws IOException {
        genericButton("/fxml/submitScreen.fxml", submitPane, facade, null);
    }


    public void logout() throws IOException {
        genericButton("/fxml/loginScreen.fxml", submitPane, facade, null);
    }

    public void handleUser() throws IOException {
        genericButton("/fxml/userScreen.fxml", submitPane, facade, null);
    }

    public void handleSession() throws IOException {
        genericButton("/fxml/sessionScreen.fxml", submitPane, facade, null);
    }

    public void handleSubmit() throws IOException {
        genericButton("/fxml/submitScreen.fxml", submitPane, facade, null);
    }

    public void updateArticle() throws IOException {
        String novoEventName = eventComboBox.getSelectionModel().getSelectedItem();

        List<Event> allEvents = facade.getAllEvent();

        UUID eventId = null;

        for (Event event : allEvents) {
            if (event.getName().equals(novoEventName)) {
                eventId = event.getId();
                break;
            }
        }

        if (eventId != null) {
            facade.updateArticle(eventId, ArticleId);
            handleSubmit();
        }
    }


}