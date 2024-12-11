package br.upe.persistence.builder;

import br.upe.persistence.Attendee;
import br.upe.persistence.Session;
import br.upe.persistence.User;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AttendeeBuilder {
    private UUID id;
    private User userId;
    private Set<Session> sessions = new HashSet<>();

    public static AttendeeBuilder builder() {
        return new AttendeeBuilder();
    }

    public AttendeeBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public AttendeeBuilder withUser(@NotNull User userId) {
        this.userId = userId;
        return this;
    }

    public AttendeeBuilder withSessions(Set<Session> sessions) {
        this.sessions = sessions;
        return this;
    }

    public AttendeeBuilder addSession(Session session) {
        this.sessions.add(session);
        return this;
    }

    public Attendee build() {
        Attendee attendee = new Attendee();
        attendee.setId(this.id);
        attendee.setUserId(this.userId);
        attendee.setSessions(this.sessions);
        return attendee;
    }
}