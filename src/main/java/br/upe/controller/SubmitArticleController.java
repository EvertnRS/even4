package br.upe.controller;

import br.upe.persistence.Persistence;
import br.upe.persistence.SubmitArticle;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

public class SubmitArticleController implements Controller {
    private Map<String, Persistence> articleHashMap;
    private static final Logger LOGGER = Logger.getLogger(SubmitArticleController.class.getName());

    public Map<String, Persistence> getArticleHashMap() {
        return articleHashMap;
    }

    public void setArticleHashMap(Map<String, Persistence> articleHashMap) {
        this.articleHashMap = articleHashMap;
    }

    @Override
    public String getData(String dataToGet) {
        String data = "";
        try {
            Persistence article = this.articleHashMap.get(dataToGet);
            if (article != null) {
                data = article.getData(dataToGet);
            }
        } catch (Exception e) {
            LOGGER.warning("Informação não existe ou é restrita");
        }
        return data;
    }

    @Override
    public void create(Object... params) throws IOException {
        if (params.length != 2) {
            LOGGER.warning("São necessários 2 parâmetros: nome do evento e caminho do arquivo.");
            return;
        }

        String eventName = (String) params[0];
        String filePath = (String) params[1];
        boolean eventFound = getFatherEventId(eventName);
        if (eventFound) {
            Persistence article = new SubmitArticle();
            article.create(eventName, filePath);
        } else {
            LOGGER.warning("Evento não encontrado.");
        }
    }

    @Override
    public void delete(Object... params) throws IOException {
        if (params.length != 1) {
            LOGGER.warning("São necessários 1 parâmetro: nome do arquivo.");
            return;
        }

        String fileName = (String) params[0];

        Persistence article = new SubmitArticle();
        article.delete(fileName);
    }

    @Override
    public boolean list(String idowner) {
        // Método não implementado
        return false;
    }

    @Override
    public void show(Object... params) {
        // Método não implementado
    }

    @Override
    public void update(Object... params) throws IOException {
        if (params.length != 2) {
            LOGGER.warning("São necessários 2 parâmetros: nome do artigo e caminho do novo arquivo.");
            return;
        }

        String articleName = (String) params[0];
        String newFilePath = (String) params[1];

        Persistence article = new SubmitArticle();
        article.update(articleName, newFilePath);
    }

    @Override
    public void read() {
        // Método não implementado (Polimorfismo)
    }

    public void read(Object... params) {
        if (params.length != 1) {
            LOGGER.warning("É necessário 1 parâmetro: nome do evento.");
            return;
        }

        String eventName = (String) params[0];
        Persistence articlePersistence = new SubmitArticle();
        this.articleHashMap = articlePersistence.read(eventName);
        if (this.articleHashMap.isEmpty()) {
            LOGGER.warning("Nenhum artigo encontrado para o evento: %s".formatted(eventName));
        } else {
            LOGGER.warning("Artigos encontrados para o evento: %s".formatted(eventName));
            for (String articleName : this.articleHashMap.keySet()) {
                LOGGER.warning(articleName);
            }
        }
    }

    @Override
    public boolean loginValidate(String email, String cpf) {
        return false;
    }

    private boolean getFatherEventId(String eventName) throws IOException {
        EventController ec = new EventController();
        Map<String, Persistence> list = ec.getEventHashMap();
        boolean found = false;
        for (Map.Entry<String, Persistence> entry : list.entrySet()) {
            Persistence listindice = entry.getValue();
            if (listindice.getData("name").equals(eventName)) {
                found = true;
                break;
            }
        }
        return found;
    }
}
