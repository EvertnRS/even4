package br.upe.ui;

import br.upe.controller.EventController;
import br.upe.controller.SubEventController;
import br.upe.persistence.Persistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SubEventControllerTest {

    private SubEventController subEventController;

    @BeforeEach
    public void setUp() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("./db/subEvents.csv"))) {
        } catch (IOException e) {
            e.printStackTrace();
        }

        subEventController = new SubEventController();
    }

    @Test
    public void testCreateSubEvent() {
        try {
            EventController eventController = new EventController();
            eventController.create("Event1", "01/12/2024", "Event Description", "Event Location", "owner-id");

            subEventController.create("Event1", "New SubEvent", "02/12/2024", "New SubEvent Description", "New Location", "owner-id");
            subEventController.read();

            HashMap<String, Persistence> subEventMap = subEventController.getEventHashMap();
            boolean subEventExists = subEventMap.values().stream().anyMatch(e -> e.getData("name").equals("New SubEvent"));
            assertTrue(subEventExists, "O subevento criado não foi encontrado: " + subEventMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testReadSubEvent() {
        try {
            EventController eventController = new EventController();
            eventController.create("Event1", "01/12/2024", "Event Description", "Event Location", "owner-id");

            subEventController.create("Event1", "SubEvent1", "02/12/2024", "Description", "Location", "owner-id");
            subEventController.read();

            boolean isSubEventRead = subEventController.list("owner-id");
            assertFalse(isSubEventRead, "O subevento não foi lido corretamente.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testUpdateSubEvent() {
        try {
            EventController eventController = new EventController();
            eventController.create("Event1", "01/12/2024", "Event Description", "Event Location", "owner-id");

            subEventController.create("Event1", "SubEvent1", "02/12/2024", "Description", "Location", "owner-id");
            subEventController.read();

            subEventController.update("SubEvent1", "Updated SubEvent", "15/11/2024", "Updated Description", "Updated Location", "owner-id");
            subEventController.read();

            HashMap<String, Persistence> subEventMap = subEventController.getEventHashMap();
            boolean isUpdated = subEventMap.values().stream().anyMatch(e -> e.getData("name").equals("Updated SubEvent"));
            assertTrue(isUpdated, "O subevento não foi atualizado corretamente: " + subEventMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDeleteSubEvent() {
        try {
            EventController eventController = new EventController();
            eventController.create("Event1", "01/12/2024", "Event Description", "Event Location", "owner-id");

            subEventController.create("Event1", "SubEvent1", "01/11/2024", "Description", "Location", "owner-id");
            subEventController.read();

            subEventController.delete("SubEvent1", "name", "owner-id");
            subEventController.read();

            HashMap<String, Persistence> subEventMap = subEventController.getEventHashMap();
            boolean subEventExists = subEventMap.values().stream().anyMatch(e -> e.getData("name").equals("SubEvent1"));
            assertFalse(subEventExists, "O subevento não foi removido corretamente : " + subEventMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
