package br.upe.persistence;

import br.upe.controller.EventController;

import java.io.*;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Session implements Persistence {
    private String id;
    private String name;
    private String date;
    private String description;
    private String location;
    private String startTime;
    private String endTime;
    private String eventId;
    private String ownerId;

    @Override
    public String getData(String dataToGet) {
        String data = "";
        try {
            switch (dataToGet) {
                case "id" -> data = this.getId();
                case "name" -> data = this.getName();
                case "date" -> data = this.getDate();
                case "description" -> data = this.getDescription();
                case "location" -> data = this.getLocation();
                case "startTime" -> data = this.getStartTime();
                case "endTime" -> data = this.getEndTime();
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
    public void setData(String dataToSet, String data) {
        try {
            switch (dataToSet) {
                case "id" -> this.setId(data);
                case "name" -> this.setName(data);
                case "description" -> this.setDescription(data);
                case "date" -> this.setDate(data);
                case "location" -> this.setLocation(data);
                case "startTime" -> this.setStartTime(data);
                case "endTime" -> this.setEndTime(data);
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
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
        if (params.length < 9) {
            System.out.println("Só pode ter 9 parâmetros");
            return; // Adicionando return para sair da função se o número de parâmetros estiver incorreto
        }

        String eventId = (String) params[0];
        String id = generateId();
        String name = (String) params[1];
        String date = (String) params[2];
        String description = (String) params[3];
        String location = (String) params[4];
        String startTime = (String) params[5];
        String endTime = (String) params[6];
        String ownerId = (String) params[7];
        HashMap<String, Persistence> eventH = (HashMap<String, Persistence>) params[8];
        String line = id + ";" + name + ";" + date + ";" + description + ";" + location + ";" + startTime + ";" + endTime + ";" + eventId + ";" + ownerId;

        try {
            File file = new File("./db/sessions.csv");
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter("./db/sessions.csv", true))) {
                writer.write(line);
                writer.newLine();
            }

            Event event = null;
            for (Map.Entry<String, Persistence> entry : eventH.entrySet()) {
                Persistence eventPersistence = entry.getValue();
                if (eventPersistence.getData("id").equals(eventId)) {
                    event = (Event) eventPersistence;
                    break;
                }
            }

            if (event == null) {
                System.out.println("Evento não encontrado.");
                return;
            }

            ArrayList<Persistence> sessionList = event.getSessionsList();
            if (sessionList == null) {
                sessionList = new ArrayList<>(); // Inicialize a lista se estiver nula
            }

            Session session = new Session();
            System.out.println("IdEvent " + event.getId());
            session.setId(id);
            session.setName(name);
            session.setDate(date);
            session.setDescription(description);
            session.setLocation(location);
            session.setStartTime(startTime);
            session.setEndTime(endTime);
            session.setOwnerId(ownerId);
            sessionList.add(session);
            System.out.println("Sessão Criada: " + session.getId());

            // Se necessário, atualizar o evento com a nova lista de sessões
            event.setSessionsList(sessionList);

            eventH.put(eventId, event);

            System.out.println("Sessões atuais: " + sessionList.size());

        } catch (IOException writerEx) {
            System.out.println("Erro na escrita do arquivo");
            writerEx.printStackTrace();
        }
    }


    @Override
    public HashMap<String, Persistence> read() {
        HashMap<String, Persistence> list = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("./db/sessions.csv"));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 9) {
                    String id = parts[0].trim();
                    String name = parts[1].trim();
                    String date = parts[2].trim();
                    String description = parts[3].trim();
                    String location = parts[4].trim();
                    String startTime = parts[5].trim();
                    String endTime = parts[6].trim();
                    String eventId = parts[7].trim();
                    String ownerId = parts[8].trim();

                    Session session = new Session();
                    session.setId(id);
                    session.setName(name);
                    session.setDate(date);
                    session.setDescription(description);
                    session.setLocation(location);
                    session.setStartTime(startTime);
                    session.setEndTime(endTime);
                    session.setEventId(eventId);
                    session.setOwnerId(ownerId);
                    list.put(session.getId(), session);
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

        HashMap<String, Persistence> sessionHashMap = (HashMap<String, Persistence>) params[0];

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("./db/sessions.csv"))) {
            for (Map.Entry<String, Persistence> entry : sessionHashMap.entrySet()) {
                Persistence session = entry.getValue();
                String line = session.getData("id") + ";" + session.getData("name") + ";" + session.getData("date") + ";" + session.getData("description") + ";" + session.getData("location") + ";" + session.getData("startTime") + ";" + session.getData("endTime") + ";" + session.getData("eventId") + ";" + session.getData("ownerId") + "\n";
                writer.write(line);
            }
            writer.close();
            System.out.println("Sessão Atualizada");
        } catch (IOException writerEx) {
            System.out.println("Erro na escrita do arquivo");
            writerEx.printStackTrace();
        }
    }

    public void delete(Object... params) {
        if (params.length > 1) {
            System.out.println("Só pode ter 1 parametro");
        }

        HashMap<String, Persistence> sessionHashMap = (HashMap<String, Persistence>) params[0];

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("./db/sessions.csv"))) {
            for (Map.Entry<String, Persistence> entry : sessionHashMap.entrySet()) {
                Persistence session = entry.getValue();
                String line = session.getData("id") + ";" + session.getData("name") + ";" + session.getData("date") + ";" + session.getData("description") + ";" + session.getData("location") + ";" + session.getData("startTime") + ";" + session.getData("endTime") + ";" + session.getData("eventId") + ";" + session.getData("ownerId") + "\n";
                writer.write(line);
            }
            writer.close();
            System.out.println("Sessão Removida");
        } catch (IOException writerEx) {
            System.out.println("Erro na escrita do arquivo");
            writerEx.printStackTrace();
        }
    }
}
