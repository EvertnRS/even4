package br.upe.ui;

import br.upe.controller.EventController;
import br.upe.controller.SessionController;
import br.upe.persistence.Persistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SessionControllerTest {
    private SessionController sessionController;
    private EventController eventController;

    @BeforeEach
    void setUp() {
        eventController = new EventController();
        sessionController = new SessionController();
    }

    @Test
    void testCreateSession() {
        sessionExists();

        Map<String, Persistence> sessionMap = sessionController.getSessionHashMap();
        boolean sessionCreated = sessionMap.values().stream()
                .anyMatch(session -> session.getData("name").equals("New Session"));
        assertTrue(sessionCreated, "A sessão criada não foi encontrado.");

        sessionDelete("New Session");
    }

    @Test
    void testUpdateSession() throws FileNotFoundException {
        sessionExists();

        sessionController.update("New Session", "Updated Session", "02/12/2024", "Updated Session Description", "New Location", "id2");

        Map<String, Persistence> sessionMap = sessionController.getSessionHashMap();
        boolean sessionUpdated = sessionMap.values().stream()
                .anyMatch(session -> session.getData("name").equals("Updated Session") && session.getData("description").equals("Updated Session Description"));
        assertTrue(sessionUpdated, "O SubEvento não foi atualizado.");

        sessionDelete("Updated Session");
    }

    @Test
    void testReadSession() {
        sessionExists();

        String sessionReaded = "";
        Map<String, Persistence> sessionHashMap = sessionController.getSessionHashMap();
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
    void testDeleteSession() {
        sessionExists();

        sessionDelete("New Session");

        Map<String, Persistence> sessionMap = sessionController.getSessionHashMap();
        boolean sessionDeleted = sessionMap.values().stream()
                .noneMatch(session -> session.getData("name").equals("New Session"));
        assertTrue(sessionDeleted, "A sessão não foi deletada.");
    }

    void sessionExists() {
        boolean sessionExists = sessionController.getSessionHashMap().values().stream()
                .anyMatch(session -> session.getData("name").equals("New Session"));

        if (!sessionExists) {
            eventController.create("Event1", "01/12/2024", "Event Description", "Event Location", "id2");
            sessionController.create("Event1", "New Session", "01/12/2024", "Session Description", "Session Location", "08:00", "10:00", "id2", "Event");
            eventController.read();
            sessionController.read();
        }
    }

    void sessionDelete(String sessionName) {
        String sessionId = sessionController.getSessionHashMap().values().stream().filter(subSession -> subSession.getData("name").equals(sessionName)).findFirst().map(session -> session.getData("id")).orElse(null);
        sessionController.delete(sessionId, "id2");

        String eventId = eventController.getEventHashMap().values().stream().filter(event -> event.getData("name").equals("Event1")).findFirst().map(event -> event.getData("id")).orElse(null);
        eventController.delete(eventId, "id2");
    }
}