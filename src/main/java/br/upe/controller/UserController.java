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
    public void create(Object... params) {
        if (params.length != 4) {
            LOGGER.warning("São necessários 4 parâmetros.");
            return;
        }

        String name = (String) params[0];
        Long cpf = Long.parseLong((String) params[1]);
        String email = (String) params[2];
        String password = (String) params[3];

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        Persistence userRepository = UserRepository.getInstance();

        userRepository.create(name, cpf, email, hashedPassword);
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

            if (!newPassword.isEmpty()) {
                newPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

            } else {
                newPassword = (String) userLog.getData(PASSWORD);
            }

            UUID userID = (UUID) userLog.getData("id");
            userLog.update(userID, name, cpf, email, newPassword, password);
        } else {
            LOGGER.warning("Email ou CPF inválido.");
        }
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
    public boolean loginValidate(String email, String password) {
        Persistence userRepository = UserRepository.getInstance();
        if (userRepository.loginValidate(email, password)) {
            this.userLog = userRepository;
            return true;
        }
        return false;
    }

}
