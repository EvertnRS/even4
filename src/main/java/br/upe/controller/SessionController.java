package br.upe.controller;

import br.upe.persistence.Session;
import br.upe.persistence.Persistence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SessionController implements Controller {

    private static final String DESCRIPTION = "description";
    private static final String NAME = "name";
    private static final String ID = "id";
    private static final String OWNER_ID = "ownerId";
    private static final String EVENT_ID = "eventId";
    private static final String LOCATION = "location";
    private static final String EVENT_TYPE = "Event";
    private static final Logger LOGGER = Logger.getLogger(SessionController.class.getName());

    private Map<String, Persistence> sessionHashMap;
    private Persistence sessionLog;

    public SessionController() throws IOException {
        this.read();
    }

    public Map<String, Persistence> getSessionHashMap() {
        return sessionHashMap;
    }

    public void setSessionHashMap(Map<String, Persistence> sessionHashMap) {
        this.sessionHashMap = sessionHashMap;
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
                case ID -> data = this.sessionLog.getData(ID);
                case NAME -> data = this.sessionLog.getData(NAME);
                case DESCRIPTION -> data = this.sessionLog.getData(DESCRIPTION);
                case "date" -> data = String.valueOf(this.sessionLog.getData("date"));
                case LOCATION -> data = this.sessionLog.getData(LOCATION);
                case EVENT_ID -> data = this.sessionLog.getData(EVENT_ID);
                case OWNER_ID -> data = this.sessionLog.getData(OWNER_ID);
                default -> throw new IOException();
            }
        } catch (IOException e) {
            LOGGER.warning("Informação não existe ou é restrita");
        }
        return data;
    }

    @Override
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

        Map<String, Persistence> eventH;

        if (params[8].equals(EVENT_TYPE)) {
            EventController eventController = new EventController();
            eventH = eventController.getEventHashMap();
        } else {
            SubEventController subEventController = new SubEventController();
            eventH = subEventController.getSubEventHashMap();
        }

        Persistence session = new Session();
        session.create(eventId, name, date, description, location, startTime, endTime, userId, eventH);
    }

    private void cascadeDelete(String id) throws IOException {

        AttendeeController attendeeController = new AttendeeController();
        attendeeController.read();
        Iterator<Map.Entry<String, Persistence>> attendeeIterator = attendeeController.getAttendeeHashMap().entrySet().iterator();
        while (attendeeIterator.hasNext()) {
            Map.Entry<String, Persistence> entry = attendeeIterator.next();
            String sessionId = entry.getValue().getData("sessionId");
            if (sessionHashMap.containsKey(sessionId)) {
                attendeeIterator.remove();
            }
        }
        attendeeController.getAttendeeHashMap().values().forEach(attendee -> {
            try {
                attendee.delete(attendeeController.getAttendeeHashMap());
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        });

        String subEventName = sessionHashMap.get(id).getData("name");
        SubmitArticleController articleController = new SubmitArticleController();
        articleController.read(subEventName);
        Iterator<Map.Entry<String, Persistence>> articleIterator = articleController.getArticleHashMap().entrySet().iterator();
        while (articleIterator.hasNext()) {
            Map.Entry<String, Persistence> entry = articleIterator.next();
            if (entry.getValue().getData(EVENT_ID).equals(id)) {
                articleIterator.remove();
            }
        }
        articleController.getArticleHashMap().values().forEach(article -> {
            try {
                article.delete(articleController.getArticleHashMap());
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        });
    }

    @Override
    public void delete(Object... params) throws IOException {
        String ownerId = "";
        cascadeDelete((String) params[0]);
        for (Map.Entry<String, Persistence> entry : sessionHashMap.entrySet()) {
            Persistence persistence = entry.getValue();
            if (persistence.getData(ID).equals(params[0])) {
                ownerId = persistence.getData(OWNER_ID);
            }
        }

        if ((params[1]).equals(ownerId)) {
            Iterator<Map.Entry<String, Persistence>> iterator = sessionHashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Persistence> entry = iterator.next();
                Persistence sessionIndice = entry.getValue();
                if (sessionIndice.getData(ID).equals(params[0])) {
                    iterator.remove();
                }
            }
            Persistence sessionPersistence = new Session();
            sessionPersistence.delete(sessionHashMap);
        } else {
            LOGGER.warning("Você não pode deletar essa Sessão");
        }
    }

    @Override
    public List<String> list(Object... params) throws IOException {
            this.read();
            List<String> userEvents = new ArrayList<>();

            try {
                for (Map.Entry<String, Persistence> entry : sessionHashMap.entrySet()) {
                    Persistence persistence = entry.getValue();
                    if (persistence.getData(OWNER_ID).equals(params[0])) {
                        userEvents.add(persistence.getData("name"));
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
                session.setData("startTime", newStartTime);
                session.setData("endTime", newEndTime);

                Persistence sessionPersistence = new Session();
                sessionPersistence.update(sessionHashMap);
                isOwner = true;
                break;
            }
        }

        if (!isOwner) {
            LOGGER.warning("Nome não pertence ao seu usuário atual");
        }
    }

    @Override
    public void read() throws IOException {
        Persistence persistence = new Session();
        this.sessionHashMap = persistence.read();
    }

    private String getFatherEventId(String eventName, String eventType) throws IOException {
        String fatherId = "";
        if (eventType.equals(EVENT_TYPE)) {
            EventController eventController = new EventController();
            Map<String, Persistence> eventH = eventController.getEventHashMap();
            for (Map.Entry<String, Persistence> entry : eventH.entrySet()) {
                Persistence eventIndice = entry.getValue();
                if (eventIndice.getData(NAME).equals(eventName)) {
                    fatherId = eventIndice.getData(ID);
                    break;
                }
            }
        } else {
            SubEventController subEventController = new SubEventController();
            Map<String, Persistence> eventH = subEventController.getSubEventHashMap();
            for (Map.Entry<String, Persistence> entry : eventH.entrySet()) {
                Persistence eventIndice = entry.getValue();
                if (eventIndice.getData(NAME).equals(eventName)) {
                    fatherId = eventIndice.getData(ID);
                    break;
                }
            }
        }
        return fatherId;
    }
}
