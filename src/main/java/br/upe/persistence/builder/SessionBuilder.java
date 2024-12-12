package br.upe.persistence.builder;

import br.upe.persistence.Event;
import br.upe.persistence.Session;
import br.upe.persistence.SubEvent;
import br.upe.persistence.User;

import java.sql.Time;
import java.util.Date;

public class SessionBuilder {
    private String name;
    private Date date;
    private String description;
    private String location;
    private Time startTime;
    private Time endTime;
    private SubEvent subEventId;
    private Event eventId;
    private User ownerId;

    public static SessionBuilder builder() {
        return new SessionBuilder();
    }

    public SessionBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public SessionBuilder withDate(Date date) {
        this.date = date;
        return this;
    }

    public SessionBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public SessionBuilder withLocation(String location) {
        this.location = location;
        return this;
    }

    public SessionBuilder withStartTime(Time startTime) {
        this.startTime = startTime;
        return this;
    }

    public SessionBuilder withEndTime(Time endTime) {
        this.endTime = endTime;
        return this;
    }

    public SessionBuilder withSubEvent(SubEvent subEventId) {
        this.subEventId = subEventId;
        return this;
    }

    public SessionBuilder withEvent(Event eventId) {
        this.eventId = eventId;
        return this;
    }

    public SessionBuilder withOwner(User ownerId) {
        this.ownerId = ownerId;
        return this;
    }

    public Session build() {
        Session session = new Session();
        session.setName(this.name);
        session.setDate(this.date);
        session.setDescription(this.description);
        session.setLocation(this.location);
        session.setStartTime(this.startTime);
        session.setEndTime(this.endTime);
        session.setSubEventId(this.subEventId);
        session.setEventId(this.eventId);
        session.setOwnerId(this.ownerId);
        return session;
    }
}
