package br.upe.ui;
/*
import br.upe.controller.EventController;
import br.upe.controller.SubEventController;
import br.upe.persistence.Repository.Persistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

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

        Map<UUID, Persistence> subEventMap = subEventController.getHashMap();
        boolean subEventCreated = subEventMap.values().stream()
                .anyMatch(subEvent -> subEvent.getData("name").equals("New SubEvent"));
        assertTrue(subEventCreated, "O SubEvento criado não foi encontrado.");

        subEventDelete("New SubEvent");
    }

    @Test
    void testUpdateSubEvent() throws IOException {
        subEventExists();

        String subEventName = (String) subEventController.getHashMap().values().stream()
                .filter(subEvent -> subEvent.getData("name").equals("New SubEvent"))
                .findFirst()
                .map(subEvent -> subEvent.getData("name"))
                .orElse(null);

        assertNotNull(subEventName, "SubEvent name should not be null");

        subEventController.update(subEventName, "Updated SubEvent", "02/12/2024", "Updated SubEvent Description", "New Location", "owner-id");
        subEventController.read();

        Map<UUID, Persistence> subEventMap = subEventController.getHashMap();
        boolean subEventUpdated = subEventMap.values().stream()
                .anyMatch(subEvent -> subEvent.getData("name").equals("Updated SubEvent") && subEvent.getData("description").equals("Updated SubEvent Description"));
        assertTrue(subEventUpdated, "The SubEvent was not updated.");

        subEventDelete("Updated SubEvent");
    }

    @Test
    void testReadSubEvent() throws IOException {
        subEventExists();

        String subEventReaded = "";
        Map<UUID, Persistence> subEventHashMap = subEventController.getHashMap();
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

        Map<UUID, Persistence> subEventMap = subEventController.getHashMap();
        boolean subEventDeleted = subEventMap.values().stream()
                .noneMatch(subEvent -> subEvent.getData("name").equals("New SubEvent"));
        assertTrue(subEventDeleted, "O SubEvento não foi deletado.");
    }

    void subEventExists() throws IOException {
        boolean subEventExists = subEventController.getHashMap().values().stream()
                .anyMatch(subEvent -> subEvent.getData("name").equals("New SubEvent"));

        if (!subEventExists) {
            eventController.create("Event1", "01/12/2024", "Event Description", "Event Location", "owner-id");
            subEventController.create("Event1", "New SubEvent", "02/12/2024", "New SubEvent Description", "New Location", "owner-id");
            eventController.read();
            subEventController.read();
        }
    }

    void subEventDelete(String subEventName) throws IOException {
        UUID subEventId = (UUID) subEventController.getHashMap().values().stream().filter(subSubEvent -> subSubEvent.getData("name").equals(subEventName)).findFirst().map(subEvent -> subEvent.getData("id")).orElse(null);
        subEventController.delete(subEventId, "owner-id");

        UUID eventId = (UUID) eventController.getHashMap().values().stream().filter(event -> event.getData("name").equals("Event1")).findFirst().map(event -> event.getData("id")).orElse(null);
        eventController.delete(eventId, "owner-id");
    }
}
*/