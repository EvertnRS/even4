package br.upe.controller;

import br.upe.persistence.Session;
import br.upe.persistence.repository.Persistence;
import br.upe.persistence.repository.SessionRepository;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class SessionController implements Controller {

    private static final String DESCRIPTION = "description";
    private static final String NAME = "name";
    private static final String ID = "id";
    private static final String OWNER_ID = "ownerId";
    private static final String EVENT_ID = "eventId";
    private static final String LOCATION = "location";
    private static final String STARTTIME = "startTime";
    private static final String ENDTIME = "endTime";
    private static final Logger LOGGER = Logger.getLogger(SessionController.class.getName());
    private Persistence sessionLog;

    @Override
    public <T> List<T> getEventArticles(UUID eventId) {
        return List.of();
    }

    @Override
    public <T> List<T> getAll() {
        SessionRepository sessionRepository = SessionRepository.getInstance();
        return (List<T>) sessionRepository.getAllSessions();
    }

    @Override
    public <T> List<T> list(Object... params) throws IOException {
        UUID userId = UUID.fromString((String) params[0]);
        SessionRepository sessionRepository = SessionRepository.getInstance();
        List<Session> allSessions = sessionRepository.getAllSessions();
        List<Session> userSessions = new ArrayList<>();

        for (Session session : allSessions) {
            if (session.getOwnerId().getId().equals(userId)) {
                userSessions.add(session);
            }
        }

        if (userSessions.isEmpty()) {
            LOGGER.warning("Seu usuário atual é organizador de nenhuma sessão");
        }

        return (List<T>) userSessions;
    }

    public Object[] create(Object... params) throws IOException {
        if (params.length != 8) {
            LOGGER.warning("Número incorreto de parâmetros. Esperado: 8");
            return new Object[]{false, null};
        }


        String name = (String) params[0];
        Date date = (Date) params[1];
        String description = (String) params[2];
        String location = (String) params[3];
        String startTime = (String) params[4];
        String endTime = (String) params[5];
        String userId = (String) params[6];
        String[] type = (String[]) params[7];
        UUID eventId = null;
        UUID subEventId = null;
        if (type[1].equals("evento")) {
            eventId = UUID.fromString(type[0]);
        } else if (type[1].equals("subEvento")) {
            subEventId = UUID.fromString(type[0]);

        }

        Persistence session = new SessionRepository();
        Object[] results = session.create(eventId, name, date, description, location, startTime, endTime, userId, subEventId);

        this.sessionLog = session;
        return results;
    }


    @Override
    public boolean delete(Object... params) throws IOException {
        SessionRepository sessionRepository = SessionRepository.getInstance();

        UUID id = (UUID) params[0];
        UUID ownerId = UUID.fromString((String) params[1]);

        return sessionRepository.delete(id, ownerId);
    }

    @Override
    public boolean update(Object... params) throws IOException {
        if (params.length != 8) {
            LOGGER.warning("Só pode ter 8 parâmetros");
            return false;
        }

        // Recuperando os parâmetros
        String oldName = (String) params[0];
        String newName = (String) params[1];
        String newDateString = (String) params[2]; // Recebendo como String
        String newDescription = (String) params[3];
        String newLocation = (String) params[4];
        UUID userId = UUID.fromString((String) params[5]);
        String newStartTime = (String) params[6];
        String newEndTime = (String) params[7];

        // Convertendo a String para Date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Formato esperado da data
        Date newDate = null;
        boolean isUpdated = false;
        try {
            newDate = dateFormat.parse(newDateString);
        } catch (ParseException e) {
            LOGGER.warning("Data inválida: " + newDateString);
            return false;
        }


        // Validações
        if (newName == null || newName.trim().isEmpty()) {
            LOGGER.warning("Nome não pode ser vazio.");
            return false;
        }

        // Obtenção das sessões do repositório
        Persistence sessionPersistence = new SessionRepository(); // Obtenção do repositório de sessões
        List<Session> sessions = ((SessionRepository) sessionPersistence).getAllSessions(); // Obtendo todas as sessões

        boolean nameExists = false;
        for (Session session : sessions) {
            // Verificando se o nome já está sendo utilizado por outra sessão
            if (session.getName().equals(newName) && !session.getName().equals(oldName)) {
                nameExists = true;
                break;
            }
        }

        if (nameExists) {
            LOGGER.warning("Nome em uso.");
            return false;
        }
        SessionRepository session = new SessionRepository();
        UUID sessionId = session.getSessionIdByNameAndUser(oldName, userId);
        if (sessionId == null) {
            LOGGER.warning("Sessão não encontrada para o nome: " + newName + " e usuário com ID: " + userId);
            return false;
        }

        isUpdated = session.update(sessionId, newName, newDate, newDescription, newLocation, newStartTime, newEndTime);
        this.sessionLog = session;
        return isUpdated;
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
                case ID -> data = (String) this.sessionLog.getData(ID);
                case NAME -> data = (String) this.sessionLog.getData(NAME);
                case DESCRIPTION -> data = (String) this.sessionLog.getData(DESCRIPTION);
                case "date" -> data = String.valueOf(this.sessionLog.getData("date"));
                case LOCATION -> data = (String) this.sessionLog.getData(LOCATION);
                case EVENT_ID -> data = (String) this.sessionLog.getData(EVENT_ID);
                case OWNER_ID -> data = (String) this.sessionLog.getData(OWNER_ID);
                case STARTTIME -> data = (String) this.sessionLog.getData(STARTTIME);
                case ENDTIME -> data = (String) this.sessionLog.getData(ENDTIME);
                default -> throw new IOException();
            }
        } catch (IOException e) {
            LOGGER.warning("Informação não existe ou é restrita");
        }
        return data;
    }


    public String setData(String dataToGet) {
        String data = "";
        if (this.sessionLog == null) {
            LOGGER.warning("Sessão não inicializada.");
            return "";
        }

        try {
            switch (dataToGet) {
                case ID -> data = (String) this.sessionLog.getData(ID);
                case NAME -> data = (String) this.sessionLog.getData(NAME);
                case DESCRIPTION -> data = (String) this.sessionLog.getData(DESCRIPTION);
                case "date" -> data = String.valueOf(this.sessionLog.getData("date"));
                case LOCATION -> data = (String) this.sessionLog.getData(LOCATION);
                case EVENT_ID -> data = (String) this.sessionLog.getData(EVENT_ID);
                case OWNER_ID -> data = (String) this.sessionLog.getData(OWNER_ID);
                case STARTTIME -> data = (String) this.sessionLog.getData(STARTTIME);
                case ENDTIME -> data = (String) this.sessionLog.getData(ENDTIME);
                default -> throw new IOException();
            }
        } catch (IOException e) {
            LOGGER.warning("Informação não existe ou é restrita");
        }
        return data;
    }

    @Override
    public Object[] isExist(Object... params) throws IOException {
        if (params.length != 2) {
            LOGGER.warning("Só pode ter 2 parâmetros");
            return new Object[]{false, null};
        }

        String name = (String) params[0];
        String ownerId = (String) params[1];
        SessionRepository sessionRepository = SessionRepository.getInstance();
        return sessionRepository.isExist(name, ownerId);
    }

}
