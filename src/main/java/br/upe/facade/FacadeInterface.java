package br.upe.facade;

import br.upe.persistence.Event;
import br.upe.persistence.repository.Persistence;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface FacadeInterface {
    // EventController methods
    List<Event> getAllEvent();
    Map<UUID, Persistence> getEventHashMap() ;
    List<Event> listEvents(Object... params) throws IOException;
    boolean createEvent(Object... params) throws IOException;
    void readEvent() throws IOException;
    boolean updateEvent(Object... params) throws IOException;
    boolean deleteEvent(Object... params) throws IOException;
    String getEventData(String dataToGet);

    // SessionController methods
    Map<UUID, Persistence> getSessionHashMap();
    List<String> listSessions(Object... params) throws IOException;
    boolean createSession(Object... params) throws IOException;
    void readSession() throws IOException;
    boolean updateSession(Object... params) throws IOException;
    boolean deleteSession(Object... params) throws IOException;
    String getSessionData(String dataToGet);

    // SubEventController methods
    Map<UUID, Persistence> getSubEventHashMap();
    List<String> listSubEvents(Object... params) throws IOException;
    boolean updateSubEvent(Object... params) throws IOException;
    boolean deleteSubEvent(Object... params) throws IOException;
    boolean createSubEvent(Object... params) throws IOException;
    void readSubEvent() throws IOException;

    // SubmitArticleController methods
    Map<UUID, Persistence> getArticleHashMap();
    boolean createArticle(Object... params) throws IOException;
    boolean deleteArticle(Object... params) throws IOException;
    boolean updateArticle(Object... params) throws IOException;
    void readArticle(String id) throws IOException;

    // UserController methods
    Map<UUID, Persistence> getUserHashMap();
    boolean createUser(Object... params) throws IOException;
    boolean updateUser(Object... params) throws IOException;
    boolean deleteUser(Object... params) throws IOException;
    void readUser() throws IOException;
    boolean loginValidate(String email, String password);
    String getUserData(String dataToGet);

    // AttendeeController methods
    Map<UUID, Persistence> getAttendeeHashMap();
    boolean createAttendee(Object... params) throws IOException;
    void readAttendee() throws IOException;
    boolean updateAttendee(Object... params) throws IOException;
    List<String> listAttendees(Object... params) throws IOException;
    boolean deleteAttendee(Object... params) throws IOException;
}