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

    @Override
    public String getData(String dataToGet) {
        switch (dataToGet) {
            case "name":
                return this.name;
            case "path":
                return this.path;
            default:
                return "";
        }
    }

    @Override
    public void setData(String dataToSet, String data) {
        if ("name".equals(dataToSet)) {
            this.name = data;
        } else if ("path".equals(dataToSet)) {
            this.path = data;
        } else {
            throw new IllegalArgumentException("Invalid dataToSet: " + dataToSet);
        }
    }

    @Override
    public HashMap<String, Persistence> read() {
        return new HashMap<>();
    }

    public HashMap<String, Persistence> read(Object... params) {
        HashMap<String, Persistence> articles = new HashMap<>();
        if (params.length != 1) {
            LOGGER.warning("É necessário 1 parâmetro: nome do evento.");
            return articles;
        }

        String eventName = (String) params[0];
        String eventFolderPath = ".\\even4\\db\\Articles\\" + eventName;

        File eventFolder = new File(eventFolderPath);
        if (!eventFolder.exists()) {
            LOGGER.warning("Pasta do evento não encontrada: %s".formatted(eventFolderPath));
            return articles;
        }

        File[] articleFiles = eventFolder.listFiles();
        if (articleFiles != null) {
            for (File articleFile : articleFiles) {
                if (articleFile.isFile()) {
                    SubmitArticle article = new SubmitArticle();
                    article.setData("name", articleFile.getName());
                    article.setData("path", articleFile.getAbsolutePath());
                    articles.put(articleFile.getName(), article);
                }
            }
        }
        return articles;
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
        if (params.length != 2) {
            LOGGER.warning("São necessários 2 parâmetros: nome do evento e caminho do arquivo.");
            return;
        }

        String eventName = (String) params[0];
        String filePath = (String) params[1];

        String eventFolderPath = ".\\even4\\db\\Articles\\" + eventName;
        File eventFolder = new File(eventFolderPath);
        File fileToMove = new File(filePath);

        File destinationFile = new File(eventFolder, fileToMove.getName());
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
        if (params.length != 2) {
            LOGGER.warning("São necessários 2 parâmetros: nome do artigo e caminho do novo arquivo.");
            return;
        }

        String articleName = (String) params[0];
        String newFilePath = (String) params[1];

        File newFile = new File(newFilePath);
        if (!newFile.exists()) {
            LOGGER.warning("Novo arquivo não existe: %s".formatted(newFilePath));
            return;
        }

        File directory = new File(".\\even4\\db\\Articles");

        File[] eventFolders = directory.listFiles(File::isDirectory);
        if (eventFolders != null) {
            for (File eventFolder : eventFolders) {
                File oldFile = new File(eventFolder, articleName);
                if (oldFile.exists()) {
                    Path filePath = oldFile.toPath();
                    try {
                        Files.delete(filePath);
                        LOGGER.warning("Arquivo antigo deletado com sucesso: %s".formatted(filePath.toAbsolutePath()));
                    } catch (IOException e) {
                        LOGGER.warning("Erro ao deletar o arquivo: %s - %s".formatted(filePath.toAbsolutePath(), e.getMessage()));
                    }
                }
            }
        }

        LOGGER.warning("Arquivo não encontrado.");
    }

    @Override
    public void delete(Object... params) {
        if (params.length != 1) {
            LOGGER.warning("São necessários 1 parâmetro: nome do arquivo.");
            return;
        }

        String fileName = (String) params[0];
        File directory = new File(".\\even4\\db\\Articles");

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
