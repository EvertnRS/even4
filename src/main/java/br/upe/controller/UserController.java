package br.upe.controller;

import br.upe.persistence.Model;
import br.upe.persistence.repository.Persistence;
import br.upe.persistence.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import static br.upe.ui.Validation.isValidCPF;
import static br.upe.ui.Validation.isValidEmail;

public class UserController implements Controller {
    private static final Logger LOGGER = Logger.getLogger(UserController.class.getName());
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private Persistence userLog;

    @Override
    public Object[] create(Object... params) {
        if (params.length != 4) {
            LOGGER.warning("São necessários 4 parâmetros.");
            return new Object[]{false, null};
        }

        String name = (String) params[0];
        Long cpf = Long.parseLong((String) params[1]);
        String email = (String) params[2];
        String password = (String) params[3];

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        Persistence userRepository = UserRepository.getInstance();

        return userRepository.create(name, cpf, email, hashedPassword);
    }


    @Override
    public boolean update(Object... params) throws IOException {
        if (params.length != 5) {
            LOGGER.warning("Só pode ter 5 parâmetros");
            return false;
        }

        String name = (String) params[0];
        Long cpf = Long.parseLong((String) params[1]);
        String email = (String) params[2];
        String newPassword = (String) params[3];
        String password = (String) params[4];
        boolean updated = false;

        if (isValidEmail(email) && isValidCPF(cpf.toString())) {

            if (!newPassword.isEmpty()) {
                newPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

            } else {
                newPassword = (String) userLog.getData(PASSWORD);
            }

            UUID userID = (UUID) userLog.getData("id");
            updated = userLog.update(userID, name, cpf, email, newPassword, password);
        } else {
            LOGGER.warning("Email ou CPF inválido.");
        }
        return updated;
    }

    @Override
    public boolean delete(Object... params) throws IOException {
        if (params.length != 1) {
            LOGGER.warning("Só pode ter 1 parametro");
            return false;
        }

        String password = (String) params[0];
        UUID userID = (UUID) userLog.getData("id");

        return userLog.delete(userID, password);
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
                case PASSWORD -> data = (String) this.userLog.getData(PASSWORD);
                default -> throw new IOException();
            }
        } catch (IOException e) {
            LOGGER.warning("Informação não existe ou é restrita");
        }
        return data;
    }

    @Override
    public String[] verifyByEventName(String eventName) {
        return new String[0];
    }

    @Override
    public String[] verifyBySessionName(String sessionName) {
        return new String[0];
    }

    @Override
    public List<String> list(Object... params) {
        // Método não implementado
        return List.of();
    }

    @Override
    public <T> List<T> getEventArticles(UUID eventId) {
        return List.of();
    }

    @Override
    public List<Model> getAll() {
        return List.of();
    }

    @Override
    public Object[] isExist(Object... params) throws IOException {
        if(params.length != 2) {
            LOGGER.warning("Só pode ter 2 parametro");
            return new Object[]{false, null};
        }
        String email = (String) params[0];
        String password = (String) params[1];
        Persistence userRepository = UserRepository.getInstance();
        Object[] results = userRepository.isExist(email, password);
        if ((boolean) results[0]) {
            this.userLog = userRepository;
            return results;
        }
        return new Object[]{false, null};
    }

}
