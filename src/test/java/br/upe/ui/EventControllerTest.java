package br.upe.ui;

import br.upe.controller.EventController;
import br.upe.persistence.Event;
import br.upe.persistence.Persistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class EventControllerTest {

    private EventController eventController;
    private HashMap<String, Persistence> mockEventMap;
    private Event testEvent;

    @BeforeEach
    public void setUp() {
        eventController = new EventController();
        mockEventMap = new HashMap<>();

        testEvent = new Event();
        testEvent.create("Test Event", "31/12/2024", "Description", "Location", "owner-id");
        mockEventMap.put(testEvent.getId(), testEvent);

        eventController.setEventHashMap(mockEventMap);
    }

    @Test
    public void testCreateEvent() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("./db/events.csv"))) {
        } catch (IOException e) {
            e.printStackTrace();
        }

        eventController.create("New Event", "01/11/2024", "New Description", "New Location", "owner-id");
        eventController.read();

        HashMap<String, Persistence> eventMap = eventController.getEventHashMap();
        boolean eventExists = eventMap.values().stream().anyMatch(e -> e.getData("name").equals("New Event"));
        assertTrue(eventExists, "O evento criado não foi encontrado.");
    }

    @Test
    public void testReadEvent() {
        HashMap<String, Persistence> eventMap = eventController.getEventHashMap();
        assertNotNull(eventMap);
        assertEquals(1, eventMap.size());
        assertEquals("Test Event", testEvent.getData("name"));
    }

    @Test
    public void testUpdateEvent() throws FileNotFoundException {
        eventController.update("Test Event", "Updated Event", "31/12/2024", "Updated Description", "Updated Location", "owner-id");

        Event updatedEvent = (Event) eventController.getEventHashMap().get(testEvent.getId());
        assertEquals("Updated Event", updatedEvent.getData("name"));
        assertEquals("31/12/2024", updatedEvent.getData("date"));
        assertEquals("Updated Description", updatedEvent.getData("description"));
        assertEquals("Updated Location", updatedEvent.getData("location"));
    }

    @Test
    public void testDeleteEvent() {
        eventController.delete("Test Event", "name", "owner-id");

        HashMap<String, Persistence> eventMap = eventController.getEventHashMap();
        assertFalse(eventMap.values().stream().anyMatch(e -> e.getData("name").equals("Test Event")));
    }
}
