package br.upe.persistence.Repository;
import br.upe.persistence.Event;
import br.upe.persistence.Persistence;
import br.upe.persistence.User;
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
    private EntityManager entityManager = JPAUtils.getEntityManagerFactory();
    private static EventRepository instance;

    public EventRepository() {}

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
        TypedQuery<Event> query = entityManager.createQuery("SELECT e FROM Event e", Event.class);
        return query.getResultList();
    }

    @Override
    public void create(Object... params) {
        if (params.length != 2) {
            LOGGER.warning("Só pode ter 2 parametros");
        }

        String name = (String) params[0];
        Date date = (Date) params[1];
        String description = (String) params[2];
        String location = (String) params[3];
        UUID ownerId = (UUID) params[4];

        User owner = entityManager.find(User.class, ownerId);

        Event event = new Event();
        event.setName(name);
        event.setDate(date);
        event.setDescription(description);
        event.setLocation(location);
        event.setOwnerId(owner);

        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(event);
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
    public HashMap<UUID, Persistence> read() throws IOException{
        return null;
    }

    @Override
    public HashMap<UUID, Persistence> read(Object... params) {
        try {
            TypedQuery<Event> query = entityManager.createQuery(
                    "SELECT e FROM Event e", Event.class);
            return (HashMap<UUID, Persistence>) query.getResultList();
        } catch (NoResultException e) {
            LOGGER.warning("Informação não encontrada.");
        }
        return null;
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

        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            Event event = entityManager.find(Event.class, id);

            if (event != null) {
                event.setName(newName);
                event.setDate(newDate);
                event.setDescription(newDescription);
                event.setLocation(newLocation);
                transaction.commit();
                LOGGER.info("Evento atualizado com sucesso.");
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
    }

    @Override
    public void delete(Object... params) throws IOException{
        if (params.length != 2) {
            LOGGER.warning("Só pode ter 2 parametros");
            return;
        }

        UUID id = (UUID) params[0];
        UUID ownerId = (UUID) params[1];

        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            Event idEvent = entityManager.find(Event.class, id);
            User owner = entityManager.find(User.class, ownerId);

            if ((owner) == null) {
                LOGGER.warning("Criador inválido.");
                return;
            }

            if (idEvent != null) {
                entityManager.remove(idEvent);
                transaction.commit();
                LOGGER.info("Evento deletado com sucesso.");
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
    }

    @Override
    public boolean loginValidate(String email, String password){
        return false;
    }

    @Override
    public Object getData(String dataToGet) {
        return null;
    }

    @Override
    public void setData(String dataToSet, Object data) {
    }

}

