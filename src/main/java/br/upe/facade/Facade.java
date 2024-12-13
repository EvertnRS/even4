package br.upe.facade;
import br.upe.controller.*;
import br.upe.persistence.Event;
import br.upe.persistence.Model;
import br.upe.persistence.Session;
import br.upe.persistence.SubEvent;
import br.upe.persistence.*;
import br.upe.persistence.repository.Persistence;
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
    public List<Model> getAllEvent() {
        return eventController.getAll();
    }

    public Map<UUID, Persistence> getEventHashMap() {
        return eventController.getHashMap();
    }

    public List<Event> listEvents(Object... params) throws IOException {
        return eventController.list(params);
    }

    public boolean createEvent(Object... params) throws IOException {
        return eventController.create(params);
    }

    public void readEvent() throws IOException {
        eventController.read();
    }

    public boolean updateEvent(Object... params) throws IOException {
        return eventController.update(params);
    }

    public boolean deleteEvent(Object... params) throws IOException {
        return eventController.delete(params);
    }

    public String getEventData(String dataToGet) {
        return eventController.getData(dataToGet);
    }


    // SessionController methods
    @Override
    public List<Model> getAllSession() {
        return sessionController.getAll();
    }

    public Map<UUID, Persistence> getSessionHashMap() {
        return sessionController.getHashMap();
    }

    public List<Model> getAllSessions() {
        return eventController.getAll();
    }


    public List<Session> listSessions(Object... params) throws IOException {
        return sessionController.list(params);
    }

    public boolean createSession(Object... params) throws IOException {
        return sessionController.create(params);
    }

    public void readSession() throws IOException {
        sessionController.read();
    }

    public boolean updateSession(Object... params) throws IOException {
        return sessionController.update(params);
    }

    public boolean deleteSession(Object... params) throws IOException {
        return sessionController.delete(params);
    }

    public String getSessionData(String dataToGet) {
        return sessionController.getData(dataToGet);
    }


    // SubEventController methods
    public List<Model> getAllSubEvent() {
        return subEventController.getAll();
    }

    public Map<UUID, Persistence> getSubEventHashMap() {
        return subEventController.getHashMap();
    }

    public List<SubEvent> listSubEvents(Object... params) throws IOException {
        return subEventController.list(params);
    }

    public boolean updateSubEvent(Object... params) throws IOException {
        return subEventController.update(params);
    }

    public boolean deleteSubEvent(Object... params) throws IOException {
        return subEventController.delete(params);
    }

    public boolean createSubEvent(Object... params) throws IOException {
        return subEventController.create(params);
    }

    public void readSubEvent() throws IOException {
        subEventController.read();
    }

    public String getSubEventData(String dataToGet) {
        return subEventController.getData(dataToGet);
    }

    // SubmitArticleController methods
    public Map<UUID, Persistence> getArticleHashMap() {
        return submitArticleController.getHashMap();
    }

    public boolean createArticle(Object... params) throws IOException {
        return submitArticleController.create(params);
    }

    public boolean deleteArticle(Object... params) throws IOException {
        return submitArticleController.delete(params);
    }

    public boolean updateArticle(Object... params) throws IOException {
        return submitArticleController.update(params);
    }

    public void readArticle(String id) throws IOException {
        submitArticleController.read();
    }

    public List<SubmitArticle> listSubmitArticles(Object... params) throws IOException {
        return submitArticleController.list(params);
    }

    public <T> List <T> getEventArticles(UUID eventId){
        return (List<T>) submitArticleController.getEventArticles(eventId);
    }

    // UserController methods
    public Map<UUID, Persistence> getUserHashMap() {
        return userController.getHashMap();
    }

    public boolean createUser(Object... params) throws IOException {
        return userController.create(params);
    }

    public void readUser() throws IOException {
        userController.read();
    }

    public boolean updateUser(Object... params) throws IOException {
        return userController.update(params);
    }

    public boolean deleteUser(Object... params) throws IOException {
        return userController.delete(params);
    }

    @Override
    public boolean loginValidate(String email, String password) {
        return userController.loginValidate(email, password);
    }

    public String getUserData(String dataToGet) {
        return userController.getData(dataToGet);
    }

    // AttendeeController methods
    public Map<UUID, Persistence> getAttendeeHashMap() {
        return attendeeController.getHashMap();
    }

    public boolean createAttendee(Object... params) throws IOException {
        return attendeeController.create(params);
    }

    public void readAttendee() throws IOException {
        attendeeController.read();
    }

    public List<Attendee> listAttendees(Object... params) throws IOException {
        return attendeeController.list(params);
    }

    public boolean deleteAttendee(Object... params) throws IOException {
        return attendeeController.delete(params);
    }
}