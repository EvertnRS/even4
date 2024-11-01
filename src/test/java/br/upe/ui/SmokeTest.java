package br.upe.ui;

import br.upe.controller.UserController;
import br.upe.persistence.Persistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SmokeTest {

    private UserController userController;

    @BeforeEach
    void setUp() throws IOException {
        userController = new UserController();
    }

    @Test
    void smokeTestCreateUser() throws IOException {
        String testEmail = "smokeuser@example.com";
        String testCpf = "123.456.789-00";

        userController.create(testEmail, testCpf);

        boolean userCreated = userController.getUserHashMap().values().stream()
                .anyMatch(user -> user.getData("email").equals(testEmail));
        assertTrue(userCreated, "Falha ao criar o usuário no smoke test.");

        userController.delete(userController.getData("id"), "id");
    }

    @Test
    void smokeTestUpdateUser() throws IOException {
        ensureUserExists();

        String updatedEmail = "updatedsmokeuser@example.com";
        userController.update(updatedEmail, "11223344556");

        assertEquals(updatedEmail, userController.getData("email"), "Falha ao atualizar o usuário no smoke test.");

        userController.delete(userController.getData("id"), "id");
    }

    @Test
    void smokeTestReadUser() throws IOException {
        ensureUserExists();

        String userId = userController.getData("id");
        String expectedEmail = "smokeuser@example.com";
        String actualEmail = userController.getUserHashMap().get(userId).getData("email");

        assertEquals(expectedEmail, actualEmail, "Falha ao ler o usuário no smoke test.");

        userController.delete(userId, "id");
    }

    @Test
    void smokeTestDeleteUser() throws IOException {
        ensureUserExists();

        String userId = userController.getData("id");
        userController.delete(userId, "id");

        boolean userDeleted = userController.getUserHashMap().values().stream()
                .noneMatch(user -> user.getData("id").equals(userId));
        assertTrue(userDeleted, "Falha ao excluir o usuário no smoke test.");
    }

    void ensureUserExists() throws IOException {
        String testEmail = "smokeuser@example.com";
        String testCpf = "09876543211";

        boolean userExists = userController.getUserHashMap().values().stream()
                .anyMatch(user -> user.getData("email").equals(testEmail));

        if (!userExists) {
            userController.create(testEmail, testCpf);
            userController.read();
        }
    }
}
