package br.upe.controller;
import br.upe.persistence.Event;
import br.upe.persistence.repository.EventRepository;
import br.upe.persistence.repository.Persistence;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class EventController implements Controller {
    private static final String OWNER_ID = "ownerId";
    private static final String DESCRIPTION = "description";
    private static final String LOCATION = "location";
    private static final String EVENT_ID = "eventId";
    private static final Logger LOGGER = Logger.getLogger(EventController.class.getName());

    private Map<UUID, Persistence> eventHashMap;
    private Persistence eventLog;


    public EventController() throws IOException {
        this.read();
    }

    public List<Event> getAll() {
        EventRepository eventRepository = EventRepository.getInstance();
        return eventRepository.getAllEvents();
    }

    public Map<UUID, Persistence> getHashMap() {
        System.out.println(eventHashMap);
        return eventHashMap;
    }

    public void setEventHashMap(Map<UUID, Persistence> eventHashMap) {
        this.eventHashMap = eventHashMap;
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

    public void update(Object... params) throws IOException {
        if (!isValidParamsLength(params)) {
            LOGGER.warning("Só pode ter 5 parametros");
            return;
        }

        UUID eventId = (UUID) params[0];
        String newName = (String) params[1];
        Date newDate = (Date) params[2];
        String newDescription = (String) params[3];
        String newLocation = (String) params[4];

        updateEvent(eventId, newName, newDate, newDescription, newLocation);
    }

    private boolean isValidParamsLength(Object... params) {
        return params.length == 5;
    }

    private void updateEvent(UUID id, String newName, Date newDate, String newDescription, String newLocation) throws IOException {
        if (id != null) {
            EventRepository eventRepository = EventRepository.getInstance();
            eventRepository.update(id, newName, newDate, newDescription, newLocation);
        } else {
            LOGGER.warning("Evento não encontrado");
        }
    }

    @Override
    public void read() throws IOException {
        Persistence eventPersistence = new EventRepository();
        this.eventHashMap = eventPersistence.read();
    }


    @Override
    public boolean loginValidate(String email, String cpf) {
        //Método não implementado
        return false;
    }

    @Override
    public String getData(String dataToGet) {
        String data = "";
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

    @Override
    public void create(Object... params) {
        if (params.length != 5) {
            LOGGER.warning("Só pode ter 5 parâmetros");
            return;
        }

        String name = (String) params[0];
        Date date = (Date) params[1];

        String description = (String) params[2];
        String location = (String) params[3];
        String idOwner = (String) params[4];

        Persistence event = new EventRepository();
        event.create(name, date, description, location, idOwner);

    }
     /*
    private void cascadeDelete(UUID id) throws IOException {
        // Deletar todas as sessões relacionadas ao evento
        SessionController sessionController = new SessionController();
        sessionController.read();
        sessionController.getHashMap().entrySet().removeIf(entry ->
                entry.getValue().getData(EVENT_ID).equals(id)
        );
        sessionController.getHashMap().values().forEach(session ->
                {
                    try {
                        session.delete(sessionController.getHashMap());
                    } catch (IOException e) {
                        throw new IllegalArgumentException(e);
                    }
                }
        );

        // Deletar todos os subeventos relacionados ao evento
        SubEventController subEventController = new SubEventController();
        subEventController.read();
        subEventController.getHashMap().entrySet().removeIf(entry ->
                entry.getValue().getData(EVENT_ID).equals(id)
        );
        subEventController.getHashMap().values().forEach(subEvent ->
                {
                    try {
                        subEvent.delete(subEventController.getHashMap());
                    } catch (IOException e) {
                        throw new IllegalArgumentException(e);
                    }
                }
        );

        // Deletar todos os participantes relacionados às sessões do evento
        AttendeeController attendeeController = new AttendeeController();
        attendeeController.read();
        attendeeController.getHashMap().entrySet().removeIf(entry -> {
            UUID sessionId = (UUID) entry.getValue().getData("sessionId");
            return sessionController.getHashMap().containsKey(sessionId);
        });
        attendeeController.getHashMap().values().forEach(attendee ->
                {
                    try {
                        attendee.delete(attendeeController.getHashMap());
                    } catch (IOException e) {
                        throw new IllegalArgumentException(e);
                    }
                }
        );

        // Deletar todos os artigos relacionados ao evento
        String eventName = (String) eventHashMap.get(id).getData("name");
        SubmitArticleController articleController = new SubmitArticleController();
        articleController.read(eventName);
        articleController.getHashMap().entrySet().removeIf(entry ->
                entry.getValue().getData(EVENT_ID).equals(id)
        );
        articleController.getHashMap().values().forEach(article ->
                {
                    try {
                        article.delete(articleController.getHashMap());
                    } catch (IOException e) {
                        throw new IllegalArgumentException(e);
                    }
                }
        );
    }*/


    @Override
    public void delete(Object... params) throws IOException {
        if (params.length != 2) {
            LOGGER.warning("Só pode ter 2 parametro");
            return;
        }

        EventRepository eventRepository = EventRepository.getInstance();

        UUID id = (UUID) params[0];
        UUID ownerId = UUID.fromString((String) params[1]);

        eventRepository.delete(id, ownerId);
    }
}
