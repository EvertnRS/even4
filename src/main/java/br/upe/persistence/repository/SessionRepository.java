package br.upe.persistence.repository;

import br.upe.persistence.*;
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
import java.util.Set;
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
        UUID eventId = (UUID) params[0];
        UUID subEventId = (UUID) params[8];

        Event event = eventId != null ? entityManager.find(Event.class, eventId) : null;
        SubEvent subEvent = subEventId != null ? entityManager.find(SubEvent.class, subEventId) : null;

        String name = (String) params[1];
        Date date = (Date) params[2];
        String description = (String) params[3];
        String location = (String) params[4];
        Time startTime = convertTime((String) params[5]);
        Time endTime = convertTime((String) params[6]);
        UUID ownerId = UUID.fromString((String) params[7]);

        User owner = entityManager.find(User.class, ownerId);
        if (owner == null) {
            LOGGER.warning("Usuário inválido.");
            return new Object[]{false, null};
        }

        // Criar sessão
        Session session = new Session();
        session.setName(name);
        session.setDate(date);
        session.setDescription(description);
        session.setLocation(location);
        session.setStartTime(startTime);
        session.setEndTime(endTime);
        session.setOwnerId(owner);
        session.setEventId(event);
        session.setSubEventId(subEvent);

        // Associar ao evento e ao usuário
        if (event != null) {
            event.getSessions().add(session);
        }

        if (subEvent != null) {
            subEvent.getSessions().add(session);
        }
        owner.getSessions().add(session);

        EntityTransaction transaction = entityManager.getTransaction();
        boolean isCreated = false;

        try {
            transaction.begin();
            entityManager.persist(session);
            if (event != null) {
                entityManager.merge(event);
            }
            if (subEvent != null) {
                entityManager.merge(subEvent);
            }
            entityManager.merge(owner);
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
            throw new CustomRuntimeException(String.format("Erro ao converter a hora: %s. Verifique o formato da entrada.", e.getMessage()), e
            );
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

        UUID sessionId = (UUID) params[0];
        UUID ownerId = (UUID) params[1];
        boolean isDeleted = false;

        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            // Buscar a sessão e o proprietário
            Session session = entityManager.find(Session.class, sessionId);
            if (session == null) {
                LOGGER.warning("Sessão não encontrada.");
                return false;
            }

            User owner = entityManager.find(User.class, ownerId);
            if (owner == null || !session.getOwnerId().getId().equals(ownerId)) {
                LOGGER.warning("Usuário não é o criador da sessão ou inválido.");
                return false;
            }

            // Remover a relação entre session e attendees
            Set<Attendee> attendees = session.getAttendees();
            for (Attendee attendee : attendees) {
                attendee.getSessions().remove(session);
            }
            session.getAttendees().clear();

            // Remover a sessão do evento
            Event event = session.getEventId();
            SubEvent subEvent = session.getSubEventId();
            if (event != null) {
                event.getSessions().remove(session);
            }
            if (subEvent != null) {
                subEvent.getSessions().remove(session);
            }

            // Remover a sessão do criador
            owner.getSessions().remove(session);

            // Remover a sessão
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

    public String[] verifyByEventName(String name){
        String[] type = new String[2]; // Define um array com dois elementos
        EntityManager entityManager = null;

        try {
            // Obtém o EntityManager a partir do JPAUtils
            entityManager = JPAUtils.getEntityManagerFactory();

            // Cria uma consulta para buscar o ID pelo nome
            TypedQuery<UUID> query = entityManager.createQuery(
                    "SELECT e.id FROM Event e WHERE e.name = :name", UUID.class);
            query.setParameter("name", name);

            // Atribui os valores ao array
            type[0] = query.getSingleResult().toString(); // ID
            type[1] = "evento"; // Tipo

            return type; // Retorna o array preenchido
        } catch (NoResultException e) {
            // Obtém o EntityManager a partir do JPAUtils
            entityManager = JPAUtils.getEntityManagerFactory();

            // Cria uma consulta para buscar o ID pelo nome
            TypedQuery<UUID> query = entityManager.createQuery(
                    "SELECT e.id FROM SubEvent e WHERE e.name = :name", UUID.class);
            query.setParameter("name", name);

            // Atribui os valores ao array
            type[0] = query.getSingleResult().toString(); // ID
            type[1] = "subEvento"; // Tipo

            return type;
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public String[] verifyBySessionName(String sessionName){
        String[] type = new String[3];
        EntityManager entityManager = null;

        try {
            if (sessionName == null || sessionName.trim().isEmpty()) {
                throw new IllegalArgumentException("O nome da sessão não pode ser nulo ou vazio");
            }

            entityManager = JPAUtils.getEntityManagerFactory();

            // Consulta para buscar a sessão com nome específico
            TypedQuery<Session> sessionQuery = entityManager.createQuery(
                    "SELECT s FROM Session s WHERE LOWER(TRIM(s.name)) = LOWER(TRIM(:name))",
                    Session.class
            );
            sessionQuery.setParameter("name", sessionName.trim());
            LOGGER.info("Session name: {}");

            List<Session> sessionResults = sessionQuery.getResultList();
            if (sessionResults.isEmpty()) {
                throw new IllegalArgumentException("Nenhum dado encontrado para o nome da sessão: " + sessionName);
            }

            // Exibe a sessão encontrada
            Session session = sessionResults.get(0);

            // Preencha os dados de retorno
            if (session.getSubEventId() != null && session.getSubEventId().getId() != null) {
                type[0] = session.getSubEventId().getId().toString();
                type[1] = "subEvento"; // Ou faça a consulta para obter o nome do subevento
                type[2] = session.getId().toString();
            } else if (session.getEventId() != null && session.getEventId().getId() != null) {
                type[0] = session.getEventId().getId().toString();
                type[1] = "evento"; // Ou faça a consulta para obter o nome do evento
                type[2] = session.getId().toString();
            }

        } catch (NoResultException e) {
            throw new IllegalArgumentException("Nenhum dado encontrado para o nome da sessão: " + sessionName, e);
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
        return type;
    }
}
