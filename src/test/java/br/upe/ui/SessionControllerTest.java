package br.upe.ui;

import br.upe.controller.SessionController;
import br.upe.persistence.Persistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SessionControllerTest {
    private SessionController sessionController;

    @BeforeEach
    void setUp() {
        sessionController = new SessionController();
    }

    @Test
    void testCreateSession() {
        boolean sessionExists = sessionController.getSessionHashMap().values().stream()
                .anyMatch(session -> session.getData("name").equals("Session1"));

        if (!sessionExists) {
            sessionController.create("Event1", "Session1", "01/12/2024", "Session Description", "Session Location", "08:00", "10:00", "owner-id", "Event");
        }
        sessionController.read();

        Map<String, Persistence> sessions = sessionController.getSessionHashMap();
        assertFalse(sessions.isEmpty(), "A sessão não foi criada corretamente.");

        boolean sessionCreated = false;
        for (Map.Entry<String, Persistence> entry : sessions.entrySet()) {
            Persistence persistence = entry.getValue();
            if (persistence.getData("name").equals("Session1")) {
                sessionCreated = true;
            }
        }
        assertTrue(sessionCreated, "A sessão 'Session1' não foi encontrada.");
    }



    @Test
    void testReadSessions() {
        try {
            boolean sessionExists = sessionController.getSessionHashMap().values().stream()
                    .anyMatch(session -> session.getData("name").equals("Session1"));

            if (!sessionExists) {
                sessionController.create("Event1", "Session1", "01/12/2024", "Session Description", "Session Location", "08:00", "10:00", "owner-id", "Event");
            }

            sessionController.read();
            Map<String, Persistence> sessions = sessionController.getSessionHashMap();

            assertFalse(sessions.isEmpty(), "As sessões não foram lidas corretamente.");

            boolean sessionRead = false;
            for (Map.Entry<String, Persistence> entry : sessions.entrySet()) {
                Persistence persistence = entry.getValue();
                if (persistence.getData("name").equals("Session1")) {
                    sessionRead = true;
                }
            }
            // A mudança está aqui
            assertTrue(sessionRead, "A sessão 'Session1' não foi encontrada nas sessões lidas.");

        } catch (Exception e) {
            e.printStackTrace();
            fail("Falha ao ler as sessões devido a: " + e.getMessage());
        }
    }



    @Test
    void testUpdateSession() throws FileNotFoundException {
        boolean session1Exists = sessionController.getSessionHashMap().values().stream()
                .anyMatch(session -> session.getData("name").equals("Session1"));

        if (!session1Exists) {
            sessionController.create("Event1", "Session1", "01/12/2024", "Session Description", "Session Location", "08:00", "10:00", "owner-id", "Event");
        }

        sessionController.read();
        sessionController.update("Session1", "Updated Session2", "04/12/2024", "Updated Description", "Updated Location", "owner-id");

        boolean updated = false;
        for (Persistence session : sessionController.getSessionHashMap().values()) {
            if (session.getData("name").equals("Updated Session2")) {
                updated = true;
                assertEquals("04/12/2024", session.getData("date"));
                assertEquals("Updated Description", session.getData("description"));
                assertEquals("Updated Location", session.getData("location"));
                break;
            }
        }
        assertTrue(updated, "A sessão 'Session2' não foi atualizada corretamente.");
    }

    @Test
    void testDeleteSession() {
        boolean session1Exists = sessionController.getSessionHashMap().values().stream()
                .anyMatch(session -> session.getData("name").equals("Session1"));

        if (!session1Exists) {
            sessionController.create("Event1", "Session2", "01/12/2024", "Session Description", "Session Location", "08:00", "10:00", "owner-id", "Event");
        }
        sessionController.delete("Session2", "name", "owner-id");

        Map<String, Persistence> sessions = sessionController.getSessionHashMap();
        boolean deleted = true;
        for (Persistence session : sessions.values()) {
            if (session.getData("name").equals("Session2")) {
                deleted = false;
                break;
            }
        }
        assertTrue(deleted, "A sessão não foi deletada corretamente.");
    }
}