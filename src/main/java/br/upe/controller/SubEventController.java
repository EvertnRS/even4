package br.upe.controller;

import br.upe.persistence.SubEvent;
import br.upe.persistence.Persistence;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class SubEventController implements Controller {
    private static final String DESCRIPTION = "description";
    private static final String LOCATION = "location";
    private static final String OWNER_ID = "ownerId";
    private static final String EVENT_ID = "eventId";
    private static final Logger LOGGER = Logger.getLogger(SubEventController.class.getName());
    private Map<String, Persistence> subEventHashMap;
    private Persistence subEventLog;

    public SubEventController() throws IOException {
        this.read();
    }

    public Map<String, Persistence> getHashMap() {
        return subEventHashMap;
    }

    public void setEventHashMap(Map<String, Persistence> subEventHashMap) {
        this.subEventHashMap = subEventHashMap;
    }

    @Override
    public String getData(String dataToGet) {
        String data = "";
        try {
            switch (dataToGet) {
                case "id" -> data = this.subEventLog.getData("id");
                case "name" -> data = this.subEventLog.getData("name");
                case DESCRIPTION -> data = this.subEventLog.getData(DESCRIPTION);
                case "date" -> data = String.valueOf(this.subEventLog.getData("date"));
                case LOCATION -> data = this.subEventLog.getData(LOCATION);
                case EVENT_ID -> data = this.subEventLog.getData(EVENT_ID);
                case OWNER_ID -> data = this.subEventLog.getData(OWNER_ID);
                default -> throw new IOException();
            }
        } catch (IOException e) {
            LOGGER.warning("Informação não existe ou é restrita");
        }
        return data;
    }

    @Override
    public void create(Object... params) throws IOException {
        if (params.length != 6) {
            LOGGER.warning("Só pode ter 6 parâmetros");
            return;
        }

        String eventId = getFatherEventId((String) params[0]);
        String name = (String) params[1];
        String date = (String) params[2];
        String description = (String) params[3];
        String location = (String) params[4];
        String userId = (String) params[5];

        Persistence subEvent = new SubEvent();
        subEvent.create(eventId, name, date, description, location, userId);
    }

    private void cascadeDelete(String id) throws IOException {
        // Deletar todas as sessões relacionadas ao SubEvento
        SessionController sessionController = new SessionController();
        sessionController.read();
        Iterator<Map.Entry<String, Persistence>> sessionIterator = sessionController.getHashMap().entrySet().iterator();
        while (sessionIterator.hasNext()) {
            Map.Entry<String, Persistence> entry = sessionIterator.next();
            if (entry.getValue().getData(EVENT_ID).equals(id)) {
                sessionIterator.remove();
            }
        }
        sessionController.getHashMap().values().forEach(session -> {
            try {
                session.delete(sessionController.getHashMap());
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        });

        // Deletar todos os participantes relacionados às sessões do evento
        AttendeeController attendeeController = new AttendeeController();
        attendeeController.read();
        Iterator<Map.Entry<String, Persistence>> attendeeIterator = attendeeController.getHashMap().entrySet().iterator();
        while (attendeeIterator.hasNext()) {
            Map.Entry<String, Persistence> entry = attendeeIterator.next();
            String sessionId = entry.getValue().getData("sessionId");
            if (sessionController.getHashMap().containsKey(sessionId)) {
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

        // Deletar todos os artigos relacionados ao SubEvento
        String subEventName = subEventHashMap.get(id).getData("name");
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
    }


    @Override
    public void delete(Object... params) throws IOException {
        String ownerId = "";
        cascadeDelete((String) params[0]);
        for (Map.Entry<String, Persistence> entry : subEventHashMap.entrySet()) {
            Persistence persistence = entry.getValue();
            if (persistence.getData("id").equals(params[0])){
                ownerId = persistence.getData(OWNER_ID);
            }
        }

        if ((params[1]).equals(ownerId)) {
            Iterator<Map.Entry<String, Persistence>> iterator = subEventHashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Persistence> entry = iterator.next();
                Persistence subEventindice = entry.getValue();
                if (subEventindice.getData("id").equals(params[0])) {
                    iterator.remove();
                }
            }
            Persistence subEventPersistence = new SubEvent();
            subEventPersistence.delete(subEventHashMap);
        } else {
            LOGGER.warning("Você não pode deletar esse SubEvento");
        }
    }

    @Override
    public List<String> list(Object... params) throws IOException {
        this.read();
        List<String> userEvents = new ArrayList<>();

        try {
            for (Map.Entry<String, Persistence> entry : subEventHashMap.entrySet()) {
                Persistence persistence = entry.getValue();
                if (persistence.getData(OWNER_ID).equals(params[0])) {
                    userEvents.add(persistence.getData("name"));
                }
            }
            if (userEvents.isEmpty()) {
                LOGGER.warning("Seu usuário atual é organizador de nenhum subevento");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userEvents;
    }

    @Override
    public void update(Object... params) throws IOException {
        if (!isValidParamsLength(params)) {
            LOGGER.warning("Só pode ter 6 parametros");
            return;
        }

        String oldName = (String) params[0];
        String newName = (String) params[1];
        String newDate = (String) params[2];
        String newDescription = (String) params[3];
        String newLocation = (String) params[4];
        String userId = (String) params[5];

        String id = getOwnerSubEventId(oldName, userId);
        if (id == null) {
            LOGGER.warning("Você não pode alterar este SubEvento");
            return;
        }

        if (isNameInUseOrEmpty(newName)) {
            LOGGER.warning("Nome em uso ou vazio");
            return;
        }

        updateSubEvent(id, newName, newDate, newDescription, newLocation);
    }

    private boolean isValidParamsLength(Object... params) {
        return params.length == 6;
    }

    private String getOwnerSubEventId(String oldName, String userId) {
        for (Map.Entry<String, Persistence> entry : subEventHashMap.entrySet()) {
            Persistence persistence = entry.getValue();
            String name = persistence.getData("name");
            String ownerId = persistence.getData(OWNER_ID);

            if (name != null && name.equals(oldName) && ownerId != null && ownerId.equals(userId)) {
                LOGGER.info("Owner found for the sub-event.");
                return persistence.getData("id");
            }
        }
        return null;
    }

    private boolean isNameInUseOrEmpty(String newName) {
        for (Map.Entry<String, Persistence> entry : subEventHashMap.entrySet()) {
            Persistence subEvent = entry.getValue();
            String name = subEvent.getData("name");
            if (name.isEmpty() || name.equals(newName)) {
                return true;
            }
        }
        return newName.isEmpty();
    }

    private void updateSubEvent(String id, String newName, String newDate, String newDescription, String newLocation) throws IOException {
        Persistence newSubEvent = subEventHashMap.get(id);
        if (newSubEvent != null) {
            newSubEvent.setData("name", newName);
            newSubEvent.setData("date", newDate);
            newSubEvent.setData(DESCRIPTION, newDescription);
            newSubEvent.setData(LOCATION, newLocation); // Using the constant LOCATION
            subEventHashMap.put(id, newSubEvent);

            Persistence subEventPersistence = new SubEvent();
            subEventPersistence.update(subEventHashMap);
        } else {
            LOGGER.warning("SubEvento não encontrado");
        }
    }


    @Override
    public void read() throws IOException {
        Persistence subEventPersistence = new SubEvent();
        this.subEventHashMap = subEventPersistence.read();
    }

    @Override
    public boolean loginValidate(String email, String cpf) {
        //Método não implementado
        return false;
    }

    private String getFatherEventId(String searchId) throws IOException {
        EventController ec = new EventController();
        String fatherId = "";
        Map<String, Persistence> list = ec.getHashMap();
        boolean found = false;
        for (Map.Entry<String, Persistence> entry : list.entrySet()) {
            Persistence listindice = entry.getValue();
            if (listindice.getData("name").equals(searchId)) {
                fatherId = listindice.getData("id");
                found = true;
                break;
            }
        }
        if (!found){
            LOGGER.warning("Evento pai não encontrado\n");

        }
        return fatherId;
    }
}