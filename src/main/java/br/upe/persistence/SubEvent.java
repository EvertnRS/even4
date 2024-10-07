package br.upe.persistence;

import java.io.*;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class SubEvent extends Event implements Persistence{
    private String eventId;
    private String id;
    private String name;
    private String date;
    private String description;
    private String location;
    private String ownerId;

    @Override
    public String getData(String dataToGet){
        String data = "";
        try {
            switch (dataToGet) {
                case "id" -> data = this.getId();
                case "name" -> data = this.getName();
                case "date" -> data = this.getDate();
                case "description" -> data = this.getDescription();
                case "location" -> data = this.getLocation();
                case "eventId" -> data = this.getEventId();
                case "ownerId" -> data = this.getOwnerId();
                default -> throw new IOException();
            }
        } catch (IOException e) {
            System.out.println("Informação não existe ou é restrita");
        }
        return data;
    }

    @Override
    public void setData(String dataToSet, String data){
        try {
            switch (dataToSet) {
                case "id" -> this.setId(data);
                case "name" -> this.setName(data);
                case "description" -> this.setDescription(data);
                case "date" -> this.setDate(data);
                case "location" -> this.setLocation(data);
                case "eventId" -> this.setEventId(data);
                case "ownerId" -> this.setOwnerId(data);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public void create(Object... params) {
        if (params.length < 6) {
            System.out.println("Só pode ter 6 parametros");
        }

        String eventId = (String) params[0];
        String id = generateId();
        String name = (String) params[1];
        String date = (String) params[2];
        String description = (String) params[3];
        String location = (String) params[4];
        String ownerId = (String) params[5];
        String line = id + ";" + name + ";" + date + ";" + description + ";" + location + ";" + eventId + ";" + ownerId;

        try {
            File file = new File("./db/subEvents.csv");
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter("./db/subEvents.csv", true))) {
                writer.write(line);
            }

            System.out.println("SubEvento Criado");
        } catch (IOException writerEx) {
            System.out.println("Erro na escrita do arquivo");
            writerEx.printStackTrace();
        }
    }
    @Override
    public HashMap<String, Persistence>  read() {
        HashMap<String, Persistence> list = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("./db/subEvents.csv"));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 7) {
                    String id = parts[0].trim();
                    String name = parts[1].trim();
                    String date = parts[2].trim();
                    String description = parts[3].trim();
                    String location = parts[4].trim();
                    String eventId = parts[5].trim();
                    String ownerId = parts[6].trim();

                    SubEvent subEvent = new SubEvent();
                    subEvent.setId(id);
                    subEvent.setName(name);
                    subEvent.setDate(date);
                    subEvent.setDescription(description);
                    subEvent.setLocation(location);
                    subEvent.setEventId(eventId);
                    subEvent.setOwnerId(ownerId);
                    list.put(subEvent.getId(), subEvent);
                }
            }
            reader.close();

        } catch (IOException readerEx) {
            System.out.println("Erro ao ler o arquivo");
            readerEx.printStackTrace();
        }
        return list;
    }

    @Override
    public HashMap<String, Persistence> read(Object... params) {
        return null;
    }

    public void update(Object... params) {
        if (params.length > 1) {
            System.out.println("Só pode ter 1 parametro");
        }

        HashMap<String, Persistence> subEventHashMap = (HashMap<String, Persistence>) params[0];

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("./db/subEvents.csv"))) {
            for (Map.Entry<String, Persistence> entry : subEventHashMap.entrySet()) {
                Persistence subEvent = entry.getValue();
                String line = subEvent.getData("id") + ";" + subEvent.getData("name") + ";" + subEvent.getData("date") + ";" + subEvent.getData("description") + ";" + subEvent.getData("location") + ";" + subEvent.getData("eventId") + ";" + subEvent.getData("ownerId") + "\n";
                writer.write(line);
            }
            writer.close();
            System.out.println("SubEvento Atualizado");
        } catch (IOException writerEx) {
            System.out.println("Erro na escrita do arquivo");
            writerEx.printStackTrace();
        }
    }

    public void delete(Object... params) {
        if (params.length > 1) {
            System.out.println("Só pode ter 1 parametro");
        }

        HashMap<String, Persistence> subEventHashMap = (HashMap<String, Persistence>) params[0];

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("./db/subEvents.csv"))) {
            for (Map.Entry<String, Persistence> entry : subEventHashMap.entrySet()) {
                Persistence subEvent = entry.getValue();
                String line = subEvent.getData("id") + ";" + subEvent.getData("name") + ";" + subEvent.getData("date") + ";" + subEvent.getData("description") + ";" + subEvent.getData("location") + ";" + subEvent.getData("eventId") + ";" + subEvent.getData("ownerId") + "\n";
                writer.write(line);
            }
            writer.close();
            System.out.println("SubEvento Removido");
        } catch (IOException writerEx) {
            System.out.println("Erro na escrita do arquivo");
            writerEx.printStackTrace();
        }
    }
}