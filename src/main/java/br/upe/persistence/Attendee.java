package br.upe.persistence;

import java.io.*;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class Attendee implements Persistence{
    private String id;
    private String userId;
    private String name;
    private String sessionId;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public void create(Object... params) {
        if (params.length < 2) {
            System.out.println("Erro: Parâmetros insuficientes.");
            return;
        }

        this.userId = (String) params[0];
        this.name = (String) params[1];
        this.sessionId = (String) params[2];
        this.id = generateId();
        String line = id + ";" + userId + ";" + name + ";" + sessionId;

        try {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("./db/attendee.csv", true))) {
                writer.write(line);
                writer.newLine();
            }

            System.out.println("Cadastro Realizado");
        } catch (IOException writerEx) {
            System.out.println("Erro na escrita do arquivo");
            writerEx.printStackTrace();
        }
    }


    @Override
    public void delete(Object... params) {
        if (params.length > 1) {
            System.out.println("Só pode ter 1 parametro");
        }

        HashMap<String, Persistence> attendeeHashMap = (HashMap<String, Persistence>) params[0];

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("./db/attendee.csv"))) {
            for (Map.Entry<String, Persistence> entry : attendeeHashMap.entrySet()) {
                Persistence attendee = entry.getValue();
                String line = attendee.getData("id") + ";" + attendee.getData("userId") + ";" + attendee.getData("name") + ";" + attendee.getData("sessionId") + "\n";
                writer.write(line);
            }
            writer.close();
            System.out.println("Inscrição Removida");
        } catch (IOException writerEx) {
            System.out.println("Erro na escrita do arquivo");
            writerEx.printStackTrace();
        }
    }

    @Override
    public void update(Object... params) {
        if (params.length > 1) {
            System.out.println("Só pode ter 1 parametro");
        }

        HashMap<String, Persistence> attendeeHashMap = (HashMap<String, Persistence>) params[0];

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("./db/attendee.csv"))) {
            for (Map.Entry<String, Persistence> entry : attendeeHashMap.entrySet()) {
                Persistence attendee = entry.getValue();
                String line = attendee.getData("id") + ";" + attendee.getData("userId") + ";" + attendee.getData("name") + ";" + attendee.getData("sessionId") + "\n";
                writer.write(line);
            }
            writer.close();
            System.out.println("Nome Atualizado");
        } catch (IOException writerEx) {
            System.out.println("Erro na escrita do arquivo");
            writerEx.printStackTrace();
        }
    }

    @Override
    public String getData(String dataToGet) {
        String data = "";
        try {
            switch (dataToGet) {
                case "name" -> data = this.getName();
                case "sessionId" -> data = this.getSessionId();
                case "userId" -> data = this.getUserId();
                case "id" -> data = this.getId();
                default -> throw new IOException();
            }
        } catch (IOException e) {
            System.out.println("Informação não existe ou é restrita");
        }
        return data;
    }


    @Override
    public void setData(String dataToSet, String data) {
        try {
            switch (dataToSet) {
                case "name" -> this.setName(data);
                case "sessionId" -> this.setSessionId(data);
                case "id" -> this.setId(data);
                case "userId" -> this.setUserId(data);
                default -> throw new IOException();
            }
        } catch (IOException e) {
            System.out.println("Informação não existe ou é restrita");
        }
    }

    private String generateId() {
        SecureRandom secureRandom = new SecureRandom();
        long timestamp = Instant.now().toEpochMilli();
        int lastThreeDigitsOfTimestamp = (int) (timestamp % 1000);
        int randomValue = secureRandom.nextInt(900) + 100;
        return String.format("%03d%03d", lastThreeDigitsOfTimestamp, randomValue);
    }

    @Override
    public HashMap<String, Persistence> read() {
        HashMap<String, Persistence> list = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("./db/attendee.csv"));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 4) {
                    String id = parts[0].trim();
                    String userId = parts[1].trim();
                    String name = parts[2].trim();
                    String sessionId = parts[3].trim();

                    Attendee attendee = new Attendee();
                    attendee.setName(name);
                    attendee.setSessionId(sessionId);
                    attendee.setId(id);
                    attendee.setUserId(userId);
                    list.put(attendee.getId(), attendee);
                }
            }
            reader.close();

        } catch (IOException readerEx) {
            System.out.println("Erro ao ler o arquivo");
            readerEx.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public HashMap<String, Persistence> read(Object... params) {
        return null;
    }
}
