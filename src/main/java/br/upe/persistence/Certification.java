package br.upe.persistence;

import java.io.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Certification {
    private static int id;
    private String name;
    private LocalDateTime date;
    private String location;
    private String cpf;
    private String eventName;
    private String duration;

    public static int getId() {
        return id;
    }

    public void setId(int id) {
        Certification.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getData(String dataToGet) {
        String data = "";
        switch (dataToGet) {
            case "name" -> data = this.getName();
            case "eventName" -> data = this.getEventName();
            case "cpf" -> data = this.getCpf();
            case "location" -> data = this.getLocation();
            case "date" -> data = String.valueOf(this.getDate());
            case "duration" -> data = this.getDuration();
            default -> throw new IllegalArgumentException("Data doesn't exist or is restricted");
        }
        return data;
    }

    public void createCertification(String attendeeName, String sessionName, String cpf, LocalDateTime date, String location, String duration) {
        String line = "Certificamos que " + attendeeName +
                " participou com êxito da sessão " + sessionName;

        File f = new File(String.format("./db/Certifications/%s.txt", attendeeName));
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(f, true))) {
            writer.write(line);
            writer.newLine();
            System.out.println("Certification Created\n");
        } catch (IOException writerEx) {
            System.out.println("Error occurred while writing: " + writerEx.getMessage());
        }
    }

    public HashMap<String, Certification> read() {
        HashMap<String, Certification> list = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("./db/events.csv"))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 7) {
                    String id = parts[0].trim();
                    String name = parts[1].trim();
                    String date = parts[2].trim();
                    String location = parts[3].trim();
                    String duration = parts[4].trim();
                    String eventName = parts[5].trim();
                    String cpf = parts[6].trim();

                    Certification certification = new Certification();
                    certification.setId(Integer.parseInt(id));
                    certification.setName(name);
                    certification.setDate(LocalDateTime.parse(date));
                    certification.setLocation(location);
                    certification.setDuration(duration);
                    certification.setEventName(eventName);
                    certification.setCpf(cpf);

                    list.put(id, certification);
                }
            }

        } catch (IOException readerEx) {
            System.out.println("Error occurred while reading: " + readerEx.getMessage());
        }
        return list;
    }
}
