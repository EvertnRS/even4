package br.upe.persistence;

import java.io.*;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class User implements Persistence {
    private String id;
    private String cpf;
    private String email;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getData(String dataToGet) {
        String data = "";
        try {
            switch (dataToGet) {
                case "email" -> data = this.getEmail();
                case "cpf" -> data = this.getCpf();
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
                case "email" -> this.setEmail(data);
                case "cpf" -> this.setCpf(data);
                case "id" -> this.setId(data);
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

    public String getCpf() {
        return this.cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public void setName(String email) {

    }

    public void create(Object... params) {

        if (params.length < 2) {
            // erro
            System.out.println("Erro: Parâmetros insuficientes.");
            return;
        }

        this.email = (String) params[0];
        this.cpf = (String) params[1];
        this.id = generateId();
        String line = id + ";" + email + ";" + cpf;

        try {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("./db/users.csv", true))) {
                writer.write(line);
                writer.newLine();
            }

            System.out.println("Usuário Criado");
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

        HashMap<String, Persistence> userHashMap = (HashMap<String, Persistence>) params[0];

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("./db/users.csv"))) {
            for (Map.Entry<String, Persistence> entry : userHashMap.entrySet()) {
                Persistence user = entry.getValue();
                String line = user.getData("id") + ";" + user.getData("email") + ";" + user.getData("cpf") + "\n";
                writer.write(line);
            }
            writer.close();
            System.out.println("Usuário Atualizado");
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

        HashMap<String, Persistence> userHashMap = (HashMap<String, Persistence>) params[0];

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("./db/users.csv"))) {
            for (Map.Entry<String, Persistence> entry : userHashMap.entrySet()) {
                Persistence user = entry.getValue();
                String line = user.getData("id") + ";" + user.getData("email") + ";" + user.getData("cpf") + "\n";
                writer.write(line);
            }
            writer.close();
            System.out.println("Usuário Removido");
        } catch (IOException writerEx) {
            System.out.println("Erro na escrita do arquivo");
            writerEx.printStackTrace();
        }
    }

    @Override
    public HashMap<String, Persistence> read() {
        HashMap<String, Persistence> list = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("./db/users.csv"));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 3) {
                    String id = parts[0].trim();
                    String email = parts[1].trim();
                    String cpf = parts[2].trim();

                    User user = new User();
                    user.setEmail(email);
                    user.setCpf(cpf);
                    user.setId(id);
                    list.put(user.getId(), user);
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
