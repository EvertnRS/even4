package br.upe.persistence;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID id;
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

    // Getters
    public User getIdOwner() {
        return ownerId;
    }

    public UUID getId() {

        return id;
    }

    public @NotNull String getName() {

        return name;
    }

    public @NotNull Date  getDate() {

        return date;
    }

    public @NotNull String getDescription() {

        return description;
    }

    public @NotNull String getLocation() {

        return location;
    }

    // Setters

    public void setId(UUID id) {

        this.id = id;
    }

    public void setName(@NotNull String name) {

        this.name = name;
    }

    public void setDate(@NotNull Date date) {

        this.date = date;
    }

    public void setDescription(@NotNull String description) {

        this.description = description;
    }

    public void setLocation(@NotNull String location) {

        this.location = location;
    }

    public void setOwnerId(@NotNull User ownerId) {

        this.ownerId = ownerId;
    }
}
