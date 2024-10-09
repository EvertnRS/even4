package br.upe.controller;

import br.upe.persistence.Session;
import br.upe.persistence.Persistence;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

public class SessionController implements Controller {
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
                case "id" -> data = this.sessionLog.getData("id");
                case "name" -> data = this.sessionLog.getData("name");
                case "description" -> data = this.sessionLog.getData("description");
                case "date" -> data = String.valueOf(this.sessionLog.getData("date"));
                case "location" -> data = this.sessionLog.getData("location");
                case "eventId" -> data = this.sessionLog.getData("eventId");
                case "ownerId" -> data = this.sessionLog.getData("ownerId");
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

        if (params[8].equals("Event")){
            EventController eventController = new EventController();
            eventH = eventController.getEventHashMap();
        } else{
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
            if (sessionIndice.getData("name").equals(name) || name.isEmpty()) {
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
            if (persistence.getData("name").equals(params[0])) {
                ownerId = persistence.getData("ownerId");
            }
        }

        if ((params[1]).equals("name") && (params[2]).equals(ownerId)) {
            Iterator<Map.Entry<String, Persistence>> iterator = sessionHashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Persistence> entry = iterator.next();
                Persistence sessionIndice = entry.getValue();
                if (sessionIndice.getData("name").equals(params[0])) {
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
        boolean isnull = true;
        try {
            boolean found = false;
            for (Map.Entry<String, Persistence> entry : sessionHashMap.entrySet()) {
                Persistence persistence = entry.getValue();
                if (persistence.getData("ownerId").equals(ownerId)) {
                    LOGGER.warning(persistence.getData("name"));
                    found = true;
                    isnull = false;
                }
            }
            if (!found) {
                LOGGER.warning("Seu usuário atual não é organizador de nenhuma Sessão\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isnull;
    }

    @Override
    public void show(Object... params) {
        this.read();
        if (params[1].equals("userId")) {
            for (Map.Entry<String, Persistence> entry : sessionHashMap.entrySet()) {
                Persistence persistence = entry.getValue();
                if (!persistence.getData("ownerId").equals(params[0])){
                    String eventName = getEventName(persistence.getData("eventId"));
                    LOGGER.warning("Nome: " + persistence.getData("name") + " - " + "Id: " + persistence.getData("id") +  "\nEvento Pai: " + eventName + " - " + "Data: " + persistence.getData("date") + " - " + "Hora: " + persistence.getData("startTime") + "\n");
                }
            }
        } else if (params[1].equals("sessionId")) {
            for (Map.Entry<String, Persistence> entry : sessionHashMap.entrySet()) {
                Persistence persistence = entry.getValue();
                if (persistence.getData("id").equals(params[0])){
                    String eventName = getEventName(persistence.getData("eventId"));
                    LOGGER.warning("Nome: " + persistence.getData("name") + " - " + "Id: " + persistence.getData("id") +  "\nEvento Pai: " + eventName + " - " + "Data: " + persistence.getData("date") + " - " + "Hora: " + persistence.getData("startTime") + "\nDescrição: " + persistence.getData("description") + " - " + "Local: " + persistence.getData("location") + "\n");
                    break;
                }
            }
        }

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
            }
        }

        if (!isEvent) {
            SubEventController subEventController = new SubEventController();
            Map<String, Persistence> subEvenH = subEventController.getSubEventHashMap();
            for (Map.Entry<String, Persistence> entry : subEvenH.entrySet()) {
                Persistence persistence = entry.getValue();
                if (persistence.getData("id").equals(id)) {
                    name = persistence.getData("name");
                }
            }
        }

        return name;
    }

    @Override
    public void update(Object... params) throws FileNotFoundException {
        if (params.length != 6) {
            LOGGER.warning("Só pode ter 6 parametros");
            return;
        }

        String oldName = (String) params[0];
        String newName = (String) params[1];
        String newDate = (String) params[2];
        String newDescription = (String) params[3];
        String newLocation = (String) params[4];
        String userId = (String) params[5];

        // Verifica se o nome novo não é vazio e se já não existe
        if (newName == null || newName.trim().isEmpty()) {
            LOGGER.warning("Nome não pode ser vazio");
            return;
        }

        boolean nameExists = sessionHashMap.values().stream()
                .anyMatch(session -> session.getData("name").equals(newName) && !session.getData("name").equals(oldName));

        if (nameExists) {
            LOGGER.warning("Nome em uso");
            return;
        }

        // Aqui estamos apenas verificando se o usuário é o proprietário
        boolean isOwner = false;
        String id = null;

        for (Map.Entry<String, Persistence> entry : sessionHashMap.entrySet()) {
            Persistence persistence = entry.getValue();
            String name = persistence.getData("name");
            String ownerId = persistence.getData("ownerId");

            if (ownerId != null && ownerId.equals(userId)) {
                isOwner = true;
                id = persistence.getData("id");
                break;
            }
        }

        if (isOwner) {
            if (id != null) {
                Persistence newSession = sessionHashMap.get(id);
                if (newSession != null) {
                    newSession.setData("name", newName);
                    newSession.setData("date", newDate);
                    newSession.setData("description", newDescription);
                    newSession.setData("location", newLocation);
                    sessionHashMap.put(id, newSession);
                    Persistence SessionPersistence = new Session();
                    SessionPersistence.update(sessionHashMap);
                } else {
                    LOGGER.warning("Sessão não encontrada");
                }
            } else {
                LOGGER.warning("Você não pode alterar esta Sessão");
            }
        } else {
            LOGGER.warning("Você não pode alterar esta Sessão");
        }
    }


    @Override
    public void read() {
        Persistence sessionPersistence = new Session();
        this.sessionHashMap = sessionPersistence.read();
    }

    @Override
    public boolean loginValidate(String email, String cpf) {
        //Método não implementado
        return false;
    }

    private String getFatherEventId(String searchId, String type) {
        Map<String, Persistence> list;
        if (type.equals("Event")){
            EventController eventController = new EventController();
            list = eventController.getEventHashMap();
        } else{
            SubEventController subEventController = new SubEventController();
            list = subEventController.getSubEventHashMap();
        }

        String fatherId = "";
        boolean found = false;
        for (Map.Entry<String, Persistence> entry : list.entrySet()) {
            Persistence listIndice = entry.getValue();
            if (listIndice.getData("name").equals(searchId)) {
                fatherId = listIndice.getData("id");
                found = true;
                break;
            }
        }
        if (!found) {
            LOGGER.warning("Evento pai não encontrado\n");
        }

        return fatherId;
    }

    private String getFatherOwnerId(String eventId, String type) {
        Map<String, Persistence> list;
        if (type.equals("Event")){
            EventController eventController = new EventController();
            list = eventController.getEventHashMap();
        } else{
            SubEventController subEventController = new SubEventController();
            list = subEventController.getSubEventHashMap();
        }

        String fatherOwnerId = "";
        for (Map.Entry<String, Persistence> entry : list.entrySet()) {
            Persistence listIndice = entry.getValue();
            if (listIndice.getData("id").equals(eventId)) {
                fatherOwnerId = listIndice.getData("ownerId");
                break;
            }
        }
        return fatherOwnerId;
    }
}
