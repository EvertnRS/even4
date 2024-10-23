package br.upe.persistence;

import java.io.*;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class User implements Persistence {
    private static final Logger LOGGER = Logger.getLogger(User.class.getName());
    private static final String CONST_EMAIL = "email";
    private static final String USER_PATH = "./db/users.csv";
    private static final String WRITE_ERROR = "Erro na escrita do arquivo";
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
                case CONST_EMAIL -> data = this.getEmail();
                case "cpf" -> data = this.getCpf();
                case "id" -> data = this.getId();
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
                case CONST_EMAIL -> this.setEmail(data);
                case "cpf" -> this.setCpf(data);
                case "id" -> this.setId(data);
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
        throw new UnsupportedOperationException("setName operation is not supported.");
    }

    public void create(Object... params) {

        if (params.length < 2) {
            // erro
            LOGGER.warning("Erro: Parâmetros insuficientes.");
            return;
        }

        this.email = (String) params[0];
        this.cpf = (String) params[1];
        this.id = generateId();
        String line = id + ";" + email + ";" + cpf;

        try {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_PATH, true))) {
                writer.write(line);
                writer.newLine();
            }

            LOGGER.warning("Usuário Criado");
        } catch (IOException writerEx) {
            LOGGER.warning(WRITE_ERROR);
            writerEx.printStackTrace();
        }
    }


    @Override
    public void update(Object... params) throws IOException {
        if (params.length > 1) {
            LOGGER.warning("Só pode ter 1 parametro");
        }

        HashMap<String, Persistence> userHashMap = (HashMap<String, Persistence>) params[0];
        BufferedWriter writer = new BufferedWriter(new FileWriter(USER_PATH));
        try (writer) {
            for (Map.Entry<String, Persistence> entry : userHashMap.entrySet()) {
                Persistence user = entry.getValue();
                String line = user.getData("id") + ";" + user.getData(CONST_EMAIL) + ";" + user.getData("cpf") + "\n";
                writer.write(line);
            }

            LOGGER.warning("Usuário Atualizado");
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

        HashMap<String, Persistence> userHashMap = (HashMap<String, Persistence>) params[0];
        BufferedWriter writer = new BufferedWriter(new FileWriter(USER_PATH));
        try (writer) {
            for (Map.Entry<String, Persistence> entry : userHashMap.entrySet()) {
                Persistence user = entry.getValue();
                String line = user.getData("id") + ";" + user.getData(CONST_EMAIL) + ";" + user.getData("cpf") + "\n";
                writer.write(line);
            }

            LOGGER.warning("Usuário Removido");
        } catch (IOException writerEx) {
            LOGGER.warning(WRITE_ERROR);
            writerEx.printStackTrace();
        } finally {
            writer.close();
        }
    }

    @Override
    public HashMap<String, Persistence> read() throws IOException {
        HashMap<String, Persistence> list = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(USER_PATH));
        try (reader) {

            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 3) {
                    String parsedId = parts[0].trim();
                    String parsedEmail = parts[1].trim();
                    String parsedCpf = parts[2].trim();

                    User user = new User();
                    user.setEmail(parsedEmail);
                    user.setCpf(parsedCpf);
                    user.setId(parsedId);
                    list.put(user.getId(), user);
                }
            }


        } catch (IOException readerEx) {
            LOGGER.warning("Erro ao ler o arquivo");
            readerEx.printStackTrace();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        } finally {
            reader.close();
        }
        return list;
    }

    @Override
    public HashMap<String, Persistence> read(Object... params) {
        return new HashMap<>();
    }

}
