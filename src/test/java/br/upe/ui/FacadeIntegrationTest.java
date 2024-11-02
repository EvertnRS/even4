package br.upe.ui;

import br.upe.controller.*;
import br.upe.facade.Facade;
import br.upe.persistence.Persistence;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FacadeIntegrationTest {

    private Facade facade;

    @BeforeEach
    void setUp() throws IOException {
        Controller userController = new UserController();

        this.facade = new Facade(userController);
    }

    @Test
    void testCreateAndGetUser() throws IOException {
        // Cria um novo usuário
        facade.createUser("user1", "password");

        // Recupera os dados do usuário
        String userData = facade.getUserData("user1");
        assertNotNull(userData, "O usuário deve ser recuperado com sucesso.");
    }

    @Test
    void testCreateAndListEvents() throws IOException {
        // Cria um novo evento
        facade.createEvent("Evento Teste", "Descrição do evento");

        // Lista eventos e verifica se o evento foi criado
        Map<String, Persistence> events = facade.getEventHashMap();
        assertFalse(events.isEmpty(), "O evento deve estar presente na lista.");
    }

    @Test
    void testSessionLifecycle() throws IOException {
        facade.createSession("Sessão 1", "Detalhes da sessão");

        Map<String, Persistence> sessions = facade.getSessionHashMap();
        assertFalse(sessions.isEmpty(), "A sessão deve estar presente na lista.");

        facade.updateSession("Sessão 1", "Novos detalhes da sessão");

        facade.readSession();
    }

    @Test
    void testCreateAndListSubEvents() throws IOException {
        facade.createSubEvent("Subevento Teste", "Detalhes do subevento");

        Map<String, Persistence> subEvents = facade.getSubEventHashMap();
        assertFalse(subEvents.isEmpty(), "O subevento deve estar presente na lista.");
    }

    @Test
    void testCreateAndReadArticle() throws IOException {
        facade.createArticle("Artigo Teste", "Conteúdo do artigo");

        Map<String, Persistence> articles = facade.getArticleHashMap();
        assertFalse(articles.isEmpty(), "O artigo deve estar presente na lista.");
    }

    @Test
    void testLoginValidation() throws IOException {
        facade.createUser("user1", "password");
        boolean isValid = facade.loginValidate("user1", "password");

        assertTrue(isValid, "O login deve ser validado com sucesso.");
    }
}
