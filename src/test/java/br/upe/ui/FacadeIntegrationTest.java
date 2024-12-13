package br.upe.ui;

import br.upe.controller.UserController;
import br.upe.facade.Facade;
import br.upe.facade.FacadeInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FacadeIntegrationTest {

    FacadeInterface facade = new Facade(UserController.getInstance());
    String email = "email@email.com";
    String password = "1234";


    FacadeIntegrationTest() throws IOException {
    }

    @Test
    void testCreateUser() throws IOException {
        if (facade.loginValidate(email, password)) {
            facade.deleteUser("1234", facade.getUserData("id"));
        }

        boolean isCreated = facade.createUser("Name", "56756756756", "email@email.com", "1234");
        if (isCreated) {
            boolean isLoggedIn = facade.loginValidate(email, password);
            if (isLoggedIn) {
                String userId = facade.getUserData("id");
                facade.deleteUser("1234", userId);
            }
        }
    }

    @Test
    void testUpdateUserName() throws IOException {
        if (facade.loginValidate(email, password)) {
            facade.deleteUser("1234", facade.getUserData("id"));
        }

        boolean isCreated = facade.createUser("Name", "56756756756", "email@email.com", "1234");
        if (isCreated) {
            boolean isLoggedIn = facade.loginValidate(email, password);
            if (isLoggedIn) {
                boolean isUpdated = facade.updateUser("Name Updated", "56756756756", "email@email.com", "1234", "1234");
                assertTrue(isUpdated);
                facade.deleteUser("1234", facade.getUserData("id"));
            }
        }
    }

    @Test
    void testUpdateUserCpf() throws IOException {
        if (facade.loginValidate(email, password)) {
            facade.deleteUser("1234", facade.getUserData("id"));
        }

        boolean isCreated = facade.createUser("Name", "56756756756", "email@email.com", "1234");
        if (isCreated) {
            boolean isLoggedIn = facade.loginValidate(email, password);
            if (isLoggedIn) {
                boolean isUpdated = facade.updateUser("Name", "67867867867", "email@email.com", "1234", "1234");
                assertTrue(isUpdated);
                facade.deleteUser("1234", facade.getUserData("id"));
            }
        }
    }

    @Test
    void testUpdateUserEmail() throws IOException {
        if (facade.loginValidate(email, password)) {
            facade.deleteUser("1234", facade.getUserData("id"));
        }

        boolean isCreated = facade.createUser("Name", "56756756756", "email@email.com", "1234");
        if (isCreated) {
            boolean isLoggedIn = facade.loginValidate(email, password);
            if (isLoggedIn) {
                boolean isUpdated = facade.updateUser("Name", "12345678901", "new_unique_email@test.com", "1234", "1234");
                assertTrue(isUpdated);
                facade.deleteUser("1234", facade.getUserData("id"));
            }
        }

    }


    @Test
    void testUpdateUserPass() throws IOException {
        if (facade.loginValidate(email, password)) {
            facade.deleteUser("1234", facade.getUserData("id"));
        }
        boolean isCreated = facade.createUser("Name", "56756756756", "email@email.com", "1234");
        if (isCreated) {
            boolean isLoggedIn = facade.loginValidate(email, password);
            if (isLoggedIn) {
                boolean isUpdated = facade.updateUser("Name", "56756756756", "email@email.com", "4321", "1234");
                assertTrue(isUpdated);
                facade.deleteUser("4321", facade.getUserData("id"));
            }
        }
    }

    @Test
    void testDeleteUser() throws IOException {
        boolean isCreated = false;
        if(!facade.loginValidate(email, password)) {
            isCreated = facade.createUser("Name", "56756756756", "email@email.com", "1234");
        }

        if (isCreated) {
            boolean isLoggedIn = facade.loginValidate(email, password);
            if (isLoggedIn) {
                boolean isDeleted = facade.deleteUser("1234", facade.getUserData("id"));
                assertTrue(isDeleted);
            }
        }
    }

    @Test
    void testCreateEvent() throws IOException {
        if (facade.loginValidate(email, password)) {
            facade.deleteUser("1234", facade.getUserData("id"));
        }

        boolean isUserCreated = facade.createUser("Name", "56756756756", "email@email.com", "1234");
        if (isUserCreated) {
            boolean isLoggedIn = facade.loginValidate(email, password);
            if (isLoggedIn) {
                String dateString = "2024-02-12";
                LocalDate localDate = LocalDate.parse(dateString);

                Date eventDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                boolean isEventCreated = facade.createEvent("Event", eventDate, "Event Test", "Location", facade.getUserData("id"));
                assertTrue(isEventCreated);
                facade.deleteEvent(facade.getEventData("id"), facade.getUserData("id"));
                facade.deleteUser("1234", facade.getUserData("id"));
            }
        }
    }

    @Test
    void testUpdateEventName() throws IOException {
        if (facade.loginValidate(email, password)) {
            facade.deleteUser("1234", facade.getUserData("id"));
        }

        boolean isUserCreated = facade.createUser("Name", "56756756756", "email@email.com", "1234");
        if (isUserCreated) {
            boolean isLoggedIn = facade.loginValidate(email, password);
            if (isLoggedIn) {
                String dateString = "2024-02-12";
                LocalDate localDate = LocalDate.parse(dateString);

                Date eventDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                boolean isEventUpdated = facade.updateEvent(facade.getEventData("id"), "Event Updated", eventDate, "Event Test", "Location");
                assertTrue(isEventUpdated);
                facade.deleteEvent(facade.getEventData("id"), facade.getUserData("id"));
                facade.deleteUser("1234", facade.getUserData("id"));
            }
        }
    }

    @Test
    void testUpdateEvenDate() throws IOException {
        if (facade.loginValidate(email, password)) {
            facade.deleteUser("1234", facade.getUserData("id"));
        }

        boolean isUserCreated = facade.createUser("Name", "56756756756", "email@email.com", "1234");
        if (isUserCreated) {
            boolean isLoggedIn = facade.loginValidate(email, password);
            if (isLoggedIn) {
                String dateString = "2024-12-12";
                LocalDate localDate = LocalDate.parse(dateString);

                Date newEventDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                boolean isEventUpdated = facade.updateEvent(facade.getEventData("id"), "Event", newEventDate, "Event Test", "Location");
                assertTrue(isEventUpdated);
                facade.deleteEvent(facade.getEventData("id"), facade.getUserData("id"));
                facade.deleteUser("1234", facade.getUserData("id"));
            }
        }
    }

    @Test
    void testUpdateEventDescription() throws IOException {
        if (facade.loginValidate(email, password)) {
            facade.deleteUser("1234", facade.getUserData("id"));
        }

        boolean isUserCreated = facade.createUser("Name", "56756756756", "email@email.com", "1234");
        if (isUserCreated) {
            boolean isLoggedIn = facade.loginValidate(email, password);
            if (isLoggedIn) {
                String dateString = "2024-02-12";
                LocalDate localDate = LocalDate.parse(dateString);

                Date eventDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                boolean isEventUpdated = facade.updateEvent(facade.getEventData("id"), "Event", eventDate, "Event Test Updated", "Location");
                assertTrue(isEventUpdated);
                facade.deleteEvent(facade.getEventData("id"), facade.getUserData("id"));
                facade.deleteUser("1234", facade.getUserData("id"));
            }
        }
    }

    @Test
    void testUpdateEventLocation() throws IOException {
        if (facade.loginValidate(email, password)) {
            facade.deleteUser("1234", facade.getUserData("id"));
        }

        boolean isUserCreated = facade.createUser("Name", "56756756756", "email@email.com", "1234");
        if (isUserCreated) {
            boolean isLoggedIn = facade.loginValidate(email, password);
            if (isLoggedIn) {
                String dateString = "2024-02-12";
                LocalDate localDate = LocalDate.parse(dateString);

                Date eventDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                boolean isEventUpdated = facade.updateEvent(facade.getEventData("id"), "Event", eventDate, "Event Test", "Location Updated");
                assertTrue(isEventUpdated);
                facade.deleteEvent(facade.getEventData("id"), facade.getUserData("id"));
                facade.deleteUser("1234", facade.getUserData("id"));
            }
        }
    }

    @Test
    void testDeleteEvent() throws IOException {
        if (facade.loginValidate(email, password)) {
            facade.deleteUser("1234", facade.getUserData("id"));
        }

        boolean isUserCreated = facade.createUser("Name", "56756756756", "email@email.com", "1234");
        if (isUserCreated) {
            boolean isLoggedIn = facade.loginValidate(email, password);
            if (isLoggedIn) {
                boolean isEventDeleted = facade.deleteEvent(facade.getEventData("id"), facade.getUserData("id"));
                assertTrue(isEventDeleted);
                facade.deleteUser("1234", facade.getUserData("id"));
            }
        }
    }

    @Test
    void testCreateSubEvent() throws IOException {
        if (facade.loginValidate(email, password)) {
            facade.deleteUser("1234", facade.getUserData("id"));
        }

        boolean isUserCreated = facade.createUser("Name", "56756756756", "email@email.com", "1234");
        if (isUserCreated) {
            boolean isLoggedIn = facade.loginValidate(email, password);
            if (isLoggedIn) {
                String dateString = "2024-02-12";
                LocalDate localDate = LocalDate.parse(dateString);

                Date eventDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                boolean isEventCreated = facade.createEvent("Event", eventDate, "Event Test", "Location", facade.getUserData("id"));
                if(isEventCreated) {
                    boolean isSubEventCreated = facade.createSubEvent(facade.getEventData("id"), "SubEvent", eventDate, "SubEvent", "Location", facade.getUserData("id"));
                    assertTrue(isSubEventCreated);
                    facade.deleteSubEvent(facade.getSubEventData("id"), facade.getUserData("id"));
                    facade.deleteEvent(facade.getEventData("id"), facade.getUserData("id"));
                    facade.deleteUser("1234", facade.getUserData("id"));
                }
            }
        }
    }

    @Test
    void testUpdateSubEventName() throws IOException {
        if (facade.loginValidate(email, password)) {
            facade.deleteUser("1234", facade.getUserData("id"));
        }

        boolean isUserCreated = facade.createUser("Name", "56756756756", "email@email.com", "1234");
        if (isUserCreated) {
            boolean isLoggedIn = facade.loginValidate(email, password);
            if (isLoggedIn) {
                String dateString = "2024-02-12";
                LocalDate localDate = LocalDate.parse(dateString);

                Date eventDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                boolean isEventCreated = facade.createEvent("Event", eventDate, "Event Test", "Location", facade.getUserData("id"));
                if(isEventCreated) {
                    boolean isSubEventCreated = facade.updateSubEvent(facade.getSubEventData("id"), "SubEvent Updated", eventDate, "SubEvent", "Location");
                    assertTrue(isSubEventCreated);
                    facade.deleteSubEvent(facade.getSubEventData("id"), facade.getUserData("id"));
                    facade.deleteEvent(facade.getEventData("id"), facade.getUserData("id"));
                    facade.deleteUser("1234", facade.getUserData("id"));
                }
            }
        }
    }

    @Test
    void testUpdateSubEventDate() throws IOException {
        if (facade.loginValidate(email, password)) {
            facade.deleteUser("1234", facade.getUserData("id"));
        }

        boolean isUserCreated = facade.createUser("Name", "56756756756", "email@email.com", "1234");
        if (isUserCreated) {
            boolean isLoggedIn = facade.loginValidate(email, password);
            if (isLoggedIn) {
                String dateString = "2024-12-13";
                LocalDate localDate = LocalDate.parse(dateString);

                Date eventDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                boolean isEventCreated = facade.createEvent("Event", eventDate, "Event Test", "Location", facade.getUserData("id"));
                if(isEventCreated) {
                    boolean isSubEventCreated = facade.updateSubEvent(facade.getSubEventData("id"), "SubEvent", eventDate, "SubEvent", "Location");
                    assertTrue(isSubEventCreated);
                    facade.deleteSubEvent(facade.getSubEventData("id"), facade.getUserData("id"));
                    facade.deleteEvent(facade.getEventData("id"), facade.getUserData("id"));
                    facade.deleteUser("1234", facade.getUserData("id"));
                }
            }
        }
    }

    @Test
    void testUpdateSubEventDescription() throws IOException {
        if (facade.loginValidate(email, password)) {
            facade.deleteUser("1234", facade.getUserData("id"));
        }

        boolean isUserCreated = facade.createUser("Name", "56756756756", "email@email.com", "1234");
        if (isUserCreated) {
            boolean isLoggedIn = facade.loginValidate(email, password);
            if (isLoggedIn) {
                String dateString = "2024-02-12";
                LocalDate localDate = LocalDate.parse(dateString);

                Date eventDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                boolean isEventCreated = facade.createEvent("Event", eventDate, "Event Test", "Location", facade.getUserData("id"));
                if(isEventCreated) {
                    boolean isSubEventCreated = facade.updateSubEvent(facade.getSubEventData("id"), "SubEvent", eventDate, "SubEvent Updated", "Location");
                    assertTrue(isSubEventCreated);
                    facade.deleteSubEvent(facade.getSubEventData("id"), facade.getUserData("id"));
                    facade.deleteEvent(facade.getEventData("id"), facade.getUserData("id"));
                    facade.deleteUser("1234", facade.getUserData("id"));
                }
            }
        }
    }

    @Test
    void testUpdateSubEventLocation() throws IOException {
        if (facade.loginValidate(email, password)) {
            facade.deleteUser("1234", facade.getUserData("id"));
        }

        boolean isUserCreated = facade.createUser("Name", "56756756756", "email@email.com", "1234");
        if (isUserCreated) {
            boolean isLoggedIn = facade.loginValidate(email, password);
            if (isLoggedIn) {
                String dateString = "2024-02-12";
                LocalDate localDate = LocalDate.parse(dateString);

                Date eventDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                boolean isEventCreated = facade.createEvent("Event", eventDate, "Event Test", "Location", facade.getUserData("id"));
                if(isEventCreated) {
                    boolean isSubEventCreated = facade.updateSubEvent(facade.getSubEventData("id"), "SubEvent", eventDate, "SubEvent", "Location Updated");
                    assertTrue(isSubEventCreated);
                    facade.deleteSubEvent(facade.getSubEventData("id"), facade.getUserData("id"));
                    facade.deleteEvent(facade.getEventData("id"), facade.getUserData("id"));
                    facade.deleteUser("1234", facade.getUserData("id"));
                }
            }
        }
    }

    @Test
    void testUpdateSubEventDelete() throws IOException {
        if (facade.loginValidate(email, password)) {
            facade.deleteUser("1234", facade.getUserData("id"));
        }

        boolean isUserCreated = facade.createUser("Name", "56756756756", "email@email.com", "1234");
        if (isUserCreated) {
            boolean isLoggedIn = facade.loginValidate(email, password);
            if (isLoggedIn) {
                String dateString = "2024-02-12";
                LocalDate localDate = LocalDate.parse(dateString);

                Date eventDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                boolean isEventCreated = facade.createEvent("Event", eventDate, "Event Test", "Location", facade.getUserData("id"));
                if(isEventCreated) {
                    boolean isSubEventDeleted = facade.deleteSubEvent(facade.getSubEventData("id"), facade.getUserData("id"));
                    assertTrue(isSubEventDeleted);
                    facade.deleteEvent(facade.getEventData("id"), facade.getUserData("id"));
                    facade.deleteUser("1234", facade.getUserData("id"));
                }
            }
        }
    }

}