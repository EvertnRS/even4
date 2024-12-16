package br.upe.persistence.builder;

import br.upe.persistence.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class UserBuilder {
    private UUID id;
    private String name;
    private Long cpf;
    private String email;
    private String password;
    private List<Event> event;
    private List<SubEvent> subEvent;
    private List<Session> session;
    private List<SubmitArticle> article;

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public UserBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public UserBuilder withName(@NotNull String name) {
        this.name = name;
        return this;
    }

    public UserBuilder withCpf(@NotNull Long cpf) {
        this.cpf = cpf;
        return this;
    }

    public UserBuilder withEmail(@NotNull String email) {
        this.email = email;
        return this;
    }

    public UserBuilder withPassword(@NotNull String password) {
        this.password = password;
        return this;
    }

    public UserBuilder withEvent(List<Event> event) {
        this.event = event;
        return this;
    }

    public UserBuilder withSubEvent(List<SubEvent> subEvent) {
        this.subEvent = subEvent;
        return this;
    }

    public UserBuilder withSession(List<Session> session) {
        this.session = session;
        return this;
    }

    public UserBuilder withArticle(List<SubmitArticle> article) {
        this.article = article;
        return this;
    }

    public User build() {
        User user = new User();
        user.setId(this.id);
        user.setName(this.name);
        user.setCpf(this.cpf);
        user.setEmail(this.email);
        user.setPassword(this.password);
        user.setEvents(this.event);
        user.setSubEvents(this.subEvent);
        user.setSessions(this.session);
        user.setArticles(this.article);
        return user;
    }
}
