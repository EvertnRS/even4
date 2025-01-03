package br.upe.persistence.repository;

import br.upe.persistence.Event;
import br.upe.persistence.User;
import br.upe.persistence.builder.EventBuilder;
import br.upe.utils.JPAUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;


public class EventRepository implements Persistence {
    private static final Logger LOGGER = Logger.getLogger(EventRepository.class.getName());
    private static EventRepository instance;
    private static final String DESCRIPTION = "description";
    private static final String LOCATION = "location";
    private static final String EVENT_NOT_FOUND = "Evento não encontrado com o ID fornecido.";

    public EventRepository() {
        // Construtor vazio porque esta classe não requer inicialização específica.
    }

    public static EventRepository getInstance() {
        if (instance == null) {
            synchronized (EventRepository.class) {
                instance = new EventRepository();
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
    public Object[] create(Object... params) {
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

        List<Event> userEvents = owner.getEvents();
        userEvents.add(event);
        owner.setEvents(userEvents);

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
        return new Object[]{isCreated, event.getId(), event.getName()};
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
                setData(id, DESCRIPTION, newDescription);
                setData(id, LOCATION, newLocation);
                transaction.commit();
                LOGGER.info("Evento atualizado com sucesso.");
                isUpdated = true;
            } else {
                LOGGER.warning(EVENT_NOT_FOUND);
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
    public Object getData(String dataToGet) {
        return null;
    }

    @Override
    public boolean delete(Object... params) throws IOException {
        if (params.length != 2) {
            LOGGER.warning("Só pode ter 2 parâmetros.");
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
            if (idEvent == null) {
                LOGGER.warning(EVENT_NOT_FOUND);
                return false;
            }

            User owner = entityManager.find(User.class, ownerId);
            if (owner == null) {
                LOGGER.warning("Criador inválido.");
                return false;
            }

            List<Event> userEvents = owner.getEvents();
            userEvents.remove(idEvent);
            owner.setEvents(userEvents);

            entityManager.remove(idEvent);
            transaction.commit();
            LOGGER.info("Evento deletado com sucesso.");
            isDeleted = true;

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
            case DESCRIPTION -> event.getDescription();
            case LOCATION -> event.getLocation();
            case "ownerId" -> event.getOwnerId().getId();
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
                    case DESCRIPTION -> event.setDescription((String) data);
                    case LOCATION -> event.setLocation((String) data);
                    default -> throw new IllegalArgumentException("Campo inválido: " + dataToSet);
                }
                entityManager.merge(event);
                transaction.commit();
            } else {
                throw new IllegalArgumentException(EVENT_NOT_FOUND);
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
    public Object[] isExist(Object... params) throws IOException {
        if (params.length != 2) {
            LOGGER.warning("Só pode ter 2 parametro");
            return new Object[]{false, null};
        }
        String name = (String) params[0];

        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        TypedQuery<Event> query = entityManager.createQuery(
                "SELECT e FROM Event e WHERE e.name = :name", Event.class);
        query.setParameter("name", name);

        try {
            Event event = query.getSingleResult();
            return new Object[]{true, event.getId()};
        } catch (NoResultException e) {
            LOGGER.warning("Evento não encontrado.");
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
        return new Object[]{false, null};
    }

}

