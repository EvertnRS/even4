package br.upe.controller;
import br.upe.persistence.Event;
import br.upe.persistence.Persistence;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class EventController implements Controller {
    private static final Logger LOGGER = Logger.getLogger(EventController.class.getName());

    private Map<String, Persistence> eventHashMap;
    private Persistence eventLog;


    public EventController() {
        this.read();
    }

    public Map<String, Persistence> getEventHashMap() {
        return eventHashMap;
    }


    public void setEventHashMap(Map<String, Persistence> eventHashMap) {
        this.eventHashMap = eventHashMap;
    }

    @Override
    public boolean list(String ownerId){
        this.read();
        boolean isnull = true;
        try {
            boolean found = false;
            for (Map.Entry<String, Persistence> entry : eventHashMap.entrySet()) {
                Persistence persistence = entry.getValue();
                if (persistence.getData("ownerId").equals(ownerId)){
                    LOGGER.warning(persistence.getData("name"));
                    found = true;
                    isnull = false;
                }
            }
            if (!found){
                LOGGER.warning("Seu usuário atual é organizador de nenhum evento");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isnull;
    }

    public List<String> list(String ownerId, String fx) {
        this.read();
        List<String> userEvents = new ArrayList<>();

        try {
            for (Map.Entry<String, Persistence> entry : eventHashMap.entrySet()) {
                Persistence persistence = entry.getValue();
                if (persistence.getData("ownerId").equals(ownerId)) {
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

    @Override
    public void show(Object... params) {
        this.setEventHashMap(eventHashMap);

        for (Map.Entry<String, Persistence> entry : eventHashMap.entrySet()) {
            Persistence persistence = entry.getValue();
            String ownerId = persistence.getData("ownerId");
            // Verifica se o evento não é de propriedade do usuário e se possui sessões
            if (!ownerId.equals(params[0])) {
                LOGGER.warning(persistence.getData("name") + " - " + persistence.getData("id"));
            }
        }
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

        boolean isOwner = false;
        String id = null;

        for (Map.Entry<String, Persistence> entry : eventHashMap.entrySet()) {
            Persistence persistence = entry.getValue();
            String name = persistence.getData("name");
            String ownerId = persistence.getData("ownerId");

            if (name != null && name.equals(oldName) && ownerId != null && ownerId.equals(userId)) {
                isOwner = true;
                id = persistence.getData("id");
                break;
            }
        }

        if (isOwner) {
            boolean nameExists = false;
            for (Map.Entry<String, Persistence> entry : eventHashMap.entrySet()) {
                Persistence event = entry.getValue();
                String name = event.getData("name");
                if (name.isEmpty() || name.equals(newName)) {
                    nameExists = true;
                    break;
                }
            }

            if (nameExists) {
                LOGGER.warning("Nome em uso ou vazio");
                return;
            }

            if (id != null) {
                Persistence newEvent = eventHashMap.get(id);
                if (newEvent != null) {
                    newEvent.setData("name", newName);
                    newEvent.setData("date", newDate);
                    newEvent.setData("description", newDescription);
                    newEvent.setData("location", newLocation);
                    eventHashMap.put(id, newEvent);
                    Persistence eventPersistence = new Event();
                    eventPersistence.update(eventHashMap);
                } else {
                    LOGGER.warning("Evento não encontrado");
                }
            } else {
                LOGGER.warning("Você não pode alterar este Evento");
            }
        } else {
            LOGGER.warning("Você não pode alterar este Evento");
        }
    }


    @Override
    public void read() {
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
                case "description" -> data = this.eventLog.getData("description");
                case "date" -> data = String.valueOf(this.eventLog.getData("date"));
                case "location" -> data = this.eventLog.getData("location");
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

    @Override
    public void delete(Object... params) {
        String ownerId = "";
        for (Map.Entry<String, Persistence> entry : eventHashMap.entrySet()) {
            Persistence persistence = entry.getValue();
            if (persistence.getData("name").equals(params[0])){
                ownerId = persistence.getData("ownerId");
            }
        }

        if ((params[1]).equals("name") && (params[2]).equals(ownerId)) {
            Iterator<Map.Entry<String, Persistence>> iterator = eventHashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Persistence> entry = iterator.next();
                Persistence eventindice = entry.getValue();
                if (eventindice.getData("name").equals(params[0])) {
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
