package br.upe.persistence;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class SubEvent extends Event implements Persistence{
    private static final Logger LOGGER = Logger.getLogger(SubEvent.class.getName());
    private static final String CONST_DESCRIPTION = "description";
    private static final String CONST_LOCATION = "location";
    private static final String EVENT_ID = "eventId";
    private static final String OWNER_ID = "ownerId";
    private static final String SUB_EVENTS_PATH = "./db/subEvents.csv";
    private static final String WRITE_ERROR = "Erro na escrita do arquivo";
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
                case CONST_DESCRIPTION -> data = this.getDescription();
                case CONST_LOCATION -> data = this.getLocation();
                case EVENT_ID -> data = this.getEventId();
                case OWNER_ID -> data = this.getOwnerId();
                default -> throw new IOException();
            }
        } catch (IOException e) {
            LOGGER.warning("Informação não existe ou é restrita");
        }
        return data;
    }

    @Override
    public void setData(String dataToSet, String data){
        try {
            switch (dataToSet) {
                case "id" -> this.setId(data);
                case "name" -> this.setName(data);
                case CONST_DESCRIPTION -> this.setDescription(data);
                case "date" -> this.setDate(data);
                case CONST_LOCATION -> this.setLocation(data);
                case EVENT_ID -> this.setEventId(data);
                case OWNER_ID -> this.setOwnerId(data);
                default -> throw new IOException();
            }
        } catch (IOException e) {
            LOGGER.warning("Informação não existe ou é restrita");
        }
    }

    private String generateUniqueId() {
        SecureRandom secureRandom = new SecureRandom();
        long timestamp = Instant.now().toEpochMilli();
        int lastThreeDigitsOfTimestamp = (int) (timestamp % 1000);
        int randomValue = secureRandom.nextInt(900) + 100;
        return String.format("%03d%03d", lastThreeDigitsOfTimestamp, randomValue);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getDate() {
        return date;
    }

    @Override
    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
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

    @Override
    public void create(Object... params) {
        if (params.length < 6) {
            LOGGER.warning("Só pode ter 6 parametros");
        }

        String parsedEventId = (String) params[0];
        String parsedId = generateUniqueId();
        String parsedName = (String) params[1];
        String parsedDate = (String) params[2];
        String parsedDescription = (String) params[3];
        String parsedLocation = (String) params[4];
        String parsedOwnerId = (String) params[5];
        String line = parsedId + ";" + parsedName + ";" + parsedDate + ";" + parsedDescription + ";" + parsedLocation + ";" + parsedEventId + ";" + parsedOwnerId + "\n";

        try {
            File file = new File(SUB_EVENTS_PATH);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(SUB_EVENTS_PATH, true))) {
                writer.write(line);
            }

            LOGGER.warning("SubEvento Criado");
        } catch (IOException writerEx) {
            LOGGER.warning(WRITE_ERROR);
            writerEx.printStackTrace();
        }
    }
    @Override
    public HashMap<String, Persistence>  read() throws IOException {
        HashMap<String, Persistence> list = new HashMap<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(SUB_EVENTS_PATH), StandardCharsets.UTF_8));
        try (reader) {

            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 7) {
                    String parsedId = parts[0].trim();
                    String parsedName = parts[1].trim();
                    String parsedDate = parts[2].trim();
                    String parsedDescription = parts[3].trim();
                    String parsedLocation = parts[4].trim();
                    String parsedEventId = parts[5].trim();
                    String parsedOwnerId = parts[6].trim();

                    SubEvent subEvent = new SubEvent();
                    subEvent.setId(parsedId);
                    subEvent.setName(parsedName);
                    subEvent.setDate(parsedDate);
                    subEvent.setDescription(parsedDescription);
                    subEvent.setLocation(parsedLocation);
                    subEvent.setEventId(parsedEventId);
                    subEvent.setOwnerId(parsedOwnerId);
                    list.put(subEvent.getId(), subEvent);
                }
            }


        } catch (IOException readerEx) {
            LOGGER.warning("Erro ao ler o arquivo");
            readerEx.printStackTrace();
        } finally {
            reader.close();
        }
        return list;
    }

    @Override
    public HashMap<String, Persistence> read(Object... params) {
        return new HashMap<>();
    }

    @Override
    public void update(Object... params) throws IOException {
        if (params.length > 1) {
            LOGGER.warning("Só pode ter 1 parametro");
        }

        HashMap<String, Persistence> subEventHashMap = (HashMap<String, Persistence>) params[0];

        BufferedWriter writer = new BufferedWriter(new FileWriter(SUB_EVENTS_PATH));
        try (writer) {
            for (Map.Entry<String, Persistence> entry : subEventHashMap.entrySet()) {
                Persistence subEvent = entry.getValue();
                String line = subEvent.getData("id") + ";" + subEvent.getData("name") + ";" + subEvent.getData("date") + ";" + subEvent.getData(CONST_DESCRIPTION) + ";" + subEvent.getData(CONST_LOCATION) + ";" + subEvent.getData(EVENT_ID) + ";" + subEvent.getData(OWNER_ID) + "\n";
                writer.write(line);
            }

            LOGGER.warning("SubEvento Atualizado");
        } catch (IOException writerEx) {
            LOGGER.warning(WRITE_ERROR);
            writerEx.printStackTrace();
        } finally {
            writer.close();
        }
    }

    @Override
    public void delete(Object... params) throws IOException {
        if (params.length > 1) {
            LOGGER.warning("Só pode ter 1 parametro");
        }

        HashMap<String, Persistence> subEventHashMap = (HashMap<String, Persistence>) params[0];

        BufferedWriter writer = new BufferedWriter(new FileWriter(SUB_EVENTS_PATH));
        try (writer) {
            for (Map.Entry<String, Persistence> entry : subEventHashMap.entrySet()) {
                Persistence subEvent = entry.getValue();
                String line = subEvent.getData("id") + ";" + subEvent.getData("name") + ";" + subEvent.getData("date") + ";" + subEvent.getData(CONST_DESCRIPTION) + ";" + subEvent.getData(CONST_LOCATION) + ";" + subEvent.getData(EVENT_ID) + ";" + subEvent.getData(OWNER_ID) + "\n";
                writer.write(line);
            }

            LOGGER.warning("SubEvento Removido");
        } catch (IOException writerEx) {
            LOGGER.warning(WRITE_ERROR);
            writerEx.printStackTrace();
        } finally {
            writer.close();
        }
    }
}