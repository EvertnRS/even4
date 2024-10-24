package br.upe.ui;

/*import br.upe.controller.UserController;
import br.upe.persistence.Persistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    @Test
    void testCreateUser() {
        userExists();

        Map<String, Persistence> userHashMap = userController.getUserHashMap();
        boolean userCreated = userHashMap.values().stream()
                .anyMatch(user -> user.getData("email").equals("newuser@example.com"));
        assertTrue(userCreated);
        userController.delete(userController.getData("id"), "id");
    }

    @Test
    void testUpdateUser() {
        userExists();

        userController.update("updateduser@example.com", "11223344556");
        boolean updateSuccessful = userController.loginValidate("updateduser@example.com", "11223344556");
        assertTrue(updateSuccessful, "Login falhou, não é possível atualizar o usuário");

        assertEquals("updateduser@example.com", userController.getData("email"));

        userController.delete(userController.getData("id"), "id");
    }

    @Test
    void testRead() {
        userExists();

        String userReaded = "";
        Map<String, Persistence> userHashMap = userController.getUserHashMap();
        for (Map.Entry<String, Persistence> entry : userHashMap.entrySet()) {
            Persistence persistence = entry.getValue();
            if (persistence.getData("email").equals("newuser@example.com")) {
                userReaded = persistence.getData("id");;
            }
        }

        assertEquals(userController.getData("id"), userReaded);
        userController.delete(userController.getData("id"), "id");
    }

    @Test
    void testDeleteUser() {
        userExists();

        userController.delete(userController.getData("id"), "id");

        boolean deleteSuccessful = userController.getUserHashMap().values().stream()
                .anyMatch(user -> user.getData("email").equals("newuser@example.com"));
        assertFalse(deleteSuccessful);

    }

    void userExists() {
        boolean userExists = userController.getUserHashMap().values().stream()
                .anyMatch(user -> user.getData("email").equals("newuser@example.com"));

        if (!userExists) {
            userController.create("newuser@example.com", "09876543211");
            userController.read();
        }

        boolean loginSuccessful = userController.loginValidate("newuser@example.com", "09876543211");
        assertTrue(loginSuccessful, "Login falhou, não é possível atualizar o usuário");
    }
}*/