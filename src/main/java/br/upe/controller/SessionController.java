package br.upe.controller;

import br.upe.persistence.Session;
import br.upe.persistence.Persistence;
import java.io.FileNotFoundException;
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

    public SessionController() {
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
    public void create(Object... params) {
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

        String eventOwnerId = getFatherOwnerId(eventId, (String) params[8]);
        Map<String, Persistence> eventH;

        if (params[8].equals(EVENT_TYPE)) {
            EventController eventController = new EventController();
            eventH = eventController.getEventHashMap();
        } else {
            SubEventController subEventController = new SubEventController();
            eventH = subEventController.getSubEventHashMap();
        }

        if (!eventOwnerId.equals(userId)) {
            LOGGER.warning("Você não pode criar uma sessão para um evento que você não possui.");
            return;
        }

        boolean inUseName = false;
        for (Map.Entry<String, Persistence> entry : this.sessionHashMap.entrySet()) {
            Persistence sessionIndice = entry.getValue();
            if (sessionIndice.getData(NAME).equals(name) || name.isEmpty()) {
                inUseName = true;
                break;
            }
        }

        if (inUseName || name.isEmpty()) {
            LOGGER.warning("Nome vazio ou em uso");
            return;
        }

        Persistence session = new Session();
        session.create(eventId, name, date, description, location, startTime, endTime, userId, eventH);
    }

    @Override
    public void delete(Object... params) {
        String ownerId = "";
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
    public boolean list(String ownerId) {
        this.read();
        boolean isnull = true;

        try {
            boolean found = false;

            for (Map.Entry<String, Persistence> entry : sessionHashMap.entrySet()) {
                Persistence persistence = entry.getValue();
                String currentOwnerId = persistence.getData(OWNER_ID);

                if (ownerId.equals(currentOwnerId)) {

                    if (LOGGER.isLoggable(Level.WARNING)) {
                        LOGGER.warning(persistence.getData(NAME));
                    }

                    found = true;
                    isnull = false;
                }
            }

            if (!found && LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.warning("Seu usuário atual não é organizador de nenhuma Sessão\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return isnull;
    }

    public List<String> list(String ownerId, String type) {
        if(type.equals("fx")){
            this.read();
            List<String> userEvents = new ArrayList<>();

            try {
                for (Map.Entry<String, Persistence> entry : sessionHashMap.entrySet()) {
                    Persistence persistence = entry.getValue();
                    if (persistence.getData(OWNER_ID).equals(ownerId)) {
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
        return List.of();
    }

    @Override
    public void show(Object... params) {
        this.read();

        String paramType = (String) params[1];
        String paramId = (String) params[0];

        for (Map.Entry<String, Persistence> entry : sessionHashMap.entrySet()) {
            Persistence persistence = entry.getValue();

            // Se for "userId", exibe sessões que não pertencem ao usuário
            if (paramType.equals("userId") && !persistence.getData(OWNER_ID).equals(paramId)) {
                logSessionDetails(persistence);
            }

            // Se for "sessionId", exibe detalhes da sessão correspondente
            else if (paramType.equals("sessionId") && persistence.getData(ID).equals(paramId)) {
                logSessionDetails(persistence);
                break; // Sai do loop após encontrar a sessão correspondente
            }
        }
    }

    private void logSessionDetails(Persistence persistence) {
        String eventName = getEventName(persistence.getData(EVENT_ID));
        if (LOGGER.isLoggable(Level.WARNING)) {
            LOGGER.warning("Nome: " + persistence.getData(NAME) + " - " + "Id: " + persistence.getData(ID) +
                    "\nEvento Pai: " + eventName + " - " + "Data: " + persistence.getData("date") +
                    " - " + "Hora: " + persistence.getData("startTime") +
                    "\nDescrição: " + persistence.getData(DESCRIPTION) + " - " + "Local: " + persistence.getData(LOCATION) + "\n");
        }
    }


    @Override
    public boolean loginValidate(String email, String cpf) {
        return false;
    }

    private String getEventName(String id) {
        String name = "";
        EventController eventController = new EventController();
        Map<String, Persistence> evenH = eventController.getEventHashMap();
        boolean isEvent = false;
        for (Map.Entry<String, Persistence> entry : evenH.entrySet()) {
            Persistence persistence = entry.getValue();
            if (persistence.getData("id").equals(id)) {
                name = persistence.getData("name");
                isEvent = true;
                break;
            }
        }

        if (!isEvent) {
            SubEventController subEventController = new SubEventController();
            Map<String, Persistence> subEvenH = subEventController.getSubEventHashMap();
            for (Map.Entry<String, Persistence> entry : subEvenH.entrySet()) {
                Persistence persistence = entry.getValue();
                if (persistence.getData("id").equals(id)) {
                    name = persistence.getData("name");
                    break;
                }
            }
        }

        return name;
    }

    @Override
    public void update(Object... params) throws FileNotFoundException {
        if (params.length != 6) {
            LOGGER.warning("Só pode ter 6 parâmetros");
            return;
        }

        String oldName = (String) params[0];
        String newName = (String) params[1];
        String newDate = (String) params[2];
        String newDescription = (String) params[3];
        String newLocation = (String) params[4];
        String userId = (String) params[5];

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
                session.update(newName, newDate, newDescription, newLocation);
                isOwner = true;
                break;
            }
        }

        if (!isOwner) {
            LOGGER.warning("Nome não pertence ao seu usuário atual");
        }
    }

    @Override
    public void read() {
        Persistence persistence = new Session();
        this.sessionHashMap = persistence.read();
    }

    private String getFatherEventId(String eventName, String eventType) {
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

    private String getFatherOwnerId(String eventId, String eventType) {
        String fatherOwnerId = "";
        if (eventType.equals(EVENT_TYPE)) {
            EventController eventController = new EventController();
            Map<String, Persistence> eventH = eventController.getEventHashMap();
            for (Map.Entry<String, Persistence> entry : eventH.entrySet()) {
                Persistence eventIndice = entry.getValue();
                if (eventIndice.getData(ID).equals(eventId)) {
                    fatherOwnerId = eventIndice.getData(OWNER_ID);
                    break;
                }
            }
        } else {
            SubEventController subEventController = new SubEventController();
            Map<String, Persistence> eventH = subEventController.getSubEventHashMap();
            for (Map.Entry<String, Persistence> entry : eventH.entrySet()) {
                Persistence eventIndice = entry.getValue();
                if (eventIndice.getData(ID).equals(eventId)) {
                    fatherOwnerId = eventIndice.getData(OWNER_ID);
                    break;
                }
            }
        }
        return fatherOwnerId;
    }
}
