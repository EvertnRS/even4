package br.upe.facade;

import br.upe.controller.*;
import br.upe.persistence.*;

import java.io.IOException;
import java.util.List;
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

    public List<Event> listEvents(Object... params) throws IOException {
        return eventController.list(params);
    }

    public Object[] createEvent(Object... params) throws IOException {
        return eventController.create(params);
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

    @Override
    public Object[] isEventExist(Object... params) throws IOException {
        return eventController.isExist(params);
    }

    // SubEventController methods

    public List<Model> getAllSubEvent() {
        return subEventController.getAll();
    }

    public List<SubEvent> listSubEvents(Object... params) throws IOException {
        return subEventController.list(params);
    }

    public Object[] createSubEvent(Object... params) throws IOException {
        return subEventController.create(params);
    }

    public boolean updateSubEvent(Object... params) throws IOException {
        return subEventController.update(params);
    }

    public boolean deleteSubEvent(Object... params) throws IOException {
        return subEventController.delete(params);
    }

    public Object getSubEventData(String dataToGet){
        return subEventController.getData(dataToGet);
    }

    @Override
    public Object[] isSubEventExist(Object... params) throws IOException {
        return subEventController.isExist(params);
    }

    // SessionController methods

    @Override
    public List<Model> getAllSession() {
        return sessionController.getAll();
    }

    public List<Model> getAllSessions() {
        return eventController.getAll();
    }


    public List<Session> listSessions(Object... params) throws IOException {
        return sessionController.list(params);
    }

    public Object[] createSession(Object... params) throws IOException {
        return sessionController.create(params);
    }

    public boolean updateSession(Object... params) throws IOException {
        return sessionController.update(params);
    }

    public boolean deleteSession(Object... params) throws IOException {
        return sessionController.delete(params);
    }

    public Object getSessionData(String dataToGet) {
        return sessionController.getData(dataToGet);
    }

    @Override
    public Object[] isSessionExist(Object... params) throws IOException {
        return sessionController.isExist(params);
    }

    public String[] verifyBySessionName(String sessionName) {
        return sessionController.verifyBySessionName(sessionName);
    }

    public String[] verifyByEventName(String sessionName) {
        return sessionController.verifyByEventName(sessionName);
    }


    // SubmitArticleController methods

    public List<SubmitArticle> listSubmitArticles(Object... params) throws IOException {
        return submitArticleController.list(params);
    }

    public <T> List<T> getEventArticles(UUID eventId) {
        return submitArticleController.getEventArticles(eventId);
    }

    public Object[] createArticle(Object... params) throws IOException {
        return submitArticleController.create(params);
    }

    public boolean deleteArticle(Object... params) throws IOException {
        return submitArticleController.delete(params);
    }

    public boolean updateArticle(Object... params) throws IOException {
        return submitArticleController.update(params);
    }

    public Object getArticleData(String dataToGet){
        return submitArticleController.getData(dataToGet);
    }

    @Override
    public Object[] isArticleExist(Object... params) throws IOException {
        return submitArticleController.isExist(params);
    }


    // UserController methods

    public Object[] createUser(Object... params) throws IOException {
        return userController.create(params);
    }

    public boolean updateUser(Object... params) throws IOException {
        return userController.update(params);
    }

    public boolean deleteUser(Object... params) throws IOException {
        return userController.delete(params);
    }

    @Override
    public boolean loginValidate(String email, String password) throws IOException {
        Object[] results = userController.isExist(email, password);
        return (boolean) results[0];
    }

    public String getUserData(String dataToGet) {
        return userController.getData(dataToGet);
    }

    // AttendeeController methods

    public Object[] createAttendee(Object... params) throws IOException {
        return attendeeController.create(params);
    }

    public List<Attendee> listAttendees(Object... params) throws IOException {
        return attendeeController.list(params);
    }

    public boolean deleteAttendee(Object... params) throws IOException {
        return attendeeController.delete(params);
    }

    public Object getAttendeeData(String dataToGet){
        return attendeeController.getData(dataToGet);
    }

    @Override
    public Object[] isAttendeeExist(Object... params) throws IOException {
        return attendeeController.isExist(params);
    }

}