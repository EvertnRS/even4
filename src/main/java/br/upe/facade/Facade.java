package br.upe.facade;
import br.upe.controller.*;
import br.upe.persistence.Persistence;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Facade implements FacadeInterface {
    private final Controller eventController;
    private final Controller sessionController;
    private final Controller subEventController;
    private final Controller submitArticleController;
    private final Controller attendeeController;
    private final Controller userController;

    public Facade(Controller userController) throws IOException {
        this.userController = userController;
        this.eventController = new EventController();
        this.sessionController = new SessionController();
        this.subEventController = new SubEventController();
        this.submitArticleController = new SubmitArticleController();
        this.attendeeController = new AttendeeController();
    }


    // EventController methods
    public Map<UUID, Persistence> getEventHashMap() {
        return eventController.getHashMap();
    }

    public List<String> listEvents(Object... params) throws IOException {
        return eventController.list(params);
    }

    public void createEvent(Object... params) throws IOException {
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

    public String getEventData(String dataToGet) {
        return eventController.getData(dataToGet);
    }


    // SessionController methods
    public Map<UUID, Persistence> getSessionHashMap() {
        return sessionController.getHashMap();
    }


    public List<String> listSessions(Object... params) throws IOException {
        return sessionController.list(params);
    }

    public void createSession(Object... params) throws IOException {
        sessionController.create(params);
    }

    public void readSession() throws IOException {
        sessionController.read();
    }

    public void updateSession(Object... params) throws IOException {
        sessionController.update(params);
    }

    public void deleteSession(Object... params) throws IOException {
        sessionController.delete(params);
    }

    public String getSessionData(String dataToGet) {
        return sessionController.getData(dataToGet);
    }


    // SubEventController methods
    public Map<UUID, Persistence> getSubEventHashMap() {
        return subEventController.getHashMap();
    }

    public List<String> listSubEvents(Object... params) throws IOException {
        return subEventController.list(params);
    }

    public void updateSubEvent(Object... params) throws IOException {
        subEventController.update(params);
    }

    public void deleteSubEvent(Object... params) throws IOException {
        subEventController.delete(params);
    }

    public void createSubEvent(Object... params) throws IOException {
        subEventController.create(params);
    }

    public void readSubEvent() throws IOException {
        subEventController.read();
    }

    // SubmitArticleController methods
    public Map<UUID, Persistence> getArticleHashMap() {
        return submitArticleController.getHashMap();
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

    public void readArticle(String id) throws IOException {
        submitArticleController.read();
    }

    // UserController methods
    public Map<UUID, Persistence> getUserHashMap() {
        return userController.getHashMap();
    }

    public void createUser(Object... params) throws IOException {
        userController.create(params);
    }

    public void readUser() throws IOException {
        userController.read();
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

    public String getUserData(String dataToGet) {
        return userController.getData(dataToGet);
    }

    // AttendeeController methods
    public Map<UUID, Persistence> getAttendeeHashMap() {
        return attendeeController.getHashMap();
    }

    public void createAttendee(Object... params) throws IOException {
        attendeeController.create(params);
    }

    public void readAttendee() throws IOException {
        attendeeController.read();
    }

    public void updateAttendee(Object... params) throws IOException {
        attendeeController.update(params);
    }

    public List<String> listAttendees(Object... params) throws IOException {
        return attendeeController.list(params);
    }

    public void deleteAttendee(Object... params) throws IOException {
        attendeeController.delete(params);
    }
}