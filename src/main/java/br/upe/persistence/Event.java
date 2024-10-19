package br.upe.persistence;
import java.io.*;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


public class Event implements Persistence {
    private static final Logger LOGGER = Logger.getLogger(Event.class.getName());
    private static final String CONST_DESCRIPTION = "description";
    private static final String CONST_LOCATION = "location";
    private static final String OWNER_ID = "ownerId";
    private static final String EVENT_PATH = "./db/events.csv";
    private String id;
    private String name;
    private String date;
    private String description;
    private String location;
    private String ownerId;
    private List<Persistence> sessionsList;

    public String getIdOwner() {
        return ownerId;
    }

    public void setIdOwner(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getId() {

        return id;
    }

    public String getName() {

        return name;
    }

    public String  getDate() {

        return date;
    }

    public String getDescription() {

        return description;
    }

    public String getLocation() {

        return location;
    }

    public void setId(String id) {

        this.id = id;
    }

    public void setName(String name) {

        this.name = name;
    }

    public void setDate(String date) {

        this.date = date;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public void setLocation(String location) {

        this.location = location;
    }

    public List<Persistence> getSessionsList() {
        return sessionsList;
    }

    public void setSessionsList(List<Persistence> sessionsList) {
        this.sessionsList = sessionsList;
    }

    public int getSessionListLSize() {
        if (sessionsList != null) {
            return this.sessionsList.size();
        }
        return 0;
    }

    @Override
    public String getData(String dataToGet) {
        String data = "";
        try {
            switch (dataToGet) {
                case "id" -> data = this.getId();
                case "name" -> data = this.getName();
                case "date" -> data = this.getDate();
                case CONST_DESCRIPTION -> data = this.getDescription();
                case CONST_LOCATION -> data = this.getLocation();
                case OWNER_ID -> data = this.getIdOwner();
                case "listSize" -> data = String.valueOf(this.getSessionListLSize());
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
                case OWNER_ID -> this.setIdOwner(data);
                default -> throw new IOException();
            }
        } catch (IOException e) {
            LOGGER.warning("Informação não existe ou é restrita");
        }
    }

    @Override
    public void create(Object... params) {
        if (params.length != 5) {
            LOGGER.warning("Erro: Parâmetros insuficientes.");
            return;
        }

        this.name = (String) params[0];
        this.date = (String) params[1];
        this.description = (String) params[2];
        this.location = (String) params[3];
        this.ownerId = (String) params[4];

        this.id = generateId();
        String line = id + ";" + name + ";" + date + ";" + description + ";" + location + ";" + ownerId + "\n";

        File f = new File(EVENT_PATH);
        try {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(f, true))) {
                writer.write(line);
            }

            LOGGER.warning("Evento Criado\n");
        } catch (IOException writerEx) {
            LOGGER.warning("Um erro ocorreu:");
            writerEx.printStackTrace();
        }
    }


    @Override
    public  HashMap<String, Persistence> read() {
        HashMap<String, Persistence> list = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(EVENT_PATH));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 6) {
                    String id = parts[0].trim();
                    String name = parts[1].trim();
                    String date = parts[2].trim();
                    String description = parts[3].trim();
                    String location = parts[4].trim();
                    String ownerId = parts[5].trim();

                    Event event = new Event();

                    event.setId(id);
                    event.setName(name);
                    event.setDate(date);
                    event.setDescription(description);
                    event.setLocation(location);
                    event.setIdOwner(ownerId);
                    list.put(event.getId(), event);
                }
            }
            reader.close();

        } catch (IOException readerEx) {
            LOGGER.warning("Error occurred while reading:");
            readerEx.printStackTrace();
        }
        return list;
    }

    @Override
    public HashMap<String, Persistence> read(Object... params) {
        return null;
    }

    @Override
    public void update(Object... params) {
        if (params.length > 1) {
            LOGGER.warning("Só pode ter 1 parametro");
        }

        HashMap<String, Persistence> userHashMap = (HashMap<String, Persistence>) params[0];

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(EVENT_PATH))) {
            for (Map.Entry<String, Persistence> entry : userHashMap.entrySet()) {
                Persistence event = entry.getValue();
                String line = event.getData("id") + ";" + event.getData("name") + ";" + event.getData("date")
                        + ";" + event.getData(CONST_DESCRIPTION)
                        + ";" + event.getData(CONST_LOCATION) + ";" + event.getData(OWNER_ID) + "\n";
                writer.write(line);
            }
            writer.close();
            LOGGER.warning("Evento Atualizado");
        } catch (IOException writerEx) {
            LOGGER.warning("Error occurred while writing:");
            writerEx.printStackTrace();
        }
    }

    @Override
    public void delete(Object... params) {
        if (params.length > 1) {
            LOGGER.warning("Só pode ter 1 parametro");
        }
        HashMap<String, Persistence> eventHashMap = (HashMap<String, Persistence>) params[0];
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(EVENT_PATH))) {
            for (Map.Entry<String, Persistence> entry : eventHashMap.entrySet()) {
                Persistence event = entry.getValue();
                String line = event.getData("id") + ";" + event.getData("name") + ";" + event.getData("date")
                        + ";" + event.getData(CONST_DESCRIPTION)
                        + ";" + event.getData(CONST_LOCATION) + ";" + event.getData(OWNER_ID) + "\n";
                writer.write(line);
            }
            writer.close();
            LOGGER.warning("Event Removed\n");
        } catch (IOException writerEx) {
            LOGGER.warning("Error occurred while writing:");
            writerEx.printStackTrace();
        }
    }

    private String generateId() {
        SecureRandom secureRandom = new SecureRandom();
        long timestamp = Instant.now().toEpochMilli();
        int lastThreeDigitsOfTimestamp = (int) (timestamp % 1000);
        int randomValue = secureRandom.nextInt(900) + 100;
        return String.format("%03d%03d", lastThreeDigitsOfTimestamp, randomValue);
    }

}

