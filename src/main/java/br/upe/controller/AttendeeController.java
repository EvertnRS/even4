package br.upe.controller;

import br.upe.persistence.Attendee;
import br.upe.persistence.Model;
import br.upe.persistence.SubEvent;
import br.upe.persistence.repository.Persistence;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AttendeeController implements Controller {
    private static final String USER_ID = "userId";
    private static final String SESSION_ID = "sessionId";
    private static final Logger LOGGER = Logger.getLogger(AttendeeController.class.getName());
    private Map<UUID, Persistence> attendeeHashMap;
    private Persistence attendeeLog;

    public AttendeeController() throws IOException {
        this.read();
    }

    public Map<UUID, Persistence> getHashMap() {
        return attendeeHashMap;
    }

    @Override
    public List<Model> getAll() {
        return List.of();
    }

    public void setAttendeeHashMap(Map<UUID, Persistence> attendeeHashMap) {
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
            LOGGER.warning("Só pode ter 2 parametros");
        }

        String name = (String) params[0];
        String sessionId = (String) params[1];
        String userId = (String) params[2];
        Persistence attendeePersistence = new Attendee();

        try {

            if (!validateSessionId(sessionId)) {
                LOGGER.warning("Id Incorreto ou Sessão não Existe");
                return;
            }

            for (Map.Entry<UUID, Persistence> entry : this.attendeeHashMap.entrySet()) {
                Persistence attendee = entry.getValue();
                if (attendee.getData(USER_ID).equals(userId) && attendee.getData(SESSION_ID).equals(sessionId)) {
                    throw new IOException();
                }
            }

            attendeePersistence.create(userId, name, sessionId);
            Persistence attendee = new Attendee();
            attendee.setData("name", name);
            attendee.setData(SESSION_ID, sessionId);
            attendee.setData(USER_ID, userId);
            this.setAttendeeLog(attendee);

        } catch (IOException exception) {
            LOGGER.warning("Usuário já cadastrado");
        }
    }

    @Override
    public void update(Object... params) throws IOException {
        if (params.length < 2) {
            LOGGER.warning("Só pode ter 2 parametros");
            return;
        }
        this.read();
        Persistence attendeePersistence = new Attendee();
        String newName = (String) params[0];
        String sessionId = (String) params[1];

        if (!validateSessionId(sessionId)) {
            LOGGER.warning("Id Incorreto ou Sessão não Existe");
            return;
        }

        boolean nameExists = false;
        for (Map.Entry<UUID, Persistence> entry : attendeeHashMap.entrySet()) {
            Persistence attendee = entry.getValue();
            String name = (String) attendee.getData("name");
            if (name.isEmpty() || name.equals(newName)) {
                nameExists = true;
                break;
            }
        }

        if (nameExists || newName.isEmpty()) {
            LOGGER.warning("Nome em uso ou vazio");
            return;
        }

        boolean found = false;
        for (Map.Entry<UUID, Persistence> entry : this.attendeeHashMap.entrySet()) {
            Persistence attendee = entry.getValue();
            if (attendee.getData(SESSION_ID).equals(sessionId)) {
                attendee.setData("name", newName);
                UUID attendeeId = (UUID) attendee.getData("id");
                this.attendeeHashMap.put(attendeeId, attendee);
                found = true;
                break;
            }
        }

        if (!found && LOGGER.isLoggable(Level.WARNING)) {
            LOGGER.warning(String.format("Nenhum attendee encontrado para a sessão %s", sessionId));
        }

        attendeePersistence.update(this.attendeeHashMap);
    }


    @Override
    public void read() throws IOException {
        Persistence attendeePersistence = new Attendee();
        this.attendeeHashMap = attendeePersistence.read();
    }

    @Override
    public void delete(Object... params) throws IOException {
        if ((params[1]).equals("id")) {
            Iterator<Map.Entry<UUID, Persistence>> iterator = attendeeHashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<UUID, Persistence> entry = iterator.next();
                Persistence attendee = entry.getValue();

                if (attendee.getData(USER_ID).equals(params[0]) && attendee.getData(SESSION_ID).equals(params[2])) {

                    iterator.remove();
                }
            }
            Persistence attendeePersistence = new Attendee();
            attendeePersistence.delete(attendeeHashMap);
        }
    }


    private boolean validateSessionId (String sessionId) throws IOException {
        SessionController sessionController = new SessionController();
        Map<UUID, Persistence> sessH = sessionController.getHashMap();
        boolean hasSession = false;
        for (Map.Entry<UUID, Persistence> entry : sessH.entrySet()) {
            Persistence session = entry.getValue();
            if (session.getData("id").equals(sessionId)) {
                hasSession = true;
            }
        }
        return hasSession;
    }

    @Override
    public List<String> list(Object... params) throws IOException {
            this.read();
            List<String> userEvents = new ArrayList<>();

            try {
                for (Map.Entry<UUID, Persistence> entry : attendeeHashMap.entrySet()) {
                    Persistence persistence = entry.getValue();
                    if (persistence.getData(USER_ID).equals(params[0])) {
                        String EventName = (String) persistence.getData("name");
                        userEvents.add(EventName);
                    }
                }
                if (userEvents.isEmpty()) {
                    LOGGER.warning("Seu usuário atual não está participando de nenhum evento");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return userEvents;
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
                case "name" -> data = (String) this.attendeeLog.getData("name");
                case SESSION_ID -> data = (String) this.attendeeLog.getData(SESSION_ID);
                case "id" -> data = (String) this.attendeeLog.getData("id");
                case USER_ID -> data = (String) this.attendeeLog.getData(USER_ID);
                default -> throw new IOException();
            }
        } catch (IOException e) {
            LOGGER.warning("Informação não existe ou é restrita");
        }
        return data;
    }


}

