package br.upe.persistence;

import java.io.*;
import java.util.HashMap;

public class SubmitArticle implements Persistence {
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
        switch (dataToSet) {
            case "name":
                this.name = data;
                break;
            case "path":
                this.path = data;
                break;
        }
    }

    @Override
    public HashMap<String, Persistence> read() {
        return null;
    }

    public HashMap<String, Persistence> read(Object... params) {
        HashMap<String, Persistence> articles = new HashMap<>();
        if (params.length != 1) {
            System.out.println("É necessário 1 parâmetro: nome do evento.");
            return articles;
        }

        String eventName = (String) params[0];
        String eventFolderPath = ".\\even4\\db\\Articles\\" + eventName;

        File eventFolder = new File(eventFolderPath);
        if (!eventFolder.exists()) {
            System.out.println("Pasta do evento não encontrada: " + eventFolderPath);
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

    }

    @Override
    public void create(Object... params) {
        if (params.length != 2) {
            System.out.println("São necessários 2 parâmetros: nome do evento e caminho do arquivo.");
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
                System.out.println("Pasta do evento criada com sucesso: " + eventFolderPath);
            } else {
                System.out.println("Erro ao criar a pasta do evento: " + eventFolderPath);
                return;
            }
        }
        if (!fileToMove.exists()) {
            System.out.println("Arquivo a ser movido não existe: " + filePath);
            return;
        }

        try {
            if (fileToMove.renameTo(destinationFile)) {
                System.out.println("Arquivo movido com sucesso para: " + destinationFile.getAbsolutePath());
            } else {
                System.out.println("Erro ao mover o arquivo.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao mover o arquivo: " + e.getMessage());
        }
    }

    @Override
    public void update(Object... params) {
        if (params.length != 2) {
            System.out.println("São necessários 2 parâmetros: nome do artigo e caminho do novo arquivo.");
            return;
        }

        String articleName = (String) params[0];
        String newFilePath = (String) params[1];

        File newFile = new File(newFilePath);
        if (!newFile.exists()) {
            System.out.println("Novo arquivo não existe: " + newFilePath);
            return;
        }

        File directory = new File(".\\even4\\db\\Articles");

        File[] eventFolders = directory.listFiles(File::isDirectory);
        if (eventFolders != null) {
            for (File eventFolder : eventFolders) {
                File oldFile = new File(eventFolder, articleName);
                if (oldFile.exists()) {
                    boolean deleted = oldFile.delete();
                    if (deleted) {
                        System.out.println("Arquivo antigo deletado com sucesso: " + oldFile.getAbsolutePath());
                    } else {
                        System.out.println("Erro ao deletar o arquivo: " + oldFile.getAbsolutePath());
                        return;
                    }
                }
            }
        }

        System.out.println("Arquivo não encontrado.");
    }

    @Override
    public void delete(Object... params) {
        if (params.length != 1) {
            System.out.println("São necessários 1 parâmetro: nome do arquivo.");
            return;
        }

        String fileName = (String) params[0];
        File directory = new File(".\\even4\\db\\Articles");

        File[] eventFolders = directory.listFiles(File::isDirectory);
        if (eventFolders != null) {
            for (File eventFolder : eventFolders) {
                File fileToDelete = new File(eventFolder, fileName);
                if (fileToDelete.exists()) {
                    if (fileToDelete.delete()) {
                        System.out.println("Arquivo deletado com sucesso.");
                    } else {
                        System.out.println("Erro ao deletar o arquivo.");
                    }
                    return;
                }
            }
        }

        System.out.println("Arquivo não encontrado.");
    }
}
