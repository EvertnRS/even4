package br.upe.ui;

import br.upe.controller.EventController;
import br.upe.persistence.Event;
import br.upe.persistence.User;
import br.upe.persistence.repository.EventRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventController eventController;
    private UUID userId;
    private String originalEnvironment;

    @BeforeEach
    public void setUp() {
        originalEnvironment = System.getProperty("ENVIRONMENT");
        System.setProperty("ENVIRONMENT", "test");

        userId = UUID.randomUUID();
        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);

        List<Event> events = new ArrayList<>();
        Event event = new Event();
        event.setOwnerId(user);
        events.add(event);

        when(eventRepository.getAllEvents()).thenReturn(events);
    }

    @AfterEach
    public void tearDown() {
        if (originalEnvironment != null) {
            System.setProperty("ENVIRONMENT", originalEnvironment);
        } else {
            System.clearProperty("ENVIRONMENT");
        }
    }
}