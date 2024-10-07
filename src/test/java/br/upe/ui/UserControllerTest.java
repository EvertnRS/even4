package br.upe.ui;

import br.upe.controller.UserController;
import br.upe.persistence.User;
import br.upe.persistence.Persistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    private UserController userController;
    private User testUser;

    @BeforeEach
    public void setUp() throws IOException {
        userController = new UserController();
        testUser = new User();

        File file = new File("./db/users.csv");
        if (file.exists()) {
            if (!file.delete()) {
                throw new IOException("Não foi possível excluir o arquivo.");
            }
        }

        // Configura o estado inicial
        testUser.create("test@example.com", "12345678900");
        HashMap<String, Persistence> mockUserMap = new HashMap<>();
        mockUserMap.put(testUser.getId(), testUser);
        userController.setUserHashMap(mockUserMap);
        userController.setUserLog(testUser);
    }

    @Test
    public void testCreateUser() {
        userController.create("newuser@example.com", "09876543211");

        HashMap<String, Persistence> updatedUserMap = new User().read();
        Persistence newUser = updatedUserMap.values().stream()
                .filter(user -> "newuser@example.com".equals(user.getData("email")))
                .findFirst()
                .orElse(null);

        assertNotNull(newUser);
        assertEquals("newuser@example.com", newUser.getData("email"));
    }

    @Test
    public void testUpdateUser() {
        userController.update("updateduser@example.com", "11223344556");

        HashMap<String, Persistence> updatedUserMap = new User().read();
        Persistence updatedUser = updatedUserMap.get(testUser.getId());

        assertNotNull(updatedUser);
        assertEquals("updateduser@example.com", updatedUser.getData("email"));
        assertEquals("11223344556", updatedUser.getData("cpf"));
    }

    @Test
    public void testDeleteUser() {
        userController.delete(testUser.getId(), "id");

        HashMap<String, Persistence> updatedUserMap = new User().read();
        assertFalse(updatedUserMap.containsKey(testUser.getId()));
    }

    @Test
    public void testRead() {
        HashMap<String, Persistence> userMap = new User().read();
        assertEquals(1, userMap.size());
    }
}
