package br.upe.ui;
/*
import br.upe.controller.*;
import br.upe.facade.Facade;
import br.upe.persistence.Repository.Persistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FacadeIntegrationTest {

    private Facade facade;

    @BeforeEach
    void setUp() throws IOException {
        facade = new Facade(new UserController());
    }

    @Test
    void testCreateAttendee() throws IOException {
        attendeeExists();

        Map<UUID, Persistence> attendeeMap = facade.getAttendeeHashMap();
        boolean attendeeCreated = attendeeMap.values().stream()
                .anyMatch(attendee -> attendee.getData("name").equals("Man"));
        assertTrue(attendeeCreated, "O participante criado não foi encontrado.");

        attendeeDelete();
    }

    @Test
    void testUpdateAttendee() throws IOException {
        attendeeExists();

        facade.updateAttendee("Duda", facade.getSessionHashMap().values().stream().filter(subSession -> subSession.getData("name").equals("Session1")).findFirst().map(session -> session.getData("id")).orElse(null));

        Map<UUID, Persistence> attendeeMap = facade.getAttendeeHashMap();
        boolean attendeeUpdated = attendeeMap.values().stream()
                .anyMatch(attendee -> attendee.getData("name").equals("Duda"));
        assertTrue(attendeeUpdated, "O Participante não foi atualizado.");

        attendeeDelete();
    }

    @Test
    void testReadAttendee() throws IOException {
        attendeeExists();

        String attendeeReaded = "";
        Map<UUID, Persistence> attendeeHashMap = facade.getAttendeeHashMap();
        for (Map.Entry<UUID, Persistence> entry : attendeeHashMap.entrySet()) {
            Persistence persistence = entry.getValue();
            if (persistence.getData("sessionId").equals(facade.getSessionHashMap().values().stream().filter(subSession -> subSession.getData("name").equals("Session1")).findFirst().map(session -> session.getData("id")).orElse(null))) {
                attendeeReaded = (String) persistence.getData("name");
            }
        }
        assertEquals("Man", attendeeReaded, "A sessão não foi lida.");
        attendeeDelete();
    }

    @Test
    void testDeleteAttendee() throws IOException {
        attendeeExists();
        attendeeDelete();

        Map<UUID, Persistence> attendeeMap = facade.getAttendeeHashMap();
        boolean attendeeDeleted = attendeeMap.values().stream()
                .noneMatch(attendee -> attendee.getData("name").equals("Man"));
        assertTrue(attendeeDeleted, "A sessão não foi deletada.");

    }

    void attendeeExists() throws IOException {
        boolean sessionExists = facade.getAttendeeHashMap().values().stream()
                .anyMatch(attendee -> attendee.getData("name").equals("Man"));

        if (!sessionExists) {
            facade.createUser("newuser@example.com", "09876543211");
            facade.readUser();
            boolean loginSuccessful = facade.loginValidate("newuser@example.com", "09876543211");
            assertTrue(loginSuccessful, "Login falhou, não é possível atualizar o usuário");
            String userId = facade.getUserData("id");

            facade.createEvent("Event2", "01/12/2024", "Event Description", "Event Location", userId);
            facade.readEvent();
            facade.createSession("Event2", "Session1", "01/12/2024", "Session Description", "Session Location", "08:00", "10:00", userId, "Event");
            facade.readSession();

            UUID sessionId = (UUID) facade.getSessionHashMap().values().stream().filter(subSession -> subSession.getData("name").equals("Session1")).findFirst().map(session -> session.getData("id")).orElse(null);
            facade.createAttendee("Man", sessionId, userId);
            facade.readAttendee();


        }
    }

    void attendeeDelete() throws IOException {
        boolean loginSuccessful = facade.loginValidate("newuser@example.com", "09876543211");
        assertTrue(loginSuccessful, "Login falhou, não é possível atualizar o usuário");
        String userId = facade.getUserData("id");
        facade.deleteUser(userId, "id");

        UUID sessionId = (UUID) facade.getSessionHashMap().values().stream().filter(subSession -> subSession.getData("name").equals("Session1")).findFirst().map(session -> session.getData("id")).orElse(null);
        facade.deleteSession(sessionId, userId);

        facade.deleteAttendee(userId, "id", sessionId);

        UUID eventId = (UUID) facade.getEventHashMap().values().stream().filter(event -> event.getData("name").equals("Event2")).findFirst().map(event -> event.getData("id")).orElse(null);
        facade.deleteEvent(eventId, userId);
    }

    @Test
    void testCreateEvent() throws IOException {
        eventExists();

        Map<UUID, Persistence> eventMap = facade.getEventHashMap();
        boolean eventCreated = eventMap.values().stream()
                .anyMatch(event -> event.getData("name").equals("New Event"));
        assertTrue(eventCreated, "O evento criado não foi encontrado.");

        eventDelete("New Event");
    }

    @Test
    void testUpdateEvent() throws IOException {
        eventExists();

        UUID eventId = (UUID) facade.getEventHashMap().values().stream()
                .filter(event -> event.getData("name").equals("New Event"))
                .findFirst()
                .map(event -> event.getData("id"))
                .orElse(null);

        assertNotNull(eventId, "Event ID should not be null");

        facade.updateEvent(eventId, "Updated Event", "31/12/2024", "Updated Description", "Updated Location", "id1");
        facade.readEvent();

        Map<UUID, Persistence> eventMap = facade.getEventHashMap();
        boolean eventUpdated = eventMap.values().stream()
                .anyMatch(event -> event.getData("name").equals("Updated Event") && event.getData("description").equals("Updated Description"));
        assertTrue(eventUpdated, "The event was not updated.");

        eventDelete("Updated Event");
    }

    @Test
    void testReadEvent() throws IOException {
        eventExists();

        String eventReaded = "";
        Map<UUID, Persistence> eventHashMap = facade.getEventHashMap();
        for (Map.Entry<UUID, Persistence> entry : eventHashMap.entrySet()) {
            Persistence persistence = entry.getValue();
            if (persistence.getData("ownerId").equals("id1")) {
                eventReaded = (String) persistence.getData("name");
            }
        }

        assertEquals("New Event", eventReaded);
        eventDelete("New Event");
    }

    @Test
    void testDeleteEvent() throws IOException {
        eventExists();
        eventDelete("New Event");

        boolean eventDeleted = facade.getEventHashMap().values().stream()
                .noneMatch(event -> event.getData("name").equals("New Event"));
        assertTrue(eventDeleted, "O Evento não foi deletado.");
    }

    void eventExists() throws IOException {
        boolean eventExists = facade.getEventHashMap().values().stream()
                .anyMatch(event -> event.getData("name").equals("New Event"));

        if (!eventExists) {
            facade.createEvent("New Event", "01/11/2024", "New Description", "New Location", "id1");
            facade.readEvent();
        }
    }

    void eventDelete(String eventName) throws IOException {
        UUID eventId = (UUID) facade.getEventHashMap().values().stream().filter(event -> event.getData("name").equals(eventName)).findFirst().map(event -> event.getData("id")).orElse(null);
        facade.deleteEvent(eventId, "id1");
    }


    @Test
    void testCreateSession() throws IOException {
        sessionExists();

        Map<UUID, Persistence> sessionMap = facade.getSessionHashMap();
        boolean sessionCreated = sessionMap.values().stream()
                .anyMatch(session -> session.getData("name").equals("New Session"));
        assertTrue(sessionCreated, "A sessão criada não foi encontrado.");

        sessionDelete("New Session");
    }

    @Test
    void testUpdateSession() throws IOException {
        sessionExists();

        String sessionName = (String) facade.getSessionHashMap().values().stream()
                .filter(session -> session.getData("name").equals("New Session"))
                .findFirst()
                .map(session -> session.getData("name"))
                .orElse(null);

        assertNotNull(sessionName, "Session name should not be null");

        facade.updateSession(sessionName, "Updated Session", "02/12/2024", "Updated Session Description", "New Location", "id2", "08:00", "10:00");
        facade.readSession();

        Map<UUID, Persistence> sessionMap = facade.getSessionHashMap();
        boolean sessionUpdated = sessionMap.values().stream()
                .anyMatch(session -> session.getData("name").equals("Updated Session") && session.getData("description").equals("Updated Session Description"));
        assertTrue(sessionUpdated, "The session was not updated.");

        sessionDelete("Updated Session");
    }


    @Test
    void testReadSession() throws IOException {
        sessionExists();

        String sessionReaded = "";
        Map<UUID, Persistence> sessionHashMap = facade.getSessionHashMap();
        for (Map.Entry<UUID, Persistence> entry : sessionHashMap.entrySet()) {
            Persistence persistence = entry.getValue();
            if (persistence.getData("ownerId").equals("id2")) {
                sessionReaded = (String) persistence.getData("name");
            }
        }
        assertEquals("New Session", sessionReaded, "A sessão não foi lida.");
        sessionDelete("New Session");
    }

    @Test
    void testDeleteSession() throws IOException {
        sessionExists();

        sessionDelete("New Session");

        Map<UUID, Persistence> sessionMap = facade.getSessionHashMap();
        boolean sessionDeleted = sessionMap.values().stream()
                .noneMatch(session -> session.getData("name").equals("New Session"));
        assertTrue(sessionDeleted, "A sessão não foi deletada.");
    }

    void sessionExists() throws IOException {
        boolean sessionExists = facade.getSessionHashMap().values().stream()
                .anyMatch(session -> session.getData("name").equals("New Session"));

        if (!sessionExists) {
            facade.createEvent("Event1", "01/12/2024", "Event Description", "Event Location", "id2");
            facade.createSession("Event1", "New Session", "01/12/2024", "Session Description", "Session Location", "08:00", "10:00", "id2", "Event");
            facade.readEvent();
            facade.readSession();
        }
    }

    void sessionDelete(String sessionName) throws IOException {
        UUID sessionId = (UUID) facade.getSessionHashMap().values().stream().filter(subSession -> subSession.getData("name").equals(sessionName)).findFirst().map(session -> session.getData("id")).orElse(null);
        facade.deleteSession(sessionId, "id2");

        UUID eventId = (UUID) facade.getEventHashMap().values().stream().filter(event -> event.getData("name").equals("Event1")).findFirst().map(event -> event.getData("id")).orElse(null);
        facade.deleteEvent(eventId, "id2");
    }


    @Test
    void testCreateSubEvent() throws IOException {
        subEventExists();

        Map<UUID, Persistence> subEventMap = facade.getSubEventHashMap();
        boolean subEventCreated = subEventMap.values().stream()
                .anyMatch(subEvent -> subEvent.getData("name").equals("New SubEvent"));
        assertTrue(subEventCreated, "O SubEvento criado não foi encontrado.");

        subEventDelete("New SubEvent");
    }

    @Test
    void testUpdateSubEvent() throws IOException {
        subEventExists();

        String subEventName = (String) facade.getSubEventHashMap().values().stream()
                .filter(subEvent -> subEvent.getData("name").equals("New SubEvent"))
                .findFirst()
                .map(subEvent -> subEvent.getData("name"))
                .orElse(null);

        assertNotNull(subEventName, "SubEvent name should not be null");

        facade.updateSubEvent(subEventName, "Updated SubEvent", "02/12/2024", "Updated SubEvent Description", "New Location", "owner-id");
        facade.readSubEvent();

        Map<UUID, Persistence> subEventMap = facade.getSubEventHashMap();
        boolean subEventUpdated = subEventMap.values().stream()
                .anyMatch(subEvent -> subEvent.getData("name").equals("Updated SubEvent") && subEvent.getData("description").equals("Updated SubEvent Description"));
        assertTrue(subEventUpdated, "The SubEvent was not updated.");

        subEventDelete("Updated SubEvent");
    }

    @Test
    void testReadSubEvent() throws IOException {
        subEventExists();

        String subEventReaded = "";
        Map<UUID, Persistence> subEventHashMap = facade.getSubEventHashMap();
        for (Map.Entry<UUID, Persistence> entry : subEventHashMap.entrySet()) {
            Persistence persistence = entry.getValue();
            if (persistence.getData("ownerId").equals("owner-id")) {
                subEventReaded = (String) persistence.getData("name");
            }
        }

        assertEquals("New SubEvent", subEventReaded, "O SubEvento não foi lido.");
        subEventDelete("New SubEvent");
    }

    @Test
    void testDeleteSubEvent() throws IOException {
        subEventExists();

        subEventDelete("New SubEvent");

        Map<UUID, Persistence> subEventMap = facade.getSubEventHashMap();
        boolean subEventDeleted = subEventMap.values().stream()
                .noneMatch(subEvent -> subEvent.getData("name").equals("New SubEvent"));
        assertTrue(subEventDeleted, "O SubEvento não foi deletado.");
    }

    void subEventExists() throws IOException {
        boolean subEventExists = facade.getSubEventHashMap().values().stream()
                .anyMatch(subEvent -> subEvent.getData("name").equals("New SubEvent"));

        if (!subEventExists) {
            facade.createEvent("Event1", "01/12/2024", "Event Description", "Event Location", "owner-id");
            facade.createSubEvent("Event1", "New SubEvent", "02/12/2024", "New SubEvent Description", "New Location", "owner-id");
            facade.readEvent();
            facade.readSubEvent();
        }
    }

    void subEventDelete(String subEventName) throws IOException {
        UUID subEventId = (UUID) facade.getSubEventHashMap().values().stream().filter(subSubEvent -> subSubEvent.getData("name").equals(subEventName)).findFirst().map(subEvent -> subEvent.getData("id")).orElse(null);
        facade.deleteSubEvent(subEventId, "owner-id");

        UUID eventId = (UUID) facade.getEventHashMap().values().stream().filter(event -> event.getData("name").equals("Event1")).findFirst().map(event -> event.getData("id")).orElse(null);
        facade.deleteEvent(eventId, "owner-id");
    }


// UserTests
    @Test
    void testCreateUser() throws IOException {
        userExists();

        Map<UUID, Persistence> userHashMap = facade.getUserHashMap();
        boolean userCreated = userHashMap.values().stream()
                .anyMatch(user -> user.getData("email").equals("newuser@example.com"));
        assertTrue(userCreated);
        facade.deleteUser(facade.getUserData("id"), "id");
    }

    @Test
    void testUpdateUser() throws IOException {
        userExists();

        facade.updateUser("updateduser@example.com", "11223344556");
        boolean updateSuccessful = facade.loginValidate("updateduser@example.com", "11223344556");
        assertTrue(updateSuccessful, "Login falhou, não é possível atualizar o usuário");

        assertEquals("updateduser@example.com", facade.getUserData("email"));

        facade.deleteUser(facade.getUserData("id"), "id");
    }

    @Test
    void testUserRead() throws IOException {
        userExists();

        String userReaded = "";
        Map<UUID, Persistence> userHashMap = facade.getUserHashMap();
        for (Map.Entry<UUID, Persistence> entry : userHashMap.entrySet()) {
            Persistence persistence = entry.getValue();
            if (persistence.getData("email").equals("newuser@example.com")) {
                userReaded = (String) persistence.getData("id");
            }
        }

        assertEquals(facade.getUserData("id"), userReaded);
        facade.deleteUser(facade.getUserData("id"), "id");
    }

    @Test
    void testDeleteUser() throws IOException {
        userExists();

        facade.deleteUser(facade.getUserData("id"), "id");

        boolean deleteSuccessful = facade.getUserHashMap().values().stream()
                .anyMatch(user -> user.getData("email").equals("newuser@example.com"));
        assertFalse(deleteSuccessful);

    }

    void userExists() throws IOException {
        boolean userExists = facade.getUserHashMap().values().stream()
                .anyMatch(user -> user.getData("email").equals("newuser@example.com"));

        if (!userExists) {
            facade.createUser("newuser@example.com", "09876543211");
            facade.readUser();
        }

        boolean loginSuccessful = facade.loginValidate("newuser@example.com", "09876543211");
        assertTrue(loginSuccessful, "Login falhou, não é possível atualizar o usuário");
    }
}
*/