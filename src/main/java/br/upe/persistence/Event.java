package br.upe.persistence;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.sql.Date;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;


public class Event implements Persistence {
    private static final Logger LOGGER = Logger.getLogger(Event.class.getName());
    private static final String CONST_DESCRIPTION = "description";
    private static final String CONST_LOCATION = "location";
    private static final String OWNER_ID = "ownerId";
    private static final String EVENT_PATH = "./db/events.csv";
    private UUID id;
    private String name;
    private Date date;
    private String description;
    private String location;
    private UUID ownerId;
    private List<Persistence> sessionsList;

    public UUID getIdOwner() {
        return ownerId;
    }

    public void setIdOwner(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public UUID getId() {

        return id;
    }

    public String getName() {

        return name;
    }

    public Date  getDate() {

        return date;
    }

    public String getDescription() {

        return description;
    }

    public String getLocation() {

        return location;
    }

    public void setId(UUID id) {

        this.id = id;
    }

    public void setName(String name) {

        this.name = name;
    }

    public void setDate(Date date) {

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
    public Object getData(String dataToGet) {
        String data = "";
        try {
            switch (dataToGet) {
                case "id" -> data = String.valueOf(this.getId());
                case "name" -> data = this.getName();
                case "date" -> data = String.valueOf(this.getDate());
                case CONST_DESCRIPTION -> data = this.getDescription();
                case CONST_LOCATION -> data = this.getLocation();
                case OWNER_ID -> data = String.valueOf(this.getIdOwner());
                case "listSize" -> data = String.valueOf(this.getSessionListLSize());
                default -> throw new IOException();
            }
        } catch (IOException e) {
            LOGGER.warning("Informação não existe ou é restrita");
        }
        return data;
    }

    @Override
    public void setData(String dataToSet, Object data){
        try {
            switch (dataToSet) {
                case "id" -> this.setId((UUID) data);
                case "name" -> this.setName((String) data);
                case CONST_DESCRIPTION -> this.setDescription((String) data);
                case "date" -> this.setDate((Date) data);
                case CONST_LOCATION -> this.setLocation((String) data);
                case OWNER_ID -> this.setIdOwner((UUID) data);
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
        this.date = (Date) params[1];
        this.description = (String) params[2];
        this.location = (String) params[3];
        this.ownerId = (UUID) params[4];

        this.id = UUID.randomUUID();
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
    public  HashMap<UUID, Persistence> read() throws IOException {
        HashMap<UUID, Persistence> list = new HashMap<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(EVENT_PATH), StandardCharsets.UTF_8));
        try (reader) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 6) {
                    UUID parsedId = UUID.fromString(parts[0].trim());
                    String parsedName = parts[1].trim();
                    Date parsedDate = Date.valueOf(parts[2].trim());
                    String parsedDescription = parts[3].trim();
                    String parsedLocation = parts[4].trim();
                    UUID parsedOwnerId = UUID.fromString(parts[5].trim());

                    Event event = new Event();

                    event.setId(parsedId);
                    event.setName(parsedName);
                    event.setDate(parsedDate);
                    event.setDescription(parsedDescription);
                    event.setLocation(parsedLocation);
                    event.setIdOwner(parsedOwnerId);
                    UUID eventId = parsedId;
                    list.put(eventId, event);
                }
            }


        } catch (IOException readerEx) {
            LOGGER.warning("Error occurred while reading:");
            readerEx.printStackTrace();
        } finally {
            reader.close();
        }
        return list;
    }

    @Override
    public HashMap<UUID, Persistence> read(Object... params) {
        return new HashMap<>();
    }

    @Override
    public void update(Object... params) throws IOException {
        if (params.length > 1) {
            LOGGER.warning("Só pode ter 1 parametro");
        }

        HashMap<UUID, Persistence> userHashMap = (HashMap<UUID, Persistence>) params[0];
        BufferedWriter writer = new BufferedWriter(new FileWriter(EVENT_PATH));
        try (writer) {
            for (Map.Entry<UUID, Persistence> entry : userHashMap.entrySet()) {
                Persistence event = entry.getValue();
                String line = event.getData("id") + ";" + event.getData("name") + ";" + event.getData("date")
                        + ";" + event.getData(CONST_DESCRIPTION)
                        + ";" + event.getData(CONST_LOCATION) + ";" + event.getData(OWNER_ID) + "\n";
                writer.write(line);
            }

            LOGGER.warning("Evento Atualizado");
        } catch (IOException writerEx) {
            LOGGER.warning("Error occurred while writing:");
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
        HashMap<String, Persistence> eventHashMap = (HashMap<String, Persistence>) params[0];
        BufferedWriter writer = new BufferedWriter(new FileWriter(EVENT_PATH));
        try (writer) {
            for (Map.Entry<String, Persistence> entry : eventHashMap.entrySet()) {
                Persistence event = entry.getValue();
                String line = event.getData("id") + ";" + event.getData("name") + ";" + event.getData("date")
                        + ";" + event.getData(CONST_DESCRIPTION)
                        + ";" + event.getData(CONST_LOCATION) + ";" + event.getData(OWNER_ID) + "\n";
                writer.write(line);
            }

            LOGGER.warning("Event Removed\n");
        } catch (IOException writerEx) {
            LOGGER.warning("Error occurred while writing:");
            writerEx.printStackTrace();
        } finally {
            writer.close();
        }
    }


}

