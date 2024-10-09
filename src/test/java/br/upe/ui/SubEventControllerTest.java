package br.upe.ui;

import br.upe.controller.EventController;
import br.upe.controller.SubEventController;
import br.upe.persistence.Persistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SubEventControllerTest {

    private SubEventController subEventController;
    private EventController eventController;
    Map<String, Persistence> EventMap;

    @BeforeEach
    void setUp() {
        eventController = new EventController();
        subEventController = new SubEventController();
    }

    @Test
    void testCreateSubEvent() {
        try {
            String eventName = null;
            for (Map.Entry<String, Persistence> entry : eventController.getEventHashMap().entrySet()) {
                Persistence event = entry.getValue();
                if (event.getData("name").equals("Event1")) {
                    eventName = event.getData("name"); // Corrigido para pegar o nome diretamente do evento
                    break;
                }
            }

            // Se o evento não for encontrado, crie um novo evento
            if (eventName == null || !eventName.equals("Event1")) {
                eventController.create("Event1", "01/12/2024", "Event Description", "Event Location", "owner-id");
            }

            // Cria o subevento
            subEventController.create("Event1", "New SubEvent", "02/12/2024", "New SubEvent Description", "New Location", "owner-id");
            subEventController.read();

            // Verifique se o subevento foi criado corretamente
            Map<String, Persistence> subEventMap = subEventController.getSubEventHashMap();
            boolean subEventExists = subEventMap.values().stream().anyMatch(e -> e.getData("name").equals("New SubEvent"));
            assertTrue(subEventExists, "O subevento criado não foi encontrado: " + subEventMap);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testReadSubEvent() {
        try {
            // Verifica se o evento principal já existe
            boolean eventExists = eventController.getEventHashMap().values().stream()
                    .anyMatch(event -> event.getData("name").equals("Event2"));

            // Se o evento não existir, cria-o
            if (!eventExists) {
                eventController.create("Event2", "01/12/2024", "Event Description", "Event Location", "1234");
            }

            // Cria um subevento dentro desse evento
            subEventController.create("Event2", "SubEvent1", "02/12/2024", "Description", "Location", "1234");
            subEventController.read();

            // Verifica se o subevento foi criado e lido corretamente
            boolean subEventExists = subEventController.getSubEventHashMap().values().stream()
                    .anyMatch(subEvent -> subEvent.getData("name").equals("SubEvent1"));

            assertTrue(subEventExists, "O subevento não foi encontrado na leitura.");

        } catch (Exception e) {
            e.printStackTrace();
            fail("Falha ao ler o subevento devido a: " + e.getMessage());
        }
    }



    @Test
    void testUpdateSubEvent() {
        try {
            // Verifica se o evento principal já existe
            boolean eventExists = eventController.getEventHashMap().values().stream()
                    .anyMatch(event -> event.getData("name").equals("Event3"));

            // Se o evento não existir, cria-o
            if (!eventExists) {
                eventController.create("Event3", "01/12/2024", "Event Description", "Event Location", "owner-id");
            }

            // Cria um subevento dentro desse evento
            subEventController.create("Event3", "SubEvent1", "02/12/2024", "Description", "Location", "owner-id");
            subEventController.read();

            // Atualiza o subevento
            subEventController.update("SubEvent1", "Updated SubEvent", "15/11/2024", "Updated Description", "Updated Location", "owner-id");
            subEventController.read();

            // Verifica se o subevento foi atualizado corretamente
            Map<String, Persistence> subEventMap = subEventController.getSubEventHashMap();
            boolean isUpdated = subEventMap.values().stream().anyMatch(e -> e.getData("name").equals("Updated SubEvent"));
            assertTrue(isUpdated, "O subevento não foi atualizado corretamente: " + subEventMap);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Falha ao atualizar o subevento devido a: " + e.getMessage());
        }
    }


    @Test
    void testDeleteSubEvent() {
        try {
            boolean eventExists = eventController.getEventHashMap().values().stream()
                    .anyMatch(event -> event.getData("name").equals("Event3"));

            // Se o evento não existir, cria-o
            if (!eventExists) {
                eventController.create("Event3", "01/12/2024", "Event Description", "Event Location", "owner-id");
            }

            subEventController.create("Event1", "SubEvent1", "01/11/2024", "Description", "Location", "1234");
            subEventController.read();

            subEventController.delete("SubEvent1", "name", "1234");
            subEventController.read();

            Map<String, Persistence> subEventMap = subEventController.getSubEventHashMap();
            boolean subEventExists = subEventMap.values().stream().anyMatch(e -> e.getData("name").equals("SubEvent1"));
            assertFalse(subEventExists, "O subevento não foi removido corretamente : " + subEventMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
