package br.upe.controller;

import br.upe.persistence.Persistence;
import br.upe.persistence.User;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

public class UserController implements Controller {
    private static final Logger LOGGER = Logger.getLogger(UserController.class.getName());
    private static final String EMAIL = "email";
    private Map<String, Persistence> userHashMap;
    private Persistence userLog;

    public UserController() throws IOException {
        this.read();
    }

    public Map<String, Persistence> getUserHashMap() {
        return userHashMap;
    }

    public void setUserHashMap(Map<String, Persistence> userHashMap) {
        this.userHashMap = userHashMap;
    }

    @Override
    public String getData(String dataToGet) {
        String data = "";
        try {
            switch (dataToGet) {
                case EMAIL -> data = this.userLog.getData(EMAIL);
                case "cpf" -> data = this.userLog.getData("cpf");
                case "id" -> data = this.userLog.getData("id");
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

        String email = (String) params[0];
        String cpf = (String) params[1];
        Persistence userPersistence = new User();
        try {
            for (Map.Entry<String, Persistence> entry : this.userHashMap.entrySet()) {
                Persistence user = entry.getValue();
                if (user.getData(EMAIL).equals(email)) {
                    throw new IOException();
                }
            }

            userPersistence.create(email, cpf);

        } catch (IOException exception) {
            LOGGER.warning("Email já cadastrado");
        }
    }

    @Override
    public void update(Object... params) throws IOException {
        if (params.length < 2) {
            LOGGER.warning("Só pode ter 2 parametros");
            return;
        }
        Persistence userPersistence = new User();
        String email = (String) params[0];
        String cpf = (String) params[1];
        Persistence user = userHashMap.get(this.userLog.getData("id"));
        if (user == null) {
            LOGGER.warning("Usuário não encontrado");
            return;
        }
        user.setData(EMAIL,email);
        user.setData("cpf", cpf);
        userHashMap.put(this.userLog.getData("id"), user);

        userPersistence.update(userHashMap);
    }

    @Override
    public void read() throws IOException {
        Persistence userPersistence = new User();
        this.userHashMap = userPersistence.read();
    }

    @Override
    public void delete(Object... params) throws IOException {
        if ((params[1]).equals("id")) {
            String idToDelete = (String) params[0];
            System.out.println("ID para deletar: " + idToDelete);

            Iterator<Map.Entry<String, Persistence>> iterator = userHashMap.entrySet().iterator();
            boolean found = false;

            while (iterator.hasNext()) {
                Map.Entry<String, Persistence> entry = iterator.next();
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
                Persistence userPersistence = new User();
                userPersistence.delete(userHashMap);
            } else {
                System.out.println("Usuário com ID " + idToDelete + " não encontrado.");
            }
        }
    }


    @Override
    public boolean list(String idowner) {
        // Método não implementado
        return false;
    }

    @Override
    public void show(Object... params) {
        // Método não implementado
    }

    public boolean loginValidate(String email, String cpf) {
        for (Map.Entry<String, Persistence> entry : this.userHashMap.entrySet()) {
            Persistence user = entry.getValue();
            if (user.getData(EMAIL).equals(email) && user.getData("cpf").equals(cpf)) {
                setUserLog(user);
                return true;
            }
        }
        return false;
    }


}
