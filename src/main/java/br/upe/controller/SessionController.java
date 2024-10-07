package br.upe.controller;

import br.upe.persistence.Event;
import br.upe.persistence.Session;
import br.upe.persistence.Persistence;
import br.upe.persistence.SubEvent;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SessionController implements Controller {
    private HashMap<String, Persistence> sessionHashMap;
    private Persistence sessionLog;

    public SessionController() {
        this.read();
    }

    public HashMap<String, Persistence> getSessionHashMap() {
        return sessionHashMap;
    }

    public void setSessionHashMap(HashMap<String, Persistence> sessionHashMap) {
        this.sessionHashMap = sessionHashMap;
    }

    @Override
    public String getData(String dataToGet) {
        String data = "";
        if (this.sessionLog == null) {
            System.out.println("Sessão não inicializada.");
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
            System.out.println("Informação não existe ou é restrita");
        }
        return data;
    }

    @Override
    public void SubmitArticleController(String string) {

    }

    @Override
    public void create(Object... params) {
        if (params.length != 9) {
            System.out.println("Número incorreto de parâmetros. Esperado: 9");
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
        HashMap<String, Persistence> eventH;

        if (params[8].equals("Event")){
            EventController eventController = new EventController();
            eventH = eventController.getEventHashMap();
        } else{
            SubEventController subEventController = new SubEventController();
            eventH = subEventController.getEventHashMap();
        }

        if (!eventOwnerId.equals(userId)) {
            System.out.println("Você não pode criar uma sessão para um evento que você não possui.");
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
            System.out.println("Nome vazio ou em uso");
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
            if (persistence.getData("name").equals((String) params[0])) {
                ownerId = persistence.getData("ownerId");
            }
        }

        if (((String) params[1]).equals("name") && ((String) params[2]).equals(ownerId)) {
            Iterator<Map.Entry<String, Persistence>> iterator = sessionHashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Persistence> entry = iterator.next();
                Persistence sessionIndice = entry.getValue();
                if (sessionIndice.getData("name").equals((String) params[0])) {
                    iterator.remove();
                }
            }
            Persistence sessionPersistence = new Session();
            sessionPersistence.delete(sessionHashMap);
        } else {
            System.out.println("Você não pode deletar essa Sessão");
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
                    System.out.println(persistence.getData("name"));
                    found = true;
                    isnull = false;
                }
            }
            if (!found) {
                System.out.println("Seu usuário atual não é organizador de nenhuma Sessão\n");
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
                    System.out.println("Nome: " + persistence.getData("name") + " - " + "Id: " + persistence.getData("id") +  "\nEvento Pai: " + eventName + " - " + "Data: " + persistence.getData("date") + " - " + "Hora: " + persistence.getData("startTime") + "\n");
                }
            }
        } else if (params[1].equals("sessionId")) {
            for (Map.Entry<String, Persistence> entry : sessionHashMap.entrySet()) {
                Persistence persistence = entry.getValue();
                if (persistence.getData("id").equals(params[0])){
                    String eventName = getEventName(persistence.getData("eventId"));
                    System.out.println("Nome: " + persistence.getData("name") + " - " + "Id: " + persistence.getData("id") +  "\nEvento Pai: " + eventName + " - " + "Data: " + persistence.getData("date") + " - " + "Hora: " + persistence.getData("startTime") + "\nDescrição: " + persistence.getData("description") + " - " + "Local: " + persistence.getData("location") + "\n");
                    break;
                }
            }
        }

    }

    private String getEventName(String id) {
        String name = "";
        EventController eventController = new EventController();
        HashMap<String, Persistence> evenH = eventController.getEventHashMap();
        boolean isEvent = false;
        for (Map.Entry<String, Persistence> entry : evenH.entrySet()) {
            Persistence persistence = entry.getValue();
            if (persistence.getData("id").equals(id)) {
                name = persistence.getData("name");
            }
        }

        if (!isEvent) {
            SubEventController subEventController = new SubEventController();
            HashMap<String, Persistence> subEvenH = subEventController.getEventHashMap();
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
            System.out.println("Só pode ter 6 parametros");
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

        for (Map.Entry<String, Persistence> entry : sessionHashMap.entrySet()) {
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
            for (Map.Entry<String, Persistence> entry : sessionHashMap.entrySet()) {
                Persistence Session = entry.getValue();
                String name = Session.getData("name");
                if (name.isEmpty() || name.equals(newName)) {
                    nameExists = true;
                    break;
                }
            }

            if (nameExists) {
                System.out.println("Nome em uso ou vazio");
                return;
            }

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
                    System.out.println("Sessão não encontrada");
                }
            } else {
                System.out.println("Você não pode alterar esta Sessão");
            }
        } else {
            System.out.println("Você não pode alterar esta Sessão");
        }
    }

    @Override
    public void read() {
        Persistence sessionPersistence = new Session();
        this.sessionHashMap = sessionPersistence.read();
    }

    @Override
    public boolean loginValidate(String email, String cpf) {
        return false;
    }

    private String getFatherEventId(String searchId, String type) {
        HashMap<String, Persistence> list;
        if (type.equals("Event")){
            EventController eventController = new EventController();
            list = eventController.getEventHashMap();
        } else{
            SubEventController subEventController = new SubEventController();
            list = subEventController.getEventHashMap();
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
            System.out.println("Evento pai não encontrado\n");
        }

        return fatherId;
    }

    private String getFatherOwnerId(String eventId, String type) {
        HashMap<String, Persistence> list;
        if (type.equals("Event")){
            EventController eventController = new EventController();
            list = eventController.getEventHashMap();
        } else{
            SubEventController subEventController = new SubEventController();
            list = subEventController.getEventHashMap();
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
