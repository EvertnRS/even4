package br.upe.controller;

import br.upe.persistence.Event;
import br.upe.persistence.Persistence;
import br.upe.persistence.Repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import static br.upe.ui.Validation.isValidCPF;
import static br.upe.ui.Validation.isValidEmail;

public class UserController implements Controller {
    private static final Logger LOGGER = Logger.getLogger(UserController.class.getName());
    private static final String EMAIL = "email";

    private static UserController instance;

    private Map<UUID, Persistence> userHashMap;
    private Persistence userLog;

    private UserController() {
    }

    public static UserController getInstance() {
        if (instance == null) {
            instance = new UserController();
        }
        return instance;
    }

    public Map<UUID, Persistence> getHashMap() {
        return userHashMap;
    }

    @Override
    public List<Event> getAll() {
        return List.of();
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
                case "cpf" -> data = this.userLog.getData("cpf").toString();
                case "id" -> data = this.userLog.getData("id").toString();
                case "name" -> data = (String) this.userLog.getData("name");
                case "password" -> data = (String) this.userLog.getData("password");
                default -> throw new IOException();
            }
        } catch (IOException e) {
            LOGGER.warning("Informação não existe ou é restrita");
        }
        return data;
    }

    public void setUserLog(UserRepository userRepository) {
        this.userLog = userRepository.getUserLog();
    }

    @Override
    public void create(Object... params) {
        if (params.length < 2) {
            LOGGER.warning("Só pode ter 2 parametros");
        }

        String name = (String) params[0];
        Long cpf = Long.parseLong((String) params[1]);
        String email = (String) params[2];
        String password = (String) params[3];

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        Persistence userRepository = UserRepository.getInstance();

        userRepository.create(name, cpf, email, hashedPassword);
        this.userHashMap.put((UUID) userRepository.getData("id"), userRepository);

    }



    @Override
    public void update(Object... params) throws IOException {
        if (params.length != 5) {
            LOGGER.warning("Só pode ter 5 parâmetros");
            return;
        }

        String name = (String) params[0];
        Long cpf = Long.parseLong((String) params[1]);
        String email = (String) params[2];
        String newPassword = (String) params[3];
        String password = (String) params[4];

        if (isValidEmail(email) && isValidCPF(cpf.toString())) {
            userLog.setData("email", email);
            userLog.setData("cpf", cpf);
            userLog.setData("name", name);

            if (!newPassword.isEmpty()) {
                newPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
                userLog.setData("password", newPassword);
            } else {
                newPassword = (String) userLog.getData("password");
            }

            UUID userID = (UUID) userLog.getData("id");
            userLog.update(userID, name, cpf, email, newPassword, password);
        } else {
            LOGGER.warning("Email ou CPF inválido.");
        }
    }

    @Override
    public void read() throws IOException {
        /*
        Persistence userRepository = UserRepository.getInstance();
        this.userHashMap = userRepository.read();
        */
    }

    @Override
    public void delete(Object... params) throws IOException {
        if (params.length != 1) {
            LOGGER.warning("Só pode ter 1 parametro");
            return;
        }

        String password = (String) params[0];
        UUID userID = (UUID) userLog.getData("id");

        userLog.delete(userID, password);
    }

    @Override
    public List<String> list(Object... params) {
        // Método não implementado
        return List.of();
    }

    @Override
    public boolean loginValidate(String email, String password) {
        Persistence userRepository = UserRepository.getInstance();
        if (userRepository.loginValidate(email, password)) {
            this.userLog = userRepository;
            return true;
        }
        return false;
    }

}
