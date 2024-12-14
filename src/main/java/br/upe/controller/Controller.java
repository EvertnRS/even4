package br.upe.controller;

import br.upe.persistence.repository.Persistence;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface Controller {
    void create(Object... params) throws IOException;

    void update(Object... params) throws IOException;

    void delete(Object... params) throws IOException;

    <T> List<T> getAll();

    <T> List<T> list(Object... params) throws IOException;

    <T> List<T> getEventArticles(UUID eventId);

    boolean loginValidate(String email, String cpf);

    String getData(String dataToGet);

}
