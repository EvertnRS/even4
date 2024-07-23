package br.upe.persistence;
import java.io.*;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.HashMap;




public class Event implements Persistence {
    private String id;
    private String name;
    private String date;
    private String description;
    private String location;
    private String ownerId;

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

    private String generateId() {
        SecureRandom secureRandom = new SecureRandom();
        long timestamp = Instant.now().toEpochMilli();
        int lastThreeDigitsOfTimestamp = (int) (timestamp % 1000);
        int randomValue = secureRandom.nextInt(900) + 100;
        return String.format("%03d%03d", lastThreeDigitsOfTimestamp, randomValue);
    }

    public void updateEvent() {

    }

    public void deleteEvent() {

    }

    @Override
    public void delete(Object... params) {

    }

    @Override
    public void update(Object... params) {

    }

    @Override
    public String getData(String dataToGet) {
        return "";
    }

    @Override
    public void setData(String dataToSet, String data) {

    }

    @Override
    public void create(Object... params) {
        if (params.length != 5) {
            System.out.println("Erro: Parâmetros insuficientes.");
            return;
        }

        this.name = (String) params[0];
        this.date = (String) params[1];
        this.description = (String) params[2];
        this.location = (String) params[3];
        this.ownerId = (String) params[4];

        this.id = generateId();
        String line = id + ";" + name + ";" + description + ";" + location + ";" + ownerId + "\n";

        File f = new File("./db/events.csv");
        try {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(f, true))) {
                writer.write(line);
                writer.newLine();
            }

            System.out.println("Evento Criado");
        } catch (IOException writerEx) {
            System.out.println("Erro na escrita do arquivo");
            writerEx.printStackTrace();
        }
    }


    @Override
    public  HashMap<String, Persistence> read() {
        HashMap<String, Persistence> list = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("./db/events.csv"));
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
            System.out.println("Erro ao ler o arquivo");
            readerEx.printStackTrace();
        }

        return list;
    }

}

