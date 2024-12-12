package br.upe.persistence;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "attendees")
public class Attendee implements Model {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @NotNull
    private User userId;

    @ManyToMany
    @JoinTable(
            name = "participations",
            joinColumns = @JoinColumn(name = "attendee_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "session_id", referencedColumnName = "id")
    )
    private Set<Session> sessions = new HashSet<>();

    public @NotNull User getUserId() {
        return userId;
    }

    public void setUserId(@NotNull User userId) {
        this.userId = userId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Set<UUID> getSessionIds() {
        Set<UUID> sessionIds = new HashSet<>();
        for (Session session : sessions) {
            sessionIds.add(session.getId());
        }
        return sessionIds;
    }


    public void setSessions(Set<Session> sessions) {
        this.sessions = sessions;
    }

    public Set<Session> getSessions() {
        return this.sessions;
    }

    public void addSession(Session session) {
        this.sessions.add(session);
    }

    @Override
    public String getName() {

        return "";
    }
}
