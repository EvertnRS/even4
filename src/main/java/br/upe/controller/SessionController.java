package br.upe.controller;

import br.upe.persistence.Model;
import br.upe.persistence.Session;
import br.upe.persistence.SubEvent;
import br.upe.persistence.repository.Persistence;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class SessionController implements Controller {

    private static final String DESCRIPTION = "description";
    private static final String NAME = "name";
    private static final String ID = "id";
    private static final String OWNER_ID = "ownerId";
    private static final String EVENT_ID = "eventId";
    private static final String LOCATION = "location";
    private static final String STARTTIME = "startTime";
    private static final String ENDTIME = "endTime";
    private static final String EVENT_TYPE = "Event";
    private static final Logger LOGGER = Logger.getLogger(SessionController.class.getName());

    private Map<UUID, Persistence> sessionHashMap;
    private Persistence sessionLog;

    public SessionController() throws IOException {
        this.sessionHashMap = new HashMap<>();
        this.read();
    }

    public Map<UUID, Persistence> getHashMap() {
        return sessionHashMap;
    }

    @Override
    public <T> List<T> getEventArticles(UUID eventId) {
        return List.of();
    }

    @Override
    public List<Model> getAll() {
        return List.of();
    }

    public void setSessionHashMap(Map<UUID, Persistence> sessionHashMap) {
        this.sessionHashMap = sessionHashMap;
        if (!sessionHashMap.isEmpty()) {
            this.sessionLog = sessionHashMap.values().iterator().next();
        }
    }

    @Override
    public String getData(String dataToGet) {
        String data = "";
        if (this.sessionLog == null) {
            LOGGER.warning("Sessão não inicializada.");
            return "";
        }

        try {
            switch (dataToGet) {
                case ID -> data = (String) this.sessionLog.getData(ID);
                case NAME -> data = (String) this.sessionLog.getData(NAME);
                case DESCRIPTION -> data = (String) this.sessionLog.getData(DESCRIPTION);
                case "date" -> data = String.valueOf(this.sessionLog.getData("date"));
                case LOCATION -> data = (String) this.sessionLog.getData(LOCATION);
                case EVENT_ID -> data = (String) this.sessionLog.getData(EVENT_ID);
                case OWNER_ID -> data = (String) this.sessionLog.getData(OWNER_ID);
                case STARTTIME -> data = (String) this.sessionLog.getData(STARTTIME);
                case ENDTIME -> data = (String) this.sessionLog.getData(ENDTIME);
                default -> throw new IOException();
            }
        } catch (IOException e) {
            LOGGER.warning("Informação não existe ou é restrita");
        }
        return data;
    }

    public void create(Object... params) throws IOException {
        if (params.length != 9) {
            LOGGER.warning("Número incorreto de parâmetros. Esperado: 9");
            return;
        }

        String eventId = getFatherEventId((String) params[0], (String) params[8]);
        String name = (String) params[1];
        String date = (String) params[2];
        String description = (String) params[3];
        String location = (String) params[4];
        String startTime = (String) params[5];
        String endTime = (String) params[6];
        String userId = (String) params[7];

        Map<UUID, Persistence> eventH;
        if (params[8].equals(EVENT_TYPE)) {
            EventController eventController = new EventController();
            eventH = eventController.getHashMap();
        } else {
            SubEventController subEventController = new SubEventController();
            eventH = subEventController.getHashMap();
        }

        /*Persistence session = new Session();
        session.create(eventId, name, date, description, location, startTime, endTime, userId, eventH);
        UUID sessionId = (UUID) session.getData(ID);
        sessionHashMap.put(sessionId, session);

        this.sessionLog = session;*/
    }
    /*
    private void cascadeDelete(String id) throws IOException {

        AttendeeController attendeeController = new AttendeeController();
        attendeeController.read();
        Iterator<Map.Entry<UUID, Persistence>> attendeeIterator = attendeeController.getHashMap().entrySet().iterator();
        while (attendeeIterator.hasNext()) {
            Map.Entry<UUID, Persistence> entry = attendeeIterator.next();
            UUID sessionId = (UUID) entry.getValue().getData("sessionId");
            if (sessionHashMap.containsKey(sessionId)) {
                attendeeIterator.remove();
            }
        }
        attendeeController.getHashMap().values().forEach(attendee -> {
            try {
                attendee.delete(attendeeController.getHashMap());
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        });

        String subEventName = (String) sessionHashMap.get(id).getData("name");
        SubmitArticleController articleController = new SubmitArticleController();
        articleController.read(subEventName);
        Iterator<Map.Entry<String, Persistence>> articleIterator = articleController.getHashMap().entrySet().iterator();
        while (articleIterator.hasNext()) {
            Map.Entry<String, Persistence> entry = articleIterator.next();
            if (entry.getValue().getData(EVENT_ID).equals(id)) {
                articleIterator.remove();
            }
        }
        articleController.getHashMap().values().forEach(article -> {
            try {
                article.delete(articleController.getHashMap());
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        });
    }*/

    @Override
    public void delete(Object... params) throws IOException {
        String ownerId = "";
        for (Map.Entry<UUID, Persistence> entry : sessionHashMap.entrySet()) {
            Persistence persistence = entry.getValue();
            if (persistence.getData(ID).equals(params[0])) {
                ownerId = (String) persistence.getData(OWNER_ID);
            }
        }

        if ((params[1]).equals(ownerId)) {
            Iterator<Map.Entry<UUID, Persistence>> iterator = sessionHashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<UUID, Persistence> entry = iterator.next();
                Persistence sessionIndice = entry.getValue();
                if (sessionIndice.getData(ID).equals(params[0])) {
                    iterator.remove();
                }
            }
            /*Persistence sessionPersistence = new Session();
            sessionPersistence.delete(sessionHashMap);*/
        } else {
            LOGGER.warning("Você não pode deletar essa Sessão");
        }
    }

    @Override
    public List<String> list(Object... params) throws IOException {
            this.read();
            List<String> userEvents = new ArrayList<>();

            try {
                for (Map.Entry<UUID, Persistence> entry : sessionHashMap.entrySet()) {
                    Persistence persistence = entry.getValue();
                    if (persistence.getData(OWNER_ID).equals(params[0])) {
                        String eventName = (String) persistence.getData("name");
                        userEvents.add(eventName);
                    }
                }
                if (userEvents.isEmpty()) {
                    LOGGER.warning("Seu usuário atual é organizador de nenhuma sessão");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return userEvents;
        }

    @Override
    public boolean loginValidate(String email, String cpf) {
        return false;
    }

    @Override
    public void update(Object... params) throws IOException {
        if (params.length != 8) {
            LOGGER.warning("Só pode ter 6 parâmetros");
            return;
        }

        String oldName = (String) params[0];
        String newName = (String) params[1];
        String newDate = (String) params[2];
        String newDescription = (String) params[3];
        String newLocation = (String) params[4];
        String userId = (String) params[5];
        String newStartTime = (String) params[6];
        String newEndTime = (String) params[7];

        if (newName == null || newName.trim().isEmpty()) {
            LOGGER.warning("Nome não pode ser vazio");
            return;
        }

        boolean nameExists = sessionHashMap.values().stream()
                .anyMatch(session -> session.getData(NAME).equals(newName) && !session.getData(NAME).equals(oldName));

        if (nameExists) {
            LOGGER.warning("Nome em uso");
            return;
        }

        boolean isOwner = false;
        for (Persistence session : sessionHashMap.values()) {

            if (session.getData(NAME).equals(oldName) && session.getData(OWNER_ID).equals(userId)) {

                session.setData("name", newName);
                session.setData("date", newDate);
                session.setData(DESCRIPTION, newDescription);
                session.setData(LOCATION, newLocation);
                session.setData(STARTTIME, newStartTime);
                session.setData(ENDTIME, newEndTime);

                /*Persistence sessionPersistence = new Session();
                sessionPersistence.update(sessionHashMap);
                isOwner = true;
                break;*/
            }
        }

        if (!isOwner) {
            LOGGER.warning("Nome não pertence ao seu usuário atual");
        }
    }

    @Override
    public void read() throws IOException {
        /*Persistence persistence = new Session();
        this.sessionHashMap = persistence.read();
        if (!sessionHashMap.isEmpty()) {
            this.sessionLog = sessionHashMap.values().iterator().next();
        }*/
    }

    private String getFatherEventId(String eventName, String eventType) throws IOException {
        String fatherId = "";
        if (eventType.equals(EVENT_TYPE)) {
            EventController eventController = new EventController();
            Map<UUID, Persistence> eventH = eventController.getHashMap();
            for (Map.Entry<UUID, Persistence> entry : eventH.entrySet()) {
                Persistence eventIndice = entry.getValue();
                if (eventIndice.getData(NAME).equals(eventName)) {
                    fatherId = (String) eventIndice.getData(ID);
                    break;
                }
            }
        } else {
            SubEventController subEventController = new SubEventController();
            Map<UUID, Persistence> eventH = subEventController.getHashMap();
            for (Map.Entry<UUID, Persistence> entry : eventH.entrySet()) {
                Persistence eventIndice = entry.getValue();
                if (eventIndice.getData(NAME).equals(eventName)) {
                    fatherId = (String) eventIndice.getData(ID);
                    break;
                }
            }
        }
        return fatherId;
    }
}
