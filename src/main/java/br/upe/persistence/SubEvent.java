package br.upe.persistence;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;
import java.sql.Date;
import java.util.UUID;

@Entity
@Table(name = "subevents")
public class SubEvent implements Model{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID id;
    @NotNull
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    @ManyToOne
    private Event eventId;
    @NotNull
    @Column(unique = true)
    private String name;
    @NotNull
    private Date date;
    @NotNull
    private String description;
    @NotNull
    private String location;
    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    @NotNull
    private User ownerId;


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public @NotNull String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public @NotNull String getDescription() {
        return description;
    }

    public void setDescription(@NotNull String description) {
        this.description = description;
    }

    public @NotNull Date getDate() {
        return date;
    }

    public void setDate(@NotNull Date date) {
        this.date = date;
    }

    public @NotNull String getLocation() {
        return location;
    }

    public void setLocation(@NotNull String location) {
        this.location = location;
    }

    public @NotNull Event getEventId() {
        return eventId;
    }

    public void setEventId(@NotNull Event eventId) {
        this.eventId = eventId;
    }

    public @NotNull User getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(@NotNull User ownerId) {
        this.ownerId = ownerId;
    }

}