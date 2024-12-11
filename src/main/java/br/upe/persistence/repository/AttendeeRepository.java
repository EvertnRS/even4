package br.upe.persistence.repository;

import br.upe.persistence.Attendee;
import br.upe.persistence.Session;
import br.upe.persistence.User;
import br.upe.utils.JPAUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class AttendeeRepository implements Persistence{
    private static final Logger LOGGER = Logger.getLogger(AttendeeRepository.class.getName());
    private static AttendeeRepository instance;

    public AttendeeRepository() {
    }

    public static AttendeeRepository getInstance() {
        if (instance == null) {
            synchronized (AttendeeRepository.class) {
                if (instance == null) {
                    instance = new AttendeeRepository();
                }
            }
        }
        return instance;
    }

    public List<Attendee> getAllAttendees() {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        TypedQuery<Attendee> query = entityManager.createQuery("SELECT e FROM Attendee e", Attendee.class);
        return query.getResultList();
    }

    @Override
    public void create(Object... params) {
        if (params.length != 2) {
            LOGGER.warning("Só pode ter 2 parametros");
        }
        UUID parsedUserId = UUID.fromString((String) params[0]);
        UUID parsedSessionId = UUID.fromString((String) params[1]);

        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            User parsedUser = entityManager.find(User.class, parsedUserId);
            if (parsedUser == null) {
                throw new IllegalArgumentException("Usuário não encontrado com o ID: " + parsedUserId);
            }

            Session session = entityManager.find(Session.class, parsedSessionId);
            if (session == null) {
                throw new IllegalArgumentException("Sessão não encontrada com o ID: " + parsedSessionId);
            }

            Attendee attendee = entityManager.createQuery(
                            "SELECT a FROM Attendee a WHERE a.userId = :userId", Attendee.class)
                    .setParameter("userId", parsedUser)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (attendee == null) {
                attendee = new Attendee();
                attendee.setId(UUID.randomUUID());
                attendee.setUserId(parsedUser);
            }

            attendee.addSession(session);

            transaction.begin();
            entityManager.persist(attendee);
            transaction.commit();

            LOGGER.info("Sessão adicionada ao Attendee: " + attendee.getId());
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
    }

    @Override
    public void delete(Object... params) throws IOException {
        if (params.length != 2) {
            LOGGER.warning("Só pode ter 2 parametros");
            return;
        }

        UUID id = (UUID) params[0];
        UUID userId = (UUID) params[1];

        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            Attendee idAttendee = entityManager.find(Attendee.class, id);
            User owner = entityManager.find(User.class, userId);

            if ((owner) == null) {
                LOGGER.warning("Usuário inválido.");
                return;
            }

            if (idAttendee != null) {
                entityManager.remove(idAttendee);
                transaction.commit();
                LOGGER.info("Participante deletado com sucesso.");
            } else {
                LOGGER.warning("Participante não encontrado com o ID fornecido.");
            }
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.severe("Erro ao deletar Participante: " + e.getMessage());
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    @Override
    public void update(Object... params) throws IOException {
        //
    }

    @Override
    public Object getData(UUID id, String dataToGet) {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        Attendee attendee = entityManager.find(Attendee.class, id);
        if (attendee == null) {
            return null;
        }
        return switch (dataToGet) {
            case "id" -> attendee.getId();
            case "name" -> attendee.getName();
            case "userId" -> attendee.getUserId();
            case "sessionIds" -> attendee.getSessionIds();
            default -> null;
        };
    }

    @Override
    public void setData(UUID eventId, String dataToSet, Object data) {
        //
    }

    @Override
    public HashMap<UUID, Persistence> read() throws IOException {
        return null;
    }

    @Override
    public HashMap<UUID, Persistence> read(Object... params) {
        return null;
    }

    @Override
    public void setData(String dataToSet, Object data) {

    }

    @Override
    public Object getData(String dataToGet) {
        return null;
    }

    @Override
    public boolean loginValidate(String email, String password) {
        return false;
    }
}