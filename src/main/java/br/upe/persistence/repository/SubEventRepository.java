package br.upe.persistence.repository;

import br.upe.persistence.Event;
import br.upe.persistence.SubEvent;
import br.upe.persistence.User;
import br.upe.utils.JPAUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.sql.Date;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class SubEventRepository implements Persistence {
    private static final Logger LOGGER = Logger.getLogger(SubEventRepository.class.getName());
    private static SubEventRepository instance;

    public SubEventRepository() {
    }

    public static SubEventRepository getInstance() {
        if (instance == null) {
            synchronized (SubEventRepository.class) {
                instance = new SubEventRepository();
            }
        }
        return instance;
    }

    public List<SubEvent> getAllSubEvents() {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        TypedQuery<SubEvent> query = entityManager.createQuery("SELECT e FROM SubEvent e", SubEvent.class);
        return query.getResultList();
    }

    @Override
    public Object getData(String dataToGet) {
        return null;
    }

    @Override
    public Object getData(UUID id, String dataToGet) {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        SubEvent subevent = entityManager.find(SubEvent.class, id);
        if (subevent == null) {
            return null;
        }
        return switch (dataToGet) {
            case "id" -> subevent.getId();
            case "name" -> subevent.getName();
            case "date" -> subevent.getDate();
            case "description" -> subevent.getDescription();
            case "location" -> subevent.getLocation();
            case "ownerId" -> subevent.getOwnerId().getId();
            case "eventId" -> subevent.getEventId().getId();
            default -> null;
        };
    }

    @Override
    public void setData(UUID eventId, String dataToSet, Object data) {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            SubEvent subevent = entityManager.find(SubEvent.class, eventId);
            if (subevent != null) {
                switch (dataToSet) {
                    case "id" -> subevent.setId((UUID) data);
                    case "name" -> subevent.setName((String) data);
                    case "date" -> subevent.setDate((Date) data);
                    case "description" -> subevent.setDescription((String) data);
                    case "location" -> subevent.setLocation((String) data);
                    case "ownerId" -> subevent.setOwnerId((User) data);
                    case "eventId" -> subevent.setEventId((Event) data);
                    default -> throw new IllegalArgumentException("Campo inválido: " + dataToSet);
                }
                entityManager.merge(subevent);
                transaction.commit();
            } else {
                throw new IllegalArgumentException("Evento não encontrado com o ID fornecido.");
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
    public void setData(String dataToSet, Object data){

    }

//    private String generateUniqueId() {
//        SecureRandom secureRandom = new SecureRandom();
//        long timestamp = Instant.now().toEpochMilli();
//        int lastThreeDigitsOfTimestamp = (int) (timestamp % 1000);
//        int randomValue = secureRandom.nextInt(900) + 100;
//        return String.format("%03d%03d", lastThreeDigitsOfTimestamp, randomValue);
//    }

    @Override
    public void create(Object... params) {
        if (params.length < 6) {
            LOGGER.warning("Só pode ter 6 parametros");
        }

        UUID parsedEventId = UUID.fromString((String) params[0]);
        String parsedName = (String) params[1];
        Date parsedDate = (Date) params[2];
        String parsedDescription = (String) params[3];
        String parsedLocation = (String) params[4];
        UUID parsedOwnerId = UUID.fromString((String) params[5]);

        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        User parsedOwner = entityManager.find(User.class, parsedOwnerId);
        Event parsedEvent = entityManager.find(Event.class, parsedEventId);

        SubEvent subevent = new SubEvent();
        subevent.setName(parsedName);
        subevent.setDate(parsedDate);
        subevent.setDescription(parsedDescription);
        subevent.setLocation(parsedLocation);
        subevent.setOwnerId(parsedOwner);
        subevent.setEventId(parsedEvent);

        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();
            entityManager.persist(subevent);
            transaction.commit();
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
    public HashMap<UUID, Persistence> read() throws IOException {
        return null;
    }

    @Override
    public HashMap<UUID, Persistence> read(Object... params) {
        try {
            EntityManager entityManager = JPAUtils.getEntityManagerFactory();
            TypedQuery<SubEvent> query = entityManager.createQuery(
                    "SELECT e FROM SubEvent e", SubEvent.class);
            return (HashMap<UUID, Persistence>) query.getResultList();
        } catch (NoResultException e) {
            LOGGER.warning("Informação não encontrada.");
        }
        return null;
    }

    @Override
    public boolean loginValidate(String email, String password) {
        return false;
    }

    @Override
    public void update(Object... params) throws IOException {
        if (params.length != 5) {
            LOGGER.warning("Só pode ter 5 parâmetros");
            return;
        }

        UUID id = (UUID) params[0];
        String newName = (String) params[1];
        Date newDate = (Date) params[2];
        String newDescription = (String) params[3];
        String newLocation = (String) params[4];

        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();
            SubEvent subevent = entityManager.find(SubEvent.class, id);

            if (subevent != null) {
                setData(id, "name", newName);
                setData(id, "date", newDate);
                setData(id, "description", newDescription);
                setData(id, "location", newLocation);
                transaction.commit();
                LOGGER.info("SubEvento atualizado com sucesso.");
            } else {
                LOGGER.warning("SubEvento não encontrado com o ID fornecido.");
            }
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.severe("Erro ao atualizar evento: " + e.getMessage());
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
        UUID ownerId = (UUID) params[1];

        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            SubEvent idSubEvent = entityManager.find(SubEvent.class, id);
            User owner = entityManager.find(User.class, ownerId);

            if ((owner) == null) {
                LOGGER.warning("Criador inválido.");
                return;
            }

            if (idSubEvent != null) {
                entityManager.remove(idSubEvent);
                transaction.commit();
                LOGGER.info("SubEvento deletado com sucesso.");
            } else {
                LOGGER.warning("SubEvento não encontrado com o ID fornecido.");
            }
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.severe("Erro ao deletar evento: " + e.getMessage());
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
}
