package br.upe.persistence;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

import java.sql.Time;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "sessions")
public class Session implements Model {
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

    @NotNull
    private Time startTime;

    @NotNull
    private Time endTime;


    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    @NotNull
    private User ownerId;

    @ManyToOne
    @JoinColumn(name = "sub_event_id", referencedColumnName = "id")
    private SubEvent subEventId;

    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    @NotNull
    private Event eventId;

    // Construtor vazio
    public Session() {}

    // Getters e Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public Event getEventId() {
        return eventId;
    }

    public void setEventId(Event eventId) {
        this.eventId = eventId;
    }

    public User getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(User ownerId) {
        this.ownerId = ownerId;
    }

    public SubEvent getSubEventId() {
        return subEventId;
    }

    public void setSubEventId(SubEvent subEvent) {
        this.subEventId = subEvent;
    }

}
