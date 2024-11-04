package br.upe.controller;

import br.upe.persistence.Persistence;
import br.upe.persistence.SubmitArticle;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class SubmitArticleController implements Controller {
    private Map<UUID, Persistence> articleHashMap = new HashMap<>();
    private static final Logger LOGGER = Logger.getLogger(SubmitArticleController.class.getName());

    public Map<UUID, Persistence> getHashMap() {
        return articleHashMap;
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
    public void create(Object... params) throws IOException {
        if (params.length != 3) {
            LOGGER.warning("São necessários 3 parâmetros: nome do evento, caminho do arquivo e id.");
            return;
        }

        String eventName = (String) params[0];
        String filePath = (String) params[1];
        UUID id = (UUID) params[2];
        boolean eventFound = getFatherEventId(eventName);
        if (eventFound) {
            Persistence article = new SubmitArticle();
            article.create(eventName, filePath, id);
            articleHashMap.put(id, article);
        } else {
            LOGGER.warning("Evento não encontrado.");
        }
    }

    @Override
    public void delete(Object... params) throws IOException {
        if (params.length != 1) {
            LOGGER.warning("É necessário 1 parâmetro: id do artigo.");
            return;
        }

        String id = (String) params[0];
        Persistence article = articleHashMap.get(id);
        if (article != null) {
            article.delete(id);
            articleHashMap.remove(id);
        } else {
            LOGGER.warning("Artigo não encontrado.");
        }
    }

    @Override
    public List<String> list(Object... params) throws IOException {
        return List.of();
    }

    @Override
    public void update(Object... params) throws IOException {
        if (params.length != 3) {
            LOGGER.warning("São necessários 3 parâmetros: nome do novo evento, nome do artigo e nome do evento antigo.");
            return;
        }
        String newEventName = (String) params[0];
        String oldEventName = (String) params[1];
        String articleName = (String) params[2];

        Persistence article = articleHashMap.get(articleName);
        if (article != null) {
            article.update(newEventName, oldEventName, articleName);
            UUID articleId = (UUID) article.getData("id");
            articleHashMap.put(articleId, article);
        } else {
            LOGGER.warning("Artigo não encontrado.");
        }
    }

    @Override
    public void read() {
        // Método não implementado (Polimorfismo)
    }

    public void read(Object... params) {
        if (params.length != 1) {
            LOGGER.warning("É necessário 1 parâmetro: userId.");
            return;
        }

        String userId = (String) params[0];
        Persistence articlePersistence = new SubmitArticle();
        this.articleHashMap = articlePersistence.read(userId);

        if (!(this.articleHashMap.isEmpty())) {
            this.articleHashMap.forEach((key, value) -> LOGGER.info(String.valueOf(key)));
        } else {
            LOGGER.warning("Nenhum artigo encontrado.");
        }
    }

    @Override
    public boolean loginValidate(String email, String cpf) {
        return false;
    }

    private boolean getFatherEventId(String eventName) throws IOException {
        EventController ec = new EventController();
        Map<UUID, Persistence> list = ec.getHashMap();
        boolean found = false;
        for (Map.Entry<UUID, Persistence> entry : list.entrySet()) {
            Persistence listindice = entry.getValue();
            if (listindice.getData("name").equals(eventName)) {
                found = true;
                break;
            }
        }
        return found;
    }
}