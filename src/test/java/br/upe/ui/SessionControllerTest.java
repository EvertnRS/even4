package br.upe.ui;

import br.upe.controller.EventController;
import br.upe.controller.SessionController;
import br.upe.persistence.Persistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SessionControllerTest {
    private SessionController sessionController;
    private EventController eventController;

    @BeforeEach
    void setUp() throws IOException {
        eventController = new EventController();
        sessionController = new SessionController();
    }

    @Test
    void testCreateSession() throws IOException {
        sessionExists();

        Map<String, Persistence> sessionMap = sessionController.getHashMap();
        boolean sessionCreated = sessionMap.values().stream()
                .anyMatch(session -> session.getData("name").equals("New Session"));
        assertTrue(sessionCreated, "A sessão criada não foi encontrado.");

        sessionDelete("New Session");
    }

    /*@Test
    void testUpdateSession() throws IOException {
        sessionExists();

        sessionController.update("New Session", "Updated Session", "02/12/2024", "Updated Session Description", "New Location", "id2");

        Map<String, Persistence> sessionMap = sessionController.getHashMap()();
        boolean sessionUpdated = sessionMap.values().stream()
                .anyMatch(session -> session.getData("name").equals("Updated Session") && session.getData("description").equals("Updated Session Description"));
        assertTrue(sessionUpdated, "O SubEvento não foi atualizado.");

        sessionDelete("Updated Session");
    }*/

    @Test
    void testReadSession() throws IOException {
        sessionExists();

        String sessionReaded = "";
        Map<String, Persistence> sessionHashMap = sessionController.getHashMap();
        for (Map.Entry<String, Persistence> entry : sessionHashMap.entrySet()) {
            Persistence persistence = entry.getValue();
            if (persistence.getData("ownerId").equals("id2")) {
                sessionReaded = persistence.getData("name");
            }
        }
        assertEquals("New Session", sessionReaded, "A sessão não foi lida.");
        sessionDelete("New Session");
    }

    @Test
    void testDeleteSession() throws IOException {
        sessionExists();

        sessionDelete("New Session");

        Map<String, Persistence> sessionMap = sessionController.getHashMap();
        boolean sessionDeleted = sessionMap.values().stream()
                .noneMatch(session -> session.getData("name").equals("New Session"));
        assertTrue(sessionDeleted, "A sessão não foi deletada.");
    }

    void sessionExists() throws IOException {
        boolean sessionExists = sessionController.getHashMap().values().stream()
                .anyMatch(session -> session.getData("name").equals("New Session"));

        if (!sessionExists) {
            eventController.create("Event1", "01/12/2024", "Event Description", "Event Location", "id2");
            sessionController.create("Event1", "New Session", "01/12/2024", "Session Description", "Session Location", "08:00", "10:00", "id2", "Event");
            eventController.read();
            sessionController.read();
        }
    }

    void sessionDelete(String sessionName) throws IOException {
        String sessionId = sessionController.getHashMap().values().stream().filter(subSession -> subSession.getData("name").equals(sessionName)).findFirst().map(session -> session.getData("id")).orElse(null);
        sessionController.delete(sessionId, "id2");

        String eventId = eventController.getHashMap().values().stream().filter(event -> event.getData("name").equals("Event1")).findFirst().map(event -> event.getData("id")).orElse(null);
        eventController.delete(eventId, "id2");
    }
}
