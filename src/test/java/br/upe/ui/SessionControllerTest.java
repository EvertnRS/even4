package br.upe.ui;

import br.upe.controller.SessionController;
import br.upe.persistence.Session;
import br.upe.persistence.Persistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class SessionControllerTest {
    private SessionController sessionController;

    @BeforeEach
    public void setUp() {
        sessionController = new SessionController();
    }

    @Test
    public void testCreateSession() {
        sessionController.create("Event1", "Session1", "01/12/2024", "Session Description", "Session Location", "08:00", "10:00", "owner-id", "Event");
        HashMap<String, Persistence> sessions = sessionController.getSessionHashMap();
        assertFalse(sessions.isEmpty(), "A sessão não foi criada corretamente.");
    }

    @Test
    public void testReadSessions() {
        sessionController.create("Event1", "Session1", "01/12/2024", "Session Description", "Session Location", "08:00", "10:00", "owner-id", "Event");
        sessionController.read();
        HashMap<String, Persistence> sessions = sessionController.getSessionHashMap();
        assertFalse(sessions.isEmpty(), "As sessões não foram lidas corretamente.");
    }

    @Test
    public void testUpdateSession() throws FileNotFoundException {
        sessionController.create("Event1", "Session1", "01/12/2024", "Session Description", "Session Location", "08:00", "10:00", "owner-id", "Event");

        sessionController.update("Session1", "Updated Session1", "02/12/2024", "Updated Description", "Updated Location", "owner-id");

        boolean updated = false;
        for (Persistence session : sessionController.getSessionHashMap().values()) {
            if (session.getData("name").equals("Updated Session1")) {
                updated = true;
                assertEquals("02/12/2024", session.getData("date"));
                assertEquals("Updated Description", session.getData("description"));
                assertEquals("Updated Location", session.getData("location"));
                break;
            }
        }
        assertTrue(updated, "A sessão não foi atualizada corretamente.");
    }


    @Test
    public void testDeleteSession() {
        sessionController.create("Event1", "Session1", "01/12/2024", "Session Description", "Session Location", "08:00", "10:00", "owner-id", "Event");
        sessionController.delete("Session1", "name", "owner-id");

        HashMap<String, Persistence> sessions = sessionController.getSessionHashMap();
        boolean deleted = true;
        for (Persistence session : sessions.values()) {
            if (session.getData("name").equals("Session1")) {
                deleted = false;
                break;
            }
        }
        assertTrue(deleted, "A sessão não foi deletada corretamente.");
    }
}