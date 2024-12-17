package br.upe.persistence.repository;

import br.upe.persistence.Event;
import br.upe.persistence.Session;
import br.upe.persistence.SubEvent;
import br.upe.persistence.User;
import br.upe.persistence.builder.SessionBuilder;
import br.upe.utils.CustomRuntimeException;
import br.upe.utils.JPAUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.io.IOException;
import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class SessionRepository implements Persistence {
    private static final Logger LOGGER = Logger.getLogger(SessionRepository.class.getName());
    private static SessionRepository instance;
    private static final String SESSION_NOT_FOUND = "Sessão não encontrada com o ID fornecido.";
    public SessionRepository() {
        // Construtor vazio porque esta classe não requer inicialização específica.
    }

    public static SessionRepository getInstance() {
        if (instance == null) {
            synchronized (SessionRepository.class) {
                instance = new SessionRepository();
            }
        }
        return instance;
    }

    public List<Session> getAllSessions() {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        TypedQuery<Session> query = entityManager.createQuery("SELECT s FROM Session s", Session.class);
        return query.getResultList();
    }

    @Override
    public Object[] create(Object... params) {
        if (params.length != 9) {
            LOGGER.warning("Devem ser fornecidos 9 parâmetros.");
            return new Object[]{false, null};
        }
        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        UUID eventId = null;
        UUID subEventId = null;
        Event event = null;
        SubEvent subevent = null;
        if (params[0] != null) {
            eventId = (UUID) params[0];
            event = entityManager.find(Event.class, eventId);
        } else if (params[8] != null) {
            subEventId = (UUID) params[8];
            subevent = entityManager.find(SubEvent.class, subEventId);
        }
        String name = (String) params[1];
        Date date = (Date) params[2];
        String description = (String) params[3];
        String location = (String) params[4];
        Time startTime = convertTime((String) params[5]);
        Time endTime = convertTime((String) params[6]);
        UUID ownerId = UUID.fromString((String) params[7]);


        User owner = entityManager.find(User.class, ownerId);
        boolean isCreated = false;

        if (owner == null) {
            LOGGER.warning("Usuário inválido.");
            return new Object[]{false, null};
        }

        Session session = SessionBuilder.builder()
                .withName(name)
                .withDate(date)
                .withDescription(description)
                .withLocation(location)
                .withStartTime(startTime)
                .withEndTime(endTime)
                .withSubEvent(subevent)
                .withEvent(event)
                .withOwner(owner)
                .build();

        List<Session> userSessions = owner.getSessions();
        userSessions.add(session);
        owner.setSessions(userSessions);

        if(event != null) {
            List<Session> eventSessions = event.getSessions();
            eventSessions.add(session);
            event.setSessions(eventSessions);
        }

        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(session);
            transaction.commit();
            isCreated = true;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.severe("Erro ao commitar a transação: " + e.getMessage());
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
        return new Object[]{isCreated, session.getId()};
    }

    public Time convertTime(String timeString) {
        Time time = null;
        try {

            // Verificar e corrigir o formato se necessário
            if (timeString.length() == 5) {  // Caso tenha só HH:mm (sem segundos)
                timeString += ":00";  // Adiciona os segundos
            }


            time = Time.valueOf(timeString);
            // Agora você pode usar startTime e endTime como objetos Time
        } catch (IllegalArgumentException e) {
            LOGGER.info("Erro ao converter a hora: " + e.getMessage());
            throw new CustomRuntimeException("Algo deu errado", e);
        }
        return time;
    }

    @Override
    public boolean update(Object... params) throws IOException {
        if (params.length != 7) {
            LOGGER.warning("Devem ser fornecidos 7 parâmetros.");
            return false;
        }

        UUID id = (UUID) params[0];
        String newName = (String) params[1];
        Date newDate = (Date) params[2];
        String newDescription = (String) params[3];
        String newLocation = (String) params[4];
        Time newStartTime = convertTime((String) params[5]);
        Time newEndTime = convertTime((String) params[6]);
        boolean isUpdated = false;
        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            Session session = entityManager.find(Session.class, id);

            if (session != null) {
                session.setName(newName);
                session.setDate(newDate);
                session.setDescription(newDescription);
                session.setLocation(newLocation);
                session.setStartTime(newStartTime);
                session.setEndTime(newEndTime);
                entityManager.merge(session);
                transaction.commit();
                LOGGER.info("Sessão atualizada com sucesso.");
                isUpdated = true;
            } else {
                LOGGER.warning(SESSION_NOT_FOUND);
            }
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.severe("Erro ao atualizar sessão: " + e.getMessage());
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
        return isUpdated;
    }

    @Override
    public boolean delete(Object... params) throws IOException {
        if (params.length != 2) {
            LOGGER.warning("Devem ser fornecidos 2 parâmetros.");
            return false;
        }
        UUID id = (UUID) params[0];
        UUID ownerId = (UUID) params[1];
        boolean isDeleted = false;

        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            Session session = entityManager.find(Session.class, id);
            if (session == null) {
                LOGGER.warning("Sessão não encontrada com o ID fornecido.");
                return false;
            }

            User owner = entityManager.find(User.class, ownerId);
            if (owner == null) {
                LOGGER.warning("Criador inválido.");
                return false;
            }

            List<Session> userSessions = owner.getSessions();
            userSessions.remove(session);
            owner.setSessions(userSessions);

            Event event = session.getEventId();
            if (event != null) {
                List<Session> eventSessions = event.getSessions();
                eventSessions.remove(session);
                event.setSessions(eventSessions);
            }

            entityManager.remove(session);
            transaction.commit();
            LOGGER.info("Sessão deletada com sucesso.");
            isDeleted = true;

        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.severe("Erro ao deletar sessão: " + e.getMessage());
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }

        return isDeleted;
    }



    @Override
    public Object getData(UUID id, String dataToGet) {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        Session session = entityManager.find(Session.class, id);
        if (session == null) {
            return null;
        }
        return switch (dataToGet) {
            case "id" -> session.getId();
            case "name" -> session.getName();
            case "date" -> session.getDate();
            case "description" -> session.getDescription();
            case "location" -> session.getLocation();
            case "startTime" -> session.getStartTime();
            case "endTime" -> session.getEndTime();
            case "eventId" -> session.getEventId();
            case "ownerId" -> session.getOwnerId().getId();
            case "subEvent_id" -> session.getSubEventId();
            default -> null;
        };
    }

    @Override
    public void setData(UUID sessionId, String dataToSet, Object data) {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Session session = entityManager.find(Session.class, sessionId);
            if (session != null) {
                switch (dataToSet) {
                    case "id" -> session.setId((UUID) data);
                    case "name" -> session.setName((String) data);
                    case "date" -> session.setDate((Date) data);
                    case "description" -> session.setDescription((String) data);
                    case "location" -> session.setLocation((String) data);
                    case "startTime" -> session.setStartTime((Time) data);
                    case "endTime" -> session.setEndTime((Time) data);
                    default -> throw new IllegalArgumentException("Campo inválido: " + dataToSet);
                }
                entityManager.merge(session);
                transaction.commit();
            } else {
                throw new IllegalArgumentException(SESSION_NOT_FOUND);
            }
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    @Override
    public Object getData(String dataToGet) {
        return null;
    }

    public UUID getSessionIdByNameAndUser(String sessionName, UUID userId) {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        try {
            // JPQL para buscar o ID da sessão com base no nome e no usuário
            String jpql = "SELECT s.id FROM Session s WHERE s.name = :sessionName AND s.ownerId.id = :userId";
            return entityManager.createQuery(jpql, UUID.class)
                    .setParameter("sessionName", sessionName)
                    .setParameter("userId", userId)
                    .getSingleResult();
        } catch (NoResultException e) {
            LOGGER.warning("Nenhuma sessão encontrada com o nome: " + sessionName + " e usuário com ID: " + userId);
            return null;
        } catch (Exception e) {
            LOGGER.severe("Erro ao buscar ID da sessão: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Object[] isExist(Object... params) {
        if (params.length != 2) {
            LOGGER.warning("Devem ser fornecidos 1 parâmetro.");
            return new Object[]{false, null};
        }

        String name = (String) params[0];
        UUID userId = UUID.fromString((String) params[1]);
        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        try {
            TypedQuery<Session> query = entityManager.createQuery("SELECT s FROM Session s WHERE s.name = :name AND s.ownerId.id = :userId", Session.class);
            query.setParameter("name", name);
            query.setParameter("userId", userId);
            Session session = query.getSingleResult();
            return new Object[]{true, session.getId()};
        } catch (NoResultException e) {
            LOGGER.warning("Nenhuma sessão encontrada com o nome: " + name + " e usuário com ID: " + userId);
        } catch (Exception e) {
            LOGGER.severe("Erro ao buscar sessão: " + e.getMessage());
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
        return new Object[]{false, null};
    }

}
