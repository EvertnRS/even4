package br.upe.controller;

import br.upe.persistence.Persistence;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface Controller {
    void create(Object... params) throws IOException;
    void update(Object... params) throws IOException;
    void read() throws IOException;
    void delete(Object... params) throws IOException;
    boolean list(String idowner) throws IOException;
    List<String> list(String idowner, String type) throws IOException;
    void show(Object... params) throws IOException;
    boolean loginValidate(String email, String cpf);
    String getData(String dataToGet);
    Map<String, Persistence> getHashMap();
}
