package br.upe.controller;

import br.upe.persistence.Event;
import br.upe.persistence.SubEvent;
import br.upe.persistence.repository.EventRepository;
import br.upe.persistence.repository.Persistence;
import br.upe.persistence.repository.SubEventRepository;
import br.upe.utils.JPAUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

public class SubEventController implements Controller {
    private static final String DESCRIPTION = "description";
    private static final String LOCATION = "location";
    private static final String OWNER_ID = "ownerId";
    private static final String EVENT_ID = "eventId";
    private static final Logger LOGGER = Logger.getLogger(SubEventController.class.getName());

    private Map<UUID, Persistence> subEventHashMap;
    private Persistence subEventLog;

    public SubEventController() throws IOException {
        this.read();
    }

    public Map<UUID, Persistence> getHashMap() {
        System.out.println(subEventHashMap);
        return subEventHashMap;
    }

    @Override
    public <T> List <T> getAll() {
        SubEventRepository subeventRepository = SubEventRepository.getInstance();
        return (List<T>) subeventRepository.getAllSubEvents();
    }

    @Override
    public <T> List<T> getEventArticles(UUID eventId) {
        return List.of();
    }

    @Override
    public <T> List <T> list(Object... params) throws IOException {
        UUID userId = UUID.fromString((String) params[0]);
        SubEventRepository subeventRepository = SubEventRepository.getInstance();
        List<SubEvent> allSubEvents = subeventRepository.getAllSubEvents();
        List<SubEvent> userSubEvents = new ArrayList<>();

        for (SubEvent subevent : allSubEvents) {
            if (subevent.getOwnerId().getId().equals(userId)) {
                userSubEvents.add(subevent);
            }
        }

        if (userSubEvents.isEmpty()) {
            LOGGER.warning("Seu usuário atual é organizador de nenhum Subevento");
        }

        return (List<T>) userSubEvents;
    }

    public void setEventHashMap(Map<UUID, Persistence> subEventHashMap) {
        this.subEventHashMap = subEventHashMap;
    }

    @Override
    public String getData(String dataToGet) {
        String data = "";
        try {
            switch (dataToGet) {
                case "id" -> data = (String) this.subEventLog.getData("id");
                case "name" -> data = (String) this.subEventLog.getData("name");
                case DESCRIPTION -> data = (String) this.subEventLog.getData(DESCRIPTION);
                case "date" -> data = String.valueOf(this.subEventLog.getData("date"));
                case LOCATION -> data = (String) this.subEventLog.getData(LOCATION);
                case EVENT_ID -> data = (String) this.subEventLog.getData(EVENT_ID);
                case OWNER_ID -> data = (String) this.subEventLog.getData(OWNER_ID);
                default -> throw new IOException();
            }
        } catch (IOException e) {
            LOGGER.warning("Informação não existe ou é restrita");
        }
        return data;
    }

    @Override
    public Object[] create(Object... params) throws IOException {
        if (params.length != 6) {
            LOGGER.warning("Só pode ter 6 parâmetros");
            return new Object[]{false, null};
        }

        UUID eventId = (params[0] instanceof UUID) ? (UUID) params[0] : UUID.fromString((String) params[0]);
        String fatherEventId = getFatherEventId(eventId);
        String name = (String) params[1];
        Date date = (Date) params[2];
        String description = (String) params[3];
        String location = (String) params[4];
        String userId = (String) params[5];

        Persistence subEvent = new SubEventRepository();
        return subEvent.create(fatherEventId, name, date, description, location, userId);
    }

//    private void cascadeDelete(String id) throws IOException {
//        // Deletar todas as sessões relacionadas ao SubEvento
//        SessionController sessionController = new SessionController();
//        sessionController.read();
//        Iterator<Map.Entry<UUID, Persistence>> sessionIterator = sessionController.getHashMap().entrySet().iterator();
//        while (sessionIterator.hasNext()) {
//            Map.Entry<UUID, Persistence> entry = sessionIterator.next();
//            if (entry.getValue().getData(EVENT_ID).equals(id)) {
//                sessionIterator.remove();
//            }
//        }
//        sessionController.getHashMap().values().forEach(session -> {
//            try {
//                session.delete(sessionController.getHashMap());
//            } catch (IOException e) {
//                throw new IllegalArgumentException(e);
//            }
//        });
//
//        // Deletar todos os participantes relacionados às sessões do evento
//        AttendeeController attendeeController = new AttendeeController();
//        attendeeController.read();
//        Iterator<Map.Entry<UUID, Persistence>> attendeeIterator = attendeeController.getHashMap().entrySet().iterator();
//        while (attendeeIterator.hasNext()) {
//            Map.Entry<UUID, Persistence> entry = attendeeIterator.next();
//            String sessionId = (String) entry.getValue().getData("sessionId");
//            if (sessionController.getHashMap().containsKey(sessionId)) {
//                attendeeIterator.remove();
//            }
//        }
//        attendeeController.getHashMap().values().forEach(attendee -> {
//            try {
//                attendee.delete(attendeeController.getHashMap());
//            } catch (IOException e) {
//                throw new IllegalArgumentException(e);
//            }
//        });
//
//        // Deletar todos os artigos relacionados ao SubEvento
//        String subEventName = (String) subEventHashMap.get(id).getData("name");
//        SubmitArticleController articleController = new SubmitArticleController();
//        articleController.read(subEventName);
//        Iterator<Map.Entry<UUID, Persistence>> articleIterator = articleController.getHashMap().entrySet().iterator();
//        while (articleIterator.hasNext()) {
//            Map.Entry<UUID, Persistence> entry = articleIterator.next();
//            if (entry.getValue().getData(EVENT_ID).equals(id)) {
//                articleIterator.remove();
//            }
//        }
//        articleController.getHashMap().values().forEach(article -> {
//            try {
//                article.delete(articleController.getHashMap());
//            } catch (IOException e) {
//                throw new IllegalArgumentException(e);
//            }
//        });
//    }


    @Override
    public boolean delete(Object... params) throws IOException {
        if (params.length != 2) {
            LOGGER.warning("É necessário fornecer exatamente 2 parâmetros.");
            return false;
        }

        SubEventRepository subeventRepository = SubEventRepository.getInstance();

        UUID id;
        UUID ownerId;



        if (params[0] instanceof UUID) {
            id = (UUID) params[0];
        } else if (params[0] instanceof String) {
            id = UUID.fromString((String) params[0]);
        } else {
            throw new IllegalArgumentException("O primeiro parâmetro deve ser do tipo UUID ou String.");
        }

        if (params[1] instanceof UUID) {
            ownerId = (UUID) params[1];
        } else if (params[1] instanceof String) {
            ownerId = UUID.fromString((String) params[1]);
        } else {
            throw new IllegalArgumentException("O segundo parâmetro deve ser do tipo UUID ou String.");
        }

        return subeventRepository.delete(id, ownerId);
    }



    @Override
    public boolean update(Object... params) throws IOException {
        if (!isValidParamsLength(params)) {
            LOGGER.warning("Só pode ter 5 parametros");
            return false;
        }

        UUID id = (UUID) params[0];
        String newName = (String) params[1];
        Date newDate = (Date) params[2];
        String newDescription = (String) params[3];
        String newLocation = (String) params[4];


        return updateSubEvent( id, newName, newDate, newDescription, newLocation);
    }

    private boolean isValidParamsLength(Object... params) {
        return params.length == 5;
    }

    private String getOwnerSubEventId(String oldName, String userId) {
        for (Map.Entry<UUID, Persistence> entry : subEventHashMap.entrySet()) {
            Persistence persistence = entry.getValue();
            String name = (String) persistence.getData("name");
            String ownerId = (String) persistence.getData(OWNER_ID);

            if (name != null && name.equals(oldName) && ownerId != null && ownerId.equals(userId)) {
                LOGGER.info("Owner found for the sub-event.");
                return (String) persistence.getData("id");
            }
        }
        return null;
    }

    private boolean updateSubEvent(UUID id, String newName, Date newDate, String newDescription, String newLocation) throws IOException {
        boolean isUpdated = false;
        if (id != null) {
            SubEventRepository subeventRepository = SubEventRepository.getInstance();
            isUpdated = subeventRepository.update(id, newName, newDate, newDescription, newLocation);
        } else {
            LOGGER.warning("Evento não encontrado");
        }
        return isUpdated;
    }


    @Override
    public void read() throws IOException {
        /*Persistence subEventPersistence = new SubEvent();
        this.subEventHashMap = subEventPersistence.read();*/
    }

    @Override
    public Object[] isExist(Object... params) throws IOException {
        if (params.length != 2) {
            LOGGER.warning("Só pode ter 2 parâmetro");
            return new Object[]{false, null};
        }
        SubEventRepository subeventRepository = SubEventRepository.getInstance();
        String name = (String) params[0];
        String ownerId = (String) params[1];
        return subeventRepository.isExist(name, ownerId);
    }


    private String getFatherEventId(UUID searchId) throws IOException {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        String fatherId = null;
        try {
            TypedQuery<Event> query = entityManager.createQuery("SELECT e FROM Event e WHERE e.id = :searchId", Event.class);
            query.setParameter("searchId", searchId);
            Event event = query.getSingleResult();
            fatherId = event.getId().toString();
        } catch (NoResultException e) {
            LOGGER.warning("Evento pai não encontrado\n");
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
        return fatherId;
    }
}