package br.upe.persistence.repository;

import br.upe.persistence.User;
import br.upe.persistence.builder.UserBuilder;
import br.upe.utils.JPAUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Logger;

public class UserRepository implements Persistence {
    private static final Logger LOGGER = Logger.getLogger(UserRepository.class.getName());
    private UUID userId;
    private static final String EMAIL = "email";

    private static UserRepository instance;

    private UserRepository() {
        // Construtor vazio porque esta classe não requer inicialização específica.
    }

    public static UserRepository getInstance() {
        if (instance == null) {
            synchronized (UserRepository.class) {
                instance = new UserRepository();
            }
        }
        return instance;
    }

    public boolean userExists(String email, Long cpf) {
        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.email = :email OR u.cpf = :cpf", User.class);
        query.setParameter(EMAIL, email);
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
    public Object[] create(Object... params) {
        if (params.length != 4) {
            LOGGER.warning("Só pode ter 4 parametro contendo o email e o cpf do usuário que deseja criar");
            return new Object[]{false, null};
        }

        String name = (String) params[0];
        Long cpf = (Long) params[1];
        String email = (String) params[2];
        String hashedPassword = (String) params[3];
        boolean isCreated = false;

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
            LOGGER.info("Usuário criado com sucesso.");
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
        return new Object[]{isCreated, user.getId()};
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
            user.setEmail(newEmail);
            user.setName(newName);
            user.setCpf(newCpf);
            user.setPassword(newPassword);
            transaction.commit();
            LOGGER.info("Usuário atualizado com sucesso.");
            isUpdated = true;
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
        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        User user = entityManager.find(User.class, id);
        if (user == null) {
            return null;
        }
        return switch (dataToGet) {
            case "id" -> user.getId();
            case "name" -> user.getName();
            case EMAIL -> user.getEmail();
            case "password" -> user.getPassword();
            case "cpf" -> user.getCpf();
            default -> null;
        };
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
            case EMAIL -> user.getEmail();
            case "password" -> user.getPassword();
            case "cpf" -> user.getCpf();
            default -> null;
        };
    }

    @Override
    public void setData(UUID eventId, String dataToSet, Object data) {
        // classe não necessita desse metodo

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

            entityManager.remove(user);
            transaction.commit();
            LOGGER.info("Usuário deletado com sucesso.");
            isDeleted = true;
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

    private boolean isPasswordEqual(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }

    @Override
    public Object[] isExist(Object... params) {
        if (params.length != 2) {
            LOGGER.warning("Só pode ter 2 parametro");
            return new Object[]{false, null};
        }
        String email = (String) params[0];
        String password = (String) params[1];
        EntityManager entityManager = JPAUtils.getEntityManagerFactory();
        try {
            TypedQuery<User> query = entityManager.createQuery(
                    "SELECT u FROM User u WHERE u.email = :email", User.class);
            query.setParameter(EMAIL, email);
            query.setMaxResults(1);
            User user = query.getSingleResult();
            if (isPasswordEqual(password, user.getPassword())) {
                this.userId = user.getId();

                return new Object[]{true, user.getId()};
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
        return new Object[]{false, null};
    }
}
