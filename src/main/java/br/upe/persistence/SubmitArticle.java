package br.upe.persistence;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.logging.Logger;

public class SubmitArticle implements Persistence {
    private static final Logger LOGGER = Logger.getLogger(SubmitArticle.class.getName());
    private String name;
    private String path;
    private String event;

    @Override
    public String getData(String dataToGet) {
        return switch (dataToGet) {
            case "name" -> this.name;
            case "path" -> this.path;
            case "event" -> this.event;
            default -> "";
        };
    }

    public void setData(String key, String value) {
        switch (key) {
            case "name":
                this.name = value;
                break;
            case "path":
                this.path = value;
                break;
            case "event":
                this.event = value;
                break;
            default:
                throw new IllegalArgumentException("Invalid dataToSet: " + key);
        }
    }


    @Override
    public HashMap<String, Persistence> read() {
        return new HashMap<>();
    }

    public HashMap<String, Persistence> read(Object... params) {
        HashMap<String, Persistence> userArticles = new HashMap<>();


        if (params.length != 1) {
            LOGGER.warning("É necessário 1 parâmetro: ID do usuário.");
            return userArticles;
        }

        String userId = (String) params[0];
        String userHome = System.getProperty("user.home");
        String articlesPath = userHome + "\\even4\\db\\Articles";

        File articlesFolder = new File(articlesPath);


        if (!articlesFolder.exists() || !articlesFolder.isDirectory()) {
            LOGGER.warning("Pasta Articles não encontrada: %s".formatted(articlesPath));
            return userArticles;
        }


        File[] eventFolders = articlesFolder.listFiles(File::isDirectory);
        if (eventFolders != null) {
            for (File eventFolder : eventFolders) {

                File[] articleFiles = eventFolder.listFiles();
                if (articleFiles != null) {
                    for (File articleFile : articleFiles) {

                        String fileName = articleFile.getName();
                        if (fileName.endsWith("_" + userId)) {

                            SubmitArticle article = new SubmitArticle();
                            article.setData("name", fileName);
                            article.setData("path", articleFile.getAbsolutePath());
                            article.setData("event", eventFolder.getName());

                            userArticles.put(fileName, article);
                        }
                    }
                }
            }
        }

        return userArticles;
    }


    @Override
    public String getName() {
        return "";
    }

    @Override
    public void setName(String email) {
        throw new UnsupportedOperationException("setName operation is not supported.");
    }

    @Override
    public void create(Object... params) {
        if (params.length != 3) {
            LOGGER.warning("São necessários 2 parâmetros: nome do evento e caminho do arquivo.");
            return;
        }

        String eventName = (String) params[0];
        String filePath = (String) params[1];
        String id = (String) params[2];
        String userHome = System.getProperty("user.home");


        String eventFolderPath = userHome + "\\even4\\db\\Articles\\" + eventName;
        File eventFolder = new File(eventFolderPath);
        File fileToMove = new File(filePath);

        File destinationFile = new File(eventFolder, fileToMove.getName() + "_" + id);
        if (!eventFolder.exists()) {
            if (eventFolder.mkdirs()) {
                LOGGER.warning("Pasta do evento criada com sucesso: %s".formatted(eventFolderPath));
            } else {
                LOGGER.warning("Erro ao criar a pasta do evento: %s".formatted(eventFolderPath));
                return;
            }
        }
        if (!fileToMove.exists()) {
            LOGGER.warning("Arquivo a ser movido não existe: %s".formatted(filePath));
            return;
        }

        try {
            if (fileToMove.renameTo(destinationFile)) {
                LOGGER.warning("Arquivo movido com sucesso para: " + destinationFile.getAbsolutePath());
            } else {
                LOGGER.warning("Erro ao mover o arquivo.");
            }
        } catch (Exception e) {
            LOGGER.warning("Erro ao mover o arquivo: " + e.getMessage());
        }
    }

    @Override
    public void update(Object... params) {
        if (params.length != 3) {
            LOGGER.warning("São necessários 3 parâmetros: nome do novo evento, nome do evento antigo e nome do artigo.");
            return;
        }

        String newEventName = (String) params[0];  // Nome do novo evento
        String oldEventName = (String) params[1];  // Nome do evento antigo
        String articleName = (String) params[2];   // Nome do artigo

        String userHome = System.getProperty("user.home");

        // Diretórios dos eventos antigo e novo
        File oldEventFolder = new File(userHome + "\\even4\\db\\Articles\\" + oldEventName);
        File newEventFolder = new File(userHome + "\\even4\\db\\Articles\\" + newEventName);


        if (!oldEventFolder.exists()) {
            LOGGER.warning("Evento antigo não encontrado: %s".formatted(oldEventName));
            return;
        }


        File oldFile = new File(oldEventFolder, articleName);
        if (!oldFile.exists()) {
            LOGGER.warning("Artigo não encontrado no evento antigo: %s".formatted(articleName));
            return;
        }


        if (!newEventFolder.exists()) {
            if (newEventFolder.mkdirs()) {
                LOGGER.warning("Pasta do novo evento criada: %s".formatted(newEventFolder.getAbsolutePath()));
            } else {
                LOGGER.warning("Erro ao criar a pasta do novo evento: %s".formatted(newEventFolder.getAbsolutePath()));
                return;
            }
        }


        File destinationFile = new File(newEventFolder, articleName);
        try {
            if (oldFile.renameTo(destinationFile)) {
                LOGGER.warning("Artigo movido com sucesso para: %s".formatted(destinationFile.getAbsolutePath()));
            } else {
                LOGGER.warning("Erro ao mover o artigo.");
            }
        } catch (Exception e) {
            LOGGER.warning("Erro ao mover o artigo: " + e.getMessage());
        }
    }


    @Override
    public void delete(Object... params) {
        if (params.length != 1) {
            LOGGER.warning("São necessários 1 parâmetro: nome do arquivo.");
            return;
        }

        String fileName = (String) params[0];
        String userHome = System.getProperty("user.home");
        File directory = new File(userHome + "\\even4\\db\\Articles");

        File[] eventFolders = directory.listFiles(File::isDirectory);
        if (eventFolders != null) {
            for (File eventFolder : eventFolders) {
                File fileToDelete = new File(eventFolder, fileName);
                if (fileToDelete.exists()) {
                    Path filePath = fileToDelete.toPath();
                    try {
                        Files.delete(filePath);
                        LOGGER.warning("Arquivo deletado com sucesso.");
                    } catch (IOException e) {
                        LOGGER.warning("Erro ao deletar o arquivo: %s".formatted(e.getMessage()));
                    }
                    return;
                }
            }
        }

        LOGGER.warning("Arquivo não encontrado.");
    }
}
