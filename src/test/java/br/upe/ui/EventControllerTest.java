package br.upe.ui;

import br.upe.controller.EventController;
import br.upe.persistence.Persistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EventControllerTest {

    private EventController eventController;

    @BeforeEach
    void setUp() throws IOException {
        eventController = new EventController();
    }

    @Test
    void testCreateEvent() throws IOException {
        eventExists();

        Map<String, Persistence> eventMap = eventController.getEventHashMap();
        boolean eventCreated = eventMap.values().stream()
                .anyMatch(event -> event.getData("name").equals("New Event"));
        assertTrue(eventCreated, "O evento criado não foi encontrado.");

        eventDelete("New Event");
    }

    @Test
    void testUpdateEvent() throws IOException {
        eventExists();

        String eventId = eventController.getEventHashMap().values().stream()
                .filter(event -> event.getData("name").equals("New Event"))
                .findFirst()
                .map(event -> event.getData("id"))
                .orElse(null);

        assertNotNull(eventId, "Event ID should not be null");

        eventController.update(eventId, "Updated Event", "31/12/2024", "Updated Description", "Updated Location", "id1");
        eventController.read();

        Map<String, Persistence> eventMap = eventController.getEventHashMap();
        boolean eventUpdated = eventMap.values().stream()
                .anyMatch(event -> event.getData("name").equals("Updated Event") && event.getData("description").equals("Updated Description"));
        assertTrue(eventUpdated, "The event was not updated.");

        eventDelete("Updated Event");
    }

    @Test
    void testReadEvent() throws IOException {
        eventExists();

        String eventReaded = "";
        Map<String, Persistence> eventHashMap = eventController.getEventHashMap();
        for (Map.Entry<String, Persistence> entry : eventHashMap.entrySet()) {
            Persistence persistence = entry.getValue();
            if (persistence.getData("ownerId").equals("id1")) {
                eventReaded = persistence.getData("name");
            }
        }

        assertEquals("New Event", eventReaded);
        eventDelete("New Event");
    }

    @Test
    void testDeleteEvent() throws IOException {
        eventExists();
        eventDelete("New Event");

        boolean eventDeleted = eventController.getEventHashMap().values().stream()
                .noneMatch(event -> event.getData("name").equals("New Event"));
        assertTrue(eventDeleted, "O Evento não foi deletado.");
    }

    void eventExists() throws IOException {
        boolean eventExists = eventController.getEventHashMap().values().stream()
                .anyMatch(event -> event.getData("name").equals("New Event"));

        if (!eventExists) {
            eventController.create("New Event", "01/11/2024", "New Description", "New Location", "id1");
            eventController.read();
        }
    }

    void eventDelete(String eventName) throws IOException {
        String eventId = eventController.getEventHashMap().values().stream().filter(event -> event.getData("name").equals(eventName)).findFirst().map(event -> event.getData("id")).orElse(null);
        eventController.delete(eventId, "id1");
    }
}
