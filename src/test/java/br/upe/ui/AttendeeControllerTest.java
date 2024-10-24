package br.upe.ui;

/*import br.upe.controller.AttendeeController;
import br.upe.controller.EventController;
import br.upe.controller.SessionController;
import br.upe.controller.UserController;
import br.upe.persistence.Persistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AttendeeControllerTest {

    private UserController userController;
    private EventController eventController;
    private SessionController sessionController;
    private AttendeeController attendeeController;

    @BeforeEach
    void setUp() {
        attendeeController = new AttendeeController();
        userController = new UserController();
        eventController = new EventController();
        sessionController = new SessionController();
    }

    @Test
    void testCreateAttendee() throws FileNotFoundException {
        attendeeExists();

        Map<String, Persistence> attendeeMap = attendeeController.getAttendeeHashMap();
        boolean attendeeCreated = attendeeMap.values().stream()
                .anyMatch(attendee -> attendee.getData("name").equals("Man"));
        assertTrue(attendeeCreated, "O participante criado não foi encontrado.");

        attendeeDelete();
    }

    @Test
    void testUpdateAttendee() throws FileNotFoundException {
        attendeeExists();

        attendeeController.update("Duda", sessionController.getSessionHashMap().values().stream().filter(subSession -> subSession.getData("name").equals("Session1")).findFirst().map(session -> session.getData("id")).orElse(null));

        Map<String, Persistence> attendeeMap = attendeeController.getAttendeeHashMap();
        boolean attendeeUpdated = attendeeMap.values().stream()
                .anyMatch(attendee -> attendee.getData("name").equals("Duda"));
        assertTrue(attendeeUpdated, "O Participante não foi atualizado.");

        attendeeDelete();
    }

    @Test
    void testReadAttendee() throws FileNotFoundException {
        attendeeExists();

        String attendeeReaded = "";
        Map<String, Persistence> attendeeHashMap = attendeeController.getAttendeeHashMap();
        for (Map.Entry<String, Persistence> entry : attendeeHashMap.entrySet()) {
            Persistence persistence = entry.getValue();
            if (persistence.getData("sessionId").equals(sessionController.getSessionHashMap().values().stream().filter(subSession -> subSession.getData("name").equals("Session1")).findFirst().map(session -> session.getData("id")).orElse(null))) {
                attendeeReaded = persistence.getData("name");
            }
        }
        assertEquals("Man", attendeeReaded, "A sessão não foi lida.");
        attendeeDelete();
    }

    @Test
    void testDeleteAttendee() throws FileNotFoundException {
        attendeeExists();
        attendeeDelete();

        Map<String, Persistence> attendeeMap = attendeeController.getAttendeeHashMap();
        boolean attendeeDeleted = attendeeMap.values().stream()
                .noneMatch(attendee -> attendee.getData("name").equals("Man"));
        assertTrue(attendeeDeleted, "A sessão não foi deletada.");

    }

    void attendeeExists() throws FileNotFoundException {
        boolean sessionExists = attendeeController.getAttendeeHashMap().values().stream()
                .anyMatch(attendee -> attendee.getData("name").equals("Man"));

        if (!sessionExists) {
            userController.create("newuser@example.com", "09876543211");
            userController.read();
            boolean loginSuccessful = userController.loginValidate("newuser@example.com", "09876543211");
            assertTrue(loginSuccessful, "Login falhou, não é possível atualizar o usuário");
            String userId = userController.getData("id");

            eventController.create("Event2", "01/12/2024", "Event Description", "Event Location", userId);
            eventController.read();
            sessionController.create("Event2", "Session1", "01/12/2024", "Session Description", "Session Location", "08:00", "10:00", userId, "Event");
            sessionController.read();

            String sessionId = sessionController.getSessionHashMap().values().stream().filter(subSession -> subSession.getData("name").equals("Session1")).findFirst().map(session -> session.getData("id")).orElse(null);
            attendeeController.create("Man", sessionId, userId);
            attendeeController.read();


        }
    }

    void attendeeDelete() {
        boolean loginSuccessful = userController.loginValidate("newuser@example.com", "09876543211");
        assertTrue(loginSuccessful, "Login falhou, não é possível atualizar o usuário");
        String userId = userController.getData("id");
        userController.delete(userId, "id");

        String sessionId = sessionController.getSessionHashMap().values().stream().filter(subSession -> subSession.getData("name").equals("Session1")).findFirst().map(session -> session.getData("id")).orElse(null);
        sessionController.delete(sessionId, userId);

        attendeeController.delete(userId, "id", sessionId);

        String eventId = eventController.getEventHashMap().values().stream().filter(event -> event.getData("name").equals("Event2")).findFirst().map(event -> event.getData("id")).orElse(null);
        eventController.delete(eventId, userId);
    }
}
*/
