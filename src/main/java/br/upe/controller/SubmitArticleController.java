package br.upe.controller;

import br.upe.persistence.Persistence;
import br.upe.persistence.SubmitArticle;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class SubmitArticleController implements Controller {
    private HashMap<String, Persistence> articleHashMap;
    private HashMap<String, Persistence> EventHashMap;

    public SubmitArticleController() {
        this.articleHashMap = new HashMap<>();
        this.EventHashMap = new HashMap<>();
    }

    public HashMap<String, Persistence> getArticleHashMap() {
        return articleHashMap;
    }

    public void setArticleHashMap(HashMap<String, Persistence> articleHashMap) {
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
            System.out.println("Informação não existe ou é restrita");
        }
        return data;
    }

    @Override
    public void SubmitArticleController(String string) {
        // Método vazio
    }

    @Override
    public void create(Object... params) throws FileNotFoundException {
        if (params.length != 2) {
            System.out.println("São necessários 2 parâmetros: nome do evento e caminho do arquivo.");
            return;
        }

        String eventName = (String) params[0];
        String filePath = (String) params[1];
        StringBuilder fatherId = new StringBuilder();
        boolean eventFound = getFatherEventId(eventName, fatherId);
        if (eventFound) {
            Persistence article = new SubmitArticle();
            article.create(eventName, filePath);
        } else {
            System.out.println("Evento não encontrado.");
        }
    }

    @Override
    public void delete(Object... params) {
        if (params.length != 1) {
            System.out.println("São necessários 1 parâmetro: nome do arquivo.");
            return;
        }

        String fileName = (String) params[0];

        Persistence article = new SubmitArticle();
        article.delete(fileName);
    }

    @Override
    public boolean list(String idowner) {
        return false;
    }

    @Override
    public void show(Object... params) {
        // Método não implementado
    }

    @Override
    public void update(Object... params) {
        if (params.length != 2) {
            System.out.println("São necessários 2 parâmetros: nome do artigo e caminho do novo arquivo.");
            return;
        }

        String articleName = (String) params[0];
        String newFilePath = (String) params[1];

        Persistence article = new SubmitArticle();
        article.update(articleName, newFilePath);
    }

    @Override
    public void read() {
        // Método read padrão vazio
    }

    public void read(Object... params) {
        if (params.length != 1) {
            System.out.println("É necessário 1 parâmetro: nome do evento.");
            return;
        }

        String eventName = (String) params[0];
        Persistence articlePersistence = new SubmitArticle();
        this.articleHashMap = articlePersistence.read(eventName);
        if (this.articleHashMap.isEmpty()) {
            System.out.println("Nenhum artigo encontrado para o evento: " + eventName);
        } else {
            System.out.println("Artigos encontrados para o evento: " + eventName);
            for (String articleName : this.articleHashMap.keySet()) {
                System.out.println(articleName);
            }
        }
    }

    @Override
    public boolean loginValidate(String email, String cpf) {
        return false;
    }

    private boolean getFatherEventId(String eventName, StringBuilder fatherId) throws FileNotFoundException {
        EventController ec = new EventController();
        HashMap<String, Persistence> list = ec.getEventHashMap();
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
