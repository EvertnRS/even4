package br.upe.facade;

import br.upe.persistence.Persistence;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface FacadeInterface {
    // EventController methods
    Map<String, Persistence> getEventHashMap() ;
    List<String> listEvents(Object... params) throws IOException;
    void createEvent(Object... params) throws IOException;
    void readEvent() throws IOException;
    void updateEvent(Object... params) throws IOException;
    void deleteEvent(Object... params) throws IOException;
    String getEventData(String dataToGet);

    // SessionController methods
    Map<String, Persistence> getSessionHashMap();
    List<String> listSessions(Object... params) throws IOException;
    void createSession(Object... params) throws IOException;
    void readSession() throws IOException;
    void updateSession(Object... params) throws IOException;
    void deleteSession(Object... params) throws IOException;
    String getSessionData(String dataToGet);

    // SubEventController methods
    Map<String, Persistence> getSubEventHashMap();
    List<String> listSubEvents(Object... params) throws IOException;
    void updateSubEvent(Object... params) throws IOException;
    void deleteSubEvent(Object... params) throws IOException;
    void createSubEvent(Object... params) throws IOException;
    void readSubEvent() throws IOException;

    // SubmitArticleController methods
    Map<String, Persistence> getArticleHashMap();
    void createArticle(Object... params) throws IOException;
    void deleteArticle(Object... params) throws IOException;
    void updateArticle(Object... params) throws IOException;
    void readArticle(String id) throws IOException;

    // UserController methods
    Map<String, Persistence> getUserHashMap();
    void createUser(Object... params) throws IOException;
    void updateUser(Object... params) throws IOException;
    void deleteUser(Object... params) throws IOException;
    void readUser() throws IOException;
    boolean loginValidate(String email, String cpf);
    String getUserData(String dataToGet);

    // AttendeeController methods
    Map<String, Persistence> getAttendeeHashMap();
    void createAttendee(Object... params) throws IOException;
    void readAttendee() throws IOException;
    void updateAttendee(Object... params) throws IOException;
    List<String> listAttendees(Object... params) throws IOException;
    void deleteAttendee(Object... params) throws IOException;
}