package br.upe.persistence;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID id;
    @NotNull
    private String name;
    @NotNull
    @Column(unique = true)
    private Long cpf;
    @NotNull
    @Column(unique = true)
    private String email;
    @NotNull
    @Column(length = 60)
    private String password;
    @OneToMany(mappedBy = "ownerId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Event> events;
    @OneToMany(mappedBy = "ownerId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubEvent> subEvents;
    @OneToMany(mappedBy = "ownerId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Session> sessions;
    @OneToMany(mappedBy = "ownerId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubmitArticle> articles;
    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attendee> participations;

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public List<SubEvent> getSubEvents() {
        return subEvents;
    }

    public void setSubEvents(List<SubEvent> subEvents) {
        this.subEvents = subEvents;
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public void setSessions(List<Session> sessions) {
        this.sessions = sessions;
    }

    public List<SubmitArticle> getArticles() {
        return articles;
    }

    public void setArticles(List<SubmitArticle> articles) {
        this.articles = articles;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public @NotNull Long getCpf() {
        return cpf;
    }

    public void setCpf(@NotNull Long cpf) {
        this.cpf = cpf;
    }

    public @NotNull String getEmail() {
        return email;
    }

    public void setEmail(@NotNull String email) {
        this.email = email;
    }

    public @NotNull String getPassword() {
        return password;
    }

    public void setPassword(@NotNull String password) {
        this.password = password;
    }

    public @NotNull String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public List<Attendee> getParticipations() {
        return participations;
    }

    public void setParticipations(List<Attendee> participations) {
        this.participations = participations;
    }
}