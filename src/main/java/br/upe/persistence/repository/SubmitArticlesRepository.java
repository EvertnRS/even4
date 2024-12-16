package br.upe.persistence.repository;

import br.upe.persistence.SubmitArticle;
import br.upe.persistence.User;
import br.upe.persistence.Event;
import br.upe.persistence.builder.SubmitArticleBuilder;
import br.upe.utils.JPAUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class SubmitArticlesRepository implements Persistence {
    private static final Logger LOGGER = Logger.getLogger(SubmitArticlesRepository.class.getName());
    private static SubmitArticlesRepository instance;

    private SubmitArticlesRepository() {
    }

    public static SubmitArticlesRepository getInstance() {
        if (instance == null) {
            synchronized (SubmitArticlesRepository.class) {
                if (instance == null) {
                    instance = new SubmitArticlesRepository();
                }
            }
        }
        return instance;
    }

    public List<SubmitArticle> getAllArticles() {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        TypedQuery<SubmitArticle> query = entityManager.createQuery("SELECT a FROM SubmitArticle a", SubmitArticle.class);
        return query.getResultList();
    }

    public List<SubmitArticle> getAllEventArticles(UUID eventId) {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        TypedQuery<SubmitArticle> query = entityManager.createQuery("SELECT a FROM SubmitArticle a WHERE a.eventId.id = :eventId", SubmitArticle.class);
        query.setParameter("eventId", eventId);
        return query.getResultList();
    }


    @Override
    public Object[] create(Object... params) {
        if (params.length != 4) {
            LOGGER.warning("São necessários 2 parâmetros: nome do evento e o artigo escolhido.");
            return new Object[]{false, null};
        }

        String eventName = (String) params[0];
        byte[] article = (byte[]) params[1];
        UUID ownerId = UUID.fromString((String) params[2]);
        String articleName = (String) params[3];
        boolean isCreated = false;

        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        EntityTransaction transaction = entityManager.getTransaction();
        User owner = entityManager.find(User.class, ownerId);

        SubmitArticle submitArticle = null;
        try {

            TypedQuery<Event> query = entityManager.createQuery(
                    "SELECT e FROM Event e WHERE e.name = :name", Event.class);
            query.setParameter("name", eventName);
            Event event = query.getSingleResult();

            if (event == null) {
                LOGGER.warning("Evento não encontrado com o nome fornecido: " + eventName);
                return new Object[]{false, null};
            }

            submitArticle = SubmitArticleBuilder.builder()
                    .withName(articleName)
                    .withArticle(article)
                    .withOwner(owner)
                    .withEvent(event)
                    .build();

            List<SubmitArticle> userArticles = owner.getArticles();
            userArticles.add(submitArticle);
            owner.setArticles(userArticles);

            List<SubmitArticle> eventArticles = event.getArticles();
            eventArticles.add(submitArticle);
            event.setArticles(eventArticles);

            transaction.begin();
            entityManager.persist(submitArticle);
            transaction.commit();
            LOGGER.info("Artigo submetido com sucesso para o evento: " + eventName);
            isCreated = true;
        } catch (NoResultException e) {
            LOGGER.warning("Evento não encontrado com o nome fornecido: " + eventName);
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.severe("Erro ao submeter o artigo: " + e.getMessage());
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
        assert submitArticle != null;
        return new Object[]{isCreated, submitArticle.getId()};
    }


    @Override
    public HashMap<UUID, Persistence> read(Object... params) {
        return null;
    }

    @Override
    public boolean update(Object... params) {
        if (params.length != 3) {
            LOGGER.warning("São necessários 3 parâmetros.");
            return false;
        }

        String articleName = (String) params[0];
        byte[] articleContent = (byte[]) params[1];
        UUID articleId = (UUID) params[2];
        boolean isUpdated = false;

        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            SubmitArticle article = entityManager.find(SubmitArticle.class, articleId);
            if (article == null) {
                LOGGER.warning("Artigo não encontrado.");
                return false;
            }


            transaction.begin();
            article.setName(articleName);
            article.setArticle(articleContent);
            entityManager.merge(article);
            transaction.commit();

            LOGGER.info("Artigo atualizado com sucesso.");
            isUpdated = true;

        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.severe("Erro ao atualizar artigo: " + e.getMessage());
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
    public boolean delete(Object... params) {
        if (params.length != 2) {
            LOGGER.warning("É necessário um parâmetro: ID do artigo e ID do proprietário.");
            return false;
        }

        UUID articleId = (UUID) params [0];
        UUID ownerId = (UUID) params [1];
        boolean isDeleted = false;

        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            SubmitArticle article = entityManager.find(SubmitArticle.class, articleId);
            User owner = entityManager.find(User.class, ownerId);
            List<SubmitArticle> userArticles = owner.getArticles();
            userArticles.remove(article);
            owner.setArticles(userArticles);

            List<SubmitArticle> eventArticles = article.getEventId().getArticles();
            eventArticles.remove(article);
            article.getEventId().setArticles(eventArticles);

            if (article == null) {
                LOGGER.warning("Artigo não encontrado.");
                return false;
            }

            transaction.begin();
            entityManager.remove(article);
            transaction.commit();

            LOGGER.info("Artigo deletado com sucesso.");
            isDeleted = true;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.severe("Erro ao deletar artigo: " + e.getMessage());
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
        return isDeleted;
    }

    @Override
    public Object[] isExist(Object... params) {
        if (params.length != 2) {
            LOGGER.warning("É necessário 2 parâmetros: ID do artigo e ID do proprietário.");
            return new Object[]{false, null};
        }

        String name = (String) params[0];
        UUID ownerId = (UUID) params[1];
        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        TypedQuery<SubmitArticle> query = entityManager.createQuery(
                "SELECT a FROM SubmitArticle a WHERE a.name = :name", SubmitArticle.class);
        query.setParameter("name", name);
        SubmitArticle article = query.getSingleResult();
        User owner = entityManager.find(User.class, ownerId);
        if (article == null || owner == null) {
            return new Object[]{false, null};
        }
        return new Object[]{true, article.getId()};
    }

    @Override
    public Object getData(String dataToGet) {
        return null; // Método não necessário nesta classe
    }

    @Override
    public Object getData(UUID id, String dataToGet) {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        SubmitArticle submitArticle = entityManager.find(SubmitArticle.class, id);
        if (submitArticle == null) {
            return null;
        }

        return switch (dataToGet) {
            case "id" -> submitArticle.getId();
            case "name" -> submitArticle.getName();
            case "article" -> submitArticle.getArticle();
            case "event" -> submitArticle.getEventId().getName();
            case "owner_id" -> submitArticle.getOwnerId();
            default -> null;
        };
    }


    @Override
    public void setData(UUID submitArticleId, String dataToSet, Object data) {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            SubmitArticle submitArticle = entityManager.find(SubmitArticle.class, submitArticleId);
            if (submitArticle != null) {
                switch (dataToSet) {
                    case "id" -> submitArticle.setId((UUID) data);
                    case "name" -> submitArticle.setName((String) data);
                    case "article" -> submitArticle.setArticle((byte[]) data);
                    default -> throw new IllegalArgumentException("Campo inválido: " + dataToSet);
                }
                entityManager.merge(submitArticle);
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
    public HashMap<UUID, Persistence> read() {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        HashMap<UUID, Persistence> articlesMap = new HashMap<>();

        try {
            TypedQuery<SubmitArticle> query = entityManager.createQuery("SELECT a FROM SubmitArticle a", SubmitArticle.class);
            List<SubmitArticle> articles = query.getResultList();
            for (SubmitArticle article : articles) {
                articlesMap.put(article.getId(), (Persistence) article);
            }
            return articlesMap;
        } catch (Exception e) {
            LOGGER.warning("Erro ao buscar todos os artigos: " + e.getMessage());
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }

        return articlesMap;
    }
}
