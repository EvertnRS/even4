package br.upe.controller;

import br.upe.persistence.Event;
import br.upe.persistence.repository.EventRepository;
import br.upe.persistence.repository.Persistence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class EventController implements Controller {
    private static final String DESCRIPTION = "description";
    private static final String LOCATION = "location";
    private static final Logger LOGGER = Logger.getLogger(EventController.class.getName());
    private Persistence eventLog;

    public EventController() throws IOException {
        //
    }

    @Override
    public <T> List<T> getAll() {
        EventRepository eventRepository = EventRepository.getInstance();
        return (List<T>) eventRepository.getAllEvents();
    }

    @Override
    public <T> List<T> list(Object... params) throws IOException {
        UUID userId = UUID.fromString((String) params[0]);
        EventRepository eventRepository = EventRepository.getInstance();
        List<Event> allEvents = eventRepository.getAllEvents();
        List<Event> userEvents = new ArrayList<>();

        for (Event event : allEvents) {
            if (event.getIdOwner().getId().equals(userId)) {
                userEvents.add(event);
            }
        }

        if (userEvents.isEmpty()) {
            LOGGER.warning("Seu usuário atual é organizador de nenhum evento");
        }

        return (List<T>) userEvents;
    }

    @Override
    public Object[] create(Object... params) {
        if (params.length != 5) {
            LOGGER.warning("Só pode ter 5 parâmetros");
            return new Object[]{false, null};
        }

        String name = (String) params[0];
        Date date = (Date) params[1];

        String description = (String) params[2];
        String location = (String) params[3];
        String idOwner = (String) params[4];

        Persistence event = new EventRepository();
        return event.create(name, date, description, location, idOwner);
    }

    public boolean update(Object... params) throws IOException {
        if (!isValidParamsLength(params)) {
            LOGGER.warning("Só pode ter 5 parametros");
            return false;
        }

        UUID eventId = (UUID) params[0];
        String newName = (String) params[1];
        Date newDate = (Date) params[2];
        String newDescription = (String) params[3];
        String newLocation = (String) params[4];

        return updateEvent(eventId, newName, newDate, newDescription, newLocation);
    }

    private boolean updateEvent(UUID id, String newName, Date newDate, String newDescription, String newLocation) throws IOException {
        boolean isUpdated = false;
        if (id != null) {
            EventRepository eventRepository = EventRepository.getInstance();
            isUpdated = eventRepository.update(id, newName, newDate, newDescription, newLocation);
        } else {
            LOGGER.warning("Evento não encontrado");
        }
        return isUpdated;
    }

    @Override
    public boolean delete(Object... params) throws IOException {
        if (params.length != 2) {
            LOGGER.warning("Só pode ter 2 parametro");
            return false;
        }

        EventRepository eventRepository = EventRepository.getInstance();

        UUID id = (UUID) params[0];
        UUID ownerId = UUID.fromString((String) params[1]);

        return eventRepository.delete(id, ownerId);
    }

    @Override
    public String getData(String dataToGet) {
        String data = "";
        this.eventLog = EventRepository.getInstance();
        try {
            switch (dataToGet) {
                case "id" -> data = (String) this.eventLog.getData("id");
                case "name" -> data = (String) this.eventLog.getData("name");
                case DESCRIPTION -> data = (String) this.eventLog.getData(DESCRIPTION);
                case "date" -> data = String.valueOf(this.eventLog.getData("date"));
                case LOCATION -> data = (String) this.eventLog.getData(LOCATION);
                default -> throw new IOException();
            }
        } catch (IOException e) {
            LOGGER.warning("Informação não existe ou é restrita");
        }
        return data;
    }

    private boolean isValidParamsLength(Object... params) {
        return params.length == 5;
    }

    @Override
    public Object[] isExist(Object... params) throws IOException {
        if (params.length != 2) {
            LOGGER.warning("Só pode ter 1 parametro");
            return new Object[]{false, null};
        }

        String name = (String) params[0];
        UUID ownerId =  UUID.fromString((String)params[1]);
        EventRepository eventRepository = EventRepository.getInstance();
        return eventRepository.isExist(name, ownerId);
    }

    @Override
    public <T> List<T> getEventArticles(UUID eventId) {
        return List.of();
    }
}
