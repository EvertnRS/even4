package br.upe.controller;

import br.upe.persistence.Model;
import br.upe.persistence.SubEvent;
import br.upe.persistence.repository.Persistence;
import br.upe.persistence.Event;
import br.upe.persistence.SubmitArticle;
import br.upe.persistence.repository.SubmitArticlesRepository;
import br.upe.persistence.repository.Persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

public class SubmitArticleController implements Controller {
    private Map<UUID, Persistence> articleHashMap = new HashMap<>();
    private static final Logger LOGGER = Logger.getLogger(SubmitArticleController.class.getName());

    private final SubmitArticlesRepository submitArticlesRepository;

    public SubmitArticleController() {
        this.submitArticlesRepository = SubmitArticlesRepository.getInstance();
    }

    public Map<UUID, Persistence> getHashMap() {
        return articleHashMap;
    }

    @Override
    public <T> List <T> getAll() {
        return (List<T>) submitArticlesRepository.getAllArticles();
    }

    public void setArticleHashMap(Map<UUID, Persistence> articleHashMap) {
        this.articleHashMap = articleHashMap;
    }

    @Override
    public String getData(String dataToGet) {
        String data = "";
        try {
            Persistence article = this.articleHashMap.get(dataToGet);
            if (article != null) {
                data = (String) article.getData(dataToGet);
            }
        } catch (Exception e) {
            LOGGER.warning("Informação não existe ou é restrita");
        }
        return data;
    }

    @Override
    public boolean create(Object... params) throws IOException {
        if (params.length != 3) {
            LOGGER.warning("São necessários 3 parâmetros: nome do evento, o conteúdo do arquivo (byte[]) e id do proprietário.");
            return false;
        }

        String eventName = (String) params[0];
        String filePath = (String) params[1];
        String ownerId = (String) params[2];



        Path path = Paths.get(filePath);
        String articleName = path.getFileName().toString();


        byte[] articleContent = Files.readAllBytes(path);

        return submitArticlesRepository.create(eventName, articleContent, ownerId, articleName);
    }


    @Override
    public boolean delete(Object... params) throws IOException {
        if (params.length != 1) {
            LOGGER.warning("É necessário 1 parâmetro: id do artigo.");
            return false;
        }
        UUID articleId = (UUID) params [0];

        return submitArticlesRepository.delete(articleId);
    }

    @Override
    public <T> List<T> list(Object... params) throws IOException {
        if (params.length != 1) {
            LOGGER.warning("É necessário 1 parâmetro: userId.");
            return List.of();
        }

        UUID userId = UUID.fromString((String) params[0]);
        List<SubmitArticle> allArticles = submitArticlesRepository.getAllArticles();
        List<SubmitArticle> userArticles = new ArrayList<>();
        for (SubmitArticle article : allArticles) {
            if (article.getOwnerId().getId().equals(userId)) {
                userArticles.add(article);
            }
        }

        if (userArticles.isEmpty()) {
            LOGGER.warning("Nenhum artigo encontrado para o usuário especificado.");
        }

        return (List<T>) userArticles;
    }

    public <T> List <T> getEventArticles(UUID eventId) {
        return (List<T>) submitArticlesRepository.getAllEventArticles(eventId);
    }

    @Override
    public boolean update(Object... params) throws IOException {
        if (params.length != 2) {
            LOGGER.warning("São necessários 2 parâmetros");
            return false;
        }

        String filePath = (String) params [0];
        UUID articleId = (UUID) params [1];

        Path path = Paths.get(filePath);
        String articleName = path.getFileName().toString();
        boolean isUpdated = false;

        byte[] articleContent = Files.readAllBytes(path);

        if (articleId != null) {
            isUpdated = submitArticlesRepository.update(articleName, articleContent, articleId);
        } else {
            LOGGER.warning("Artigo não encontrado.");
        }
        return isUpdated;
    }

    @Override
    public void read() {
        articleHashMap.clear();

        HashMap<UUID, Persistence> articles = submitArticlesRepository.read();
        if (!articles.isEmpty()) {
            this.articleHashMap.putAll(articles);
            articles.forEach((key, value) -> LOGGER.info("Artigo encontrado: " + key));
        } else {
            LOGGER.warning("Nenhum artigo encontrado.");
        }
    }


    public void read(Object... params) {

    }

    @Override
    public boolean loginValidate(String email, String cpf) {
        return false;
    }
}
