package br.upe.controller;

import br.upe.persistence.Persistence;
import br.upe.persistence.User;
import br.upe.persistence.UserRepository;
import br.upe.utils.JPAUtils;
import jakarta.persistence.EntityManager;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class UserController implements Controller {
    private static final Logger LOGGER = Logger.getLogger(UserController.class.getName());
    private static final EntityManager entityManager = JPAUtils.getEntityManagerFactory();
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

        Persistence userRepository = UserRepository.getInstance();
        userRepository.update(userHashMap);
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
                Persistence userRepository = UserRepository.getInstance();
                userRepository.delete(userHashMap);
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

    @Override
    public boolean loginValidate(String email, String password) {
        Persistence userRepository = UserRepository.getInstance();
        if (userRepository.loginValidate(email, password)) {
            this.userLog = userRepository;  // Atribui o repositório como userLog de tipo Persistence
            return true;
        }
        return false;
    }

}
