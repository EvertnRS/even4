package br.upe.controller;

import br.upe.persistence.Persistence;
import br.upe.persistence.User;
import br.upe.persistence.UserRepository;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class UserController implements Controller {
    private static final Logger LOGGER = Logger.getLogger(UserController.class.getName());
    private static final String EMAIL = "email";
    private Map<UUID, Persistence> userHashMap;
    private Persistence userLog;

    public UserController() throws IOException {
        this.read();
    }

    public Map<UUID, Persistence> getHashMap() {
        return userHashMap;
    }

    public void setUserHashMap(Map<UUID, Persistence> userHashMap) {
        this.userHashMap = userHashMap;
    }

    @Override
    public String getData(String dataToGet) {
        String data = "";
        try {
            switch (dataToGet) {
                case EMAIL -> data = (String) this.userLog.getData(EMAIL);
                case "cpf" -> data = (String) this.userLog.getData("cpf");
                case "id" -> data = (String) this.userLog.getData("id");
                case "name" -> data = (String) this.userLog.getData("name");
                case "password" -> data = (String) this.userLog.getData("password");
                default -> throw new IOException();
            }
        } catch (IOException e) {
            LOGGER.warning("Informação não existe ou é restrita");
        }
        return data;
    }

    public void setUserLog(Persistence userLog) {
        this.userLog = userLog;
    }

    @Override
    public void create(Object... params) {
        if (params.length < 2) {
            LOGGER.warning("Só pode ter 2 parametros");
        }

        String name = (String) params[0];
        String cpf = (String) params[1];
        String email = (String) params[2];
        String password = (String) params[3];

        Persistence userRepository = new UserRepository();
        try {
            for (Map.Entry<UUID, Persistence> entry : this.userHashMap.entrySet()) {
                Persistence user = entry.getValue();
                if (user.getData(EMAIL).equals(email)) {
                    throw new IOException();
                }
            }

            userRepository.create(name, cpf, email, password);

        } catch (IOException exception) {
            LOGGER.warning("Email já cadastrado");
        }
    }

    @Override
    public void update(Object... params) throws IOException {

        if (params.length < 2) {
            LOGGER.warning("Só pode ter 2 parâmetros");
            return;
        }

        String email = (String) params[0];
        String cpf = (String) params[1];
        Persistence user = userHashMap.get(this.userLog.getData("id"));

        if (user == null) {
            LOGGER.warning("Usuário não encontrado para atualização");
            return;
        }

        System.out.println("Atualizando usuário com ID: " + this.userLog.getData("id"));
        System.out.println("Novo email: " + email);
        System.out.println("CPF permanece como: " + cpf);

        user.setData("email", email);
        user.setData("cpf", cpf);
        UUID userID = (UUID) user.getData("id");

        userHashMap.put(userID, user);

        Persistence userPersistence = new UserRepository();
        userPersistence.update(userHashMap);
    }


    @Override
    public void read() throws IOException {
        Persistence userPersistence = new UserRepository();
        this.userHashMap = userPersistence.read();
    }

    @Override
    public void delete(Object... params) throws IOException {
        if ((params[1]).equals("id")) {
            String idToDelete = (String) params[0];
            System.out.println("ID para deletar: " + idToDelete);

            Iterator<Map.Entry<UUID, Persistence>> iterator = userHashMap.entrySet().iterator();
            boolean found = false;

            while (iterator.hasNext()) {
                Map.Entry<UUID, Persistence> entry = iterator.next();
                Persistence user = entry.getValue();
                String userId = (String) user.getData("id");

                if (userId.equals(idToDelete)) {
                    iterator.remove();
                    found = true;
                    System.out.println("Usuário removido da memória.");
                    break;
                }
            }

            if (found) {
                Persistence userPersistence = new UserRepository();
                userPersistence.delete(userHashMap);
            } else {
                System.out.println("Usuário com ID " + idToDelete + " não encontrado.");
            }
        }
    }

    @Override
    public List<String> list(Object... params) {
        // Método não implementado
        return List.of();
    }

    public boolean loginValidate(String email, String password) {
        for (Map.Entry<UUID, Persistence> entry : this.userHashMap.entrySet()) {
            Persistence user = entry.getValue();
            if (user.getData(EMAIL).equals(email) && user.getData("password").equals(password)) {
                setUserLog(user);
                return true;
            }
        }
        return false;
    }


}
