package br.upe.controller;

import br.upe.persistence.Session;
import br.upe.persistence.Persistence;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SessionController implements Controller {

    private static final String DESCRIPTION = "description";
    private static final String NAME = "name";
    private static final String ID = "id";
    private static final String OWNER_ID = "ownerId";
    private static final String EVENT_ID = "eventId";
    private static final String LOCATION = "location"; // Constante para "location"
    private static final String EVENT_TYPE = "Event"; // Constante para "Event"
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
        if (this.sessionLog == null) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.warning("Sessão não inicializada.");
            }
            return "";
        }

        try {
            return switch (dataToGet) {
                case ID, NAME, DESCRIPTION, LOCATION, EVENT_ID, OWNER_ID -> this.sessionLog.getData(dataToGet);
                case "date" -> String.valueOf(this.sessionLog.getData("date"));
                default -> throw new IOException("Informação não encontrada.");
            };
        } catch (IOException e) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.warning("Informação não existe ou é restrita");
            }
            return "";
        }
    }

    @Override
    public void create(Object... params) {
        if (params.length != 9) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.warning("Número incorreto de parâmetros. Esperado: 9");
            }
            return;
        }

        String eventId = getFatherEventId((String) params[0], (String) params[8]);
        String eventOwnerId = getFatherOwnerId(eventId, (String) params[8]);
        if (!eventOwnerId.equals(params[7])) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.warning("Você não pode criar uma sessão para um evento que você não possui.");
            }
            return;
        }

        String name = (String) params[1];
        if (name.isEmpty() || sessionHashMap.values().stream().anyMatch(session -> session.getData(NAME).equals(name))) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.warning("Nome vazio ou em uso");
            }
            return;
        }

        Persistence session = new Session();
        session.create(eventId, name, (String) params[2], (String) params[3], (String) params[4],
                (String) params[5], (String) params[6], (String) params[7],
                params[8].equals(EVENT_TYPE) ? new EventController().getEventHashMap() : new SubEventController().getSubEventHashMap());
    }

    @Override
    public void delete(Object... params) {
        String ownerId = sessionHashMap.values().stream()
                .filter(session -> session.getData(ID).equals(params[0]))
                .map(session -> session.getData(OWNER_ID))
                .findFirst()
                .orElse("");

        if (!params[1].equals(ownerId)) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.warning("Você não pode deletar essa Sessão");
            }
            return;
        }

        sessionHashMap.entrySet().removeIf(entry -> entry.getValue().getData(ID).equals(params[0]));

        Persistence sessionPersistence = new Session();
        sessionPersistence.delete(sessionHashMap);
    }

    @Override
    public boolean list(String ownerId) {
        this.read();
        boolean found = sessionHashMap.values().stream()
                .filter(session -> session.getData(OWNER_ID).equals(ownerId))
                .peek(session -> {
                    if (LOGGER.isLoggable(Level.WARNING)) {
                        LOGGER.warning(session.getData(NAME));
                    }
                })
                .findAny()
                .isPresent();

        if (!found && LOGGER.isLoggable(Level.WARNING)) {
            LOGGER.warning("Seu usuário atual não é organizador de nenhuma Sessão\n");
        }
        return !found;
    }

    @Override
    public void show(Object... params) {
        this.read();
        if ("userId".equals(params[1])) {
            sessionHashMap.values().stream()
                    .filter(session -> !session.getData(OWNER_ID).equals(params[0]))
                    .forEach(session -> {
                        if (LOGGER.isLoggable(Level.WARNING)) {
                            LOGGER.warning(String.format("Nome: %s - Id: %s\nEvento Pai: %s - Data: %s - Hora: %s\n",
                                    session.getData(NAME), session.getData(ID),
                                    getEventName(session.getData(EVENT_ID)),
                                    session.getData("date"), session.getData("startTime")));
                        }
                    });
        } else if ("sessionId".equals(params[1])) {
            sessionHashMap.values().stream()
                    .filter(session -> session.getData(ID).equals(params[0]))
                    .findFirst()
                    .ifPresent(session -> {
                        if (LOGGER.isLoggable(Level.WARNING)) {
                            LOGGER.warning(String.format("Nome: %s - Id: %s\nEvento Pai: %s - Data: %s - Hora: %s\nDescrição: %s - Local: %s\n",
                                    session.getData(NAME), session.getData(ID),
                                    getEventName(session.getData(EVENT_ID)),
                                    session.getData("date"), session.getData("startTime"),
                                    session.getData(DESCRIPTION), session.getData(LOCATION)));
                        }
                    });
        }
    }

    @Override
    public boolean loginValidate(String email, String cpf) {
        return false;
    }

    private String getEventName(String id) {
        String name = "";
        EventController eventController = new EventController();
        Map<String, Persistence> eventHashMap = eventController.getEventHashMap();

        if (eventHashMap.containsKey(id)) {
            name = eventHashMap.get(id).getData(NAME);
        } else {
            SubEventController subEventController = new SubEventController();
            Map<String, Persistence> subEventHashMap = subEventController.getSubEventHashMap();
            name = subEventHashMap.getOrDefault(id, new Session()).getData(NAME);
        }

        return name;
    }

    @Override
    public void update(Object... params) throws FileNotFoundException {
        if (params.length != 6) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.warning("Só pode ter 6 parâmetros");
            }
            return;
        }

        String oldName = (String) params[0];
        String newName = (String) params[1];
        String userId = (String) params[5];

        if (newName == null || newName.trim().isEmpty()) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.warning("Nome não pode ser vazio");
            }
            return;
        }

        if (sessionHashMap.values().stream()
                .anyMatch(session -> session.getData(NAME).equals(newName) && !session.getData(NAME).equals(oldName))) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.warning("Nome em uso");
            }
            return;
        }

        boolean updated = false;
        for (Persistence session : sessionHashMap.values()) {
            if (session.getData(NAME).equals(oldName) && session.getData(OWNER_ID).equals(userId)) {
                session.update(newName, (String) params[2], (String) params[3], (String) params[4]);
                updated = true;
                break;
            }
        }

        if (!updated && LOGGER.isLoggable(Level.WARNING)) {
            LOGGER.warning("Nome não pertence ao seu usuário atual");
        }
    }

    @Override
    public void read() {
        this.sessionHashMap = new Session().read();
    }

    private String getFatherEventId(String eventName, String eventType) {
        Map<String, Persistence> eventMap = eventType.equals(EVENT_TYPE) ?
                new EventController().getEventHashMap() :
                new SubEventController().getSubEventHashMap();

        return eventMap.values().stream()
                .filter(event -> event.getData(NAME).equals(eventName))
                .map(event -> event.getData(ID))
                .findFirst()
                .orElse("");
    }

    private String getFatherOwnerId(String eventId, String eventType) {
        Map<String, Persistence> eventMap = eventType.equals(EVENT_TYPE) ?
                new EventController().getEventHashMap() :
                new SubEventController().getSubEventHashMap();

        return eventMap.values().stream()
                .filter(event -> event.getData(ID).equals(eventId))
                .map(event -> event.getData(OWNER_ID))
                .findFirst()
                .orElse("");
    }
}
