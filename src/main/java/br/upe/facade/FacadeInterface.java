package br.upe.facade;

import br.upe.persistence.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface FacadeInterface {

    // EventController methods

    List<Model> getAllEvent();

    List<Event> listEvents(Object... params) throws IOException;

    Object[] createEvent(Object... params) throws IOException;

    boolean updateEvent(Object... params) throws IOException;

    boolean deleteEvent(Object... params) throws IOException;

    String getEventData(String dataToGet);

    Object[] isEventExist(Object... params) throws IOException;


    // SubEventController methods

    List<SubEvent> listSubEvents(Object... params) throws IOException;

    List<Model> getAllSubEvent();

    Object[] createSubEvent(Object... params) throws IOException;

    boolean updateSubEvent(Object... params) throws IOException;

    boolean deleteSubEvent(Object... params) throws IOException;

    Object getSubEventData(String dataToGet);

    Object[] isSubEventExist(Object... params) throws IOException;


    // SessionController methods

    List<Model> getAllSession();

    List<Session> listSessions(Object... params) throws IOException;

    Object[] createSession(Object... params) throws IOException;

    boolean updateSession(Object... params) throws IOException;

    boolean deleteSession(Object... params) throws IOException;

    Object getSessionData(String dataToGet);

    Object[] isSessionExist(Object... params) throws IOException;


    // SubmitArticleController methods

    List<SubmitArticle> listSubmitArticles(Object... params) throws IOException;

    <T> List<T> getEventArticles(UUID eventId);

    Object[] createArticle(Object... params) throws IOException;

    boolean deleteArticle(Object... params) throws IOException;

    boolean updateArticle(Object... params) throws IOException;

    Object getArticleData(String dataToGet);

    Object[] isArticleExist(Object... params) throws IOException;


    // UserController methods

    Object[] createUser(Object... params) throws IOException;

    boolean updateUser(Object... params) throws IOException;

    boolean deleteUser(Object... params) throws IOException;

    boolean loginValidate(String email, String password) throws IOException;

    String getUserData(String dataToGet);



    // AttendeeController methods

    List<Attendee> listAttendees(Object... params) throws IOException;

    Object[] createAttendee(Object... params) throws IOException;

    boolean deleteAttendee(Object... params) throws IOException;

    Object getAttendeeData(String dataToGet);

    Object[] isAttendeeExist(Object... params) throws IOException;

}