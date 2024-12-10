package br.upe.persistence.builder;

import br.upe.persistence.Event;
import br.upe.persistence.User;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.UUID;

public class EventBuilder {
    private UUID id;
    private String name;
    private Date date;
    private String description;
    private String location;
    private User ownerId;

    public static EventBuilder builder() {
        return new EventBuilder();
    }

    public EventBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public EventBuilder withName(@NotNull String name) {
        this.name = name;
        return this;
    }

    public EventBuilder withDate(@NotNull Date date) {
        this.date = date;
        return this;
    }

    public EventBuilder withDescription(@NotNull String description) {
        this.description = description;
        return this;
    }

    public EventBuilder withLocation(@NotNull String location) {
        this.location = location;
        return this;
    }

    public EventBuilder withOwner(@NotNull User ownerId) {
        this.ownerId = ownerId;
        return this;
    }

    public Event build() {
        Event event = new Event();
        event.setId(this.id);
        event.setName(this.name);
        event.setDate(this.date);
        event.setDescription(this.description);
        event.setLocation(this.location);
        event.setOwnerId(this.ownerId);
        return event;
    }
}