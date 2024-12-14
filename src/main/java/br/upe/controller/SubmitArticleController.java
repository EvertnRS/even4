package br.upe.controller;

import br.upe.persistence.SubmitArticle;
import br.upe.persistence.repository.Persistence;
import br.upe.persistence.repository.SubmitArticlesRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

public class SubmitArticleController implements Controller {
    private static final Logger LOGGER = Logger.getLogger(SubmitArticleController.class.getName());

    private final SubmitArticlesRepository submitArticlesRepository;

    public SubmitArticleController() {
        this.submitArticlesRepository = SubmitArticlesRepository.getInstance();
    }

    @Override
    public <T> List<T> getAll() {
        return (List<T>) submitArticlesRepository.getAllArticles();
    }

    @Override
    public String getData(String dataToGet) {
        // Não implementado
        return null;
    }

    @Override
    public void create(Object... params) throws IOException {
        if (params.length != 3) {
            LOGGER.warning("São necessários 3 parâmetros: nome do evento, o conteúdo do arquivo (byte[]) e id do proprietário.");
            return;
        }

        String eventName = (String) params[0];
        String filePath = (String) params[1];
        String ownerId = (String) params[2];


        Path path = Paths.get(filePath);
        String articleName = path.getFileName().toString();


        byte[] articleContent = Files.readAllBytes(path);

        submitArticlesRepository.create(eventName, articleContent, ownerId, articleName);
    }


    @Override
    public void delete(Object... params) throws IOException {
        if (params.length != 1) {
            LOGGER.warning("É necessário 1 parâmetro: id do artigo.");
            return;
        }
        UUID articleId = (UUID) params[0];

        submitArticlesRepository.delete(articleId);

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

    public <T> List<T> getEventArticles(UUID eventId) {
        return (List<T>) submitArticlesRepository.getAllEventArticles(eventId);
    }

    @Override
    public void update(Object... params) throws IOException {
        if (params.length != 2) {
            LOGGER.warning("São necessários 2 parâmetros");
            return;
        }

        String filePath = (String) params[0];
        UUID articleId = (UUID) params[1];

        Path path = Paths.get(filePath);
        String articleName = path.getFileName().toString();

        byte[] articleContent = Files.readAllBytes(path);

        if (articleId != null) {
            submitArticlesRepository.update(articleName, articleContent, articleId);
        } else {
            LOGGER.warning("Artigo não encontrado.");
        }
    }

    @Override
    public boolean loginValidate(String email, String cpf) {
        return false;
    }
}
