package br.upe.persistence.repository;

import br.upe.persistence.User;
import br.upe.persistence.builder.UserBuilder;
import br.upe.utils.JPAUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.mindrot.jbcrypt.BCrypt;

import java.io.*;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

public class UserRepository implements Persistence {
    private static final Logger LOGGER = Logger.getLogger(UserRepository.class.getName());
    private UUID userId;

    private static UserRepository instance;

    private UserRepository() {}

    public static UserRepository getInstance() {
        if (instance == null) {
            synchronized (UserRepository.class) {
                if (instance == null) {
                    instance = new UserRepository();
                }
            }
        }
        return instance;
    }

    public boolean userExists(String email, Long cpf) {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.email = :email OR u.cpf = :cpf", User.class);
        query.setParameter("email", email);
        query.setParameter("cpf", cpf);

        try {
            query.getSingleResult();
            return true;
        } catch (NoResultException e) {
            return false;
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    @Override
    public boolean create(Object... params) {
        if (params.length != 4) {
            LOGGER.warning("Só pode ter 4 parametro contendo o email e o cpf do usuário que deseja criar");
        }

        String name = (String) params[0];
        Long cpf = (Long) params[1];
        String email = (String) params[2];
        String hashedPassword = (String) params[3];
        boolean isCreated = false;

        System.out.println("password: " + hashedPassword);

        if (userExists(email, cpf)) {
            LOGGER.warning("Usuário com este email ou CPF já existe");
            return false;
        }

        User user = UserBuilder.builder()
                .withEmail(email)
                .withCpf(cpf)
                .withName(name)
                .withPassword(hashedPassword)
                .build();


        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(user);
            transaction.commit();
            isCreated = true;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.severe("Erro ao commitar a transação: " + e.getMessage());
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
        return isCreated;
    }


    @Override
    public boolean update(Object... params) throws IOException {
        if (params.length != 6) {
            LOGGER.warning("Só pode ter 6 parâmetros");
            return false;
        }

        UUID id = (UUID) params[0];
        String newName = (String) params[1];
        Long newCpf = (Long) params[2];
        String newEmail = (String) params[3];
        String newPassword = (String) params[4];
        String password = (String) params[5];
        boolean isUpdated = false;

        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            User user = entityManager.find(User.class, id);

            if (!isPasswordEqual(password, user.getPassword())) {
                LOGGER.warning("Senha inválida");
                return false;
            }

            if (user != null) {
                user.setEmail(newEmail);
                user.setName(newName);
                user.setCpf(newCpf);
                user.setPassword(newPassword);
                transaction.commit();
                LOGGER.info("Usuário atualizado com sucesso.");
                isUpdated = true;
            } else {
                LOGGER.warning("Usuário não encontrado com o ID fornecido.");
            }
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.severe("Erro ao atualizar usuário: " + e.getMessage());
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
        return isUpdated;
    }


    @Override
    public Object getData(UUID id, String dataToGet) {
       return null;
    }

    @Override
    public Object getData(String dataToGet) {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        User user = entityManager.find(User.class, userId);
        if (user == null) {
            return null;
        }
        return switch (dataToGet) {
            case "id" -> user.getId();
            case "name" -> user.getName();
            case "email" -> user.getEmail();
            case "password" -> user.getPassword();
            case "cpf" -> user.getCpf();
            default -> null;
        };
    }

    @Override
    public void setData(UUID eventId, String dataToSet, Object data) {

    }

    public void setData(String dataToSet, Object data) {
        /*
        switch (dataToSet) {
            case "email" -> userLog.setEmail((String) data);
            case "cpf" -> userLog.setCpf((Long) data);
            case "name" -> userLog.setName((String) data);
            case "password" -> userLog.setPassword((String) data);
            default -> LOGGER.warning("Informação não existe ou é restrita");
        }*/
    }

    @Override
    public boolean delete(Object... params) throws IOException {
        if (params.length != 2) {
            LOGGER.warning("Só pode ter 2 parametro");
            return false;
        }

        UUID id = (UUID) params[0];
        String password = (String) params[1];
        boolean isDeleted = false;

        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            User user = entityManager.find(User.class, id);

            if (!isPasswordEqual(password, user.getPassword())) {
                LOGGER.warning("Senha inválida");
                return false;
            }

            if (user != null) {
                entityManager.remove(user);
                transaction.commit();
                LOGGER.info("Usuário deletado com sucesso.");
                isDeleted = true;
            } else {
                LOGGER.warning("Usuário não encontrado com o ID fornecido.");
            }
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.severe("Erro ao deletar usuário: " + e.getMessage());
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
        return isDeleted;
    }

    @Override
    public HashMap<UUID, Persistence> read() throws IOException {
        HashMap<UUID, Persistence> list = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader("./db/users.csv"));
        try (reader) {

            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 3) {
                    String parsedId = parts[0].trim();
                    String parsedEmail = parts[1].trim();
                    String parsedCpf = parts[2].trim();

                    User user = new User();
                    user.setId(UUID.fromString(parsedId));
                    user.setCpf(Long.parseLong(parsedCpf));
                    user.setEmail(parsedEmail);

                    UserRepository userRepository = new UserRepository();
                    list.put(user.getId(), userRepository);
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
    public HashMap<UUID, Persistence> read(Object... params) {
        return null;
    }

    private boolean isPasswordEqual(String password, String hashedPassword) {
        System.out.println("password: " + password);
        System.out.println("hashedPassword: " + hashedPassword);
        System.out.println("isPasswordEqual: " + BCrypt.checkpw(password, hashedPassword));
        return BCrypt.checkpw(password, hashedPassword);
    }

    public boolean loginValidate(String email, String password) {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        try {
            TypedQuery<User> query = entityManager.createQuery(
                    "SELECT u FROM User u WHERE u.email = :email", User.class);
            query.setParameter("email", email);
            User user = query.getSingleResult();

            if (isPasswordEqual(password, user.getPassword())) {
                this.userId = user.getId();

                return true;
            }
        } catch (NoResultException e) {
            LOGGER.warning("Usuário não encontrado com o email fornecido.");
        } catch (Exception e) {
            LOGGER.severe("Erro ao validar login: " + e.getMessage());
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
        return false;
    }

    /*private UserRepository parseToUserRepository(User user) {
        UserRepository userRepository = new UserRepository();
        userRepository.setData("id", user.getId());
        userRepository.setData("email", user.getEmail());
        userRepository.setData("cpf", user.getCpf());
        userRepository.setData("name", user.getName());
        userRepository.setData("password", user.getPassword());
        return userRepository;
    }*/


}
