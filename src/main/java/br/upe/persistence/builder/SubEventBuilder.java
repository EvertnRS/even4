package br.upe.persistence.builder;

import br.upe.persistence.Event;
import br.upe.persistence.SubEvent;
import br.upe.persistence.User;
import org.jetbrains.annotations.NotNull;
import java.sql.Date;
import java.util.UUID;

public class SubEventBuilder {
    private Event id;
    private String name;
    private Date date;
    private String description;
    private String location;
    private User ownerId;

    public static SubEventBuilder builder() {
        return new SubEventBuilder();
    }

    public SubEventBuilder withId(Event id) {
        this.id = id;
        return this;
    }

    public SubEventBuilder withName(@NotNull String name) {
        this.name = name;
        return this;
    }

    public SubEventBuilder withDate(@NotNull Date date) {
        this.date = date;
        return this;
    }

    public SubEventBuilder withDescription(@NotNull String description) {
        this.description = description;
        return this;
    }

    public SubEventBuilder withLocation(@NotNull String location) {
        this.location = location;
        return this;
    }

    public SubEventBuilder withOwner(@NotNull User ownerId) {
        this.ownerId = ownerId;
        return this;
    }

    public SubEvent build() {
        SubEvent subevent = new SubEvent();
        subevent.setEventId(this.id);
        subevent.setName(this.name);
        subevent.setDate(this.date);
        subevent.setDescription(this.description);
        subevent.setLocation(this.location);
        subevent.setOwnerId(this.ownerId);
        return subevent;
    }
}