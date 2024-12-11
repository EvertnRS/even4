package br.upe.persistence.builder;

import br.upe.persistence.Event;
import br.upe.persistence.SubmitArticle;
import br.upe.persistence.User;

import java.util.UUID;

public class SubmitArticleBuilder {
    private UUID id;
    private String name;
    private User ownerId;
    private Event eventId;
    private byte[] article;

    public static SubmitArticleBuilder builder() {
        return new SubmitArticleBuilder();
    }

    public SubmitArticleBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public SubmitArticleBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public SubmitArticleBuilder withOwner(User ownerId) {
        this.ownerId = ownerId;
        return this;
    }

    public SubmitArticleBuilder withEvent(Event eventId) {
        this.eventId = eventId;
        return this;
    }

    public SubmitArticleBuilder withArticle(byte[] article) {
        this.article = article;
        return this;
    }

    public SubmitArticle build() {
        SubmitArticle submitArticle = new SubmitArticle();
        submitArticle.setId(this.id);
        submitArticle.setName(this.name);
        submitArticle.setOwnerId(this.ownerId);
        submitArticle.setEventId(this.eventId);
        submitArticle.setArticle(this.article);
        return submitArticle;
    }
}
