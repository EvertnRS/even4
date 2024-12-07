package br.upe.controller;

import br.upe.persistence.Event;
import br.upe.persistence.repository.Persistence;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface Controller {
    boolean create(Object... params) throws IOException;
    boolean update(Object... params) throws IOException;
    void read() throws IOException;
    boolean delete(Object... params) throws IOException;
    <T> List <T> list(Object... params) throws IOException;
    boolean loginValidate(String email, String cpf);
    String getData(String dataToGet);
    Map<UUID, Persistence> getHashMap();
    List<Event> getAll();
}
