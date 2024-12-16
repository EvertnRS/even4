package br.upe.facade;

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

public interface FacadeInterface {
    // EventController methods
    List<Model> getAllEvent();
    Map<UUID, Persistence> getEventHashMap() ;
    List<Event> listEvents(Object... params) throws IOException;
    Object[] createEvent(Object... params) throws IOException;
    void readEvent() throws IOException;
    boolean updateEvent(Object... params) throws IOException;
    boolean deleteEvent(Object... params) throws IOException;
    String getEventData(String dataToGet);
    Object[] isEventExist(Object... params) throws IOException;

    // SessionController methods
    List<Model> getAllSession();
    Map<UUID, Persistence> getSessionHashMap();
    List<Session> listSessions(Object... params) throws IOException;
    Object[] createSession(Object... params) throws IOException;
    void readSession() throws IOException;
    boolean updateSession(Object... params) throws IOException;
    boolean deleteSession(Object... params) throws IOException;
    String getSessionData(String dataToGet);
    Object[] isSessionExist(Object... params) throws IOException;

    // SubEventController methods
    Map<UUID, Persistence> getSubEventHashMap();
    List<SubEvent> listSubEvents(Object... params) throws IOException;
    List<Model> getAllSubEvent();
    boolean updateSubEvent(Object... params) throws IOException;
    boolean deleteSubEvent(Object... params) throws IOException;
    Object[] createSubEvent(Object... params) throws IOException;
    void readSubEvent() throws IOException;
    String getSubEventData(String dataToGet);
    Object[] isSubEventExist(Object... params) throws IOException;

    // SubmitArticleController methods
    Map<UUID, Persistence> getArticleHashMap();
    Object[] createArticle(Object... params) throws IOException;
    boolean deleteArticle(Object... params) throws IOException;
    boolean updateArticle(Object... params) throws IOException;
    void readArticle(String id) throws IOException;
    List<SubmitArticle> listSubmitArticles(Object... params) throws IOException;
    <T> List <T> getEventArticles(UUID eventId);
    Object[] isArticleExist(Object... params) throws IOException;

    // UserController methods
    Map<UUID, Persistence> getUserHashMap();
    Object[] createUser(Object... params) throws IOException;
    boolean updateUser(Object... params) throws IOException;
    boolean deleteUser(Object... params) throws IOException;
    void readUser() throws IOException;
    boolean loginValidate(String email, String password) throws IOException;
    String getUserData(String dataToGet);

    // AttendeeController methods
    Map<UUID, Persistence> getAttendeeHashMap();
    Object[] createAttendee(Object... params) throws IOException;
    void readAttendee() throws IOException;
    List<Attendee> listAttendees(Object... params) throws IOException;
    boolean deleteAttendee(Object... params) throws IOException;
    Object[] isAttendeeExist(Object... params) throws IOException;
}