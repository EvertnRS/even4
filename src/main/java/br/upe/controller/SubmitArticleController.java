package br.upe.controller;

import br.upe.persistence.Persistence;
import br.upe.persistence.SubmitArticle;
import java.io.IOException;
import java.util.List;
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
        if (params.length != 3) {
            LOGGER.warning("São necessários 2 parâmetros: nome do evento e caminho do arquivo.");
            return;
        }

        String eventName = (String) params[0];
        String filePath = (String) params[1];
        String id = (String) params[2];
        boolean eventFound = getFatherEventId(eventName);
        if (eventFound) {
            Persistence article = new SubmitArticle();
            article.create(eventName, filePath, id);
        } else {
            LOGGER.warning("Evento não encontrado.");
        }
    }

    @Override
    public void delete(Object... params) throws IOException {
        if (params.length != 1) {
            LOGGER.warning("São necessários 2 parâmetro.");
            return;
        }

        String fileName = (String) params[0];
        System.out.println(fileName);
        Persistence article = new SubmitArticle();
        article.delete(fileName);
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


        Persistence article = new SubmitArticle();
        article.update(newEventName, oldEventName, articleName);
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

        if (this.articleHashMap.isEmpty()) {

        } else {
            this.articleHashMap.forEach((key, value) -> LOGGER.info(key));
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