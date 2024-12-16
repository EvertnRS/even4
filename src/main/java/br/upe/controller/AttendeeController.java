package br.upe.controller;

import br.upe.persistence.*;
import br.upe.persistence.repository.AttendeeRepository;
import br.upe.persistence.repository.EventRepository;
import br.upe.persistence.repository.Persistence;
import br.upe.utils.JPAUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

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
    public <T> List <T> getAll() {
        AttendeeRepository attendeeRepository = AttendeeRepository.getInstance();
        return (List<T>) attendeeRepository.getAllAttendees();
    }

    @Override
    public <T> List<T> getEventArticles(UUID eventId) {
        return List.of();
    }

    @Override
    public <T> List<T> list(Object... params) throws IOException {
        UUID userId = UUID.fromString((String) params[0]);
        AttendeeRepository eventRepository = AttendeeRepository.getInstance();
        List<Attendee> allAttendees = eventRepository.getAllAttendees();
        List<Attendee> userAttendees = new ArrayList<>();

        for (Attendee attendee : allAttendees) {
            if (attendee.getUserId().getId().equals(userId)) {
                userAttendees.add(attendee);
            }
        }

        if (userAttendees.isEmpty()) {
            LOGGER.warning("Seu usuário não participa de nenhum evento");
        }

        return (List<T>) userAttendees;
    }

    @Override
    public Object[] create(Object... params) throws IOException {
        if (params.length < 2) {
            LOGGER.warning("Só pode ter 2 parametros");
        }

        String sessionId = getSessionId((String) params[0]);
        String userId = params[1].toString();

        Persistence attendee = new AttendeeRepository();
        return attendee.create(userId, sessionId);
    }

    @Override
    public boolean update(Object... params) throws IOException {
        return false;
    }

    @Override
    public void read() throws IOException {
        //
    }

    @Override
    public boolean delete(Object... params) throws IOException {
        if (params.length != 2) {
            LOGGER.warning("Só pode ter 2 parametro");
            return false;
        }

        AttendeeRepository attendeeRepository = AttendeeRepository.getInstance();

        UUID id = (UUID) params[0];
        UUID userId = UUID.fromString((String) params[1]);

        return attendeeRepository.delete(id, userId);
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

    private String getSessionId(String searchName) {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        String fatherId = null;
        try {
            TypedQuery<Session> query = entityManager.createQuery("SELECT e FROM Session e WHERE e.name = :name", Session.class);
            query.setParameter("name", searchName);
            Session session = query.getSingleResult();
            fatherId = session.getId().toString();
        } catch (NoResultException e) {
            LOGGER.warning("Sessão não encontrada\n");
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
        return fatherId;
    }

    @Override
    public Object[] isExist(Object... params) throws IOException {
        if (params.length != 1) {
            LOGGER.warning("Só pode ter 1 parametro");
            return new Object[]{false, null};
        }

        String userId = (String) params[0];
        AttendeeRepository attendeeRepository = AttendeeRepository.getInstance();
        return attendeeRepository.isExist(userId);
    }

}

