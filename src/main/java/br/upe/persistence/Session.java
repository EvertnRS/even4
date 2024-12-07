package br.upe.persistence;

import br.upe.persistence.repository.EventRepository;
import br.upe.persistence.repository.Persistence;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.sql.Time;
import java.time.Instant;
import java.util.*;
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
    private UUID id;
    private String name;
    private Date date;
    private String description;
    private String location;
    private Time startTime;
    private Time endTime;
    private UUID eventId;
    private UUID ownerId;

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
                case CONST_START_TIME -> data = String.valueOf(this.getStartTime());
                case CONST_END_TIME -> data = String.valueOf(this.getEndTime());
                case EVENT_ID -> data = String.valueOf(this.getEventId());
                case OWNER_ID -> data = String.valueOf(this.getOwnerId());
                default -> throw new IOException();
            }
        } catch (IOException e) {
            LOGGER.warning("Informação não existe ou é restrita");
        }
        return data;
    }

    @Override
    public Object getData(UUID eventId, String dataToGet) {
        return null;
    }

    @Override
    public void setData(UUID eventId, String dataToSet, Object data) {

    }

    @Override
    public void setData(String dataToSet, Object data) {
        try {
            switch (dataToSet) {
                case "id" -> this.setId((UUID) data);
                case "name" -> this.setName((String) data);
                case CONST_DESCRIPTION -> this.setDescription((String) data);
                case "date" -> this.setDate((Date) data);
                case CONST_LOCATION -> this.setLocation((String) data);
                case CONST_START_TIME -> this.setStartTime((Time) data);
                case CONST_END_TIME -> this.setEndTime((Time) data);
                case EVENT_ID -> this.setEventId((UUID) data);
                case OWNER_ID -> this.setOwnerId((UUID) data);
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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public boolean create(Object... params) {
        if (params.length < 9) {
            LOGGER.warning("Só pode ter 9 parâmetros");
            return false; // Adicionando return para sair da função se o número de parâmetros estiver incorreto
        }

        UUID parsedEventId = (UUID) params[0];
        UUID parsedId = UUID.randomUUID();
        String parsedName = (String) params[1];
        Date parsedDate = (Date) params[2];
        String parsedDescription = (String) params[3];
        String parsedLocation = (String) params[4];
        Time parsedStartTime = (Time) params[5];
        Time parsedEndTime = (Time) params[6];
        UUID parsedOwnerId = (UUID) params[7];
        HashMap<UUID, Persistence> eventH = (HashMap<UUID, Persistence>) params[8];
        String line = parsedId + ";" + parsedName + ";" + parsedDate + ";" + parsedDescription + ";" + parsedLocation + ";" + parsedStartTime + ";" + parsedEndTime + ";" + parsedEventId + ";" + parsedOwnerId;
        boolean isCreated = false;
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

            EventRepository eventRepository = null;
            for (Map.Entry<UUID, Persistence> entry : eventH.entrySet()) {
                Persistence eventPersistence = entry.getValue();
                if (eventPersistence.getData("id").equals(eventId)) {
                    eventRepository = (EventRepository) eventPersistence;
                    break;
                }
            }

            if (eventRepository == null) {
                LOGGER.warning("Evento não encontrado.");
                return false;
            }

            /*List<Persistence> sessionList = eventRepository.getSessionsList();
            if (sessionList == null) {
                sessionList = new ArrayList<>(); // Inicialize a lista se estiver nula
            }*/

            Session session = new Session();
            /*LOGGER.warning("IdEvent " + eventRepository.getId());*/
            session.setId(parsedId);
            session.setName(parsedName);
            session.setDate(parsedDate);
            session.setDescription(parsedDescription);
            session.setLocation(parsedLocation);
            session.setStartTime(parsedStartTime);
            session.setEndTime(parsedEndTime);
            session.setOwnerId(parsedOwnerId);
           /* sessionList.add(session);*/
            LOGGER.warning("Sessão Criada: " + session.getId());
            isCreated = true;

            // Se necessário, atualizar o evento com a nova lista de sessões
            /*eventRepository.setSessionsList(sessionList);

            eventH.put(eventId, eventRepository);

            LOGGER.warning("Sessões atuais: " + sessionList.size());*/

        } catch (IOException writerEx) {
            LOGGER.warning(WRITE_ERROR);
            writerEx.printStackTrace();
        }
        return isCreated;
    }


    @Override
    public HashMap<UUID, Persistence> read() throws IOException {
        HashMap<UUID, Persistence> list = new HashMap<>();
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
                    session.setId(UUID.fromString(parsedId));
                    session.setName(parsedName);
                    session.setDate(java.sql.Date.valueOf(parsedDate));
                    session.setDescription(parsedDescription);
                    session.setLocation(parsedLocation);
                    session.setStartTime(Time.valueOf(parsedStartTime));
                    session.setEndTime(Time.valueOf(parsedEndTime));
                    session.setEventId(UUID.fromString(parsedEventId));
                    session.setOwnerId(UUID.fromString(parsedOwnerId));
                    UUID sessionId = session.getId();
                    list.put(sessionId, session);
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
    public HashMap<UUID, Persistence> read(Object... params) {
        return new HashMap<>();
    }

    @Override
    public boolean loginValidate(String email, String password) {
        return false;
    }

    public boolean update(Object... params) throws IOException {
        if (params.length > 1) {
            LOGGER.warning("Só pode ter 1 parametro");
        }

        boolean isUpdated = false;
        HashMap<String, Persistence> sessionHashMap = (HashMap<String, Persistence>) params[0];
        BufferedWriter writer = new BufferedWriter(new FileWriter(SESSION_PATH));

        try (writer) {
            for (Map.Entry<String, Persistence> entry : sessionHashMap.entrySet()) {
                Persistence session = entry.getValue();
                String line = session.getData("id") + ";" + session.getData("name") + ";" + session.getData("date") + ";" + session.getData(CONST_DESCRIPTION) + ";" + session.getData(CONST_LOCATION) + ";" + session.getData(CONST_START_TIME) + ";" + session.getData(CONST_END_TIME) + ";" + session.getData(EVENT_ID) + ";" + session.getData(OWNER_ID) + "\n";
                writer.write(line);
            }

            LOGGER.warning("Sessão Atualizada");
            isUpdated = true;
        } catch (IOException writerEx) {
            LOGGER.warning(WRITE_ERROR);
            writerEx.printStackTrace();
        } finally {
            writer.close();
        }
        return isUpdated;
    }

    public boolean delete(Object... params) throws IOException {
        if (params.length > 1) {
            LOGGER.warning("Só pode ter 1 parametro");
        }

        boolean isDeleted = false;
        HashMap<String, Persistence> sessionHashMap = (HashMap<String, Persistence>) params[0];
        BufferedWriter writer = new BufferedWriter(new FileWriter(SESSION_PATH));

        try (writer) {
            for (Map.Entry<String, Persistence> entry : sessionHashMap.entrySet()) {
                Persistence session = entry.getValue();
                String line = session.getData("id") + ";" + session.getData("name") + ";" + session.getData("date") + ";" + session.getData(CONST_DESCRIPTION) + ";" + session.getData(CONST_LOCATION) + ";" + session.getData(CONST_START_TIME) + ";" + session.getData(CONST_END_TIME) + ";" + session.getData(EVENT_ID) + ";" + session.getData(OWNER_ID) + "\n";
                writer.write(line);
            }

            LOGGER.warning("Sessão Removida");
            isDeleted = true;
        } catch (IOException writerEx) {
            LOGGER.warning(WRITE_ERROR);
            writerEx.printStackTrace();
        } finally {
            writer.close();
        }
        return isDeleted;
    }
}
