package br.upe.controller;

import br.upe.persistence.Model;
import br.upe.persistence.SubEvent;
import br.upe.persistence.repository.Persistence;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface Controller {
    Object[] create(Object... params) throws IOException;
    boolean update(Object... params) throws IOException;
    void read() throws IOException;
    boolean delete(Object... params) throws IOException;

    <T> List <T> list(Object... params) throws IOException;
    Object[] isExist(Object... params) throws IOException;
    String getData(String dataToGet);
    Map<UUID, Persistence> getHashMap();

    <T> List <T> getAll();

    <T> List <T> getEventArticles(UUID eventId);
}
