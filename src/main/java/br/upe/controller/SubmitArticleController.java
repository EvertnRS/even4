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

    public void addArticle(SubmitArticle article) {
        // Aqui estamos garantindo que o nome salvo será apenas "art.txt"
        String articleName = new File(article.getData("name")).getName(); // Nome simples, sem prefixos ou caminho
        String uniqueKey = article.getData("ownerId") + "_" + articleName; // Usa o nome simples no HashMap
        articleHashMap.put(uniqueKey, article);
        LOGGER.info("Artigo adicionado: " + uniqueKey); // Verifica o nome salvo no HashMap
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
        // metodo não ultilizado
    }


    @Override
    public void delete(Object... params) throws IOException {
        if (params.length != 1) {
            LOGGER.warning("São necessários 1 parâmetro: nome do arquivo.");
            return;
        }

        // Obtém o nome simples do arquivo
        String fileName = new File((String) params[0]).getName();
        Persistence article = articleHashMap.get(fileName);

        if (article != null) {
            article.delete(fileName); // Exclui o arquivo pelo nome simples
            articleHashMap.remove(fileName); // Remove do HashMap
            LOGGER.info("Artigo excluído: " + fileName);
        } else {
            LOGGER.warning("Artigo não encontrado: " + fileName);
        }
    }





    public boolean list(String userId) {
        LOGGER.info("Listando artigos para o usuário: %s".formatted(userId));
        articleHashMap.clear(); // Limpa o mapa de artigos antes de carregar novos
        // Chama o método read apenas com o userId para buscar todos os artigos
        read(userId); // O método read agora carrega todos os artigos associados ao userId
        LOGGER.info("Artigos disponíveis no HashMap: " + articleHashMap.keySet());
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

    public void create(String eventName, String filePath, String userId) throws IOException {
        boolean eventFound = getFatherEventId(eventName);
        if (eventFound) {
            SubmitArticle article = new SubmitArticle("", filePath, userId);
            article.create(eventName, filePath, userId);
            addArticle(article); // Aqui você deve garantir que a chave é criada corretamente.
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