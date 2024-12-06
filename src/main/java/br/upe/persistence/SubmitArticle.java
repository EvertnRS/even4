package br.upe.persistence;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Entity
@Table(name = "articles")
public class SubmitArticle implements Model {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @NotNull
    @Column(unique = true)
    private String name;


    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private User ownerId;


    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    private Event eventId;

    @NotNull
    @Lob
    private byte[] article;


    // Getters
    public UUID getId() {
        return id;
    }

    public User getOwnerId() {
        return ownerId;
    }

    public Event getEventId() {
        return eventId;
    }

    public byte[] getArticle() {
        return article;
    }

    // Setters
    public void setId(UUID id) {
        this.id = id;
    }

    public void setOwnerId(@NotNull User ownerId) {
        this.ownerId = ownerId;
    }

    public void setEventId(@NotNull Event eventId) {
        this.eventId = eventId;
    }

    public void setArticle(@NotNull byte[] article) {
        this.article = article;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
