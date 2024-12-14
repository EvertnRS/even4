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

    public void createEvent(Object... params) throws IOException {
        eventController.create(params);
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

    // SubEventController methods

    public List<Model> getAllSubEvent() {
        return subEventController.getAll();
    }

    public List<SubEvent> listSubEvents(Object... params) throws IOException {
        return subEventController.list(params);
    }

    public void createSubEvent(Object... params) throws IOException {
        subEventController.create(params);
    }

    public void updateSubEvent(Object... params) throws IOException {
        subEventController.update(params);
    }

    public void deleteSubEvent(Object... params) throws IOException {
        subEventController.delete(params);
    }

    public Object getSubEventData(String dataToGet){
        return subEventController.getData(dataToGet);
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

    public void createSession(Object... params) throws IOException {
        sessionController.create(params);
    }

    public void updateSession(Object... params) throws IOException {
        sessionController.update(params);
    }

    public void deleteSession(Object... params) throws IOException {
        sessionController.delete(params);
    }

    public Object getSessionData(String dataToGet) {
        return sessionController.getData(dataToGet);
    }


    // SubmitArticleController methods

    public List<SubmitArticle> listSubmitArticles(Object... params) throws IOException {
        return submitArticleController.list(params);
    }

    public <T> List<T> getEventArticles(UUID eventId) {
        return submitArticleController.getEventArticles(eventId);
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

    public Object getArticleData(String dataToGet){
        return submitArticleController.getData(dataToGet);
    }


    // UserController methods

    public void createUser(Object... params) throws IOException {
        userController.create(params);
    }

    public void updateUser(Object... params) throws IOException {
        userController.update(params);
    }

    public void deleteUser(Object... params) throws IOException {
        userController.delete(params);
    }

    @Override
    public boolean loginValidate(String email, String password) {
        return userController.loginValidate(email, password);
    }

    public String getUserData(String dataToGet) {
        return userController.getData(dataToGet);
    }

    // AttendeeController methods

    public void createAttendee(Object... params) throws IOException {
        attendeeController.create(params);
    }

    public List<Attendee> listAttendees(Object... params) throws IOException {
        return attendeeController.list(params);
    }

    public void deleteAttendee(Object... params) throws IOException {
        attendeeController.delete(params);
    }

    public Object getAttendeeData(String dataToGet){
        return attendeeController.getData(dataToGet);
    }

}