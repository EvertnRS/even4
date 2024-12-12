package br.upe.persistence.repository;
import br.upe.persistence.Event;
import br.upe.persistence.User;
import br.upe.persistence.builder.EventBuilder;
import br.upe.utils.JPAUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.io.*;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;


public class EventRepository implements Persistence {
    private static final Logger LOGGER = Logger.getLogger(EventRepository.class.getName());
    private static EventRepository instance;

    public EventRepository() {
    }

    public static EventRepository getInstance() {
        if (instance == null) {
            synchronized (EventRepository.class) {
                if (instance == null) {
                    instance = new EventRepository();
                }
            }
        }
        return instance;
    }

    public List<Event> getAllEvents() {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        TypedQuery<Event> query = entityManager.createQuery("SELECT e FROM Event e", Event.class);
        return query.getResultList();
    }

    @Override
    public boolean create(Object... params) {
        if (params.length != 5) {
            LOGGER.warning("Só pode ter 5 parametros");
        }

        String name = (String) params[0];
        Date date = (Date) params[1];
        String description = (String) params[2];
        String location = (String) params[3];
        UUID ownerId = UUID.fromString((String) params[4]);
        boolean isCreated = false;

        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        User owner = entityManager.find(User.class, ownerId);
        if (owner == null) {
            throw new IllegalArgumentException("Usuário não encontrado com o ID: " + ownerId);
        }

        Event event = EventBuilder.builder()
                .withName(name)
                .withDate(date)
                .withDescription(description)
                .withLocation(location)
                .withOwner(owner)
                .build();

        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(event);
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
        return isCreated;
    }

    @Override
    public HashMap<UUID, Persistence> read() throws IOException {
        return null;
    }

    @Override
    public HashMap<UUID, Persistence> read(Object... params) {
        try {
            EntityManager entityManager = JPAUtils.getEntityManagerFactory();
            TypedQuery<Event> query = entityManager.createQuery(
                    "SELECT e FROM Event e", Event.class);
            return (HashMap<UUID, Persistence>) query.getResultList();
        } catch (NoResultException e) {
            LOGGER.warning("Informação não encontrada.");
        }
        return null;
    }

    @Override
    public boolean update(Object... params) throws IOException {
        if (params.length != 5) {
            LOGGER.warning("Só pode ter 5 parâmetros");
            return false;
        }

        UUID id = (UUID) params[0];
        String newName = (String) params[1];
        Date newDate = (Date) params[2];
        String newDescription = (String) params[3];
        String newLocation = (String) params[4];
        boolean isUpdated = false;

        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            Event event = entityManager.find(Event.class, id);

            if (event != null) {
                setData(id, "name", newName);
                setData(id, "date", newDate);
                setData(id, "description", newDescription);
                setData(id, "location", newLocation);
                transaction.commit();
                LOGGER.info("Evento atualizado com sucesso.");
                isUpdated = true;
            } else {
                LOGGER.warning("Evento não encontrado com o ID fornecido.");
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
        return isUpdated;
    }

    @Override
    public void setData(String dataToSet, Object data) {

    }

    @Override
    public Object getData(String dataToGet) {
        return null;
    }

    @Override
    public boolean delete(Object... params) throws IOException {
        if (params.length != 2) {
            LOGGER.warning("Só pode ter 2 parametros");
            return false;
        }

        UUID id = (UUID) params[0];
        UUID ownerId = (UUID) params[1];
        boolean isDeleted = false;

        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            Event idEvent = entityManager.find(Event.class, id);
            User owner = entityManager.find(User.class, ownerId);

            if ((owner) == null) {
                LOGGER.warning("Criador inválido.");
                return false;
            }

            if (idEvent != null) {
                entityManager.remove(idEvent);
                transaction.commit();
                LOGGER.info("Evento deletado com sucesso.");
                isDeleted = true;
            } else {
                LOGGER.warning("Evento não encontrado com o ID fornecido.");
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
        return isDeleted;
    }

    @Override
    public boolean loginValidate(String email, String password) {
        return false;
    }

    @Override
    public Object getData(UUID id, String dataToGet) {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        Event event = entityManager.find(Event.class, id);
        if (event == null) {
            return null;
        }
        return switch (dataToGet) {
            case "id" -> event.getId();
            case "name" -> event.getName();
            case "date" -> event.getDate();
            case "description" -> event.getDescription();
            case "location" -> event.getLocation();
            case "ownerId" -> event.getIdOwner().getId();
            default -> null;
        };
    }

    @Override
    public void setData(UUID eventId, String dataToSet, Object data) {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Event event = entityManager.find(Event.class, eventId);
            if (event != null) {
                switch (dataToSet) {
                    case "id" -> event.setId((UUID) data);
                    case "name" -> event.setName((String) data);
                    case "date" -> event.setDate((Date) data);
                    case "description" -> event.setDescription((String) data);
                    case "location" -> event.setLocation((String) data);
                    default -> throw new IllegalArgumentException("Campo inválido: " + dataToSet);
                }
                entityManager.merge(event);
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
}

