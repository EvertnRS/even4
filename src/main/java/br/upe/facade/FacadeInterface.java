package br.upe.facade;

import br.upe.persistence.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface FacadeInterface {

    // EventController methods

    List<Model> getAllEvent();

    List<Event> listEvents(Object... params) throws IOException;

    void createEvent(Object... params) throws IOException;

    void updateEvent(Object... params) throws IOException;

    void deleteEvent(Object... params) throws IOException;

    String getEventData(String dataToGet);


    // SubEventController methods

    List<SubEvent> listSubEvents(Object... params) throws IOException;

    List<Model> getAllSubEvent();

    void createSubEvent(Object... params) throws IOException;

    void updateSubEvent(Object... params) throws IOException;

    void deleteSubEvent(Object... params) throws IOException;

    Object getSubEventData(String dataToGet);


    // SessionController methods

    List<Model> getAllSession();

    List<Session> listSessions(Object... params) throws IOException;

    void createSession(Object... params) throws IOException;

    void updateSession(Object... params) throws IOException;

    void deleteSession(Object... params) throws IOException;

    Object getSessionData(String dataToGet);


    // SubmitArticleController methods

    List<SubmitArticle> listSubmitArticles(Object... params) throws IOException;

    <T> List<T> getEventArticles(UUID eventId);

    void createArticle(Object... params) throws IOException;

    void deleteArticle(Object... params) throws IOException;

    void updateArticle(Object... params) throws IOException;

    Object getArticleData(String dataToGet);


    // UserController methods

    void createUser(Object... params) throws IOException;

    void updateUser(Object... params) throws IOException;

    void deleteUser(Object... params) throws IOException;

    boolean loginValidate(String email, String password);

    String getUserData(String dataToGet);


    // AttendeeController methods

    List<Attendee> listAttendees(Object... params) throws IOException;

    void createAttendee(Object... params) throws IOException;

    void deleteAttendee(Object... params) throws IOException;

    Object getAttendeeData(String dataToGet);

}