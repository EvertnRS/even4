package br.upe.controller;

import br.upe.persistence.Attendee;
import br.upe.persistence.Persistence;
import br.upe.persistence.Session;
import br.upe.persistence.User;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AttendeeController implements Controller {
    private HashMap<String, Persistence> attendeeHashMap;
    private Persistence attendeeLog;

    public AttendeeController() {
        this.read();
    }

    public HashMap<String, Persistence> getAttendeeHashMap() {
        return attendeeHashMap;
    }

    public void setAttendeeHashMap(HashMap<String, Persistence> attendeeHashMap) {
        this.attendeeHashMap = attendeeHashMap;
    }

    public Persistence getAttendeeLog() {
        return attendeeLog;
    }

    public void setAttendeeLog(Persistence attendeeLog) {
        this.attendeeLog = attendeeLog;
    }

    @Override
    public void create(Object... params) throws FileNotFoundException {
        if (params.length < 2) {
            System.out.println("Só pode ter 2 parametros");
        }

        String name = (String) params[0];
        String sessionId = (String) params[1];
        String userId = (String) params[2];
        Persistence attendeePersistence = new Attendee();

        try {

            if (!validateSessionId(sessionId)) {
                System.out.println("Id Incorreto ou Sessão não Existe");
                return;
            }

            for (Map.Entry<String, Persistence> entry : this.attendeeHashMap.entrySet()) {
                Persistence attendee = entry.getValue();
                if (attendee.getData("userId").equals(userId) && attendee.getData("sessionId").equals(sessionId)) {
                    throw new IOException();
                }
            }

            attendeePersistence.create(userId, name, sessionId);
            Persistence attendee = new Attendee();
            attendee.setData("name", name);
            attendee.setData("sessionId", sessionId);
            attendee.setData("userId", userId);
            this.setAttendeeLog(attendee);

        } catch (IOException exception) {
            System.out.println("Usuário já cadastrado");
        }
    }

    @Override
    public void update(Object... params) throws FileNotFoundException {
        if (params.length < 2) {
            System.out.println("Só pode ter 2 parametros");
            return;
        }
        this.read();
        Persistence attendeePersistence = new Attendee();
        String newName = (String) params[0];
        String sessionId = (String) params[1];

        if (!validateSessionId(sessionId)) {
            System.out.println("Id Incorreto ou Sessão não Existe");
            return;
        }

        boolean nameExists = false;
        for (Map.Entry<String, Persistence> entry : attendeeHashMap.entrySet()) {
            Persistence attendee = entry.getValue();
            String name = attendee.getData("name");
            if (name.isEmpty() || name.equals(newName)) {
                nameExists = true;
                break;
            }
        }

        if (nameExists || newName.isEmpty()) {
            System.out.println("Nome em uso ou vazio");
            return;
        }

        boolean found = false;
        for (Map.Entry<String, Persistence> entry : this.attendeeHashMap.entrySet()) {
            Persistence attendee = entry.getValue();
            if (attendee.getData("sessionId").equals(sessionId)) {
                attendee.setData("name", newName);
                this.attendeeHashMap.put(attendee.getData("id"), attendee);
                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println("Nenhum attendee encontrado para a sessão " + sessionId);
            return;
        }

        attendeePersistence.update(this.attendeeHashMap);
    }


    @Override
    public void read() {
        Persistence attendeePersistence = new Attendee();
        this.attendeeHashMap = attendeePersistence.read();
    }

    @Override
    public void delete(Object... params) {
        if (((String) params[1]).equals("id")) {
            Iterator<Map.Entry<String, Persistence>> iterator = attendeeHashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Persistence> entry = iterator.next();
                Persistence attendee = entry.getValue();

                if (attendee.getData("userId").equals((String) params[0]) && attendee.getData("sessionId").equals(params[2])) {

                    iterator.remove();
                }
            }
            Persistence attendeePersistence = new Attendee();
            attendeePersistence.delete(attendeeHashMap);
        }
    }


    private boolean validateSessionId (String sessionId) {
        SessionController sessionController = new SessionController();
        HashMap<String, Persistence> sessH = sessionController.getSessionHashMap();
        boolean hasSession = false;
        for (Map.Entry<String, Persistence> entry : sessH.entrySet()) {
            Persistence session = entry.getValue();
            if (session.getData("id").equals(sessionId)) {
                hasSession = true;
            }
        }
        return hasSession;
    }

    @Override
    public boolean list(String idowner) {
        this.read();
        boolean isnull = true;
        try {
            boolean found = false;
            for (Map.Entry<String, Persistence> entry : attendeeHashMap.entrySet()) {
                Persistence persistence = entry.getValue();
                if (persistence.getData("userId").equals(idowner)){
                    String[] results = getSessionById(persistence.getData("sessionId"));
                    System.out.println("Nome: " + results[0] + " - " + "Data: " + results[2] +  "\nDescrição: " + results[1] + " - " + "Local: " + results[3] + " - " + "Hora: " + results[4] + "\n");
                    found = true;
                    isnull = false;
                }
            }
            if (!found){
                System.out.println("Seu usuário atual não é inscrito em nenhum evento\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isnull;
    }

    private String[] getSessionById (String sessionId) {
        String name = "";
        String description = "";
        String date = "";
        String location = "";
        String startTime = "";
        SessionController sessionController = new SessionController();
        HashMap<String, Persistence> sessH = sessionController.getSessionHashMap();

        for (Map.Entry<String, Persistence> entry : sessH.entrySet()) {
            Persistence persistence = entry.getValue();
            if (persistence.getData("id").equals(sessionId)) {
                name = persistence.getData("name");
                description = persistence.getData("description");
                date = persistence.getData("date");
                location = persistence.getData("location");
                startTime = persistence.getData("startTime");
                break;
            }
        }
        return new String[] {name, description, date, location, startTime};
    }


    @Override
    public void show(Object... params) {

    }

    @Override
    public boolean loginValidate(String email, String cpf) {
        return false;
    }

    @Override
    public String getData(String dataToGet) {
        String data = "";
        try {
            switch (dataToGet) {
                case "name" -> data = this.attendeeLog.getData("name");
                case "sessionId" -> data = this.attendeeLog.getData("sessionId");
                case "id" -> data = this.attendeeLog.getData("id");
                case "userId" -> data = this.attendeeLog.getData("userId");
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


}

