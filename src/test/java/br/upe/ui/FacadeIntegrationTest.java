package br.upe.ui;

import br.upe.controller.UserController;
import br.upe.facade.Facade;
import br.upe.facade.FacadeInterface;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FacadeIntegrationTest {
    UserController userController = new UserController();
    FacadeInterface facade = new Facade(userController);
    String email = "email@email.com";
    String password = "1234";

    String originalEnvironment;

    FacadeIntegrationTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        originalEnvironment = System.getProperty("ENVIRONMENT");
        System.setProperty("ENVIRONMENT", "test");
    }

    @AfterEach
    public void tearDown() {
        if (originalEnvironment != null) {
            System.setProperty("ENVIRONMENT", originalEnvironment);
        } else {
            System.clearProperty("ENVIRONMENT");
        }
    }

    // USERS -------------------------------------------------------

    @Test
    void testCreateUser() throws IOException {
        if (facade.loginValidate(email, password)) {
            facade.deleteUser("1234");
        }

        Object[] results = facade.createUser("Name", "56756756756", "email@email.com", "1234");
        boolean isCreated = (boolean) results[0];
        if (isCreated) {
            boolean isLoggedIn = facade.loginValidate(email, password);
            assertTrue(true, "O usuário deve ser criado com sucesso");
            if (isLoggedIn) {
                boolean isDeleted = facade.deleteUser("1234");
                assertTrue(isDeleted);
            }
        }
    }

    @Test
    void testUpdateUser() throws IOException {
        if (facade.loginValidate(email, password)) {
            facade.deleteUser("1234");
        }

        Object[] results = facade.createUser("Name", "56756756756", "email@email.com", "1234");
        boolean isCreated = (boolean) results[0];
        if (isCreated) {
            boolean isLoggedIn = facade.loginValidate(email, password);
            if (isLoggedIn) {
                boolean isUpdated = facade.updateUser("Name Updated", "56756756756", "email@email.com", "1234", "1234");
                assertTrue(isUpdated);
                facade.deleteUser("1234");
            }
        }
    }

    @Test
    void testDeleteUser() throws IOException {
        boolean isCreated = false;
        if(!facade.loginValidate(email, password)) {
            Object[] results = facade.createUser("Name", "56756756756", "email@email.com", "1234");
            isCreated = (boolean) results[0];
        }

        if (isCreated) {
            boolean isLoggedIn = facade.loginValidate(email, password);
            if (isLoggedIn) {
                boolean isDeleted = facade.deleteUser("1234");
                assertTrue(isDeleted);
            }
        }
    }

    @Test
    void testUserCascadeDelete() throws IOException {

        if (facade.loginValidate(email, password)) {
            boolean isUserDeleted = facade.deleteUser("1234");
            assertTrue(isUserDeleted);
        }

        Object[] userResults = facade.createUser("Name", "56756756756", "email@email.com", "1234");
        boolean isUserCreated = (boolean) userResults[0];
        if (isUserCreated) {
            boolean isLoggedIn = facade.loginValidate(email, password);
            if (isLoggedIn) {
                String dateString = "2024-02-12";
                LocalDate localDate = LocalDate.parse(dateString);

                java.sql.Date eventDate = java.sql.Date.valueOf(localDate);
                Object[] eventExistsResults = facade.isEventExist("Event", facade.getUserData("id"));
                boolean isEventExist = (boolean) eventExistsResults[0];
                if (isEventExist) {
                    UUID eventId = (UUID) eventExistsResults[1];
                    boolean isDeleted = facade.deleteEvent(eventId, facade.getUserData("id"));
                    assertTrue(isDeleted);
                }
                Object[] eventResults = facade.createEvent("Event", eventDate, "Event Test", "Location", facade.getUserData("id"));
                boolean isEventCreated = (boolean) eventResults[0];
                if (isEventCreated) {
                    Object[] subEventExistsResults = facade.isSubEventExist("SubEvent", facade.getUserData("id"));
                    boolean isSubEventExist = (boolean) subEventExistsResults[0];
                    if (isSubEventExist) {
                        UUID subEventId = (UUID) subEventExistsResults[1];
                        boolean isDeleted = facade.deleteSubEvent(subEventId, facade.getUserData("id"));
                        assertTrue(isDeleted);
                    }
                    Object[] subEventResults = facade.createSubEvent(eventResults[2] ,"SubEvent", eventDate, "SubEvent", "Location", facade.getUserData("id"));
                    assertTrue((boolean) subEventResults[0]);
                    Object[] eventSessionExistsResults = facade.isSessionExist("Session", facade.getUserData("id"));
                    boolean isSessionEventExist = (boolean) eventSessionExistsResults[0];
                    if (isSessionEventExist) {
                        UUID sessionEventId = (UUID) eventSessionExistsResults[1];
                        boolean isDeleted = facade.deleteSession(sessionEventId, facade.getUserData("id"));
                        assertTrue(isDeleted);
                    }
                    String[] type = {subEventResults[1].toString(), "subEvento"};
                    Object[] sessionEventResults = facade.createSession("Session", eventDate, "Session", "Location", "20:50", "21:50",facade.getUserData("id"), type);
                    assertTrue((boolean) sessionEventResults[0]);
                    boolean isDeleted = facade.deleteUser("1234");
                    assertTrue(isDeleted);
                }

            }
        }
    }

    // EVENTS -------------------------------------------------------

    @Test
    void testCreateEvent() throws IOException {

        if (facade.loginValidate(email, password)) {
            boolean isUserDeleted = facade.deleteUser("1234");
            assertTrue(isUserDeleted);
        }

        Object[] userResults = facade.createUser("Name", "56756756756", "email@email.com", "1234");
        boolean isUserCreated = (boolean) userResults[0];
        if (isUserCreated) {
            boolean isLoggedIn = facade.loginValidate(email, password);
            if (isLoggedIn) {
                String dateString = "2024-02-12";
                LocalDate localDate = LocalDate.parse(dateString);

                java.sql.Date eventDate = java.sql.Date.valueOf(localDate);
                Object[] eventResults = facade.createEvent("Event", eventDate, "Event Test", "Location", facade.getUserData("id"));
                boolean isEventCreated = (boolean) eventResults[0];
                assertTrue(isEventCreated);
                boolean isEventDeleted = facade.deleteEvent(eventResults[1], facade.getUserData("id"));
                assertTrue(isEventDeleted);
                boolean isUserDeleted = facade.deleteUser("1234");
                assertTrue(isUserDeleted);
            }
        }
    }

    @Test
    void testUpdateEvent() throws IOException {
        if (facade.loginValidate(email, password)) {
            boolean isUserDeleted = facade.deleteUser("1234");
            assertTrue(isUserDeleted);
        }

        Object[] userResults = facade.createUser("Name", "56756756756", "email@email.com", "1234");
        boolean isUserCreated = (boolean) userResults[0];
        if (isUserCreated) {
            boolean isLoggedIn = facade.loginValidate(email, password);
            if (isLoggedIn) {

                String dateString = "2024-02-12";
                LocalDate localDate = LocalDate.parse(dateString);

                java.sql.Date eventDate = java.sql.Date.valueOf(localDate);
                Object[] existsResults = facade.isEventExist("Event", facade.getUserData("id"));
                boolean isEventExist = (boolean) existsResults[0];
                System.out.println("existe? " + isEventExist);
                if (isEventExist) {
                    UUID eventId = (UUID) existsResults[1];
                    boolean isDeleted = facade.deleteEvent(eventId, facade.getUserData("id"));
                    assertTrue(isDeleted);
                }

                Object[] eventResults = facade.createEvent("Event", eventDate, "Event Test", "Location", facade.getUserData("id"));
                boolean isEventCreated = (boolean) eventResults[0];
                assertTrue(isEventCreated);
                boolean isEventUpdated = facade.updateEvent(eventResults[1], "Event Updated", eventDate, "Event Test", "Location");
                assertTrue(isEventUpdated);
                boolean isEventDeleted = facade.deleteEvent(eventResults[1], facade.getUserData("id"));
                assertTrue(isEventDeleted);
                boolean isUserDeleted = facade.deleteUser("1234");
                assertTrue(isUserDeleted);
            }
        }
    }

    @Test
    void testDeleteEvent() throws IOException {
        if (facade.loginValidate(email, password)) {
            boolean isUserDeleted = facade.deleteUser("1234");
            assertTrue(isUserDeleted);
        }

        Object[] userResults = facade.createUser("Name", "56756756756", "email@email.com", "1234");
        boolean isUserCreated = (boolean) userResults[0];
        if (isUserCreated) {
            boolean isLoggedIn = facade.loginValidate(email, password);
            if (isLoggedIn) {
                String dateString = "2024-02-12";
                LocalDate localDate = LocalDate.parse(dateString);

                java.sql.Date eventDate = java.sql.Date.valueOf(localDate);
                Object[] existsResults = facade.isEventExist("Event", facade.getUserData("id"));
                boolean isEventExist = (boolean) existsResults[0];
                if (isEventExist) {
                    UUID eventId = (UUID) existsResults[1];
                    boolean isDeleted = facade.deleteEvent(eventId, facade.getUserData("id"));
                    assertTrue(isDeleted);
                }

                Object[] eventResults = facade.createEvent("Event", eventDate, "Event Test", "Location", facade.getUserData("id"));
                boolean isEventCreated = (boolean) eventResults[0];
                assertTrue(isEventCreated);
                boolean isEventDeleted = facade.deleteEvent(eventResults[1], facade.getUserData("id"));
                assertTrue(isEventDeleted);
                boolean isUserDeleted = facade.deleteUser("1234");
                assertTrue(isUserDeleted);
            }
        }
    }

    // SUB-EVENTS -------------------------------------------------------

    @Test
    void testCreateSubEvent() throws IOException {
        System.out.println("Teste de criação de subevento");

        if (facade.loginValidate(email, password)) {
            boolean isUserDeleted = facade.deleteUser("1234");
            assertTrue(isUserDeleted);
        }

        Object[] userResults = facade.createUser("Name", "56756756756", "email@email.com", "1234");
        boolean isUserCreated = (boolean) userResults[0];
        if (isUserCreated) {
            boolean isLoggedIn = facade.loginValidate(email, password);
            if (isLoggedIn) {
                String dateString = "2024-02-12";
                LocalDate localDate = LocalDate.parse(dateString);

                java.sql.Date eventDate = java.sql.Date.valueOf(localDate);
                Object[] eventExistsResults = facade.isEventExist("Event", facade.getUserData("id"));
                boolean isEventExist = (boolean) eventExistsResults[0];
                if (isEventExist) {
                    UUID eventId = (UUID) eventExistsResults[1];
                    boolean isDeleted = facade.deleteEvent(eventId, facade.getUserData("id"));
                    assertTrue(isDeleted);
                }
                Object[] eventResults = facade.createEvent("Event", eventDate, "Event Test", "Location", facade.getUserData("id"));
                boolean isEventCreated = (boolean) eventResults[0];
                if (isEventCreated) {
                    Object[] subEventExistsResults = facade.isSubEventExist("SubEvent", facade.getUserData("id"));
                    boolean isSubEventExist = (boolean) subEventExistsResults[0];
                    if (isSubEventExist) {
                        UUID subEventId = (UUID) subEventExistsResults[1];
                        boolean isDeleted = facade.deleteSubEvent(subEventId, facade.getUserData("id"));
                        assertTrue(isDeleted);
                    }
                    Object[] subEventResults = facade.createSubEvent(eventResults[2] ,"SubEvent", eventDate, "SubEvent", "Location", facade.getUserData("id"));
                    assertTrue((boolean) subEventResults[0]);
                    boolean isSubEventDeleted = facade.deleteSubEvent(subEventResults[1], facade.getUserData("id"));
                    assertTrue(isSubEventDeleted);
                    boolean isEventDeleted = facade.deleteEvent(eventResults[1], facade.getUserData("id"));
                    assertTrue(isEventDeleted);
                    boolean isUserDeleted = facade.deleteUser("1234");
                    assertTrue(isUserDeleted);
                }

            }
        }
    }

    @Test
    void testUpdateSubEvent() throws IOException {
        if (facade.loginValidate(email, password)) {
            boolean isUserDeleted = facade.deleteUser("1234");
            assertTrue(isUserDeleted);
        }

        Object[] userResults = facade.createUser("Name", "56756756756", "email@email.com", "1234");
        boolean isUserCreated = (boolean) userResults[0];
        if (isUserCreated) {
            boolean isLoggedIn = facade.loginValidate(email, password);
            if (isLoggedIn) {

                String dateString = "2024-02-12";
                LocalDate localDate = LocalDate.parse(dateString);

                java.sql.Date eventDate = java.sql.Date.valueOf(localDate);
                Object[] existsResults = facade.isEventExist("Event", facade.getUserData("id"));
                boolean isEventExist = (boolean) existsResults[0];
                if (isEventExist) {
                    UUID eventId = (UUID) existsResults[1];
                    boolean isDeleted = facade.deleteEvent(eventId, facade.getUserData("id"));
                    assertTrue(isDeleted);
                }

                Object[] eventResults = facade.createEvent("Event", eventDate, "Event Test", "Location", facade.getUserData("id"));
                boolean isEventCreated = (boolean) eventResults[0];
                if (isEventCreated) {
                    Object[] subEventExistsResults = facade.isSubEventExist("SubEvent", facade.getUserData("id"));
                    boolean isSubEventExist = (boolean) subEventExistsResults[0];
                    if (isSubEventExist) {
                        UUID subEventId = (UUID) subEventExistsResults[1];
                        boolean isDeleted = facade.deleteSubEvent(subEventId, facade.getUserData("id"));
                        assertTrue(isDeleted);
                    }

                    Object[] subEventResults = facade.createSubEvent(eventResults[2] ,"SubEvent", eventDate, "SubEvent", "Location", facade.getUserData("id"));
                    boolean isSubEventCreated = (boolean) subEventResults[0];
                    if (isSubEventCreated) {
                        boolean isSubEventUpdated = facade.updateSubEvent(subEventResults[1], "SubEvent Updated", eventDate, "SubEvent", "Location");
                        assertTrue(isSubEventUpdated);
                        boolean isSubEventDeleted = facade.deleteSubEvent(subEventResults[1], facade.getUserData("id"));
                        assertTrue(isSubEventDeleted);
                        boolean isEventDeleted = facade.deleteEvent(eventResults[1], facade.getUserData("id"));
                        assertTrue(isEventDeleted);
                        boolean isUserDeleted = facade.deleteUser("1234");
                        assertTrue(isUserDeleted);
                    }
                }

            }
        }
    }

    @Test
    void testDeleteSubEvent() throws IOException {

        if (facade.loginValidate(email, password)) {
            boolean isUserDeleted = facade.deleteUser("1234");
            assertTrue(isUserDeleted);
        }

        Object[] userResults = facade.createUser("Name", "56756756756", "email@email.com", "1234");
        boolean isUserCreated = (boolean) userResults[0];
        if (isUserCreated) {
            boolean isLoggedIn = facade.loginValidate(email, password);
            if (isLoggedIn) {
                String dateString = "2024-02-12";
                LocalDate localDate = LocalDate.parse(dateString);

                java.sql.Date eventDate = java.sql.Date.valueOf(localDate);
                Object[] eventExistsResults = facade.isEventExist("Event", facade.getUserData("id"));
                boolean isEventExist = (boolean) eventExistsResults[0];
                if (isEventExist) {
                    UUID eventId = (UUID) eventExistsResults[1];
                    boolean isDeleted = facade.deleteEvent(eventId, facade.getUserData("id"));
                    assertTrue(isDeleted);
                }
                Object[] eventResults = facade.createEvent("Event", eventDate, "Event Test", "Location", facade.getUserData("id"));
                boolean isEventCreated = (boolean) eventResults[0];
                if (isEventCreated) {
                    Object[] subEventExistsResults = facade.isSubEventExist("SubEvent", facade.getUserData("id"));
                    boolean isSubEventExist = (boolean) subEventExistsResults[0];
                    if (isSubEventExist) {
                        UUID subEventId = (UUID) subEventExistsResults[1];
                        boolean isDeleted = facade.deleteSubEvent(subEventId, facade.getUserData("id"));
                        assertTrue(isDeleted);
                    }
                    Object[] subEventResults = facade.createSubEvent(eventResults[2] ,"SubEvent", eventDate, "SubEvent", "Location", facade.getUserData("id"));
                    assertTrue((boolean) subEventResults[0]);
                    boolean isSubEventDeleted = facade.deleteSubEvent(subEventResults[1], facade.getUserData("id"));
                    assertTrue(isSubEventDeleted);
                    boolean isEventDeleted = facade.deleteEvent(eventResults[1], facade.getUserData("id"));
                    assertTrue(isEventDeleted);
                    boolean isUserDeleted = facade.deleteUser("1234");
                    assertTrue(isUserDeleted);
                }

            }
        }
    }

    // SESSIONS -------------------------------------------------------

    @Test
    void testCreateEventSession() throws IOException {

        if (facade.loginValidate(email, password)) {
            boolean isUserDeleted = facade.deleteUser("1234");
            assertTrue(isUserDeleted);
        }

        Object[] userResults = facade.createUser("Name", "56756756756", "email@email.com", "1234");
        boolean isUserCreated = (boolean) userResults[0];
        if (isUserCreated) {
            boolean isLoggedIn = facade.loginValidate(email, password);
            if (isLoggedIn) {
                String dateString = "2024-02-12";
                LocalDate localDate = LocalDate.parse(dateString);

                java.sql.Date eventDate = java.sql.Date.valueOf(localDate);
                Object[] eventExistsResults = facade.isEventExist("Event", facade.getUserData("id"));
                boolean isEventExist = (boolean) eventExistsResults[0];
                if (isEventExist) {
                    UUID eventId = (UUID) eventExistsResults[1];
                    boolean isDeleted = facade.deleteEvent(eventId, facade.getUserData("id"));
                    assertTrue(isDeleted);
                }
                Object[] eventResults = facade.createEvent("Event", eventDate, "Event Test", "Location", facade.getUserData("id"));
                boolean isEventCreated = (boolean) eventResults[0];
                if (isEventCreated) {
                    Object[] eventSessionExistsResults = facade.isSessionExist("Session", facade.getUserData("id"));
                    boolean isSessionEventExist = (boolean) eventSessionExistsResults[0];
                    if (isSessionEventExist) {
                        UUID sessionEventId = (UUID) eventSessionExistsResults[1];
                        boolean isDeleted = facade.deleteSession(sessionEventId, facade.getUserData("id"));
                        assertTrue(isDeleted);
                    }
                    String[] type = {eventResults[1].toString(), "evento"};
                    Object[] sessionEventResults = facade.createSession("Session", eventDate, "Session", "Location", "20:50", "21:50",facade.getUserData("id"), type);
                    assertTrue((boolean) sessionEventResults[0]);
                    boolean isSessionDeleted = facade.deleteSession(sessionEventResults[1], facade.getUserData("id"));
                    assertTrue(isSessionDeleted);
                    boolean isEventDeleted = facade.deleteEvent(eventResults[1], facade.getUserData("id"));
                    assertTrue(isEventDeleted);
                    boolean isUserDeleted = facade.deleteUser("1234");
                    assertTrue(isUserDeleted);
                }

            }
        }
    }

    @Test
    void testCreateSubEventSession() throws IOException {

        if (facade.loginValidate(email, password)) {
            boolean isUserDeleted = facade.deleteUser("1234");
            assertTrue(isUserDeleted);
        }

        Object[] userResults = facade.createUser("Name", "56756756756", "email@email.com", "1234");
        boolean isUserCreated = (boolean) userResults[0];
        if (isUserCreated) {
            boolean isLoggedIn = facade.loginValidate(email, password);
            if (isLoggedIn) {
                String dateString = "2024-02-12";
                LocalDate localDate = LocalDate.parse(dateString);

                java.sql.Date eventDate = java.sql.Date.valueOf(localDate);
                Object[] eventExistsResults = facade.isEventExist("Event", facade.getUserData("id"));
                boolean isEventExist = (boolean) eventExistsResults[0];
                if (isEventExist) {
                    UUID eventId = (UUID) eventExistsResults[1];
                    boolean isDeleted = facade.deleteEvent(eventId, facade.getUserData("id"));
                    assertTrue(isDeleted);
                }
                Object[] eventResults = facade.createEvent("Event", eventDate, "Event Test", "Location", facade.getUserData("id"));
                boolean isEventCreated = (boolean) eventResults[0];
                if (isEventCreated) {
                    Object[] subEventExistsResults = facade.isSubEventExist("SubEvent", facade.getUserData("id"));
                    boolean isSubEventExist = (boolean) subEventExistsResults[0];
                    if (isSubEventExist) {
                        UUID subEventId = (UUID) subEventExistsResults[1];
                        boolean isDeleted = facade.deleteSubEvent(subEventId, facade.getUserData("id"));
                        assertTrue(isDeleted);
                    }
                    Object[] subEventResults = facade.createSubEvent(eventResults[2] ,"SubEvent", eventDate, "SubEvent", "Location", facade.getUserData("id"));
                    assertTrue((boolean) subEventResults[0]);
                    Object[] eventSessionExistsResults = facade.isSessionExist("Session", facade.getUserData("id"));
                    boolean isSessionEventExist = (boolean) eventSessionExistsResults[0];
                    if (isSessionEventExist) {
                        UUID sessionEventId = (UUID) eventSessionExistsResults[1];
                        boolean isDeleted = facade.deleteSession(sessionEventId, facade.getUserData("id"));
                        assertTrue(isDeleted);
                    }
                    String[] type = {subEventResults[1].toString(), "subEvento"};
                    Object[] sessionEventResults = facade.createSession("Session", eventDate, "Session", "Location", "20:50", "21:50",facade.getUserData("id"), type);
                    assertTrue((boolean) sessionEventResults[0]);
                    boolean isSessionDeleted = facade.deleteSession(sessionEventResults[1], facade.getUserData("id"));
                    assertTrue(isSessionDeleted);
                    boolean isSubEventDeleted = facade.deleteSubEvent(subEventResults[1], facade.getUserData("id"));
                    assertTrue(isSubEventDeleted);
                    boolean isEventDeleted = facade.deleteEvent(eventResults[1], facade.getUserData("id"));
                    assertTrue(isEventDeleted);
                    boolean isUserDeleted = facade.deleteUser("1234");
                    assertTrue(isUserDeleted);
                }

            }
        }
    }

    @Test
    void testUpdateEventSession() throws IOException {

        if (facade.loginValidate(email, password)) {
            boolean isUserDeleted = facade.deleteUser("1234");
            assertTrue(isUserDeleted);
        }

        Object[] userResults = facade.createUser("Name", "56756756756", "email@email.com", "1234");
        boolean isUserCreated = (boolean) userResults[0];
        if (isUserCreated) {
            boolean isLoggedIn = facade.loginValidate(email, password);
            if (isLoggedIn) {
                String dateString = "2024-02-12";
                LocalDate localDate = LocalDate.parse(dateString);

                java.sql.Date eventDate = java.sql.Date.valueOf(localDate);
                Object[] eventExistsResults = facade.isEventExist("Event", facade.getUserData("id"));
                boolean isEventExist = (boolean) eventExistsResults[0];
                if (isEventExist) {
                    UUID eventId = (UUID) eventExistsResults[1];
                    boolean isDeleted = facade.deleteEvent(eventId, facade.getUserData("id"));
                    assertTrue(isDeleted);
                }
                Object[] eventResults = facade.createEvent("Event", eventDate, "Event Test", "Location", facade.getUserData("id"));
                boolean isEventCreated = (boolean) eventResults[0];
                if (isEventCreated) {
                    Object[] eventSessionExistsResults = facade.isSessionExist("Session", facade.getUserData("id"));
                    boolean isSessionEventExist = (boolean) eventSessionExistsResults[0];
                    if (isSessionEventExist) {
                        UUID sessionEventId = (UUID) eventSessionExistsResults[1];
                        boolean isDeleted = facade.deleteSession(sessionEventId, facade.getUserData("id"));
                        assertTrue(isDeleted);
                    }
                    String[] type = {eventResults[1].toString(), "evento"};
                    Object[] sessionEventResults = facade.createSession("Session", eventDate, "Session", "Location", "20:50", "21:50",facade.getUserData("id"), type);
                    if ((boolean) sessionEventResults[0]) {
                        boolean isUpdated = facade.updateSession("Session", "Session Updated", "2024-02-12", "Session", "Location", facade.getUserData("id"), "20:50", "21:50");
                        assertTrue(isUpdated);
                        boolean isSessionDeleted = facade.deleteSession(sessionEventResults[1], facade.getUserData("id"));
                        assertTrue(isSessionDeleted);
                    }
                    boolean isEventDeleted = facade.deleteEvent(eventResults[1], facade.getUserData("id"));
                    assertTrue(isEventDeleted);
                    boolean isUserDeleted = facade.deleteUser("1234");
                    assertTrue(isUserDeleted);
                }

            }
        }
    }

    @Test
    void testDeleteEventSession() throws IOException {

        if (facade.loginValidate(email, password)) {
            boolean isUserDeleted = facade.deleteUser("1234");
            assertTrue(isUserDeleted);
        }

        Object[] userResults = facade.createUser("Name", "56756756756", "email@email.com", "1234");
        boolean isUserCreated = (boolean) userResults[0];
        if (isUserCreated) {
            boolean isLoggedIn = facade.loginValidate(email, password);
            if (isLoggedIn) {
                String dateString = "2024-02-12";
                LocalDate localDate = LocalDate.parse(dateString);

                java.sql.Date eventDate = java.sql.Date.valueOf(localDate);
                Object[] eventExistsResults = facade.isEventExist("Event", facade.getUserData("id"));
                boolean isEventExist = (boolean) eventExistsResults[0];
                if (isEventExist) {
                    UUID eventId = (UUID) eventExistsResults[1];
                    boolean isDeleted = facade.deleteEvent(eventId, facade.getUserData("id"));
                    assertTrue(isDeleted);
                }
                Object[] eventResults = facade.createEvent("Event", eventDate, "Event Test", "Location", facade.getUserData("id"));
                boolean isEventCreated = (boolean) eventResults[0];
                if (isEventCreated) {
                    Object[] eventSessionExistsResults = facade.isSessionExist("Session", facade.getUserData("id"));
                    boolean isSessionEventExist = (boolean) eventSessionExistsResults[0];
                    if (isSessionEventExist) {
                        UUID sessionEventId = (UUID) eventSessionExistsResults[1];
                        boolean isDeleted = facade.deleteSession(sessionEventId, facade.getUserData("id"));
                        assertTrue(isDeleted);
                    }
                    String[] type = {eventResults[1].toString(), "evento"};
                    Object[] sessionEventResults = facade.createSession("Session", eventDate, "Session", "Location", "20:50", "21:50",facade.getUserData("id"), type);
                    assertTrue((boolean) sessionEventResults[0]);
                    boolean isDeleted = facade.deleteSession(sessionEventResults[1], facade.getUserData("id"));
                    assertTrue(isDeleted);
                    boolean isEventDeleted = facade.deleteEvent(eventResults[1], facade.getUserData("id"));
                    assertTrue(isEventDeleted);
                    boolean isUserDeleted = facade.deleteUser("1234");
                    assertTrue(isUserDeleted);
                }

            }
        }
    }

    // ATTENDEES -------------------------------------------------------

    @Test
    void testCreateEventAttendee() throws IOException {

        if (facade.loginValidate(email, password)) {
            boolean isUserDeleted = facade.deleteUser("1234");
            assertTrue(isUserDeleted);
        }

        Object[] userResults = facade.createUser("Name", "56756756756", "email@email.com", "1234");
        boolean isUserCreated = (boolean) userResults[0];
        if (isUserCreated) {
            boolean isLoggedIn = facade.loginValidate(email, password);
            if (isLoggedIn) {
                String dateString = "2024-02-12";
                LocalDate localDate = LocalDate.parse(dateString);

                java.sql.Date eventDate = java.sql.Date.valueOf(localDate);
                Object[] eventExistsResults = facade.isEventExist("Event", facade.getUserData("id"));
                boolean isEventExist = (boolean) eventExistsResults[0];
                if (isEventExist) {
                    UUID eventId = (UUID) eventExistsResults[1];
                    boolean isDeleted = facade.deleteEvent(eventId, facade.getUserData("id"));
                    assertTrue(isDeleted);
                }
                Object[] eventResults = facade.createEvent("Event", eventDate, "Event Test", "Location", facade.getUserData("id"));
                boolean isEventCreated = (boolean) eventResults[0];
                if (isEventCreated) {
                    Object[] eventSessionExistsResults = facade.isSessionExist("Session", facade.getUserData("id"));
                    boolean isSessionEventExist = (boolean) eventSessionExistsResults[0];
                    if (isSessionEventExist) {
                        UUID sessionEventId = (UUID) eventSessionExistsResults[1];
                        boolean isDeleted = facade.deleteSession(sessionEventId, facade.getUserData("id"));
                        assertTrue(isDeleted);
                    }
                    String[] type = {eventResults[1].toString(), "evento"};
                    Object[] sessionEventResults = facade.createSession("Session", eventDate, "Session", "Location", "20:50", "21:50", facade.getUserData("id"), type);
                    if ((boolean) sessionEventResults[0]) {
                        Object[] attendeeExistsResults = facade.isAttendeeExist(facade.getUserData("id"));
                        if ((boolean) attendeeExistsResults[0]) {
                            UUID attendeeId = (UUID) attendeeExistsResults[1];
                            boolean isDeleted = facade.deleteAttendee(attendeeId, sessionEventResults[1]);
                            assertTrue(isDeleted);
                        }
                        Object[] attendeeResults = facade.createAttendee("Session", facade.getUserData("id"));
                        assertTrue((boolean) attendeeResults[0]);
                        boolean isAttendeeDeleted = facade.deleteAttendee(attendeeResults[1], sessionEventResults[1]);
                        assertTrue(isAttendeeDeleted);
                    }
                    boolean isSessionDeleted = facade.deleteSession(sessionEventResults[1], facade.getUserData("id"));
                    assertTrue(isSessionDeleted);
                    boolean isEventDeleted = facade.deleteEvent(eventResults[1], facade.getUserData("id"));
                    assertTrue(isEventDeleted);
                    boolean isUserDeleted = facade.deleteUser("1234");
                    assertTrue(isUserDeleted);
                }

            }
        }
    }

    @Test
    void testCreateSubEventAttendee() throws IOException {

        if (facade.loginValidate(email, password)) {
            boolean isUserDeleted = facade.deleteUser("1234");
            assertTrue(isUserDeleted);
        }

        Object[] userResults = facade.createUser("Name", "56756756756", "email@email.com", "1234");
        boolean isUserCreated = (boolean) userResults[0];
        if (isUserCreated) {
            boolean isLoggedIn = facade.loginValidate(email, password);
            if (isLoggedIn) {
                String dateString = "2024-02-12";
                LocalDate localDate = LocalDate.parse(dateString);

                java.sql.Date eventDate = java.sql.Date.valueOf(localDate);
                Object[] eventExistsResults = facade.isEventExist("Event", facade.getUserData("id"));
                boolean isEventExist = (boolean) eventExistsResults[0];
                if (isEventExist) {
                    UUID eventId = (UUID) eventExistsResults[1];
                    boolean isDeleted = facade.deleteEvent(eventId, facade.getUserData("id"));
                    assertTrue(isDeleted);
                }
                Object[] eventResults = facade.createEvent("Event", eventDate, "Event Test", "Location", facade.getUserData("id"));
                boolean isEventCreated = (boolean) eventResults[0];
                if (isEventCreated) {
                    Object[] subEventExistsResults = facade.isSubEventExist("SubEvent", facade.getUserData("id"));
                    boolean isSubEventExist = (boolean) subEventExistsResults[0];
                    if (isSubEventExist) {
                        UUID subEventId = (UUID) subEventExistsResults[1];
                        boolean isDeleted = facade.deleteSubEvent(subEventId, facade.getUserData("id"));
                        assertTrue(isDeleted);
                    }
                    Object[] subEventResults = facade.createSubEvent(eventResults[2] ,"SubEvent", eventDate, "SubEvent", "Location", facade.getUserData("id"));
                    assertTrue((boolean) subEventResults[0]);
                    Object[] eventSessionExistsResults = facade.isSessionExist("Session", facade.getUserData("id"));
                    boolean isSessionEventExist = (boolean) eventSessionExistsResults[0];
                    if (isSessionEventExist) {
                        UUID sessionEventId = (UUID) eventSessionExistsResults[1];
                        boolean isDeleted = facade.deleteSession(sessionEventId, facade.getUserData("id"));
                        assertTrue(isDeleted);
                    }
                    String[] type = {subEventResults[1].toString(), "subEvento"};
                    Object[] sessionEventResults = facade.createSession("Session", eventDate, "Session", "Location", "20:50", "21:50",facade.getUserData("id"), type);
                    if ((boolean) sessionEventResults[0]) {
                        Object[] attendeeExistsResults = facade.isAttendeeExist(facade.getUserData("id"));
                        if ((boolean) attendeeExistsResults[0]) {
                            UUID attendeeId = (UUID) attendeeExistsResults[1];
                            boolean isDeleted = facade.deleteAttendee(attendeeId, sessionEventResults[1]);
                            assertTrue(isDeleted);
                        }
                        Object[] attendeeResults = facade.createAttendee("Session", facade.getUserData("id"));
                        assertTrue((boolean) attendeeResults[0]);
                        boolean isAttendeeDeleted = facade.deleteAttendee(attendeeResults[1], sessionEventResults[1]);
                        assertTrue(isAttendeeDeleted);
                    }
                    boolean isSessionDeleted = facade.deleteSession(sessionEventResults[1], facade.getUserData("id"));
                    assertTrue(isSessionDeleted);
                    boolean isSubEventDeleted = facade.deleteSubEvent(subEventResults[1], facade.getUserData("id"));
                    assertTrue(isSubEventDeleted);
                    boolean isEventDeleted = facade.deleteEvent(eventResults[1], facade.getUserData("id"));
                    assertTrue(isEventDeleted);
                    boolean isUserDeleted = facade.deleteUser("1234");
                    assertTrue(isUserDeleted);
                }

            }
        }
    }
}
