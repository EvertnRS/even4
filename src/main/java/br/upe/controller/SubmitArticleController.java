package br.upe.controller;

import br.upe.persistence.SubmitArticle;
import br.upe.persistence.Persistence;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class SubmitArticleController implements Controller {
    private Map<String, Persistence> articleHashMap = new HashMap<>();
    private static final Logger LOGGER = Logger.getLogger(SubmitArticleController.class.getName());

    // Adição do método addArticle
    public void addArticle(SubmitArticle article) {
        articleHashMap.put(article.getData("id"), article);
    }

    @Override
    public void create(Object... params) throws IOException {
        // Método criado anteriormente
    }

    @Override
    public void update(Object... params) throws IOException {
        // Método atualizado abaixo
        if (params.length != 2) {
            LOGGER.warning("São necessários 2 parâmetros: nome do artigo e caminho do novo arquivo.");
            return;
        }

        String articleName = (String) params[0];
        String newFilePath = (String) params[1];

        Persistence article = articleHashMap.get(articleName);
        if (article != null) {
            article.update(articleName, newFilePath);
        } else {
            LOGGER.warning("Artigo não encontrado: " + articleName);
        }
    }

    @Override
    public void read() throws IOException {

    }



    @Override
    public void delete(Object... params) throws IOException {
        // Método atualizado abaixo
        if (params.length != 1) {
            LOGGER.warning("São necessários 1 parâmetro: nome do arquivo.");
            return;
        }

        String fileName = (String) params[0];
        Persistence article = articleHashMap.get(fileName);
        if (article != null) {
            article.delete(fileName);
        } else {
            LOGGER.warning("Artigo não encontrado: " + fileName);
        }
    }

    public boolean list(String userId) {
        LOGGER.info("Listando artigos para o usuário: %s".formatted(userId));
        articleHashMap.clear(); // Limpa o mapa de artigos antes de carregar novos
        // Chama o método read apenas com o userId para buscar todos os artigos
        read(userId); // O método read agora carrega todos os artigos associados ao userId

        // Verifica se existem artigos carregados
        if (articleHashMap.isEmpty()) {
            LOGGER.warning("Nenhum artigo encontrado para o usuário: %s".formatted(userId));
        } else {
            LOGGER.info("Artigos encontrados: %d".formatted(articleHashMap.size()));
        }

        return !articleHashMap.isEmpty(); // Retorna true se houver artigos
    }



    @Override
    public void show(Object... params) throws IOException {
        // Método criado anteriormente
    }

    @Override
    public boolean loginValidate(String email, String cpf) {
        return false;
    }

    public Map<String, Persistence> getArticleHashMap() {
        return articleHashMap;
    }

    public void setArticleHashMap(Map<String, Persistence> articleHashMap) {
        this.articleHashMap = articleHashMap;
    }

    // Método para obter dados do artigo
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

    // Atualização do método create para incluir criação de artigos
    public void create(String eventName, String filePath, String userId) throws IOException {
        boolean eventFound = getFatherEventId(eventName);
        if (eventFound) {
            SubmitArticle article = new SubmitArticle("", filePath, "ownerId");
            article.create(eventName, filePath, userId);
            addArticle(article);
        } else {
            LOGGER.warning("Evento não encontrado.");
        }
    }


    public void read(String userId) {
        // Agora apenas usa o userId para buscar os artigos em todos os eventos
        LOGGER.info("Iniciando leitura de artigos para o usuário: %s".formatted(userId));

        // Lê os artigos para todos os eventos usando o SubmitArticle
        SubmitArticle articlePersistence = new SubmitArticle("", "", userId); // Cria instância com userId
        this.articleHashMap = articlePersistence.read(userId); // Chama o método read passando apenas o userId

        if (articleHashMap.isEmpty()) {
            LOGGER.warning("Nenhum artigo encontrado para o usuário: %s".formatted(userId));
        } else {
            LOGGER.info("Artigos encontrados: %d".formatted(articleHashMap.size()));
        }
    }




    // Validação do evento
    private boolean getFatherEventId(String eventName) throws IOException {
        EventController ec = new EventController();
        Map<String, Persistence> list = ec.getEventHashMap();
        for (Map.Entry<String, Persistence> entry : list.entrySet()) {
            Persistence listindice = entry.getValue();
            if (listindice.getData("name").equals(eventName)) {
                return true;
            }
        }
        return false;
    }
}