package br.upe.facade;
import br.upe.controller.*;
import br.upe.persistence.Persistence;
import java.io.IOException;
import java.util.List;
import java.util.Map;


public class Facade {

    private final EventController eventController;
    private final SessionController sessionController;
    private final SubEventController subEventController;
    private final SubmitArticleController submitArticleController;
    private final UserController userController;

    public Facade() throws IOException {
        this.eventController = new EventController();
        this.sessionController = new SessionController();
        this.subEventController = new SubEventController();
        this.submitArticleController = new SubmitArticleController();
        this.userController = new UserController();
    }

    // EventController methods
    public Map<String, Persistence> getEventHashMap() {
        return eventController.getEventHashMap();
    }

    public boolean listEvents(String ownerId) throws IOException {
        return eventController.list(ownerId);
    }

    public List<String> listEvents(String ownerId, String type) throws IOException {
        return eventController.list(ownerId, type);
    }

    public void createEvent(Object... params) {
        eventController.create(params);
    }

    public void readEvent() throws IOException {
        eventController.read();
    }

    public void updateEvent(Object... params) throws IOException {
        eventController.update(params);
    }

    public void deleteEvent(Object... params) throws IOException {
        eventController.delete(params);
    }

    // SessionController methods
    public Map<String, Persistence> getSessionHashMap() {
        return sessionController.getSessionHashMap();
    }

    public boolean listSessions(String ownerId) throws IOException {
        return sessionController.list(ownerId);
    }

    public List<String> listSessions(String ownerId, String type) throws IOException {
        return sessionController.list(ownerId, type);
    }

    public void updateSession(Object... params) throws IOException {
        sessionController.update(params);
    }

    public void deleteSession(Object... params) throws IOException {
        sessionController.delete(params);
    }

    // SubEventController methods
    public Map<String, Persistence> getSubEventHashMap() {
        return subEventController.getSubEventHashMap();
    }

    public boolean listSubEvents(String ownerId) throws IOException {
        return subEventController.list(ownerId);
    }

    public List<String> listSubEvents(String ownerId, String type) throws IOException {
        return subEventController.list(ownerId, type);
    }

    public void updateSubEvent(Object... params) throws IOException {
        subEventController.update(params);
    }

    public void deleteSubEvent(Object... params) throws IOException {
        subEventController.delete(params);
    }

    // SubmitArticleController methods
    public Map<String, Persistence> getArticleHashMap() {
        return submitArticleController.getArticleHashMap();
    }

    public void createArticle(Object... params) throws IOException {
        submitArticleController.create(params);
    }

    public void deleteArticle(Object... params) throws IOException {
        submitArticleController.delete(params);
    }

    public void updateArticle(Object... params) throws IOException {
        submitArticleController.update(params);
    }

    // UserController methods
    public Map<String, Persistence> getUserHashMap() {
        return userController.getUserHashMap();
    }

    public void createUser(Object... params) {
        userController.create(params);
    }

    public void updateUser(Object... params) throws IOException {
        userController.update(params);
    }

    public void deleteUser(Object... params) throws IOException {
        userController.delete(params);
    }

    public boolean loginValidate(String email, String cpf) {
        return userController.loginValidate(email, cpf);
    }
}