package br.upe.persistence;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class Session implements Persistence {
    private static final Logger LOGGER = Logger.getLogger(Session.class.getName());
    private static final String CONST_DESCRIPTION = "description";
    private static final String CONST_LOCATION = "location";
    private static final String CONST_START_TIME = "startTime";
    private static final String CONST_END_TIME = "endTime";
    private static final String EVENT_ID = "eventId";
    private static final String OWNER_ID = "ownerId";
    private static final String SESSION_PATH = "./db/sessions.csv";
    private static final String WRITE_ERROR = "Erro na escrita do arquivo";
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
                case CONST_DESCRIPTION -> data = this.getDescription();
                case CONST_LOCATION -> data = this.getLocation();
                case CONST_START_TIME -> data = this.getStartTime();
                case CONST_END_TIME -> data = this.getEndTime();
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
    public void setData(String dataToSet, String data) {
        try {
            switch (dataToSet) {
                case "id" -> this.setId(data);
                case "name" -> this.setName(data);
                case CONST_DESCRIPTION -> this.setDescription(data);
                case "date" -> this.setDate(data);
                case CONST_LOCATION -> this.setLocation(data);
                case CONST_START_TIME -> this.setStartTime(data);
                case CONST_END_TIME -> this.setEndTime(data);
                case EVENT_ID -> this.setEventId(data);
                case OWNER_ID -> this.setOwnerId(data);
                default -> throw new IOException();
            }
        } catch (IOException e) {
            LOGGER.warning("Informação não existe ou é restrita");
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
            LOGGER.warning("Só pode ter 9 parâmetros");
            return; // Adicionando return para sair da função se o número de parâmetros estiver incorreto
        }

        String parsedEventId = (String) params[0];
        String parsedId = generateId();
        String parsedName = (String) params[1];
        String parsedDate = (String) params[2];
        String parsedDescription = (String) params[3];
        String parsedLocation = (String) params[4];
        String parsedStartTime = (String) params[5];
        String parsedEndTime = (String) params[6];
        String parsedOwnerId = (String) params[7];
        HashMap<String, Persistence> eventH = (HashMap<String, Persistence>) params[8];
        String line = parsedId + ";" + parsedName + ";" + parsedDate + ";" + parsedDescription + ";" + parsedLocation + ";" + parsedStartTime + ";" + parsedEndTime + ";" + parsedEventId + ";" + parsedOwnerId;

        try {
            File file = new File(SESSION_PATH);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(SESSION_PATH, true))) {
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
                LOGGER.warning("Evento não encontrado.");
                return;
            }

            List<Persistence> sessionList = event.getSessionsList();
            if (sessionList == null) {
                sessionList = new ArrayList<>(); // Inicialize a lista se estiver nula
            }

            Session session = new Session();
            LOGGER.warning("IdEvent " + event.getId());
            session.setId(parsedId);
            session.setName(parsedName);
            session.setDate(parsedDate);
            session.setDescription(parsedDescription);
            session.setLocation(parsedLocation);
            session.setStartTime(parsedStartTime);
            session.setEndTime(parsedEndTime);
            session.setOwnerId(parsedOwnerId);
            sessionList.add(session);
            LOGGER.warning("Sessão Criada: " + session.getId());

            // Se necessário, atualizar o evento com a nova lista de sessões
            event.setSessionsList(sessionList);

            eventH.put(eventId, event);

            LOGGER.warning("Sessões atuais: " + sessionList.size());

        } catch (IOException writerEx) {
            LOGGER.warning(WRITE_ERROR);
            writerEx.printStackTrace();
        }
    }


    @Override
    public HashMap<String, Persistence> read() throws IOException {
        HashMap<String, Persistence> list = new HashMap<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(SESSION_PATH), StandardCharsets.UTF_8));
        try (reader) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 9) {
                    String parsedId = parts[0].trim();
                    String parsedName = parts[1].trim();
                    String parsedDate = parts[2].trim();
                    String parsedDescription = parts[3].trim();
                    String parsedLocation = parts[4].trim();
                    String parsedStartTime = parts[5].trim();
                    String parsedEndTime = parts[6].trim();
                    String parsedEventId = parts[7].trim();
                    String parsedOwnerId = parts[8].trim();

                    Session session = new Session();
                    session.setId(parsedId);
                    session.setName(parsedName);
                    session.setDate(parsedDate);
                    session.setDescription(parsedDescription);
                    session.setLocation(parsedLocation);
                    session.setStartTime(parsedStartTime);
                    session.setEndTime(parsedEndTime);
                    session.setEventId(parsedEventId);
                    session.setOwnerId(parsedOwnerId);
                    list.put(session.getId(), session);
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

    public void update(Object... params) throws IOException {
        if (params.length > 1) {
            LOGGER.warning("Só pode ter 1 parametro");
        }

        HashMap<String, Persistence> sessionHashMap = (HashMap<String, Persistence>) params[0];
        BufferedWriter writer = new BufferedWriter(new FileWriter(SESSION_PATH));

        try (writer) {
            for (Map.Entry<String, Persistence> entry : sessionHashMap.entrySet()) {
                Persistence session = entry.getValue();
                String line = session.getData("id") + ";" + session.getData("name") + ";" + session.getData("date") + ";" + session.getData(CONST_DESCRIPTION) + ";" + session.getData(CONST_LOCATION) + ";" + session.getData(CONST_START_TIME) + ";" + session.getData(CONST_END_TIME) + ";" + session.getData(EVENT_ID) + ";" + session.getData(OWNER_ID) + "\n";
                writer.write(line);
            }

            LOGGER.warning("Sessão Atualizada");
        } catch (IOException writerEx) {
            LOGGER.warning(WRITE_ERROR);
            writerEx.printStackTrace();
        } finally {
            writer.close();
        }
    }

    public void delete(Object... params) throws IOException {
        if (params.length > 1) {
            LOGGER.warning("Só pode ter 1 parametro");
        }

        HashMap<String, Persistence> sessionHashMap = (HashMap<String, Persistence>) params[0];
        BufferedWriter writer = new BufferedWriter(new FileWriter(SESSION_PATH));

        try (writer) {
            for (Map.Entry<String, Persistence> entry : sessionHashMap.entrySet()) {
                Persistence session = entry.getValue();
                String line = session.getData("id") + ";" + session.getData("name") + ";" + session.getData("date") + ";" + session.getData(CONST_DESCRIPTION) + ";" + session.getData(CONST_LOCATION) + ";" + session.getData(CONST_START_TIME) + ";" + session.getData(CONST_END_TIME) + ";" + session.getData(EVENT_ID) + ";" + session.getData(OWNER_ID) + "\n";
                writer.write(line);
            }

            LOGGER.warning("Sessão Removida");
        } catch (IOException writerEx) {
            LOGGER.warning(WRITE_ERROR);
            writerEx.printStackTrace();
        } finally {
            writer.close();
        }
    }
}
