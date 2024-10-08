package br.upe.controller;

import br.upe.persistence.SubEvent;
import br.upe.persistence.Persistence;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

public class SubEventController implements Controller {
    private static final Logger LOGGER = Logger.getLogger(SubEventController.class.getName());
    private Map<String, Persistence> subEventHashMap;
    private Persistence subEventLog;

    public SubEventController() {
        this.read();
    }

    public Map<String, Persistence> getSubEventHashMap() {
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
                case "description" -> data = this.subEventLog.getData("description");
                case "date" -> data = String.valueOf(this.subEventLog.getData("date"));
                case "location" -> data = this.subEventLog.getData("location");
                case "eventId" -> data = this.subEventLog.getData("eventId");
                case "ownerId" -> data = this.subEventLog.getData("ownerId");
                default -> throw new IOException();
            }
        } catch (IOException e) {
            LOGGER.warning("Informação não existe ou é restrita");
        }
        return data;
    }

    @Override
    public void create(Object... params) throws FileNotFoundException {
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

        String eventOwnerId = getFatherOwnerId(eventId);

        if (!eventOwnerId.equals(userId)) {
            LOGGER.warning("Você não pode criar um subevento para um evento que você não possui.");
            return;
        }

        boolean nomeEmUso = false;
        for (Map.Entry<String, Persistence> entry : this.subEventHashMap.entrySet()) {
            Persistence subEvent = entry.getValue();
            if (subEvent.getData("name").equals(name)) {
                nomeEmUso = true;
                break;
            }
        }

        if (nomeEmUso || name.isEmpty()) {
            LOGGER.warning("Nome vazio ou em uso");
            return;
        }

        if (!validateEventDate(date, eventId)){
            return;
        }

        Persistence subEvent = new SubEvent();
        subEvent.create(eventId, name, date, description, location, userId);
    }


    @Override
    public void delete(Object... params) {
        String ownerId = "";
        for (Map.Entry<String, Persistence> entry : subEventHashMap.entrySet()) {
            Persistence persistence = entry.getValue();
            if (persistence.getData("id").equals(params[0])){
                ownerId = persistence.getData("ownerId");
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
            Persistence SubEventPersistence = new SubEvent();
            SubEventPersistence.delete(subEventHashMap);
        } else {
            LOGGER.warning("Você não pode deletar esse SubEvento");
        }
    }

    @Override
    public boolean list(String idowner) {
        this.read();
        boolean isnull = true;
        try {
            boolean found = false;
            for (Map.Entry<String, Persistence> entry : subEventHashMap.entrySet()) {
                Persistence persistence = entry.getValue();
                if (persistence.getData("ownerId").equals(idowner)){
                    LOGGER.warning(persistence.getData("name"));
                    found = true;
                    isnull = false;
                }
            }
            if (!found){
                LOGGER.warning("Seu usuário atual é organizador de nenhum SubEvento\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isnull;
    }

    @Override
    public void show(Object... params) {
        this.read();
        for (Map.Entry<String, Persistence> entry : subEventHashMap.entrySet()) {
            Persistence persistence = entry.getValue();
            if (!persistence.getData("ownerId").equals(params[0])){
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

        for (Map.Entry<String, Persistence> entry : subEventHashMap.entrySet()) {
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
            for (Map.Entry<String, Persistence> entry : subEventHashMap.entrySet()) {
                Persistence subEvent = entry.getValue();
                String name = subEvent.getData("name");
                if (name.isEmpty() || name.equals(newName)) {
                    nameExists = true;
                    break;
                }
            }

            if (nameExists || newName.isEmpty()) {
                LOGGER.warning("Nome em uso ou vazio");
                return;
            }

            if (id != null) {
                Persistence newSubEvent = subEventHashMap.get(id);
                if (newSubEvent != null) {
                    newSubEvent.setData("name", newName);
                    newSubEvent.setData("date", newDate);
                    newSubEvent.setData("description", newDescription);
                    newSubEvent.setData("location", newLocation);
                    subEventHashMap.put(id, newSubEvent);
                    Persistence subEventPersistence = new SubEvent();
                    subEventPersistence.update(subEventHashMap);
                } else {
                    LOGGER.warning("SubEvento não encontrado");
                }
            } else {
                LOGGER.warning("Você não pode alterar este SubEvento");
            }
        } else {
            LOGGER.warning("Você não pode alterar este SubEvento");
        }
    }

    @Override
    public void read() {
        Persistence subEventPersistence = new SubEvent();
        this.subEventHashMap = subEventPersistence.read();
    }

    @Override
    public boolean loginValidate(String email, String cpf) {
        //Método não implementado
        return false;
    }

    private String getFatherEventId(String searchId) {
        EventController ec = new EventController();
        String fatherId = "";
        Map<String, Persistence> list = ec.getEventHashMap();
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

    private String getFatherOwnerId(String eventId){
        EventController ec = new EventController();
        String fatherOwnerId = "";
        Map<String, Persistence> list = ec.getEventHashMap();
        for (Map.Entry<String, Persistence> entry : list.entrySet()) {
            Persistence listindice = entry.getValue();
            if (listindice.getData("id").equals(eventId)) {
                fatherOwnerId = listindice.getData("ownerId");
                break;
            }
        }
        return fatherOwnerId;
    }

    private boolean validateEventDate(String date, String searchId) {
        EventController ec = new EventController();
        Map<String, Persistence> list = ec.getEventHashMap();

        Persistence listIndice = list.get(searchId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate eventDate;
        eventDate = LocalDate.parse(listIndice.getData("date"), formatter);

        LocalDate inputDate;
        inputDate = LocalDate.parse(date, formatter);
        if (eventDate.isAfter(inputDate)) {
            LOGGER.warning("A data não pode ser anterior ao seu Evento Pai\n");
            return false;
        }

        return true;
    }
}