package br.upe.persistence;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class SubmitArticle implements Persistence {
    private static final Logger LOGGER = Logger.getLogger(SubmitArticle.class.getName());
    private Map<String, String> data;

    // Construtor atualizado com id, name, ownerId
    public SubmitArticle(String id, String name, String ownerId) {
        data = new HashMap<>();
        data.put("id", id);
        data.put("name", name);
        data.put("ownerId", ownerId);
    }

    // Métodos de persistência utilizando o mapa 'data'
    @Override
    public String getData(String key) {
        return data.get(key);
    }

    @Override
    public void setData(String key, String value) {
        data.put(key, value);
    }

    @Override
    public HashMap<String, Persistence> read() {
        return new HashMap<>();
    }

    @Override
    public HashMap<String, Persistence> read(Object... params) {
        HashMap<String, Persistence> articles = new HashMap<>();

        // Verifica se o parâmetro userId foi passado
        if (params.length != 1) {
            LOGGER.warning("São necessários 1 parâmetro: ID do usuário.");
            return articles;
        }

        String userId = (String) params[0];
        String userHome = System.getProperty("user.home");
        File directory = new File(userHome + "\\even4\\db\\Articles");

        // Verifica se a pasta base existe
        if (!directory.exists() || !directory.isDirectory()) {
            LOGGER.warning("Pasta de artigos não encontrada: %s".formatted(directory.getAbsolutePath()));
            return articles;
        }

        // Listar todas as pastas de eventos
        File[] eventFolders = directory.listFiles(File::isDirectory);
        if (eventFolders != null) {
            for (File eventFolder : eventFolders) {
                // Listar os arquivos de artigo dentro de cada pasta de evento
                File[] articleFiles = eventFolder.listFiles();
                if (articleFiles != null) {
                    for (File articleFile : articleFiles) {
                        if (articleFile.isFile() && articleFile.getName().startsWith(userId + "_")) {
                            // Verifica se o arquivo pertence ao usuário pelo ID no nome do arquivo
                            String articleName = articleFile.getName().substring(userId.length() + 1); // Remove o userId do nome
                            SubmitArticle article = new SubmitArticle(articleName, articleFile.getAbsolutePath(), userId);
                            articles.put(articleName, article);
                        }
                    }
                } else {
                    LOGGER.warning("Nenhum arquivo encontrado na pasta do evento: %s".formatted(eventFolder.getName()));
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
        if (params.length != 3) {
            LOGGER.warning("São necessários 3 parâmetros: nome do evento, caminho do arquivo e ID do usuário.");
            return;
        }

        String eventName = (String) params[0];
        String filePath = (String) params[1];
        String userId = (String) params[2];

        String userHome = System.getProperty("user.home");
        String eventFolderPath = userHome + "\\even4\\db\\Articles\\" + eventName;

        File eventFolder = new File(eventFolderPath);
        File fileToMove = new File(filePath);
        File destinationFile = new File(eventFolder, userId + "_" + fileToMove.getName()); // Nomeia o arquivo com base no ID do usuário

        if (!eventFolder.exists()) {
            if (eventFolder.mkdirs()) {
                LOGGER.info("Pasta do evento criada com sucesso: %s".formatted(eventFolderPath));
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
                LOGGER.info("Arquivo movido com sucesso para: " + destinationFile.getAbsolutePath());
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
            LOGGER.warning("São necessários 3 parâmetros: nome do artigo, caminho do novo arquivo e ID do usuário.");
            return;
        }

        String articleName = (String) params[0];
        String newFilePath = (String) params[1];
        String userId = (String) params[2];

        File newFile = new File(newFilePath);
        if (!newFile.exists()) {
            LOGGER.warning("Novo arquivo não existe: %s".formatted(newFilePath));
            return;
        }

        String userHome = System.getProperty("user.home");
        File directory = new File(userHome + "\\even4\\db\\Articles");

        File[] eventFolders = directory.listFiles(File::isDirectory);
        if (eventFolders != null) {
            for (File eventFolder : eventFolders) {
                File oldFile = new File(eventFolder, userId + "_" + articleName);
                if (oldFile.exists()) {
                    Path filePath = oldFile.toPath();
                    try {
                        Files.delete(filePath);
                        LOGGER.info("Arquivo antigo deletado com sucesso: %s".formatted(filePath.toAbsolutePath()));
                    } catch (IOException e) {
                        LOGGER.warning("Erro ao deletar o arquivo: %s - %s".formatted(filePath.toAbsolutePath(), e.getMessage()));
                    }
                }
            }
        }

        // Move o novo arquivo para o diretório apropriado
        create(params[0], newFilePath, userId); // Reutiliza o método create para mover o novo arquivo
    }


    @Override
    public void delete(Object... params) {
        if (params.length != 2) {
            LOGGER.warning("São necessários 2 parâmetros: nome do arquivo e ID do usuário.");
            return;
        }

        String fileName = (String) params[0];
        String userId = (String) params[1];

        String userHome = System.getProperty("user.home");
        File directory = new File(userHome + "\\even4\\db\\Articles");

        File[] eventFolders = directory.listFiles(File::isDirectory);
        if (eventFolders != null) {
            for (File eventFolder : eventFolders) {
                File fileToDelete = new File(eventFolder, userId + "_" + fileName);
                if (fileToDelete.exists()) {
                    Path filePath = fileToDelete.toPath();
                    try {
                        Files.delete(filePath);
                        LOGGER.info("Arquivo deletado com sucesso.");
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