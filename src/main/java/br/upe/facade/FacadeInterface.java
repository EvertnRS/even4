package br.upe.facade;

import br.upe.persistence.Event;
import br.upe.persistence.Model;
import br.upe.persistence.SubEvent;
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
    void createEvent(Object... params) throws IOException;
    void readEvent() throws IOException;
    void updateEvent(Object... params) throws IOException;
    void deleteEvent(Object... params) throws IOException;
    String getEventData(String dataToGet);

    // SessionController methods
    Map<UUID, Persistence> getSessionHashMap();
    List<String> listSessions(Object... params) throws IOException;
    void createSession(Object... params) throws IOException;
    void readSession() throws IOException;
    void updateSession(Object... params) throws IOException;
    void deleteSession(Object... params) throws IOException;
    String getSessionData(String dataToGet);

    // SubEventController methods
    Map<UUID, Persistence> getSubEventHashMap();
    List<SubEvent> listSubEvents(Object... params) throws IOException;
    List<Model> getAllSubEvent();
    void updateSubEvent(Object... params) throws IOException;
    void deleteSubEvent(Object... params) throws IOException;
    void createSubEvent(Object... params) throws IOException;
    void readSubEvent() throws IOException;

    // SubmitArticleController methods
    Map<UUID, Persistence> getArticleHashMap();
    void createArticle(Object... params) throws IOException;
    void deleteArticle(Object... params) throws IOException;
    void updateArticle(Object... params) throws IOException;
    void readArticle(String id) throws IOException;

    // UserController methods
    Map<UUID, Persistence> getUserHashMap();
    void createUser(Object... params) throws IOException;
    void updateUser(Object... params) throws IOException;
    void deleteUser(Object... params) throws IOException;
    void readUser() throws IOException;
    boolean loginValidate(String email, String password);
    String getUserData(String dataToGet);

    // AttendeeController methods
    Map<UUID, Persistence> getAttendeeHashMap();
    void createAttendee(Object... params) throws IOException;
    void readAttendee() throws IOException;
    void updateAttendee(Object... params) throws IOException;
    List<String> listAttendees(Object... params) throws IOException;
    void deleteAttendee(Object... params) throws IOException;
}