package br.upe.persistence.repository;

import br.upe.persistence.Attendee;
import br.upe.persistence.Session;
import br.upe.persistence.User;
import br.upe.persistence.builder.AttendeeBuilder;
import br.upe.utils.JPAUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.io.IOException;
import java.util.Arrays;
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
    public Object[] create(Object... params) {
        if (params.length != 2) {
            LOGGER.warning("São necessários exatamente 2 parâmetros.");
            return new Object[]{false, null};
        }

        UUID parsedUserId = UUID.fromString((String) params[0]);
        UUID parsedSessionId = UUID.fromString((String) params[1]);
        boolean isCreated = false;

        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            // 1. Certifique-se de que o User está gerenciado
            User parsedUser = entityManager.find(User.class, parsedUserId);
            if (parsedUser == null) {
                LOGGER.warning("Usuário não encontrado: " + parsedUserId);
                return new Object[]{false, null};
            }

            // 2. Certifique-se de que a Session está gerenciada
            Session session = entityManager.find(Session.class, parsedSessionId);
            if (session == null) {
                LOGGER.warning("Sessão não encontrada: " + parsedSessionId);
                return new Object[]{false, null};
            }

            // 3. Busque ou crie o Attendee no estado gerenciado
            Attendee attendee = entityManager.createQuery(
                            "SELECT a FROM Attendee a WHERE a.userId = :userId", Attendee.class)
                    .setParameter("userId", parsedUser)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (attendee == null) {
                attendee = AttendeeBuilder.builder()
                        .withId(UUID.randomUUID())
                        .withUser(parsedUser)
                        .build();
                transaction.begin();
                entityManager.persist(attendee);
                transaction.commit();
            } else {
                attendee = entityManager.merge(attendee); // Reanexa o Attendee se ele já existe
            }

            // 4. Adicione a sessão ao Attendee gerenciado
            transaction.begin();
            attendee.addSession(session);
            entityManager.merge(attendee); // Salva a modificação no Attendee
            transaction.commit();

            LOGGER.info("Sessão adicionada ao Attendee: " + attendee.getId());
            isCreated = true;

            return new Object[]{isCreated, attendee.getId()};
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.severe("Erro ao commitar a transação: " + e.getMessage());
            throw e;
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }



    @Override
    public boolean delete(Object... params) throws IOException {
        if (params.length != 2) {
            LOGGER.warning("Só pode ter 2 parametros");
            return false;
        }

        UUID id = (UUID) params[0];
        UUID userId = (UUID) params[1];
        boolean isDeleted = false;

        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            Attendee idAttendee = entityManager.find(Attendee.class, id);
            User owner = entityManager.find(User.class, userId);

            if ((owner) == null) {
                LOGGER.warning("Usuário inválido.");
                return false;
            }

            if (idAttendee != null) {
                entityManager.remove(idAttendee);
                transaction.commit();
                LOGGER.info("Participante deletado com sucesso.");
                isDeleted = true;
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
        return true;
    }

    @Override
    public boolean update(Object... params) throws IOException {
        return false;
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
    public Object[] isExist(Object... params) throws IOException {
        if (params.length != 1) {
            LOGGER.warning("Só pode ter 1 parametro");
            return new Object[]{false, null};
        }

        String userId = (String) params[0];

        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        TypedQuery<Attendee> query = entityManager.createQuery(
                "SELECT e FROM Attendee e WHERE e.userId.id = :userId", Attendee.class);
        query.setParameter("userId", UUID.fromString(userId));
        try {
            Attendee attendee = query.getSingleResult();
            return new Object[]{true, attendee.getId()};
        } catch (Exception e) {
            LOGGER.warning("Participante não encontrado.");
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
        return new Object[]{false, null};
    }
}
