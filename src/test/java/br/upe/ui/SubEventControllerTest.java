package br.upe.ui;

import br.upe.controller.EventController;
import br.upe.controller.SubEventController;
import br.upe.persistence.Persistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SubEventControllerTest {

    private SubEventController subEventController;
    private EventController eventController;

    @BeforeEach
    void setUp() throws IOException {
        eventController = new EventController();
        subEventController = new SubEventController();
    }

    @Test
    void testCreateSubEvent() throws IOException {
        subEventExists();

        Map<String, Persistence> subEventMap = subEventController.getSubEventHashMap();
        boolean subEventCreated = subEventMap.values().stream()
                .anyMatch(subEvent -> subEvent.getData("name").equals("New SubEvent"));
        assertTrue(subEventCreated, "O SubEvento criado não foi encontrado.");

        subEventDelete("New SubEvent");
    }

    /*@Test
    void testUpdateSubEvent() throws IOException {
        subEventExists();

        subEventController.update("New SubEvent", "Updated SubEvent", "02/12/2024", "Updated SubEvent Description", "New Location", "owner-id");

        Map<String, Persistence> subEventMap = subEventController.getSubEventHashMap();
        boolean subEventUpdated = subEventMap.values().stream()
                .anyMatch(subEvent -> subEvent.getData("name").equals("Updated SubEvent") && subEvent.getData("description").equals("Updated SubEvent Description"));
        assertTrue(subEventUpdated, "O SubEvento não foi atualizado.");

        subEventDelete("Updated SubEvent");
    }*/

    @Test
    void testReadSubEvent() throws IOException {
        subEventExists();

        String subEventReaded = "";
        Map<String, Persistence> subEventHashMap = subEventController.getSubEventHashMap();
        for (Map.Entry<String, Persistence> entry : subEventHashMap.entrySet()) {
            Persistence persistence = entry.getValue();
            if (persistence.getData("ownerId").equals("owner-id")) {
                subEventReaded = persistence.getData("name");
            }
        }

        assertEquals("New SubEvent", subEventReaded, "O SubEvento não foi lido.");
        subEventDelete("New SubEvent");
    }

    @Test
    void testDeleteSubEvent() throws IOException {
        subEventExists();

        subEventDelete("New SubEvent");

        Map<String, Persistence> subEventMap = subEventController.getSubEventHashMap();
        boolean subEventDeleted = subEventMap.values().stream()
                .noneMatch(subEvent -> subEvent.getData("name").equals("New SubEvent"));
        assertTrue(subEventDeleted, "O SubEvento não foi deletado.");
    }

    void subEventExists() throws IOException {
        boolean subEventExists = subEventController.getSubEventHashMap().values().stream()
                .anyMatch(subEvent -> subEvent.getData("name").equals("New SubEvent"));

        if (!subEventExists) {
            eventController.create("Event1", "01/12/2024", "Event Description", "Event Location", "owner-id");
            subEventController.create("Event1", "New SubEvent", "02/12/2024", "New SubEvent Description", "New Location", "owner-id");
            eventController.read();
            subEventController.read();
        }
    }

    void subEventDelete(String subEventName) throws IOException {
        String subEventId = subEventController.getSubEventHashMap().values().stream().filter(subSubEvent -> subSubEvent.getData("name").equals(subEventName)).findFirst().map(subEvent -> subEvent.getData("id")).orElse(null);
        subEventController.delete(subEventId, "owner-id");

        String eventId = eventController.getEventHashMap().values().stream().filter(event -> event.getData("name").equals("Event1")).findFirst().map(event -> event.getData("id")).orElse(null);
        eventController.delete(eventId, "owner-id");
    }
}
