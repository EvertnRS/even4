package br.upe.controller;
import br.upe.persistence.Event;
import br.upe.persistence.Persistence;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class EventController implements Controller {
    private static final String OWNER_ID = "ownerId";
    private static final String DESCRIPTION = "description";
    private static final String LOCATION = "location";
    private static final String EVENT_ID = "eventId";
    private static final Logger LOGGER = Logger.getLogger(EventController.class.getName());

    private Map<String, Persistence> eventHashMap;
    private Persistence eventLog;


    public EventController() throws IOException {
        this.read();
    }

    public Map<String, Persistence> getHashMap() {
        System.out.println(eventHashMap);
        return eventHashMap;
    }

    public void setEventHashMap(Map<String, Persistence> eventHashMap) {
        this.eventHashMap = eventHashMap;
    }

    @Override
    public List<String> list(Object... params) throws IOException {
            this.read();
            List<String> userEvents = new ArrayList<>();

            try {
                for (Map.Entry<String, Persistence> entry : eventHashMap.entrySet()) {
                    Persistence persistence = entry.getValue();
                    if (persistence.getData(OWNER_ID).equals(params[0])) {
                        userEvents.add(persistence.getData("name"));
                    }
                }
                if (userEvents.isEmpty()) {
                    LOGGER.warning("Seu usuário atual é organizador de nenhum evento");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return userEvents;
        }

    public void update(Object... params) throws IOException {
        if (!isValidParamsLength(params)) {
            LOGGER.warning("Só pode ter 6 parametros");
            return;
        }

        String eventId = (String) params[0];
        String newName = (String) params[1];
        String newDate = (String) params[2];
        String newDescription = (String) params[3];
        String newLocation = (String) params[4];
        //por que não ta usando?
        String userId = (String) params[5];

        String eventValid = getEventById(eventId);

        if (eventValid == null) {
            LOGGER.warning("Você não pode alterar este Evento");
            return;
        }

        if (isNameInUseOrEmpty(newName)) {
            LOGGER.warning("Nome em uso ou vazio");
            return;
        }

        updateEvent(eventId, newName, newDate, newDescription, newLocation);
    }

    private boolean isValidParamsLength(Object... params) {
        return params.length == 6;
    }

    private String getOwnerEventId(String oldName, String userId) {
        for (Map.Entry<String, Persistence> entry : eventHashMap.entrySet()) {
            Persistence persistence = entry.getValue();
            String name = persistence.getData("name");
            String ownerId = persistence.getData(OWNER_ID);

            if (name != null && name.equals(oldName) && ownerId != null && ownerId.equals(userId)) {
                return persistence.getData("id");
            }
        }
        return null;
    }

    private String getEventById(String id) {
        for (Map.Entry<String, Persistence> entry : eventHashMap.entrySet()) {
            Persistence persistence = entry.getValue();
            if (persistence.getData("id").equals(id)) {
                return persistence.getData("name");
            }
        }
        return null;
    }

    private boolean isNameInUseOrEmpty(String newName) {
        for (Map.Entry<String, Persistence> entry : eventHashMap.entrySet()) {
            Persistence event = entry.getValue();
            String name = event.getData("name");
            if (name.isEmpty() || name.equals(newName)) {
                return true;
            }
        }
        return false;
    }

    private void updateEvent(String id, String newName, String newDate, String newDescription, String newLocation) throws IOException {
        Persistence newEvent = eventHashMap.get(id);
        if (newEvent != null) {
            newEvent.setData("name", newName);
            newEvent.setData("date", newDate);
            newEvent.setData(DESCRIPTION, newDescription);
            newEvent.setData(LOCATION, newLocation);
            eventHashMap.put(id, newEvent);
            Persistence eventPersistence = new Event();
            eventPersistence.update(eventHashMap);
        } else {
            LOGGER.warning("Evento não encontrado");
        }
    }


    @Override
    public void read() throws IOException {
        Persistence eventPersistence = new Event();
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
                case "id" -> data = this.eventLog.getData("id");
                case "name" -> data = this.eventLog.getData("name");
                case DESCRIPTION -> data = this.eventLog.getData(DESCRIPTION);
                case "date" -> data = String.valueOf(this.eventLog.getData("date"));
                case LOCATION -> data = this.eventLog.getData(LOCATION);
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
        String date = (String) params[1];
        String description = (String) params[2];
        String location = (String) params[3];
        String idOwner = (String) params[4];

        for (Map.Entry<String, Persistence> entry : this.eventHashMap.entrySet()) {
            Persistence event = entry.getValue();
            if (event.getData("name").equals(name) || name.isEmpty()) {
                LOGGER.warning("Nome em uso ou vazio");
                return;
            }
        }

        Persistence event = new Event();
        event.create(name, date, description, location, idOwner);

    }

    private void cascadeDelete(String id) throws IOException {
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
            String sessionId = entry.getValue().getData("sessionId");
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
        String eventName = eventHashMap.get(id).getData("name");
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
    }


    @Override
    public void delete(Object... params) throws IOException {
        String ownerId = "";
        cascadeDelete((String) params[0]);
        for (Map.Entry<String, Persistence> entry : eventHashMap.entrySet()) {
            Persistence persistence = entry.getValue();
            if (persistence.getData("id").equals(params[0])){
                ownerId = persistence.getData(OWNER_ID);
            }
        }

        if ((params[1]).equals(ownerId)) {
            cascadeDelete((String) params[0]);
            Iterator<Map.Entry<String, Persistence>> iterator = eventHashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Persistence> entry = iterator.next();
                Persistence eventindice = entry.getValue();
                if (eventindice.getData("id").equals(params[0])) {
                    iterator.remove();
                }
            }
            Persistence eventPersistence = new Event();
            eventPersistence.delete(eventHashMap);
        } else {
            LOGGER.warning("Você não pode deletar esse evento");
        }
    }
}
